package demo.mable.user.repository;

import demo.mable.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String nickname);

    boolean existsByUsername(String nickname);

    boolean existsByEmail(String email);
}
