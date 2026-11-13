const chatbox = document.getElementById("chat-box");
const msgInput = document.getElementById("msg-input");
const sendBtn = document.getElementById("send-btn");

const protocol = location.protocol === "https:" ? "wss" : "ws";

const stompClient =  new StompJs.Client({
    brokerURL: `${protocol}://${location.host}/ws`,
    reconnectDelay : 5000,

    onConnect: () => {
        console.log("STOMP 연결 성공");

        stompClient.subscribe(
            `/topic/rooms/${roomId}`,
            message => {
                const players = JSON.parse(message.body);
                waitingRoomRenderPlayers(players);
            }
        );

        stompClient.subscribe(`/user/queue/room/${roomId}/actions`, message => {
            const event = JSON.parse(message.body);

            if(event.isHost) {
                gameActionBtn.textContent = "시작";
            }
        })

        stompClient.subscribe(`/topic/rooms/${roomId}.chatMessage`, message => {
            const msg = JSON.parse(message.body);
            MessageBox(msg);
        })

        stompClient.subscribe(`/topic/rooms/${roomId}/action`, message => {
            const event = JSON.parse(message.body);

            if(event.actionType === "READY_CHANGED") {
                updateReadyState(event);
                return;
            }
        })

        stompClient.subscribe(`/topic/rooms/${roomId}/gameStart`, message => {
            const event = JSON.parse(message.body);

            if(event.actionType === "GAME_STARTED") {
                window.location.href = `/games/${event.gameId}`;
            }
        })
    } ,

    onWebSocketError: error => {
        console.error("WebSocket 오류", error);
    },

    onStompError: frame => {
        console.error("STOMP 오류", frame);
    }
});

function updateReadyState(event) {
    const playerCard = document.querySelector(`.player-card[data-user-id="${event.userId}"]`);

    if(!playerCard) {
        return;
    }

    playerCard.classList.toggle("ready", event.ready);
    const playerState = playerCard.querySelector(".player-state")

    if(playerState) {
        playerState.textContent = event.ready ? "준비 완료" : "";
    }


}

function MessageBox(msg) {
    const nameSpan = document.createElement("span");
    const contentSpan = document.createElement("span");

    nameSpan.textContent = msg.senderName + ": ";
    contentSpan.textContent = msg.content;
    nameSpan.appendChild(contentSpan);
    chatbox.appendChild(nameSpan);
    // chatbox.appendChild(contentSpan);
    chatbox.scrollTop = chatbox.scrollHeight
}

gameActionBtn.addEventListener("click", () => {

    stompClient.publish({
        destination: `/app/rooms/${roomId}/action`,
        body: JSON.stringify({})
    });


});

sendBtn.addEventListener("click",() => {
    sendRoomChatMessage();
})

msgInput.addEventListener("keydown", (e) => {

    if(e.key === "Enter" && !e.isComposing) {
        e.preventDefault();
        sendRoomChatMessage();
        msgInput.value = "";
    }
})

function sendRoomChatMessage() {
    stompClient.publish({
        destination: `/app/rooms/${roomId}`,
        body: JSON.stringify({
            content: msgInput.value.trim()
        })
    });
}
stompClient.activate();
