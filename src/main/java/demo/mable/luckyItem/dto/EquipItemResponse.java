package demo.mable.luckyItem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EquipItemResponse {
    private Long ownedItemId;
    private Long slotIndex;
    private String imagePath;

    public EquipItemResponse(Long userSelectedItemId, Long indexId, String imagePath) {
        this.ownedItemId = userSelectedItemId;
        this.slotIndex = indexId;
        this.imagePath = imagePath;
    }

    public static EquipItemResponse from(Long userSelectedItemId, Long indexId, String imagePath) {
        return new EquipItemResponse(userSelectedItemId, indexId, imagePath);
    }
}
