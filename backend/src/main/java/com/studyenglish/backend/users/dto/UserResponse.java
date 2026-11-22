package com.studyenglish.backend.users.dto;

import com.studyenglish.backend.users.User;
import com.studyenglish.backend.users.UserRole;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String nickname,
        String email,
        UserRole role,
        Instant createdAt
) {
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
