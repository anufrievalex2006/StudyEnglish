package com.studyenglish.backend.users;

import com.studyenglish.backend.common.PageResponse;
import com.studyenglish.backend.users.dto.RoleUpdateDto;
import com.studyenglish.backend.users.dto.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService service;

    @GetMapping
    public PageResponse<UserResponse> list(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return PageResponse.from(service.search(role, name, page, size), UserResponse::from);
    }

    @PatchMapping("/{id}/role")
    public UserResponse changeRole(@PathVariable UUID id, @Valid @RequestBody RoleUpdateDto req) {
        return UserResponse.from(service.changeRole(id, req.role()));
    }
}
