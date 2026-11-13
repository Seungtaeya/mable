package demo.mable.luckyItem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class SelectItemRequest {
    private Long indexId;
    private Long selectedLuckyItemId;
}
