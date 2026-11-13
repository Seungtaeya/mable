const characterForm = document.getElementById("character-form");
const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
const nameInput = document.getElementById("characterName");
const imageInput = document.getElementById("characterImage");
const tollDiscountInput = document.getElementById("tollDiscount");
const bonusGameWinInput = document.getElementById("bonusGameWin");
const specialCostDisCountInput = document.getElementById("specialCostDisCount");
const goldenFortuneAcquisitionInput = document.getElementById("goldenFortuneAcquisition");
const constructCostDisCountInput = document.getElementById("constructCostDisCount");
const diceControlInput = document.getElementById("diceControl");
const acquisitionCostDiscountInput = document.getElementById("acquisitionCostDiscount");
const rankInput = document.getElementById("rank");

const characterButton = document.getElementById("character");
const characterSkillButton = document.getElementById("character-skill-button");
const characterSkillForm = document.getElementById("character-skill-form");

const skillImageInput = document.getElementById("skillImage");
const skillCodeInput = document.getElementById("skillCode");
const skillNameInput = document.getElementById("skillName");
const skillDescriptionInput = document.getElementById("skillDescription");
const triggerTypeInput = document.getElementById("triggerType");
const skillCharacterIdInput = document.getElementById("skillCharacterId");
const activationRateInput = document.getElementById("activationRate");
const effectValueInput = document.getElementById("effectValue");
const displayOrderInput = document.getElementById("displayOrder");
const luckyItemButton = document.getElementById("lucky-item");

characterButton.addEventListener("click", () => {
    characterForm.hidden = false;
    characterSkillForm.hidden = true;
    luckyItemForm.hidden = true;
});

characterSkillButton.addEventListener("click", () => {
    characterForm.hidden = true;
    characterSkillForm.hidden = false;
    luckyItemForm.hidden = true;
});

luckyItemButton.addEventListener("click", () => {
    characterForm.hidden = true;
    characterSkillForm.hidden = true;
    luckyItemForm.hidden = false;
});



characterSkillForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const imageFile = skillImageInput.files[0];

        if (!imageFile) {
            alert("스킬 이미지를 선택해 주세요.");
            return;
        }

        const request = {
            skillCode: skillCodeInput.value,
            skillName: skillNameInput.value,
            skillDescription:
            skillDescriptionInput.value,
            triggerType:
            triggerTypeInput.value,
            characterId:
                Number(skillCharacterIdInput.value),
            activationRate:
                Number(activationRateInput.value),
            effectValue:
                Number(effectValueInput.value),
            displayOrder:
                Number(displayOrderInput.value)
        };

        const formData = new FormData();

        formData.append(
            "data",
            new Blob(
                [JSON.stringify(request)],
                {type: "application/json"}
            )
        );

        formData.append(
            "image",
            imageFile
        );

        try {
            const response = await fetch(
                "/api/admin/character-skill",
                {
                    method: "POST",
                    headers: {
                        [csrfHeader]: csrfToken
                    },
                    body: formData
                }
            );

            if (!response.ok) {
                const message = await response.text();

                console.error(
                    response.status,
                    message
                );

                alert(message || "캐릭터 스킬 저장에 실패했습니다.");
                return;
            }

            alert("캐릭터 스킬이 저장됐습니다.");

            characterSkillForm.reset();

        } catch (error) {
            console.error(error);
            alert("서버와 통신할 수 없습니다.");
        }
    }
);

characterForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const imageFile = imageInput.files[0];

    if (!imageFile) {
        alert("캐릭터 이미지를 선택해 주세요.");
        return;
    }

    const request = {
        characterName: nameInput.value,
        tollDiscount: Number(tollDiscountInput.value),
        bonusGameWin: Number(bonusGameWinInput.value),
        specialCostDisCount:
            Number(specialCostDisCountInput.value),
        goldenFortuneAcquisition:
            Number(goldenFortuneAcquisitionInput.value),
        constructCostDisCount:
            Number(constructCostDisCountInput.value),
        diceControl: Number(diceControlInput.value),
        acquisitionCostDiscount:
            Number(acquisitionCostDiscountInput.value),
        rank: rankInput.value
    };

    const formData = new FormData();

    formData.append(
        "data",
        new Blob(
            [JSON.stringify(request)],
            {type: "application/json"}
        )
    );

    formData.append("image", imageFile);

    try {
        const response = await fetch(
            "/api/admin/character",
            {
                method: "POST",
                headers: {
                    [csrfHeader]: csrfToken
                },
                body: formData
            }
        );

        if (!response.ok) {
            const message = await response.text();

            alert(
                message ||
                "캐릭터 저장 중 문제가 발생했습니다."
            );
            return;
        }
        alert("캐릭터가 성공적으로 저장됐습니다.");
        characterForm.reset();

    } catch (error) {
        console.error(error);
        alert("서버와 통신할 수 없습니다.");
    }
});