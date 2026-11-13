package demo.mable.luckyItem.controller;

import demo.mable.global.security.CustomUserDetails;
import demo.mable.luckyItem.dto.*;
import demo.mable.luckyItem.service.LuckItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LuckyItemApiController {

    private final LuckItemService luckItemService;

    @PostMapping("/users/me/draw-luckyItems")
    public ResponseEntity<List<DrawLuckyItemsResponse>> drawItems(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<DrawLuckyItemsResponse> drawLuckyItemsResponses = luckItemService.drawLuckyItems(userDetails.getUserId());
        return ResponseEntity.ok(drawLuckyItemsResponses);
    }

    @GetMapping("/users/me/lucky-items")
    public ResponseEntity<List<UserOwnedLuckyItemsResponse>> getItems(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<UserOwnedLuckyItemsResponse> ownedItems = luckItemService.findOwnedLuckyItemsByUserId(userDetails.getUserId());
        return ResponseEntity.ok(ownedItems);
    }

    @DeleteMapping("/users/me/lucky-items")
    public ResponseEntity<Void> deleteItems(@AuthenticationPrincipal CustomUserDetails userDetails,@RequestBody @Valid DeleteLuckyItemsRequest request) {
        luckItemService.softDeleteLuckyItem(userDetails.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/me/selected-item")
    public ResponseEntity<EquipItemResponse> equipItem(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody SelectItemRequest request) {
        EquipItemResponse equipItemResponse = luckItemService.equipItem(userDetails.getUserId(), request);
        return ResponseEntity.ok(equipItemResponse);
    }

    @GetMapping("/users/me/equipped-items")
    public ResponseEntity<List<EquipItemResponse>> EquippedItems(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<EquipItemResponse> equippedItemsByUserId = luckItemService.findEquippedItemsByUserId(userDetails.getUserId());
        return ResponseEntity.ok(equippedItemsByUserId);
    }
}
