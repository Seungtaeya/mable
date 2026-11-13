package demo.mable.user.entity;

import demo.mable.user.enums.UserRole;
import demo.mable.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 50 )
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "username", length = 20)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 25)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @Column(name = "created_at")
    private LocalDateTime created;

    @Column(name = "updated_at")
    private LocalDateTime updated;
    @Column(name = "deleted_at")
    private LocalDateTime deleted;

    @PrePersist
    private void create() {
        this.created = LocalDateTime.now();
    }

    public void createUser(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = UserRole.USER;
        this.status = UserStatus.ACTIVE;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", created=" + created +
                ", updated=" + updated +
                ", deleted=" + deleted +
                '}';
    }
}
