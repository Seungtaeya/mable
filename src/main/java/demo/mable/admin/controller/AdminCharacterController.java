package demo.mable.admin.controller;

import demo.mable.character.service.CharacterService;
import demo.mable.skill.enums.SkillCode;
import demo.mable.skill.enums.TriggerType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminCharacterController {

    private final CharacterService characterService;

    @GetMapping("/adminPage")
    public String adminHome(Model model) {

        model.addAttribute("skillCodes", SkillCode.values());
        model.addAttribute("triggerTypes", TriggerType.values());
        model.addAttribute("characters", characterService.getSPlusCharacters());
        return "admin/adminHome";
    }
}
