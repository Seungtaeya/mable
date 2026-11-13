package demo.mable.luckyItem.entity;

import demo.mable.luckyItem.enums.UserLuckyItemStatus;
import demo.mable.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Table(name = "user_lucky_items")
@NoArgsConstructor
public class UserLuckyItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lucky_item_id")
    private LuckyItem luckyItem;

    @Column(name = "item_status")
    @Enumerated(value = EnumType.STRING)
    private UserLuckyItemStatus status = UserLuckyItemStatus.ACTIVE;

    @OneToMany(mappedBy = "userLuckyItem")
    private List<UserSelectedLuckyItem> userSelectedLuckyItems = new ArrayList<>();

    public void assignUser(User user) {
        this.user = user;
    }

    public void assignLuckyItem(LuckyItem luckyItem) {
        this.luckyItem = luckyItem;
    }

    public void markAsRemoved(UserLuckyItemStatus status) {
        this.status = status;
    }
    public void markAsSelected(UserLuckyItemStatus status) {
        this.status = status;
    }

    public void markAsACTIVE(UserLuckyItemStatus status) {
        this.status = status;
    }
}