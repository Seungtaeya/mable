package demo.mable.auth.service;

import demo.mable.auth.dto.SignupRequest;
import demo.mable.user.entity.User;
import demo.mable.user.exception.DuplicatedEmailException;
import demo.mable.user.exception.DuplicatedUsernameException;
import demo.mable.user.exception.PasswordMismatchException;
import demo.mable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long registerUser(SignupRequest request) {

        if(userRepository.existsByUsername(request.username())) {
            throw new DuplicatedUsernameException();
        }

        if(request.email() != null && !request.email().isBlank() && userRepository.existsByEmail(request.email())) {
            throw new DuplicatedEmailException();
        }

        User user = new User();
        if(!request.password().equals(request.checkPassword())) {
            throw new PasswordMismatchException();
        }

        String encodePassword = passwordEncoder.encode(request.password());
        user.createUser(request.email(), encodePassword, request.username());
        User save = userRepository.save(user);
        return save.getId();
    }
}
