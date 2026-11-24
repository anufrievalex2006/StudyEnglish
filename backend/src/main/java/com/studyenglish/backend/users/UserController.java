package com.studyenglish.backend.users;

import com.studyenglish.backend.users.dto.PasswordUpdateDto;
import com.studyenglish.backend.users.dto.ProfileUpdateDto;
import com.studyenglish.backend.users.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        return UserResponse.from(service.getCurrent(getId(jwt)));
    }

    @PatchMapping
    public UserResponse update(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProfileUpdateDto req) {
        return UserResponse.from(service.updateProfile(getId(jwt), req.name(), req.nickname()));
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody PasswordUpdateDto req) {
        service.changePassword(getId(jwt), req.curPassword(), req.newPassword());
    }

    private static UUID getId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
