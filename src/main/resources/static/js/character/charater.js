const csrfToken = document.querySelector('meta[name="_csrf"]').content
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content
const user_cards = document.getElementById("user-cards");
const characterCard = document.getElementById("character-card");
const characterInfo = document.getElementById("character-info");
const count = document.getElementById("count");
const a = document.getElementById("owned-cards");
const drawCard = document.getElementById("drawCards");
const characterSynthesis = document.getElementById("character-synthesis");

const characterSkillDiv = document.getElementById("character-skill");

characterSynthesis.addEventListener("click", () => {
    loadCharacterSynthesisView();
})
document.addEventListener("DOMContentLoaded", () => {
    getCards();
    selectedCharacterCard();
    selectedLuckyItems();
})

drawCard.addEventListener("click", () => {
    window.open("/draw-cards", "캐릭터 뽑기", "width=1300px, height=900px");

})

async function loadCharacterSynthesisView() {
    document.body.innerHTML = "";
    const nav = document.createElement("nav");
    const characterCard = document.createElement("h2");
    const synthesis = document.createElement("h2");
    const characterSynthesisPanel = document.createElement("div");
    const characterPanel1 = document.createElement("div");
    const characterPanel2 = document.createElement("div");
    const characterPanel3 = document.createElement("div");
    const synthesisBtn = document.createElement("button");
    const image1 = document.createElement("img");
    const image2 = document.createElement("img");
    const image3 = document.createElement("img");
    const plusSymbol = document.createElement("span");
    const equalsSymbol = document.createElement("span");
    const charactersPanel = document.createElement("div");

    characterCard.textContent = "캐릭터 카드";
    characterCard.id = "characterCard";
    synthesis.textContent = "합성";
    synthesis.className = "active";

    nav.append(characterCard, synthesis);

    characterSynthesisPanel.id = "character-synthesis-panel";

    characterPanel1.id = "character-panel1";
    characterPanel1.className = "character-synthesis-slot";
    image1.id = "left-character-img";
    image1.className = "left-character-img";
    image1.alt = "첫 번째 합성 캐릭터"
    characterPanel1.appendChild(image1);

    plusSymbol.className = "synthesis-symbol"
    plusSymbol.textContent = "+";

    characterPanel2.id = "character-panel2";
    characterPanel2.className = "character-synthesis-slot";
    image2.id = "right-character-img";
    image2.className = "right-character-img";
    image2.alt = "두 번째 합성 캐릭터"
    characterPanel2.appendChild(image2);

    equalsSymbol.className = "synthesis-symbol"
    equalsSymbol.textContent = "=";

    characterPanel3.id = "character-panel3";
    characterPanel3.className = "character-synthesis-slot result-slot";

    image3.id = "synthesis-result-img";
    image3.className = "synthesis-result-img";
    image3.alt = "캐릭터 합성 결과";
    characterPanel3.appendChild(image3);

    charactersPanel.id = "synthesis-character-list";

    synthesisBtn.id = "synthesis-btn";
    synthesisBtn.type = "button";
    synthesisBtn.textContent = "캐릭터 합성";
    synthesisBtn.disabled = true;

    // 합성에 넣을 캐릭터 list
    const characters = await fetchCharacterCards();

    charactersPanel.replaceChildren();

    characters.forEach(character => {
        const cardButton = document.createElement("button");
        const img = document.createElement("img");

        cardButton.type = "button";
        cardButton.className = "synthesis-character-card";
        cardButton.dataset.userCharacterId = character.id;

        if(character.status === "SELECTED") {
            cardButton.classList.add("SELECTED");
        }
        img.className = "cardImage";
        img.src = character.imagePath;

        cardButton.appendChild(img);
        charactersPanel.appendChild(cardButton);
    });

    characterSynthesisPanel.append(characterPanel1, plusSymbol, characterPanel2, equalsSymbol, characterPanel3, synthesisBtn);
    document.body.append(nav, characterSynthesisPanel, charactersPanel);

    charactersPanel.scrollTop = charactersPanel.scrollHeight;

    let image1CharacterId;
    let image2CharacterId;

    charactersPanel.addEventListener("click", (event) => {
        image3.removeAttribute("src"); /* 합성 재료 클릭시 합성 결과 이미지 삭제*/

        const cardButton = event.target.closest(".synthesis-character-card");
        const leftImage = document.getElementById("left-character-img")
        if (!cardButton) {
            return;
        }

        if (!leftImage.hasAttribute("src")) {
            image1CharacterId = cardButton.dataset.userCharacterId;
            image1.src = cardButton.querySelector(".cardImage").getAttribute("src");
        } else if (!image2.hasAttribute("src")) {
            image2CharacterId = cardButton.dataset.userCharacterId;
            image2.src = cardButton.querySelector(".cardImage").getAttribute("src");
        }

        if (image1.src && image2.src) {
            synthesisBtn.disabled = false
        }

    })

    characterSynthesisPanel.addEventListener("click", (event) => {
        const leftImage = event.target.closest(".left-character-img");
        const rightImage = event.target.closest(".right-character-img");

        if (leftImage) {
            leftImage.removeAttribute("src");
            return;
        } else if (rightImage) {
            rightImage.removeAttribute("src");
            return;
        }
    })

    synthesisBtn.addEventListener("click", async () => {
        const data = {leftCharacterId: image1CharacterId, rightCharacterId: image2CharacterId}
        const response = await fetch(`/api/users/me/characters/synthesize`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            alert("합성 데이터를 서버에 전송 실패");
            return;
        }

        const result = await response.json();
        image3.src = result.imagePath;
        image1.removeAttribute("src");
        image2.removeAttribute("src");
        synthesisBtn.disabled = true;

        charactersPanel.innerHTML = "";
        const charArr = await fetchCharacterCards();
        charactersPanel.replaceChildren();

        charArr.forEach(char => {
            const cardButton = document.createElement("button");
            const img = document.createElement("img");

            cardButton.type = "button";
            cardButton.className = "synthesis-character-card";
            cardButton.dataset.userCharacterId = char.id;

            if(char.status === "SELECTED"){
                cardButton.classList.add("SELECTED");
            }
            img.className = "cardImage";
            img.src = char.imagePath;

            cardButton.appendChild(img);
            charactersPanel.appendChild(cardButton);
        });
    })

    characterCard.addEventListener("click", () => {
        window.location.href = "/characters";
    })
}

async function selectedLuckyItems() {
    const response = await fetch("/api/users/me/equipped-items",{
        method: "GET"
    })

    if(!response.ok) {
        alert("아이템 가져오는데 실패 했습니다.");
        return;
    }

    const items = await response.json();

    const itemsDiv = document.getElementById("lucky-items");

    items.forEach(item => {
        const button = document.createElement("button");
        const img = document.createElement("img");

        button.className = "selectedItem";
        button.dataset.slotIndex = item.slotIndex;

        img.className = "selectedItemImage";
        img.src = item.imagePath;
        img.alt = "아이템 이미지";
        img.dataset.userSelectedItemId = item.ownedItemId
        button.appendChild(img);
        itemsDiv.append(button);
    })
}

async function selectedCharacterCard() {
    const response = await fetch("/api/users/me/character");

    if(!response.ok) {
        // alert("데이터를 가져오는데 문제가 발생했습니다.");
        return;
    }

    const character = await response.json();


    const image = document.createElement("img");
    const skillImg = document.createElement("img");
    const skillItem = document.createElement("div");
    const skillSpan = document.createElement("span");

    skillItem.className = "character-skill-item";

    image.src = character.imagePath;
    image.alt = character.characterName;
    image.className = "character-image";

    if(Array.isArray(character.skillResponses) && character.skillResponses.length > 0) {
        skillImg.src = character.skillResponses[0].imagePath;
        skillImg.alt = "캐릭터 스킬";
        skillImg.className = "character-skill";

        const description = character.skillResponses.map(skill => skill.skillDescription).join(", ");

        skillSpan.textContent = description;
        skillItem.append(skillImg, skillSpan)

        characterSkillDiv.append(skillItem);
    }
    
    characterCard.replaceChildren(image);



    characterInfo.replaceChildren(
        createStatusGauge("통행료 할인", character.tollDiscount),
        createStatusGauge("보너스 게임 승리", character.bonusGameWin),
        createStatusGauge("특수비용 할인", character.specialCostDiscount),
        createStatusGauge("황금 포춘 획득", character.goldenFortuneAcquisition),
        createStatusGauge("건설비용 할인", character.constructionCostDiscount),
        createStatusGauge("주사위 컨트롤", character.diceControl),
        createStatusGauge("인수비용 할인", character.acquisitionCostDiscount)
    );

}

function createStatusGauge(name, rawValue) {
    const value = Math.max(
        0,
        Math.min(100, Number(rawValue) || 0)
    );

    const item = document.createElement("div");
    const nameSpan = document.createElement("span");
    const valueSpan = document.createElement("span");
    const gaugeTrack = document.createElement("div");
    const gaugeFill = document.createElement("div");

    item.className = "status-item";
    nameSpan.className = "status-name";
    valueSpan.className = "status-value";
    gaugeTrack.className = "status-gauge";
    gaugeFill.className = "status-gauge-fill";

    nameSpan.textContent = name;
    valueSpan.textContent = value;

    gaugeTrack.setAttribute("role", "progressbar");
    gaugeTrack.setAttribute("aria-valuemin", "0");
    gaugeTrack.setAttribute("aria-valuemax", "100");
    gaugeTrack.setAttribute("aria-valuenow", value);

    gaugeFill.style.width = `${value}%`;

    gaugeTrack.appendChild(gaugeFill);
    item.append(nameSpan, valueSpan, gaugeTrack);

    return item;
}

a.addEventListener("click", (event) => {
    const aTag = event.target.closest(".owned-card");
    const characterId = aTag.dataset.characterId;
    const idx = aTag.dataset.idx;

    window.open(`/character/${idx}/${characterId}`, "캐릭터 정보", "width=1200px, height=700px");

})

async function fetchCharacterCards() {
    const response = await fetch("/api/character-cards");

    if (!response.ok) {
        throw new Error("서버에서 데이터를 가져오는데 실패했습니다.");
    }

    return response.json();
}

async function getCards(){

    const cards = await fetchCharacterCards();
    count.textContent = cards.length + "/200";
    getCharacterCards(cards);
}

function getCharacterCards(cards) {

    let idx = 0;
    cards.forEach(card => {

        const aTag = document.createElement("a");
        const image = document.createElement("img");

        aTag.className = "owned-card";
        if(card.status === "SELECTED") {
            aTag.classList.add("SELECTED");
        }
        aTag.href = "#";
        aTag.dataset.characterId = card.id;
        aTag.dataset.idx = idx++;
        image.className = "cardImage";
        image.src = card.imagePath;
        image.alt = card.characterName;
        aTag.appendChild(image);
        user_cards.appendChild(aTag);
    })
    user_cards.scrollTop = 0;
}