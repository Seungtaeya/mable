package demo.mable.luckyItem.service;

import demo.mable.admin.enums.AssetType;
import demo.mable.admin.service.ImageStorageService;
import demo.mable.luckyItem.dto.*;
import demo.mable.luckyItem.entity.LuckyItem;
import demo.mable.luckyItem.entity.UserLuckyItem;
import demo.mable.luckyItem.entity.UserSelectedLuckyItem;
import demo.mable.luckyItem.enums.UserLuckyItemStatus;
import demo.mable.luckyItem.repository.LuckyItemRepository;
import demo.mable.luckyItem.repository.UserLuckyItemRepository;
import demo.mable.luckyItem.repository.UserSelectLuckyItemRepository;
import demo.mable.skill.entity.Skill;
import demo.mable.skill.repository.SkillRepo;
import demo.mable.user.entity.User;
import demo.mable.user.exception.UserNotFoundException;
import demo.mable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LuckItemService {

    private final LuckyItemRepository luckItemRepository;
    private final UserLuckyItemRepository userLuckyItemRepository;
    private final UserSelectLuckyItemRepository userSelectLuckyItemRepository;
    private final SkillRepo skillRepo;
    private final ImageStorageService imageStorageService;
    private final UserRepository userRepository;

    @Transactional
    public void createLuckyItem(CreateLuckyItemRequest request, MultipartFile image) throws IOException {

        if(request == null) {
            throw new IllegalArgumentException("LuckItemService : request is null");
        }

        String imagePath = imageStorageService.save(image, AssetType.LUCKY_ITEM);

        System.out.println("request.skillCode().name() = " + request.skillCode().name());
        Skill skill = new Skill(request.skillCode(),request.triggerType(),request.itemName(), request.skillDescription(), imagePath);
        Skill saveSkill = skillRepo.save(skill);

        LuckyItem luckyItem = new LuckyItem(request.itemName(), request.itemRank(), request.activationRate());
        luckyItem.assignSkill(saveSkill);
        luckItemRepository.save(luckyItem);
    }

    @Transactional
    public List<DrawLuckyItemsResponse> drawLuckyItems(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        List<LuckyItem> items = luckItemRepository.findAll();

        List<LuckyItem> drawItems = new ArrayList<>(5);
        for(int i = 0; i < 5; i++) {

            drawItems.add(items.get((int)(Math.random() * items.size())));
        }

        for (LuckyItem drawItem : drawItems) {
            UserLuckyItem uli = new UserLuckyItem();
            uli.assignUser(user);
            uli.assignLuckyItem(drawItem);
            userLuckyItemRepository.save(uli);
        }
        return drawItems.stream().map(item -> DrawLuckyItemsResponse.from(item.getItemName(),item.getSkill().getImagePath(),item.getItemRank())).toList();

    }

    @Transactional
    public void softDeleteLuckyItem(Long userId, DeleteLuckyItemsRequest request) {
        List<UserLuckyItem> items = userLuckyItemRepository.findAllByUser_Id(userId);

        for (UserLuckyItem item : items) {
            for(int i = 0; i < request.getOwnedItemIds().size(); i++) {
                System.out.println("request.getOwnedItemIds().get(i) = " + request.getOwnedItemIds().get(i));
                if(item.getId().equals(request.getOwnedItemIds().get(i))) {
                    item.markAsRemoved(UserLuckyItemStatus.REMOVED);
                    break;
                }
            }
        }
    }

    public List<UserOwnedLuckyItemsResponse> findOwnedLuckyItemsByUserId(Long userId) {
        return userLuckyItemRepository.findAllByUserId(userId);
    }

    public OwnedItemInfoResponse findByOwnedItemId(Long ownedItemId) {
        UserLuckyItem userLuckyItem = userLuckyItemRepository.findById(ownedItemId).orElseThrow(() -> new IllegalArgumentException("존재하지 않습니다."));
        return OwnedItemInfoResponse.from(userLuckyItem.getLuckyItem().getSkill().getImagePath(), userLuckyItem.getLuckyItem().getSkill().getSkillDescription(), userLuckyItem.getLuckyItem().getItemName());
    }

    public List<EquipItemResponse> findEquippedItemsByUserId(Long userId) {
        return userSelectLuckyItemRepository.findByUser_Id(userId).stream().map(item -> EquipItemResponse.from(item.getUserLuckyItem().getId(),item.getIndex(),item.getUserLuckyItem().getLuckyItem().getSkill().getImagePath())).toList();
    }

    @Transactional
    public EquipItemResponse equipItem(Long userId, SelectItemRequest request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        UserLuckyItem selectedItemRequest = userLuckyItemRepository.findById(request.getSelectedLuckyItemId()).orElseThrow(() -> new IllegalArgumentException("선택한 아이템을 가지고 있지 않습니다."));

        if(!(request.getIndexId() >= 0 && request.getIndexId() < 4)) {
            throw new IllegalArgumentException("슬롯 번호는 0 ~ 3만 가능합니다");
        }

        if(selectedItemRequest.getStatus().equals(UserLuckyItemStatus.REMOVED)) {
            throw new IllegalArgumentException("삭제된 아이템은 장착할 수 없습니다.");
        }

        if(selectedItemRequest.getStatus().equals(UserLuckyItemStatus.SELECTED)) {
            throw new IllegalArgumentException("장착중인 아이템이여서 중복으로 장착할 수 없습니다.");
        }

        List<UserSelectedLuckyItem> userSelectedLuckyItems = userSelectLuckyItemRepository.findByUser_Id(userId);

        if(!userSelectedLuckyItems.isEmpty()) {
            for (UserSelectedLuckyItem userSelectedLuckyItem : userSelectedLuckyItems) {
                if (userSelectedLuckyItem.getIndex().equals(request.getIndexId())) {
                    userSelectedLuckyItem.getUserLuckyItem().markAsACTIVE(UserLuckyItemStatus.ACTIVE);

                    userSelectedLuckyItem.changeUserLuckyItem(selectedItemRequest);
                    userSelectedLuckyItem.getUserLuckyItem().markAsSelected(UserLuckyItemStatus.SELECTED);

                    return EquipItemResponse.from(userSelectedLuckyItem.getUserLuckyItem().getId(),userSelectedLuckyItem.getIndex(),userSelectedLuckyItem.getUserLuckyItem().getLuckyItem().getSkill().getImagePath());
                }
            }
        }

        UserSelectedLuckyItem selectedLuckyItem = new UserSelectedLuckyItem(request.getIndexId());
        selectedLuckyItem.assignUser(user);
        selectedLuckyItem.assignUserLuckyItem(selectedItemRequest);
        UserSelectedLuckyItem save = userSelectLuckyItemRepository.save(selectedLuckyItem);

        selectedItemRequest.markAsSelected(UserLuckyItemStatus.SELECTED);
        return EquipItemResponse.from(save.getUserLuckyItem().getId(), save.getIndex(),save.getUserLuckyItem().getLuckyItem().getSkill().getImagePath());
    }


}
