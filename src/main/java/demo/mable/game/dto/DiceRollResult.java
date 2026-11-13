package demo.mable.game.dto;


import demo.mable.luckyItem.entity.UserSelectedLuckyItem;

public record DiceRollResult(int left, int right, boolean activated, String UserLuckyItemImagePath) {
}
