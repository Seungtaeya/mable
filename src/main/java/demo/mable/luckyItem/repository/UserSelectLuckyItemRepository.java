package demo.mable.luckyItem.repository;

import demo.mable.luckyItem.dto.EquipItemResponse;
import demo.mable.luckyItem.entity.UserSelectedLuckyItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserSelectLuckyItemRepository extends JpaRepository<UserSelectedLuckyItem, Long> {
    List<UserSelectedLuckyItem> findByUser_Id(Long userId);

    @Query(value = """
        select new demo.mable.luckyItem.dto.EquipItemResponse(
            usli.index,
            sk.imagePath,
            li.itemName,
            sk.skillDescription
        ) from User user
        join UserSelectedLuckyItem usli on usli.user = user
        join usli.userLuckyItem uli
        join uli.luckyItem li 
        left join li.skill sk
        where user.id = :userId
""")
    List<EquipItemResponse> findEquipItemByUserId(Long userId);
}
