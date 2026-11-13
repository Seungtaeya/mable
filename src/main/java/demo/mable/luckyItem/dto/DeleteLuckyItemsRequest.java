package demo.mable.luckyItem.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DeleteLuckyItemsRequest {

    @NotEmpty
    private List<Long> ownedItemIds;
}
