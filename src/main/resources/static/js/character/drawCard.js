const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
const premium10 = document.getElementById("premiumPack10");
const premium30 = document.getElementById("premiumPack30");
const renderCardsDiv = document.getElementById("renderCards");
const homeBtn = document.getElementById("homeBtn");

premium30.addEventListener("click",  async () => {

    const cards = await requestDrawCards("premium30")

    renderCards(cards);
    window.opener.location.reload();
})

premium10.addEventListener("click",  async () => {

    const cards = await requestDrawCards("premium10")

    renderCards(cards);
    window.opener.location.reload();
})

homeBtn.addEventListener("click", () => {
    window.close();
    window.opener.location.href = "/lobby";
})

async function requestDrawCards(premium) {
    const response = await fetch(`/api/users/me/draw-cards`, {
        method: "POST",
        headers: {
            "Content-Type": "text/plain",
            [csrfHeader]: csrfToken
        },
        body: premium
    });

    if(!response.ok) {
        alert("카드를 뽑는데 실패했습니다.")
        return;
    }

    return  response.json();
}

function renderCards(cards) {
    renderCardsDiv.replaceChildren();

        cards.forEach(card => {
            const image = document.createElement("img");

            image.className = "viewCard";
            image.dataset.rank = card.rank;
            image.src = card.imagePath;
            image.alt = card.characterName;

            renderCardsDiv.appendChild(image);
        })
    const button = document.createElement("button");
    button.type = "button";
    button.textContent = "닫기";

    button.addEventListener("click", () => {
        renderCardsDiv.replaceChildren();
    });

    renderCardsDiv.appendChild(button);
}
