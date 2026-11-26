package com.studyenglish.backend.dtos.responses;

import com.studyenglish.backend.models.api.User;
import com.studyenglish.backend.models.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse{
    private UUID id;
    private String name;
    private String nickname;
    private String email;
    private UserRole role;
    private Instant createdAt;

    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(),
                u.getName(),
                u.getNickname(),
                u.getEmail(),
                u.getRole(),
                u.getCreatedAt()
        );
    }
}
