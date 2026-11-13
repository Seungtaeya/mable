package demo.mable.luckyItem.repository;

import demo.mable.luckyItem.dto.UserOwnedLuckyItemsResponse;
import demo.mable.luckyItem.entity.UserLuckyItem;
import demo.mable.luckyItem.enums.UserLuckyItemStatus;
import demo.mable.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserLuckyItemRepository extends JpaRepository<UserLuckyItem, Long> {

//    @Query(value = """
//        select new demo.mable.luckyItem.dto.UserOwnedLuckyItemsResponse(uli.id,
//        li.imagePath, uli.luckyItem.itemName, uli.status)
//        from UserLuckyItem uli join uli.luckyItem.skill li where uli.user.id = :userId and (uli.status = UserLuckyItemStatus.ACTIVE or uli.status = UserLuckyItemStatus.SELECTED)
//""")
 @Query(value = """
        select new demo.mable.luckyItem.dto.UserOwnedLuckyItemsResponse(uli.id,
        li.imagePath, uli.luckyItem.itemName, uli.status,usli.index) 
        from UserLuckyItem uli left join uli.userSelectedLuckyItems usli join uli.luckyItem.skill li where uli.user.id = :userId and (uli.status = UserLuckyItemStatus.ACTIVE or uli.status = UserLuckyItemStatus.SELECTED)
""")
    List<UserOwnedLuckyItemsResponse> findAllByUserId(Long userId);

    List<UserLuckyItem> findAllByUser_Id(Long userId);

}
