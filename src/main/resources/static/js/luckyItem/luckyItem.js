const selectItemsDiv = document.getElementById("selectedItems");
const userItems = document.getElementById("user-items");
const count = document.getElementById("count");
const drawItemsBtn = document.getElementById("drawItems");
const sell = document.getElementById("sell");

const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content
const csrfToken = document.querySelector('meta[name="_csrf"]').content

document.addEventListener("DOMContentLoaded", async () => {
    const items = await getItems();
    renderOwnedItems(items);
});

let selectedItemId = 0;
window.addEventListener("message", (event) => {
    event.preventDefault()
    console.log("postMessage call ")
    if(event.origin !== window.location.origin) return;

    selectedItemId = event.data.itemId;
})


selectItemsDiv.addEventListener("click", async (event) => {
    if(selectedItemId !== 0) {
        const target = event.target.closest(".selectItem");

        const request = {indexId: Number(target.dataset.buttonId), selectedLuckyItemId:selectedItemId}
        const response = await fetch("/api/users/me/selected-item", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(request)
        })

        if (!response.ok) {
            alert("아이템 장착 실패");
            return;
        }

        const data = await response.json();

        equipItem(data)
        selectedItemId = 0;
        location.reload();
        return;
    }
    return;
})

function equipItem(data) {
    const buttons = document.querySelectorAll(".selectItem");

    buttons.forEach(button => {
        const img = document.createElement("img");
        if(button.dataset.buttonId === String(data.slotIndex)) {

            button.textContent = "";
            img.src = data.imagePath;
            img.dataset.userSelectedItemId = data.ownedItemId;
            img.alt = "아이템 이미지"

            button.replaceChildren(img);
        }
    })
}

drawItemsBtn.addEventListener("click", () => {
    window.location.href = "/draw/lucky-item"
});

userItems.addEventListener("click", event => {
    const target = event.target.closest(".owned-item");

    if (!target) return;

    if (isSellMode) {
        target.classList.toggle("is-selected");
        updateSellButton();
        return;
    }

    window.open(`/luckyItem-info/${target.dataset.ownedItemId}`,"아이템 정보", "width=700px, height=700px");

});

let isSellMode = false;

sell.addEventListener("click", async () => {
    if (!isSellMode) {
        isSellMode = true;

        userItems.classList.add("sell-mode");
        sell.classList.add("is-active");
        sell.textContent = "판매할 아이템을 선택하세요";

        return;
    }

    const selectedItems = [...document.querySelectorAll(".owned-item.is-selected")]

    if (selectedItems.length === 0) {
        exitSellMode();
        return;
    }

    const selectedItemIds = selectedItems.map(item => Number(item.dataset.ownedItemId));

    const success = await fetch("/api/users/me/lucky-items",{
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            [csrfHeader]:csrfToken
        },
        body: JSON.stringify({
            ownedItemIds: selectedItemIds
        })
    });

    if(!success.ok) {
        alert("아이템 판매에 실패 했습니다.");
        return;
    }

    alert("아이템이 판매되었습니다.");

    selectedItems.forEach(item => item.remove());

    count.textContent = userItems.querySelectorAll(".owned-item").length + " / 200";

    exitSellMode()
});

function updateSellButton() {
    const selectedCount = document.querySelectorAll(
        ".owned-item.is-selected"
    ).length;

    sell.textContent = selectedCount === 0
        ? "판매할 아이템을 선택하세요"
        : `${selectedCount}개 판매하기`;
}

function exitSellMode() {
    isSellMode = false;

    document.querySelectorAll(".owned-item.is-selected")
        .forEach(item => item.classList.remove("is-selected"));

    userItems.classList.remove("sell-mode");
    sell.classList.remove("is-active");
    sell.textContent = "판매";
}
async function getItems() {
    const response = await fetch("/api/users/me/lucky-items", {
        method: "GET"
    });

    if(!response.ok) {
        alert("아이템 가져오는데 실패 했습니다.")
        return;
    }

    return response.json();
}

function renderOwnedItems(items) {
    count.textContent = items.length + " / 200";

    items.forEach(item => {
        const aTag = document.createElement("a");
        const image = document.createElement("img");

        aTag.className = "owned-item";
        aTag.dataset.ownedItemId = item.ownedItemId;
        // aTag.dataset.status = item.status;

        if(item.status === "SELECTED") {
            aTag.classList.add("SELECTED");
        }

        image.className = "itemImage";
        image.src = item.imagePath;
        image.alt = item.itemName;
        aTag.appendChild(image);

        if(item.status === "SELECTED") {
            equipItem(item);
        }
        userItems.appendChild(aTag);
    })
    userItems.scrollTop = 0;
}