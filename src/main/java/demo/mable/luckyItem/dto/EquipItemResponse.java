package demo.mable.luckyItem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class EquipItemResponse {
    private Long ownedItemId;
    private Long slotIndex;
    private String imagePath;
    private String itemName;
    private String itemDescription;

    public EquipItemResponse(Long userSelectedItemId, Long indexId, String imagePath) {
        this.ownedItemId = userSelectedItemId;
        this.slotIndex = indexId;
        this.imagePath = imagePath;
    }

    public EquipItemResponse(Long slotIndex, String imagePath, String itemName, String itemDescription) {
        this.slotIndex = slotIndex;
        this.imagePath = imagePath;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
    }

    public static EquipItemResponse from(Long userSelectedItemId, Long indexId, String imagePath) {
        return new EquipItemResponse(userSelectedItemId, indexId, imagePath);
    }
}
