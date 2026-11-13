const turnOrderButtons = document.getElementById("turnOrderButtons");
const rollDiceBtn = document.getElementById("roll-dice-btn");
const pieceLayer = document.getElementById("piece-layer");
const protocol = location.protocol === "https:" ? "wss" : "ws";

const dice1Div = document.getElementById("dice1")
const dice2Div = document.getElementById("dice2")
const tiles = document.getElementById("tiles");

const diceAimButton = document.getElementById("roll-dice-btn");
const diceAim = document.getElementById("dice-aim");
const diceAimValue = document.getElementById("dice-aim-value");
const diceAimBar = document.getElementById("dice-aim-bar");
const diceResult = document.getElementById("dice-result");
const trainTravelPrompt = document.getElementById("train-travel-prompt");
let trainTravelSelection = null;

let constructionPopup = null;
let skillConstructionPopup = null;
let upgradeConstructionPopup = null;
let trapNoticeTimer = null;
let gameFinished = null;

const stompClient = new StompJs.Client({
    brokerURL: `${protocol}://${location.host}/ws`,
    reconnectDelay: 5000,

    onConnect() {
        console.log("연결성공");

        stompClient.subscribe(`/topic/games/${gameId}/turn-order/button-selected`, message => {
            const response = JSON.parse(message.body);
            buttonDisabled(response);
        })

        stompClient.subscribe(`/topic/games/${gameId}/turn-order-determined`, async message => {

            console.log("첫 턴 확정 후 첫 턴 누군지 보여주는 메서드 호출")
            const data = JSON.parse(message.body);

            showTurnOrderResult(data.firstCardIndex)
            await getPlayers();

            const datasetUserId = Number(document.body.dataset.userId);
            if (data.currentTurnUserId === datasetUserId) {
                pieceLayer.className = "is-my-turn";
                rollDiceBtn.hidden = false;

                console.log(data.playerId)
                let playerPiece = document.querySelector(`.player-piece[data-game-player-id="${data.playerId}"]`)
                playerPiece.classList.add("is-mine");
            }
        })

        let moveAnimation = Promise.resolve();
        stompClient.subscribe(`/topic/games/${gameId}/move`, message => {
            // const data = JSON.parse(message.body);
            //
            // diceResult.hidden = false
            // animateDice(data.dice1,data.dice2);
            // setTimeout(()=>{
            //     movePlayerPiece(data)
            //     diceResult.hidden = true;
            // },1500)

            const data = JSON.parse(message.body);


            moveAnimation = moveAnimation.then(async () => {
                diceResult.hidden = false;
                animateDice(data.dice1, data.dice2);

                showActivatedItemIcons(data.playerId,data.diceControlActivated, data.activatedItemImagePath);

                await new Promise(resolve => setTimeout(resolve, 1500));

                diceResult.hidden = true;
                await movePlayerPiece(data);
            }).catch(error => console.error("말 이동 실패:", error));
        })

        stompClient.subscribe(`/topic/games/${gameId}/building-purchased`, message => {
            const data = JSON.parse(message.body);
            console.log(data);
            renderBuildingOnTile(data);
            console.log(data.playerCash)
            payment(data.playerId,data.playerCash);
            const state = skillConstructionPopup;

            const upgrade = upgradeConstructionPopup;

            if (
                upgrade?.purchaseRequested &&
                Number(data.tileId) === Number(upgrade.tileId) &&
                String(data.playerId) === String(upgrade.playerId)
            ) {
                upgradeConstructionPopup = null;
                upgrade.popup.close();
            }


            showMyLand([]);
        })
        stompClient.subscribe(`/topic/games/${gameId}/skill-building-purchase`, response => {
            const data = JSON.parse(response.body);
            console.log(data);
            renderBuildingOnTile(data);
            console.log(data.playerCash)
            payment(data.playerId,data.playerCash);
            const state = skillConstructionPopup;

            if (state?.purchaseRequested && Number(data.tileId) === Number(state.tileId)) {
                state.popup.close();
                skillConstructionPopup = null;
            }
            showMyLand([]);
        })

        stompClient.subscribe(`/topic/games/${gameId}/toll-calculated`, response => {
            const data = JSON.parse(response.body);

            updatePlayersAndTilesStatus(data);

            if(gameFinished) return;
            if(!data.landmark) {
                console.log(data.isLandmark)
                openAcquisitionForm(data);
            }
        })

        stompClient.subscribe(`/topic/games/${gameId}/building-acquired`, response => {
            const data = JSON.parse(response.body);

            animateDeedTransfer(data)
        })

        stompClient.subscribe(`/topic/games/${gameId}/turn-changed`, async response => {
            const data = JSON.parse(response.body);
            await moveAnimation;
            if(gameFinished) return;
            if(data.nextAction === "TRAP") {
                showTrapNotice();
            }
            const choosingTravel = data.nextAction === "TRAIN_TRAVEL";
            const isMyTurn =
                String(data.currentTurnUserId) === document.body.dataset.userId;

            showMyLand([]);
            addIsMyTurnClassName(data, !choosingTravel);

            showTrainTravelTargets(
                choosingTravel && isMyTurn ? data.eligibleTileIndexes : []
            );
        })

        stompClient.subscribe(`/topic/games/${gameId}/skill-move`, async response => {
            const data = JSON.parse(response.body)

            const piece = document.querySelector(`.player-piece[data-game-player-id="${data.playerId}"]`);
            if(!piece) return;
            showActivatedItemIcons(data.playerId,data.diceControlActivated, data.activatedItemImagePath);
            await animatePieceByTiles(piece, {...data, moveSteps: 0}, pieceLayer);
            showMyLand(data.eligibleTileIndexes, data.playerId);
            console.log(data.eligibleTileIndexes)
        })

        stompClient.subscribe(`/topic/games/${gameId}/train-travel`, response => {
            const data = JSON.parse(response.body);

            moveAnimation = moveAnimation.then(async () => {
                const piece = pieceLayer.querySelector(
                    `.player-piece[data-game-player-id="${data.playerId}"]`
                );
                if (!piece) return;

                if (data.activated && data.itemImagePath) {
                    showActivatedItemIcons(data.playerId, true, [data.itemImagePath]);
                }

                await animatePieceByTiles(piece, { ...data, moveSteps: 0 }, pieceLayer);
            }).catch(error => console.error("기차여행 이동 실패:", error));
        })

        stompClient.subscribe(`/topic/games/${gameId}/train-travel-arrived`, response => {
            const data = JSON.parse(response.body);

            movePlayerPiece(data);
            showTrainTravelTargets([])
        })

        stompClient.subscribe(`/topic/games/${gameId}/game-finished`, response => {
            const data = JSON.parse(response.body);
            gameFinished = true;

            showMyLand([]);
            showTrainTravelTargets([]);

            showGameResult(data)
        })
    },

    onWebSocketError(error) {
        console.error("WebSocket 오류", error);
    },
    onStompError(error) {
        console.error("STOMP 오류", error);
    }
});

function showGameResult(data) {
    if(data == null) return;

    const gameResultDiv = document.getElementById("gameResult");
    const h3 = document.createElement("h3");
    const nameSpan = document.createElement("span");
    const rankNoSpan = document.createElement("span");
    const cashSpan = document.createElement("span");
    const finishReasonSpan = document.createElement("span");
    const lobbyBtn = document.createElement("button");

    gameResultDiv.hidden = false;

    gameResultDiv.className = "game-result";

    h3.textContent = "!! 게임 결과 !!";
    nameSpan.textContent = data.userName;
    rankNoSpan.textContent = data.rankNo;
    cashSpan.textContent = `${Number(data.cash).toLocaleString("ko-KR")}원`;
    finishReasonSpan.textContent = data.finishReason;
    lobbyBtn.textContent = "로비로";
    lobbyBtn.type = "button";
    lobbyBtn.id = "lobbyBtn";

    gameResultDiv.replaceChildren(h3,nameSpan,rankNoSpan,cashSpan, finishReasonSpan, lobbyBtn);

    lobbyBtn.addEventListener("click", () => {
        window.location.href = "/lobby";
    })
}
function showTrapNotice() {
    const notice = document.getElementById("trap-notice");

    clearTimeout(trapNoticeTimer);
    notice.hidden = false;

    trapNoticeTimer = setTimeout(() => {
        notice.hidden = true;
    }, 1500)
}

function showUpgradeConstructionOptions(tileId, playerId) {
    const popup = window.open(
        `/games/${gameId}/tiles/${tileId}/upgrade-construction`,
        "랜드마크 건설",
        "width=600,height=500"
    );

    if (!popup) return;

    const state = { popup, tileId, playerId, purchaseRequested: false };
    upgradeConstructionPopup = state;

    const timerId = setInterval(() => {
        if (!popup.closed) return;

        clearInterval(timerId);
        if (upgradeConstructionPopup !== state) return;

        upgradeConstructionPopup = null;
        if (state.purchaseRequested) return;

        stompClient.publish({
            destination: `/app/games/${gameId}/players/${playerId}/building-upgrade-skip`,
        });
    }, 300);
}

function showMyLand(eligibleTileIndexes, playerId) {
    const eligible = new Set((eligibleTileIndexes ?? []).map(String));

    document.querySelectorAll("#tiles .board-tile")
        .forEach(tile => {
        if(tile.dataset.tileType === "TOURIST_SPOT") return;
        tile.dataset.playerId = playerId;
        tile.classList.toggle(
            "is-build-target",
            eligible.has(tile.dataset.pathIndex)
        );
    });
}

function showTrainTravelTargets(tileIndexes = []) {
    const eligible = new Set((tileIndexes ?? []).map(String));

    tileBoard.querySelectorAll(".board-tile").forEach(tile => {
        tile.classList.toggle(
            "is-train-travel-target",
            eligible.has(tile.dataset.pathIndex)
        );
    });

    trainTravelSelection = eligible.size > 0 ? { submitted: false } : null;
    trainTravelPrompt.hidden = !trainTravelSelection;
    trainTravelPrompt.textContent = "기차여행 · 이동할 지역을 선택하세요";
}

const tileBoard = document.getElementById("tiles");

tileBoard?.addEventListener("click", event => {
    const tile = event.target.closest(".board-tile.is-build-target");
    if (!tile || !tileBoard.contains(tile)) return;
    const tileId = tile.dataset.pathIndex;
    const popup = window.open(`/games/${gameId}/players/${tile.dataset.playerId}/tiles/${tileId}/skill-construction`, "스킬 추가 건설", "width=600,height=500");

    if (popup) {
        skillConstructionPopup = { popup, tileId, purchaseRequested: false };
    }}
);

tileBoard?.addEventListener("click", event => {
    const tile = event.target.closest(".board-tile.is-train-travel-target");
    const selection = trainTravelSelection;

    if (!tile || !tileBoard.contains(tile)) return;
    if (!selection || selection.submitted || !stompClient.connected) return;

    selection.submitted = true;
    trainTravelPrompt.textContent = "이동을 요청했어요.";

    stompClient.publish({
        destination: `/app/games/${gameId}/players/me/train-travel-select`,
        body: JSON.stringify({ tileId: Number(tile.dataset.pathIndex) })
    });

});

function showActivatedItemIcons(playerId,diceControlActivated, imagePaths) {
    if (!Array.isArray(imagePaths) || imagePaths.length === 0) return;
    if(!diceControlActivated) return;
    const card = document.querySelector(`#player-Card [data-player-id="${playerId}"]`)?.closest(".player-card");

    if (!card) return;

    card.querySelector(".activated-items")?.remove();

    const list = document.createElement("ul");
    list.className = "activated-items";

    for (const imagePath of imagePaths) {
        const li = document.createElement("li");
        const img = document.createElement("img");

        img.src = imagePath;
        img.alt = "발동한 아이템";

        li.appendChild(img);
        list.appendChild(li);
    }

    card.classList.add("has-activated-items");
    card.appendChild(list);

    list.addEventListener("animationend", () => {
        list.remove();
        card.classList.remove("has-activated-items");
    }, { once: true });
}

function animateDice(left, right) {
    dice1Div.classList.add("is-rolling")
    dice2Div.classList.add("is-rolling")
    const randomFace = () => Math.floor(Math.random() * 6) + 1;
    let i = 0;
    let j = 6;
    const intervalId = setInterval(() => {
        renderDice(randomFace(), randomFace());
    }, 80);

    setTimeout(() => {
        clearInterval(intervalId);

        dice1Div.classList.remove("is-rolling");
        dice2Div.classList.remove("is-rolling");

        renderDice(left, right);
    }, 800);
}

function renderDice(left, right) {

    dice1Div.dataset.face = left;
    dice2Div.dataset.face = right;

}

function animateDeedTransfer(data) {
    const group = document.querySelector(
        `#building-layer .building-group[data-path-index="${data.tileId}"]`
    );

    if (group && data.newOwnerId != null) {
        group.dataset.ownerPlayerId = String(data.newOwnerId);
        applyBuildingOwnerColor(group);

        // 같은 칸에 효과가 남아 있으면 새 효과로 교체
        group.querySelector(".deed-transfer-fx")?.remove();

        const effect = document.createElement("div");
        effect.className = "deed-transfer-fx";

        const badge = document.createElement("span");
        badge.className = "deed-transfer-badge";
        badge.textContent = "건물 인수!";

        effect.appendChild(badge);
        group.appendChild(effect);

        setTimeout(() => effect.remove(), 1100);
    }

    payment(data.previousOwnerId, data.previousOwnerCash);
    payment(data.newOwnerId, data.newOwnerCash);

}

function updatePlayersAndTilesStatus(data) {
    console.log(data.tollPlayerStatus);
    const tileOwner = document.querySelector(`.player-cash[data-player-id="${data.tileOwnerId}"]`);
    const tollPlayer = document.querySelector(`.player-cash[data-player-id="${data.tollPayerId}"]`);

    if(data.tollPlayerStatus === "BANKRUPT") {
        const buildingGroups = document.querySelectorAll(`.building-group[data-owner-player-id="${data.tollPayerId}"]`)

        buildingGroups.forEach(building => {
            building.remove();
        })
        tollPlayer.textContent = "파산";
        tileOwner.textContent = `${Number(data.tileOwnerCash ).toLocaleString("ko-KR")}원`;
        return;
    }

    tileOwner.textContent = `${Number(data.tileOwnerCash ).toLocaleString("ko-KR")}원`;
    tollPlayer.textContent = `${Number(data.tollPayerCash).toLocaleString("ko-KR")}원`;
}

function openAcquisitionForm(data) {
    const playerPiece = document.querySelector(`.player-piece[data-game-player-id="${data.tollPayerId}"]`);
    if (!playerPiece.classList.contains("is-mine")) return;
    if (data.tollPlayerStatus === "BANKRUPT") return;

    window.open(`/games/${gameId}/tiles/${data.tileId}/acquisition`, "건물인수", "width=600,height=500");
    pieceLayer.classList.remove("is-my-turn");
    playerPiece.classList.remove("is-mine");
}

function payment(playerId, remainingCash) {
    const cashElement = document.querySelector(`.player-cash[data-player-id="${playerId}"]`);

    if (!cashElement) return;

    cashElement.textContent = `${Number(remainingCash).toLocaleString("ko-KR")}원`;
}

function getTileInnerEdge(tile, pathIndex) {
    // 0이면 경계선에 바로 붙고, 값을 올리면 타일 쪽으로 들어온다.
    const inset = 6;

    // 회전하기 전 타일 기준. 보드는 한 변에 8칸.
    const innerEdges = [
        [[0, inset], [100, inset]],               // 아래쪽 행: 위쪽 변
        [[100 - inset, 0], [100 - inset, 100]],   // 왼쪽 열: 오른쪽 변
        [[0, 100 - inset], [100, 100 - inset]],   // 위쪽 행: 아래쪽 변
        [[inset, 0], [inset, 100]]                // 오른쪽 열: 왼쪽 변
    ];

    const edge = innerEdges[Math.floor(pathIndex / 8)];
    if (!edge) return null;

    // 타일에 기준점 두 개를 잠깐 붙여 회전한 뒤의 실제 화면 좌표를 구한다.
    const anchors = edge.map(([left, top]) => {
        const anchor = document.createElement("span");
        anchor.style.cssText = `
            position: absolute;
            width: 0;
            height: 0;
            visibility: hidden;
            pointer-events: none;
        `;
        anchor.style.left = `${left}%`;
        anchor.style.top = `${top}%`;
        return anchor;
    });

    tile.append(...anchors);

    const points = anchors
        .map(anchor => {
            const { left, top } = anchor.getBoundingClientRect();
            return { x: left, y: top };
        })
        .sort((a, b) => a.x - b.x);

    anchors.forEach(anchor => anchor.remove());

    return points;
}


function renderBuildingOnTile(data) {
    const layer = document.getElementById("building-layer");

    // 서버의 tileId가 보드의 pathIndex를 뜻하는 현재 구조 기준.
    const tile = document.querySelector(
        `.board-tile[data-path-index="${data.tileId}"]`
    );

    if (!layer || !tile) return;

    const pathIndex = tile.dataset.pathIndex;
    const types = [];

    if (data.has_landmark) {
        types.push("landmark");
    } else {
        if (data.has_house) types.push("house");
        if (data.has_villa) types.push("villa");
        if (data.has_apartment) types.push("apartment");

        if (types.length === 0 && data.has_flag) {
            types.push("flag");
        }
    }

    let group = layer.querySelector(
        `.building-group[data-path-index="${pathIndex}"]`
    );

    if (types.length === 0) {
        group?.remove();
        return;
    }

    const tileRect = tile.getBoundingClientRect();
    const layerRect = layer.getBoundingClientRect();

    if (tileRect.width === 0 || tileRect.height === 0) return;

    const edge = getTileInnerEdge(tile, Number(pathIndex));
    if (!edge) return;

    const [start, end] = edge;

    if (!group) {
        group = document.createElement("div");
        group.className = "building-group";
        group.dataset.pathIndex = pathIndex;
        layer.appendChild(group);
    }

    // 건설 완료 응답의 playerId는 구매한 플레이어의 GamePlayer ID.
    group.dataset.ownerPlayerId = String(data.playerId ?? "");
    applyBuildingOwnerColor(group);

    group.style.left = `${tileRect.left - layerRect.left}px`;
    group.style.top = `${tileRect.top - layerRect.top}px`;
    group.style.width = `${tileRect.width}px`;
    group.style.height = `${tileRect.height}px`;
    group.style.setProperty(
        "--building-unit",
        `${Math.min(tileRect.width / 124, 1.45)}px`
    );

    const buildings = types.map((type, index) => {
        const building = document.createElement("span");
        building.className = `building ${type}`;

        // 한 채는 가운데, 세 채는 선의 10%·50%·90%에 배치한다.
        const slot = index - (types.length - 1) / 2;
        const t = 0.5 + slot * 0.4;

        const x = start.x + (end.x - start.x) * t;
        const y = start.y + (end.y - start.y) * t;

        building.style.left = `${x - tileRect.left}px`;
        building.style.top = `${y - tileRect.top}px`;
        building.style.zIndex = String(Math.round(y - tileRect.top));

        // 깃발은 천에 직접 색을 입히므로 받침을 만들지 않는다.
        if (type !== "flag") {
            const base = document.createElement("span");
            base.className = "building-owner-base";
            building.appendChild(base);
        }

        return building;
    });

    const ownerLine = document.createElement("span");
    ownerLine.className = "building-owner-line";

    const dx = end.x - start.x;
    const dy = end.y - start.y;

// 경계 양끝에 5%씩 여백을 둔다.
    ownerLine.style.left = `${start.x - tileRect.left + dx * 0.05}px`;
    ownerLine.style.top = `${start.y - tileRect.top + dy * 0.05}px`;
    ownerLine.style.width = `${Math.hypot(dx, dy) * 0.9}px`;
    ownerLine.style.transform =
        `translateY(-50%) rotate(${Math.atan2(dy, dx)}rad)`;

    group.replaceChildren(ownerLine, ...buildings);
}

function addIsMyTurnClassName(data, allowDice = true) {
    console.log("addIsMyTurnClassName() Call :" + data.currentTurnUserId);

    const datasetUserId = Number(document.body.dataset.userId);
    if (data.currentTurnUserId === datasetUserId) {
        rollDiceBtn.hidden = !allowDice
        pieceLayer.className = "is-my-turn";

        console.log(data.nextPlayerId)
        let playerPiece = document.querySelector(`.player-piece[data-game-player-id="${data.nextPlayerId}"]`)
        playerPiece.classList.add("is-mine");
        diceAim.hidden = false;
    } else {
        const isMyturn = document.getElementById("piece-layer");
        isMyturn.classList.remove("is-my-turn");
        let playerPieceAll = document.querySelectorAll(`.player-piece`)
        playerPieceAll.forEach(playerPiece =>  {
            playerPiece.classList.remove("is-mine");
        })
        rollDiceBtn.hidden = true;
        diceAim.hidden = true;
    }
    return;
}
function getPieceTilePoint(tileIndex, layer) {
    const tile = document.querySelector(
        `.board-tile[data-path-index="${tileIndex}"]`
    );
    if (!tile) return null;

    const tileRect = tile.getBoundingClientRect();
    const layerRect = layer.getBoundingClientRect();

    return {
        x: tileRect.left + tileRect.width / 2 - layerRect.left,
        y: tileRect.top + tileRect.height / 2 - layerRect.top
    };
}

function placePiece(piece, point) {
    piece.style.left = `${point.x}px`;
    piece.style.top = `${point.y}px`;
}

async function animatePieceByTiles(piece, data, layer) {
    const tileCount = 32;
    const start = Number(piece.dataset.pathIndex);
    const end = Number(data.destinationTileIndex);
    const destination = getPieceTilePoint(end, layer);

    if (!destination) return false;

    const distance = (end - start + tileCount) % tileCount;
    const rollSteps = Number(data.moveSteps ?? (Number(data.dice1) + Number(data.dice2)));

    const shouldJump =
        Number.isInteger(start) &&
        start >= 0 && start < tileCount &&
        distance > 0 &&
        distance === rollSteps &&
        !window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    if (shouldJump) {
        const points = Array.from({ length: distance + 1 }, (_, i) =>
            getPieceTilePoint((start + i) % tileCount, layer)
        );

        if (points.every(point => point !== null)) {
            piece.classList.add("is-stepping");

            try {
                for (let i = 1; i < points.length; i++) {
                    const from = points[i - 1];
                    const to = points[i];

                    const frames = [0, 0.25, 0.5, 0.75, 1].map(t => ({
                        offset: t,
                        left: `${from.x + (to.x - from.x) * t}px`,
                        top: `${from.y + (to.y - from.y) * t - 20 * Math.sin(Math.PI * t)}px`
                    }));

                    const jump = piece.animate(frames, {
                        duration: 145,
                        easing: "linear",
                        fill: "forwards"
                    });

                    await jump.finished;
                    placePiece(piece, to);
                    jump.cancel();
                }
            } finally {
                piece.classList.remove("is-stepping");
            }
        }
    }

    placePiece(piece, destination);
    piece.dataset.pathIndex = String(end);
    return true;
}

async function movePlayerPiece(data) {
    const playerPiece = pieceLayer?.querySelector(
        `.player-piece[data-game-player-id="${data.playerId}"]`
    );
    const tileId = playerPiece.dataset.pathIndex;
    const playerCard = document.querySelector(".player-cash");
    if (!playerPiece) return;

    if((Number(tileId) + (Number(data.dice1) + Number(data.dice2))) > 31) {
        const playerCard = document.querySelector(`.player-cash[data-player-id="${data.playerId}"]`);
        playerCard.textContent = (Number(playerCard.textContent) + 1000000) +"";
    }
    const isMyMove = playerPiece.classList.contains("is-mine");
    const moved = await animatePieceByTiles(playerPiece, data, pieceLayer);

    if (!moved || !isMyMove) return;


    if (data.landingAction === "UPGRADE") {
        showUpgradeConstructionOptions(data.destinationTileIndex, data.playerId);
        return;
    }

    if (data.existsTile === true) {
        stompClient.publish({
            destination: `/app/games/${gameId}/player/me/toll-payment`,
            body: JSON.stringify({
                playerId: data.playerId,
                tileIndex: data.destinationTileIndex
            })
        });
    } else if (data.existsTile === false) {
        showConstructionOptions(data.destinationTileIndex, data.playerId);
    }
}

function showConstructionOptions(tileId, playerId) {
    const tile = document.querySelector(`.board-tile[data-path-index="${tileId}"]`);

    if(tile.dataset.tileType === "CITY" || tile.dataset.tileType === "TOURIST_SPOT") {
        const popup = window.open(`/games/${gameId}/${playerId}/tiles/${tileId}/construction`, "건물 구매창", "width=600,height=500");

        if (!popup) return; // 팝업이 차단된 경우는 '닫기'가 아님

        const state = { popup, tileId, playerId, purchaseRequested: false };
        constructionPopup = state;

        const timerId = setInterval(() => {
            if (!popup.closed) return;

            clearInterval(timerId);

            if (constructionPopup !== state) return;
            constructionPopup = null;

            if (state.purchaseRequested) return;

            stompClient.publish({
                destination: `/app/games/${gameId}/players/${playerId}/building-skip`,

            });
        }, 300);
    }
}

window.addEventListener("message", (event) => {
    event.preventDefault()
    if(event.origin !== window.location.origin) return;

    if(event.data.type === "BUILDING_PURCHASE_REQUEST") {

        console.log(event.data.playerId)
        console.log(event.data.tileId)
        console.log(event.data.buildingLevels)
        const data = {
            tileId: event.data.tileId,
            playerId: event.data.playerId,
            buildingLevels: event.data.buildingLevels
        };

        const state = constructionPopup;


        if (!state || event.source !== state.popup || state.purchaseRequested) return;

        state.purchaseRequested = true;

        stompClient.publish({
            destination: `/app/games/${gameId}/players/me/building-purchase`,
            body: JSON.stringify(data)
        });
    }else if (event.data.type === "SKILL_BUILD_PURCHASE_REQUEST") {
        const upgrade = upgradeConstructionPopup;
        const isUpgrade = !!upgrade && event.source === upgrade.popup;
        const state = isUpgrade ? upgrade : skillConstructionPopup;

        if (!state || event.source !== state.popup || state.purchaseRequested) return;
        if (Number(event.data.tileId) !== Number(state.tileId)) return;

        const action = isUpgrade ? "building-upgrade" : "skill-building-purchase";

        state.purchaseRequested = true;

        stompClient.publish({
            destination: `/app/games/${gameId}/players/me/${action}`,
            body: JSON.stringify({
                tileId: state.tileId,
                playerId: isUpgrade ? state.playerId : event.data.playerId,
                buildingLevels: event.data.buildingLevels
            })
        });
    }else {
        const data = {
            tileId: event.data.tileId,
            playerId: event.data.playerId
        }
        stompClient.publish({
            destination: `/app/games/${gameId}/player/me/building-acquire`,
            body: JSON.stringify(data)
        });
    }
    return;
})

turnOrderButtons.addEventListener("click", (event) => {
    const button = event.target.closest(".turnOrderButton");

    if (!button) return;

    stompClient.publish({
        destination: `/app/game/${gameId}/players/turnOrder`,
        body: JSON.stringify({
            selectTurn: Number(button.value)
        })
    });
    buttonsDisable()

});

function showTurnOrderResult(firstCardIdx) {
    const cards = document.querySelectorAll(".turnOrderButton");
    const firstCard = document.querySelector(`.turnOrderButton[value="${firstCardIdx}"]`);

    cards.forEach(card => {
        card.disabled = true;

        // if (Number(card.value) !== firstCardIdx) {
        //     card.style.display = "none";
        // }
    })

    firstCard.classList.add("first-turn");

    setTimeout(() => {
        const turnOrderButtons = document.getElementById("turnOrderButtons");
        firstCard.style.display = "none";
        turnOrderButtons.style.display = "none";
    },1500);

    return;
}

function buttonsDisable() {
    const buttons = document.querySelectorAll(".turnOrderButton");
    buttons.forEach(button => {
        button.disabled = true;

    })
}

function buttonDisabled(response) {

    const button = document.querySelector(`.turnOrderButton[value="${response.selectTurnOrder}"]`)
    const loginUser = Number(document.body.dataset.userId);

    if(loginUser === Number(response.playerId)) {
        button.classList.add("is-selected");
    } else  {
        button.classList.add("is-other-selection");
    }
    button.disabled = true;
}

let targetSum = 2;
let direction = 1;
let aimTimer;
let activePointerId = null;

function renderDiceAim() {
    diceAimValue.textContent = targetSum;
    diceAimBar.value = targetSum - 2;
}

diceAimButton.addEventListener("pointerdown", event => {
    if (event.button !== 0 || activePointerId !== null) return;

    activePointerId = event.pointerId;
    console.log(event.pointerId)
    diceAimButton.setPointerCapture(event.pointerId);

    targetSum = 2;
    direction = 1;
    diceAim.hidden = false;
    renderDiceAim();

    aimTimer = setInterval(() => {
        if (targetSum === 12) direction = -1;
        else if (targetSum === 2) direction = 1;

        targetSum += direction;
        renderDiceAim();
    }, 150);
});

function stopDiceAim(event, shouldRoll) {
    if (event.pointerId !== activePointerId) return;

    clearInterval(aimTimer);
    activePointerId = null;

    if (!shouldRoll) {
        diceAim.hidden = true;
        return;
    }

    console.log("목표 합계:", targetSum);

    // 확인이 끝나면 여기서 targetSum을 웹소켓으로 보내면 돼.
    stompClient.publish(
        {
            destination: `/app/games/${gameId}/dice-roll`,
            body: JSON.stringify(Number(targetSum))
        })

    diceAimButton.hidden = true;
    diceAim.hidden = true;

}

diceAimButton.addEventListener("pointerup", event => stopDiceAim(event, true));
diceAimButton.addEventListener("pointercancel", event => stopDiceAim(event, false));

stompClient.activate();
