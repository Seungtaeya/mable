package demo.mable.user.service;

import demo.mable.game.repository.GameResultRepository;
import demo.mable.user.dto.UserRankingResponse;
import demo.mable.user.entity.User;
import demo.mable.user.exception.UserNotFoundException;
import demo.mable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final GameResultRepository gameResultRepository;

    public Long getUserIdByUsername(String userName) {
        User user = userRepository.findByUsername(userName).orElseThrow(UserNotFoundException::new);
        return user.getId();
    }

    public List<UserRankingResponse> getRankings(Integer page) {
        List<UserRankingResponse> rankings = gameResultRepository.findRankings(page * 10);
        int idx = 1;
        for (UserRankingResponse ranking : rankings) {
            ranking.setRanking(page * 10 + idx++);
        }
        return rankings;
    }

    public int getRankingCount() {
        return userRepository.findAll().size();
    }
}
