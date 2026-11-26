package com.studyenglish.backend.dtos.requests.auth;

import jakarta.validation.constraints.NotBlank;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    @NotBlank(message = "Введите email")
    private String email;
    @NotBlank(message = "Введите пароль")
    private String password;
    private boolean rememberMe;
}
