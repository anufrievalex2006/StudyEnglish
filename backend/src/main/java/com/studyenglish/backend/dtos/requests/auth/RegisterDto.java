package com.studyenglish.backend.dtos.requests.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
    @NotBlank(message = "Введите имя")
    @Size(max = 100, message = "Имя не должно быть длиннее 100 символов")
    private String name;
    @NotBlank(message = "Введите никнейм")
    @Size(min = 3, max = 50, message = "Никнейм должен быть от 3 до 50 символов")
    @Pattern(regexp = "^[A-Za-z0-9_]*$",
            message = "Никнейм может содержать только латинские буквы, цифры и _")
    private String nickname;
    @NotBlank(message = "Введите email")
    @Email(message = "Некорректный email")
    @Size(max = 255, message = "Email не должен быть длиннее 255 символов")
    private String email;
    @NotBlank(message = "Введите пароль")
    @Size(min = 8, max = 72, message = "Пароль должен быть от 8 до 72 символов")
    private String password;
}
