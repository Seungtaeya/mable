package demo.mable.auth.controller;

import demo.mable.auth.dto.SignupRequest;
import demo.mable.auth.service.AuthService;
import demo.mable.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class signupController {

    private final AuthService authService;

    @GetMapping("/registry")
    public String signupPage() {
        return "auth/signup";
    }

    @PostMapping("/registry")
    public String register(@Valid SignupRequest request, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        Long Id = authService.registerUser(request);
        return "redirect:/login";
    }
}
