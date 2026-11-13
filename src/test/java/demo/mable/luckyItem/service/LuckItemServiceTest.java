package demo.mable.luckyItem.service;

import demo.mable.luckyItem.dto.SelectItemRequest;
import demo.mable.luckyItem.entity.UserSelectedLuckyItem;
import demo.mable.luckyItem.repository.UserSelectLuckyItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LuckItemServiceTest {

    @Autowired
    LuckItemService luckItemService;

    @Autowired
    UserSelectLuckyItemRepository userSelectLuckyItemRepository;
    @Test
    void equipItem() {
        SelectItemRequest selectItemRequest = new SelectItemRequest();
        selectItemRequest.setIndexId(0L);
//        selectItemRequest.setSelectedItemId(1L);
        luckItemService.equipItem(30L,selectItemRequest);

        List<UserSelectedLuckyItem> byUserId = userSelectLuckyItemRepository.findByUser_Id(30L);
        assertThat(byUserId.size()).isEqualTo(1);
        assertThat(byUserId.getFirst().getUserLuckyItem().getId()).isEqualTo(1);
        assertThat(byUserId.getFirst().getUserLuckyItem().getLuckyItem().getItemName()).isEqualTo("스피드 레이서의 헬멧");
    }
}