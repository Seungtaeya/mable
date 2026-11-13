package demo.mable.character.controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CharacterController {

    @GetMapping("/characters")
    public String characterPage() {
        return "character/characterHome";
    }

    @GetMapping("/character/{idx}/{characterId}")
    public String characterInfo(@PathVariable int idx,
            @PathVariable Long characterId, Model model) {
        model.addAttribute("idx", idx);
        model.addAttribute("characterId", characterId);
        return "character/characterInfo";
    }

    @GetMapping("/draw-cards")
    public String drawCards() {
        return "character/drawCardForm";
    }
}
