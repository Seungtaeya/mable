package demo.mable.game.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class GameProperties {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_player_id")
    private GamePlayer player;

    @Column(name = "tile_id")
    private int tileId;
    @Column(name = "construction_cost")
    private long constructionCost;

    @Column(name = "has_flag")
    private boolean flag;
    @Column(name = "has_house")
    private boolean house;
    @Column(name = "has_villa")
    private boolean villa;
    @Column(name = "has_apartment")
    private boolean apartment;
    @Column(name = "has_landmark")
    private boolean landmark;

    public void assignGame(Game game) {
        this.game = game;
    }
    public void assignGamePlayer(GamePlayer gamePlayer) {
        this.player = gamePlayer;
    }

    public void updateConstructionCost(long constructionCost) {
        this.constructionCost = constructionCost;
    }
    public void addTile(int tileId) {
        this.tileId = tileId;
    }
    public void changeTileOwner(GamePlayer gamePlayer) {
        this.player = gamePlayer;
    }
    public void constructionFlag(boolean flag) {
        this.flag = flag;
    }
    public void constructionHouse(boolean house) {
        this.house = house;
    }
    public void constructionVilla(boolean villa) {
        this.villa = villa;
    }
    public void constructionApartment(boolean apartment) {
        this.apartment = apartment;
    }
    public void constructionLandmark(boolean landmark) {
        this.landmark = landmark;
    }
}
