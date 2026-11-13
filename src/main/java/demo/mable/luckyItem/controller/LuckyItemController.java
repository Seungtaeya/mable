package demo.mable.luckyItem.controller;

import demo.mable.luckyItem.dto.OwnedItemInfoResponse;
import demo.mable.luckyItem.service.LuckItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class LuckyItemController {

    private final LuckItemService luckItemService;

    @GetMapping("/luckyItem")
    public String luckyItemPage() {
        return "luckyItem/UserLuckyItemHome";
    }

    @GetMapping("/draw/lucky-item")
    public String drawLuckyItemPage() {
        return "luckyItem/drawLuckyItemForm";
    }

    @GetMapping("/luckyItem-info/{ownedItemId}")
    public String info(@PathVariable Long ownedItemId, Model model) {
        OwnedItemInfoResponse byOwnedItemId = luckItemService.findByOwnedItemId(ownedItemId);
        model.addAttribute("ownedItemId", ownedItemId);
        model.addAttribute("info", byOwnedItemId);
        return "luckyItem/itemInfo";
    }
}
