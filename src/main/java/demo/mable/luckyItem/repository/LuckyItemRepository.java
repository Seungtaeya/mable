package demo.mable.luckyItem.repository;

import demo.mable.luckyItem.entity.LuckyItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LuckyItemRepository extends JpaRepository<LuckyItem, Long> {
}
