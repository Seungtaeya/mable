package demo.mable.luckyItem.dto;

import demo.mable.luckyItem.enums.UserLuckyItemStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserOwnedLuckyItemsResponse {

    private Long ownedItemId;
    private String imagePath;
    private String itemName;
    private UserLuckyItemStatus status;
    private Long slotIndex;

    public UserOwnedLuckyItemsResponse(Long ownedItemId, String imagePath, String itemName, UserLuckyItemStatus status, Long slotIndex) {
        this.ownedItemId = ownedItemId;
        this.imagePath = imagePath;
        this.itemName = itemName;
        this.status = status;
        this.slotIndex = slotIndex;
    }

    public static UserOwnedLuckyItemsResponse from(Long ownedItemId, String imagePath, String itemName,UserLuckyItemStatus status, Long slotIndex) {
        return new UserOwnedLuckyItemsResponse(ownedItemId, imagePath,itemName,status, slotIndex);
    }
}
