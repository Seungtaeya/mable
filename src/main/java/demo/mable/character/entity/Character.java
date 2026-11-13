package demo.mable.character.entity;

import demo.mable.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "characters")
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "character_name")
    private String characterName;
    @Column(name = "portrait_image_path")
    private String imagePath;
    @Column(name = "piece_image_path")
    private String pieceImage;
    @Column(name = "toll_discount")
    private int tollDiscount;
    @Column(name = "bonus_game_victory")
    private int bonusGameWin;
    @Column(name = "special_cost_discount")
    private int specialCostDiscount;
    @Column(name = "golden_fortune_acquisition")
    private int goldenFortuneAcquisition;
    @Column(name = "construction_cost_discount")
    private int constructionCostDiscount;
    @Column(name = "dice_control")
    private int diceControl;
    @Column(name = "acquisition_cost_discount")
    private int acquisitionCostDiscount;
    @Column(name = "character_rank")
    private String rank;

    public Character(String characterName, String imagePath, int tollDiscount, int bonusGameWin, int specialCostDiscount, int goldenFortuneAcquisition, int constructionCostDiscount, int diceControl, int acquisitionCostDiscount, String rank) {
        this.characterName = characterName;
        this.imagePath = imagePath;
        this.tollDiscount = tollDiscount;
        this.bonusGameWin = bonusGameWin;
        this.specialCostDiscount = specialCostDiscount;
        this.goldenFortuneAcquisition = goldenFortuneAcquisition;
        this.constructionCostDiscount = constructionCostDiscount;
        this.diceControl = diceControl;
        this.acquisitionCostDiscount = acquisitionCostDiscount;
        this.rank = rank;
    }

    // 로비에서 캐릭터 만드는 버튼 만들기
    // 캐릭터 정보 입력해서 캐릭터 만들기
    // 유저테이블이랑 캐릭터 테이블 연관관계 설정하기
    // 로비 + 게임 대기방 + 게임 에서 캐릭터 보이게 수정
    // 할 수 있다면 게임판에서도 캐릭터 보이도록 수정

}
