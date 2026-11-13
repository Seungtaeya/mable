const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

const playerList = document.getElementById("player-list");
const roomId = Number(document.body.dataset.roomId);
const gameActionBtn = document.getElementById("gameActionBtn");
const currentUserId = Number(document.body.dataset.userId);
document.addEventListener("DOMContentLoaded", async () => {
    renderGameActionButton()
    getPlayers(roomId)
})

async function renderGameActionButton() {
    try{
        const response = await fetch(`/api/rooms/${roomId}/hostId`, {
            method: "GET",
        })
        if(!response.ok) {
            alert("호스트 아이디를 가져오는중 문제가 발생했습니다.");
            return;
        }

        const isHost = await response.json()

        changeActionBtn(isHost);

    } catch (error) {
        alert("error message: " + error.message)
    }

}

function changeActionBtn(isHost) {
    gameActionBtn.textContent = isHost ? "시작" : "준비";
    gameActionBtn.dataset.host = String(isHost);

    gameActionBtn.classList.remove("cancel-ready");
}

async function getPlayers(roomId) {
      const response = await fetch(`/api/rooms/${roomId}/players`, {
          method: "GET",
          headers: {"Content-Type" : "application/json"}
      })

    if(!response.ok) {
        alert("플레이어 데이터를 가져오는데 실패 했습니다.");
        return;
    }
    const players = await response.json();
    console.log("waitingRoom.js_playerInfo",players);
    waitingRoomRenderPlayers(players);
}

function waitingRoomRenderPlayers(players){
    playerList.replaceChildren();

    players.forEach(player => {
        const li = document.createElement("li");

        li.className = "player-card";
        li.dataset.userId = player.userId;

        li.classList.toggle("host", player.isHost);
        li.classList.toggle("ready", player.ready);
        const playerName = document.createElement("strong");
        playerName.className = "player-name";
        playerName.textContent = player.userName;

        const playerState = document.createElement("span");
        playerState.className = "player-state";

        if (player.isHost) {
            playerState.textContent = "방장";
        } else {
            playerState.textContent = player.ready
                ? "준비 완료"
                : "";
        }

        li.append(playerName, playerState);
        playerList.appendChild(li);


    });

}


async function leave() {

    try {

        const response = await fetch(`/api/rooms/${roomId}/players/me`, {
            method: "DELETE",
            headers: {
                [csrfHeader]:csrfToken
            },
        })

        if(!response.ok) {
            alert("퇴장중에 문제가 발생 했습니다. 다시 시도해주세요.");
            return;
        }

        location.href = `/lobby`;

    } catch (error) {
        alert(error.message);
    }

}
