package com.studyenglish.backend.users;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Setter
    @Column(nullable = false, length = 100)
    private String name;
    @Setter
    @Column(nullable = false, length = 50)
    private String nickname;
    @Column(nullable = false)
    private String email;
    @Setter
    @Column(nullable = false)
    private String passwordHash;
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;
    @Column(nullable = false)
    private Instant createdAt;

    @Builder
    public User(String name, String nickname, String email, String passwordHash, UserRole role) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = Instant.now();
    }
}
