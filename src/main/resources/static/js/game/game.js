const ul = document.getElementById("tiles");
const gameId = Number(document.body.dataset.gameId);
const playerCard = document.getElementById("player-Card");
const turnOrderDiv = document.getElementById("turnOrderButtons");
const lobbyBtn = document.getElementById("lobbyBtn");

const propertyPlayerColors = ["#60b7ee", "#f29583", "#b69aef", "#7fd39c"];
const propertyColorByPlayerId = new Map();
const defaultPropertyColor = "#b6c2cc";


document.addEventListener("DOMContentLoaded",() => {
    getMap();
    turnOrderButton();
})

async function turnOrderButton() {
    try {
        const response = await fetch(`/api/games/${gameId}/players/count`);

        if(!response.ok) {
            alert("플레이어 인원수를 가져오는데 실패 했습니다.");
            return;
        }

        const playerCount = await response.json();

        for (let i = 0; i < playerCount; i++) {
            const button = document.createElement("button");
            const check = document.createElement("span");
            check.className = "selection-check";
            check.textContent = "√"
            button.className = "turnOrderButton";
            button.type = "button";
            button.value = "" + i;

            button.appendChild(check);
            turnOrderDiv.appendChild(button);
        }
    } catch (error) {
        alert("errorMessage: " + error.message);
    }
}

async function getMap() {

    try {
        const response = await fetch("/api/maps");

        if(!response.ok) {
            alert("맵 정보를 가져오는데 실패했습니다.");
            return;
        }

        const tiles = await response.json();
        renderTiles(tiles);

    } catch (error){
        alert("error: " + error.message);
        return;
    }
}

async function getPlayers() {
    try {
        const response = await fetch(`/api/games/${gameId}/players`);

        if(!response.ok) {
            alert("플레이어 정보를 가져오는도중 에러가 발생했습니다.");
            return;
        }

        const players = await response.json();

        renderPlayers(players);
        renderPlayerPieceOnTile(players, 0)
    } catch (message) {
        alert(message.message);
        return;
    }

}

function renderPlayerPieceOnTile(players, tileId) {
    players.forEach(player => {
        const tile = document.querySelector(`.board-tile[data-path-index="${tileId}"]`)
        const layer = document.getElementById("piece-layer");
        const div = document.createElement("div");

        div.className = "player-piece";
        div.dataset.gamePlayerId = String(player.playerId);
        div.dataset.pathIndex = String(tileId);

        div.dataset.gamePlayerId = player.playerId;
        div.dataset.data_turn_order = player.turnOrder;
        // layer.innerHTML = "";
        if (!tile || !layer) return;

        const tileRect = tile.getBoundingClientRect();
        const layerRect = layer.getBoundingClientRect();

        const x = tileRect.left + tileRect.width / 2 - layerRect.left;
        const y = tileRect.top + tileRect.height / 2 - layerRect.top;

        const img = document.createElement("img");
        img.alt = "플레이어 말";
        img.src = player.pieceImage
        img.className = "player-piece"
        div.style.left = `${x}px`;
        div.style.top = `${y}px`;
        div.appendChild(img);
        layer.appendChild(div);
    })
}

function setBuildingOwnerColors(players) {
    propertyColorByPlayerId.clear();

    players.forEach(player => {
        if (player.playerId == null || player.turnOrder == null) return;

        const color = propertyPlayerColors[player.turnOrder];
        if (color) {
            propertyColorByPlayerId.set(String(player.playerId), color);
        }
    });

    // 건물이 플레이어 목록보다 먼저 도착했어도 색을 갱신한다.
    document.querySelectorAll("#building-layer .building-group")
        .forEach(applyBuildingOwnerColor);
}

function applyBuildingOwnerColor(group) {
    const color = propertyColorByPlayerId.get(group.dataset.ownerPlayerId)
        ?? defaultPropertyColor;

    group.style.setProperty("--property-owner-color", color);
}


function renderPlayers(players) {
    setBuildingOwnerColors(players)
    players.forEach(player => {
        const li = document.createElement("li");
        const playerName = document.createElement("span");
        const playerCash = document.createElement("span");
        const cashLabel = document.createElement("label");
        const img = document.createElement("img");
        const rankLabel = document.createElement("label");

        cashLabel.className = "cashLabel"
        li.className = "player-card";
        li.dataset.turnOrder = player.turnOrder;

        playerName.textContent = player.userName;
        cashLabel.textContent = "보유머니";
        playerCash.textContent = player.cash;
        playerCash.dataset.playerId = player.playerId
        playerCash.className = "player-cash";
        img.alt = player.characterName;
        img.src = player.characterImage;
        img.className = "characterImage";
        img.style.height = "100px";

        rankLabel.textContent = player.turnOrder + 1

        // renderPlayerPieceOnTile(player, 0);

        cashLabel.append(playerCash, img,rankLabel);
        li.append(playerName, cashLabel);
        playerCard.appendChild(li);
    })
}

function renderTiles(tiles) {
    tiles.forEach(tile => {
        const li = document.createElement("li");

        let position = getTilePosition(tile.pathIndex);

        li.classList.add("board-tile");

        li.dataset.pathIndex = tile.pathIndex;
        li.dataset.tileType = tile.type;
        li.dataset.landPrice = tile.landPrice;
        if(tile.groupCode != null) {
            li.dataset.groupCode = tile.groupCode;
        }

        li.style.gridRow = position.row;
        li.style.gridColumn = position.column;
        li.dataset.edge = position.edge
        li.textContent = tile.name;

        ul.appendChild(li);
    })
}

function getTilePosition(pathIndex) {
    const side = Math.floor(pathIndex / 8);
    const offset = pathIndex % 8;
    const corner = offset === 0;

    if(side === 0) {
        return {
            row: 9,
            column: 9 - offset,
            edge: corner ? "corner" : "bottom"
        }
    } else if (side === 1) {
        return {
            row: 9 - offset,
            column: 1,
            edge: corner ? "corner" : "left"
        }
    } else if(side === 2) {
        return {
            row: 1,
            column: 1 + offset,
            edge: corner ? "corner" : "top"
        }
    } else if(side === 3) {
        return {
            row: 1 + offset,
            column: 9,
            edge: corner ? "corner" : "left"
        }
    }
}

lobbyBtn.addEventListener("click", () => {
    location.href = "/lobby";
})