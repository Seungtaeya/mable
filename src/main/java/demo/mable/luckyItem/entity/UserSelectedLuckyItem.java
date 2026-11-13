package demo.mable.luckyItem.entity;

import demo.mable.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Table(name = "user_selected_lucky_items")
@Entity
public class UserSelectedLuckyItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_lucky_item_id")
    private UserLuckyItem userLuckyItem;

    @Column(name = "index_number")
    private Long index;
    @Column(name = "create_date")
    private LocalDateTime createAt;

    public UserSelectedLuckyItem(Long index) {
        this.index = index;
    }

    @PrePersist
    private void createTime() {
        this.createAt = LocalDateTime.now();
    }

    public void assignUser(User user) {
        this.user = user;
    }
    public void assignUserLuckyItem(UserLuckyItem item) {
        this.userLuckyItem = item;
    }

    public void changeUserLuckyItem(UserLuckyItem item) {
        this.userLuckyItem = item;
    }
}
