package demo.mable.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class loginController {

    @GetMapping("/login")
    public String login(String error, Model model) {

        if(error != null) {
            String errorMessage = switch (error) {
                case "max-session" -> "로그인된 계정 입니다.";
                case "locked" -> "잠긴 계정입니다.";
                default -> "로그인 처리 중 오류가 발생했습니다.";
            };

            model.addAttribute("errorMessage", errorMessage);
        }
        return "auth/login";
    }
}
