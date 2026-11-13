const luckyItemForm = document.getElementById("lucky-item-form");

const luckyItemNameInput = document.getElementById("luckyItemName");
const luckyItemImageInput = document.getElementById("luckyItemImage");
const luckyItemRankInput = document.getElementById("luckyItemRank");
const luckyItemSkillCodeInput = document.getElementById("luckyItemSkillCode");
const luckyItemTriggerTypeInput = document.getElementById("luckyItemTriggerType");
const luckyItemActivationRateInput = document.getElementById("luckyItemActivationRate");
const luckyItemSkillDescriptionInput = document.getElementById("luckyItemSkillDescription");

luckyItemButton.addEventListener("click", () => {
    characterForm.hidden = true;
    characterSkillForm.hidden = true;
    luckyItemForm.hidden = false;
});

luckyItemForm.addEventListener("submit", async(event) => {
    event.preventDefault()
    const imageFile = luckyItemImageInput.files[0];

    if(!imageFile) {
        alert("이미지 파일을 선택해주세요.")
        return;
    }

    const request = {
        skillCode: luckyItemSkillCodeInput.value,
        triggerType: luckyItemTriggerTypeInput.value,
        skillDescription: luckyItemSkillDescriptionInput.value,
        itemName: luckyItemNameInput.value,
        itemRank: luckyItemRankInput.value,
        activationRate: luckyItemActivationRateInput.value
    };

    const formData = new FormData();

    formData.append("data",new Blob(
        [JSON.stringify(request)],
        {type: "application/json"}
    ));

    formData.append(
        "image",
        imageFile
    );

    const response = await fetch("/api/admin/luckyItem", {
        method: "POST",
        headers:{
            [csrfHeader]: csrfToken
        },
        body: formData
    })

    if(!response.ok) {
        alert("아이템 저장에 실패했습니다.");
        return;
    }

    alert("아이템이 저장됐습니다.");

    luckyItemForm.reset();
})