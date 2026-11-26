package com.studyenglish.backend.controllers;

import com.studyenglish.backend.dtos.responses.PageResponse;
import com.studyenglish.backend.models.enums.UserRole;
import com.studyenglish.backend.dtos.requests.update.RoleUpdateDto;
import com.studyenglish.backend.dtos.responses.UserResponse;
import com.studyenglish.backend.services.UserService;
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
