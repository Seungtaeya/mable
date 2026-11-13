package demo.mable.luckyItem.repository;

import demo.mable.luckyItem.entity.UserSelectedLuckyItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSelectLuckyItemRepository extends JpaRepository<UserSelectedLuckyItem, Long> {
    List<UserSelectedLuckyItem> findByUser_Id(Long userId);
}
