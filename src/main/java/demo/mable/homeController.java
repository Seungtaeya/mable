package demo.mable;

import demo.mable.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class homeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomUserDetails customUserDetails , Model model) {

        if(customUserDetails != null) {
            model.addAttribute("username", customUserDetails.getUsername());
        }
//        return "home/root";
        return "redirect:/lobby";
    }
}
