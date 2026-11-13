package demo.mable.user.service;

import demo.mable.user.entity.User;
import demo.mable.user.exception.UserNotFoundException;
import demo.mable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public Long getUserIdByUsername(String userName) {
        User user = userRepository.findByUsername(userName).orElseThrow(UserNotFoundException::new);
        return user.getId();
    }
}
