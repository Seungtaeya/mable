const chat_box = document.getElementById("chat_box");
const chat = document.getElementById("msg");
const send = document.getElementById("send");

const protocol = location.protocol === "https" ? "wss" : "ws";

const stompClient = new StompJs.Client({
    brokerURL: `${protocol}://${location.host}/ws`,
    reconnectDelay: 5000,

    onConnect:()=> {
        console.log("Stomp 연결 성공")

        stompClient.subscribe(`/topic/lobby/chat`, message => {
            const msg = JSON.parse(message.body);
            const contentSpan = document.createElement("div");
            contentSpan.className = "chat-message";
            contentSpan.textContent = `${msg.senderName} : ${msg.content}`;
            chat_box.appendChild(contentSpan);
            chat_box.scrollTop = chat_box.scrollHeight
        });

        stompClient.subscribe("/topic/rooms/create", data => {
            const room = JSON.parse(data.body);
            addRoom(room);
        })


    },

    onWebSocketError: error => {
        console.error("WebSocket 오류", error);
    },

    onStompError: frame => {
        console.error("STOMP 오류", frame);
    }

});

chat.addEventListener("keydown", key => {
    if(key.key === "Enter" && !key.isComposing) {
        key.preventDefault()
        stompClient.publish({
            destination: `/app/lobby/chat`,
            body: JSON.stringify({
                content: chat.value.trim()
        })
    })
        chat.value = "";
    }
})

stompClient.activate();