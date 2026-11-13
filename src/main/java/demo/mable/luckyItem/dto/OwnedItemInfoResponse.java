package demo.mable.luckyItem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OwnedItemInfoResponse {

    private String imagePath;
    private String itemDescription;
    private String itemName;

    public OwnedItemInfoResponse(String imagePath, String itemDescription, String itemName) {
        this.imagePath = imagePath;
        this.itemDescription = itemDescription;
        this.itemName = itemName;
    }

    public static OwnedItemInfoResponse from(String imagePath, String itemDescription, String itemName) {
        return new OwnedItemInfoResponse(imagePath, itemDescription, itemName);
    }
}
