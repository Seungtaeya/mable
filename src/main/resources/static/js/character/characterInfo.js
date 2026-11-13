const character = document.getElementById("character");
const status = document.getElementById("status");
const characterLuckyItem = document.getElementById("character-luckyItem");
const skill = document.getElementById("skill");
const characterId = document.getElementById("characterId").value;
const idx = document.getElementById("idx").value;
const useBtn = document.getElementById("useBtn");

const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
const csrfToken = document.querySelector('meta[name="_csrf"]').content;
document.addEventListener("DOMContentLoaded", () => {
    characterInfo();
})

useBtn.addEventListener("click",async () => {
    console.log("idx: " + idx);
    console.log("characterId: " + characterId);
    const data = {
        idx: Number(idx),
        characterId: characterId
    };
    const response = await fetch("/api/users/me/selected-character", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(data)
    });

    if(response.status !== 201) {
        alert("사용요청이 실패했습니다.");
        // window.close();
        return;
    }

    if(window.opener && !window.opener.closed) {
        window.opener.location.reload();
    }

    window.close();
})

async function characterInfo() {
    console.log(characterId)
    const response = await fetch(`/api/character-info/${characterId}`)
    if(!response.ok){
        alert("선택하신 캐릭터 정보를 가져오는데 실패했습니다.");
        return;
    }

    const data = await response.json();

    const image = document.createElement("img");

    image.src = data.imagePath;
    image.alt = data.characterName;
    image.className = "character-image";

    character.replaceChildren(image);



    status.replaceChildren(
        createStatusGauge("통행료 할인", data.tollDiscount),
        createStatusGauge("보너스 게임 승리", data.bonusGameWin),
        createStatusGauge("특수비용 할인", data.specialCostDiscount),
        createStatusGauge("황금 포춘 획득", data.goldenFortuneAcquisition),
        createStatusGauge("건설비용 할인", data.constructionCostDiscount),
        createStatusGauge("주사위 컨트롤", data.diceControl),
        createStatusGauge("인수비용 할인", data.acquisitionCostDiscount)
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