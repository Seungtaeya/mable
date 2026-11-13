package demo.mable.luckyItem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DrawLuckyItemsResponse {

    private String luckyItemName;
    private String imagePath;
    private String Rank;


    public DrawLuckyItemsResponse(String luckyItemName, String imagePath, String rank) {
        this.luckyItemName = luckyItemName;
        this.imagePath = imagePath;
        Rank = rank;
    }

    public static DrawLuckyItemsResponse from(String luckyItemName, String imagePath, String rank) {
        return new DrawLuckyItemsResponse(luckyItemName, imagePath, rank);
    }
}
