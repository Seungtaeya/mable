const createRoomBtn = document.getElementById("createRoom_btn");
const roomId = document.getElementById("hidden_roomId");
const joinBtn = document.getElementById("joinBtn");
const roomTable = document.getElementById("room-table");
const roomName = document.getElementById("search-input");
const roomList = document.getElementById("room-list");
const ranking = document.getElementById("rankingBtn");
const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

createRoomBtn.addEventListener('click',() =>{
    lobby();
});
ranking.addEventListener("click", () => {
    window.location.href = "/ranking";
})

document.getElementById("all").addEventListener("click", async () => {
    const response = await fetch("/api/rooms");

    if(!response.ok) {
        alert("방들을 가져오는데 실패했습니다. 화면을 새로고침 해주세요.");
        return;
    }
    const rooms = await response.json();
    renderRooms(rooms);
})

document.getElementById("waiting").addEventListener("click", async () => {
    const response = await fetch("/api/rooms/waiting");

    if(!response.ok) {
        alert("방들을 가져오는데 실패했습니다. 화면을 새로고침 해주세요.");
        return;
    }
    const rooms = await response.json();
    renderRooms(rooms);
})

document.getElementById("playGame").addEventListener("click", async () => {
    const response = await fetch("/api/rooms/playGame");

    if(!response.ok) {
        alert("방들을 가져오는데 실패했습니다. 화면을 새로고침 해주세요.");
        return;
    }
    const rooms = await response.json();
    renderRooms(rooms);
})

document.getElementById("refresh").addEventListener("click",() => {

})
roomName.addEventListener("keydown", (key) => {
    if(key.key === "Enter" && !key.isComposing) {
        key.preventDefault();
        searchRoomName();
    }
})
function lobby() {
    window.open('/room','방만들기 팝업', 'width=700px, height=800px');
}

document.addEventListener("DOMContentLoaded", () => {
    loadRooms();
    useCharacterCard();
})

async function loadRooms() {
    try {
        const response = await fetch("/api/rooms", {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        })

        if(!response.ok) {
            alert("방 정보를 가져오는 중 문제가 발생했습니다.")
            return;
        }

        const rooms = await response.json();
        renderRooms(rooms)
    } catch (error) {
        alert(error.message)
    }

}

async function useCharacterCard() {
    const response = await fetch("/api/users/me/selected-character-card", {
        method: "GET"
    });

    const characterCard = await response.json();
    if(characterCard.charactersCount !== 0) {
        if (!response.ok) {
            alert("사용자가 선택한 캐릭터 카드를 가져오는데 실패 했습니다.");
            return;
        }
    }

    const useCharacter = document.getElementById("useCharacterCard");
    console.log(characterCard.imagePath)
    useCharacter.src = characterCard.imagePath;
    useCharacter.alt = characterCard.characterName;
}
async function joinGameRoom(roomId) {
    console.log(roomId)
    try{
        const response = await fetch(`/api/room/${roomId}`, {
            method: "POST",
            headers: {
                "Content-Type" : "application/json",
                [csrfHeader]: csrfToken
            }
        });

        if(!response.ok) {
            const error = await response.json().catch(() => null);
            alert(error.message);
            return;
        }

        location.href = `/rooms/${roomId}`;
    } catch (error) {
        alert(error.message)
    }
}

async function searchRoomName() {
    const response = await fetch(`/api/search/rooms/${roomName.value}`)

    if(!response.ok) {
        alert("검색 실패");
        return;
    }

    const rooms = await response.json();
    roomList.replaceChildren();
    renderRooms(rooms);
}

function renderRooms(rooms) {
    roomList.innerHTML = "";

    rooms.forEach((room) => {
        const tr = document.createElement("tr");
        const titleTd = document.createElement("td");
        const hostNameTd = document.createElement("td");
        const personnel = document.createElement("td");
        const roomStatus = document.createElement("td");
        const joinBtn = document.createElement("button");
        const roomId = document.createElement("input");

        titleTd.textContent = room.title;
        hostNameTd.textContent = room.hostName;
        personnel.textContent = `${room.currentPlayerCount} / ${room.maxPlayers}`;
        roomStatus.textContent = room.status;
        joinBtn.onclick = () => joinGameRoom(room.roomId);
        joinBtn.textContent = "입장";
        roomId.type = "hidden";
        roomId.value = room.roomId;

        tr.appendChild(titleTd);
        tr.appendChild(hostNameTd);
        tr.appendChild(personnel);
        tr.appendChild(roomStatus);
        tr.appendChild(joinBtn);
        tr.appendChild(roomId);

        roomList.appendChild(tr);
    })
    roomTable.scrollTop = 0;

}

function addRoom(room) {
    const tr = document.createElement("tr");
    const titleTd = document.createElement("td");
    const hostNameTd = document.createElement("td");
    const personnel = document.createElement("td");
    const roomStatus = document.createElement("td");
    const joinBtn = document.createElement("button");
    const roomId = document.createElement("input");

    titleTd.textContent = room.title;
    hostNameTd.textContent = room.hostName;
    personnel.textContent = `${room.currentPlayerCount} / ${room.maxPlayers}`;
    roomStatus.textContent = room.status;
    joinBtn.onclick = () => joinGameRoom(room.roomId);
    joinBtn.textContent = "입장";
    roomId.type = "hidden";
    roomId.value = room.roomId;

    tr.appendChild(titleTd);
    tr.appendChild(hostNameTd);
    tr.appendChild(personnel);
    tr.appendChild(roomStatus);
    tr.appendChild(joinBtn);
    tr.appendChild(roomId);

    roomTable.appendChild(tr);
}

