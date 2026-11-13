const createRoomBtn = document.getElementById("createRoom_btn");
const roomId = document.getElementById("hidden_roomId");
const joinBtn = document.getElementById("joinBtn");
const roomTable = document.getElementById("room-table");

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

createRoomBtn.addEventListener('click',() =>{
    lobby();
});


function lobby() {
    window.open('/room','방만들기 팝업', 'width=700px, height=800px');
}

document.addEventListener("DOMContentLoaded", () => {
    loadRooms()
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
            alert("방에 입장하는데 실패 했습니다.");
            return;
        }

        location.href = `/rooms/${roomId}`;
    } catch (error) {
        alert(error.message)
    }
}

async function clearAllRoomsByAdmin() {

    try {
        const response = await fetch("/api/admin/rooms", {
            method: "DELETE",
            headers: {
                "Content-Type" : "application/json",
                [csrfHeader]: [csrfToken]
            }
        });

        if(!response.ok) {
            alert("데이터 삭제중 에러가 발생했습니다. 다시 시도해주세요.");
            return;
        }

    }catch (error) {
        alert(error.message);
    }

}

function renderRooms(rooms) {


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

        roomTable.appendChild(tr);
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

