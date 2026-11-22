package com.studyenglish.backend.users;

import com.studyenglish.backend.common.error.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private static final int MAX_PASSWORD_BYTES = 72;

    private final UserRepo repo;
    private final PasswordEncoder encoder;

    public User createStudent(String name, String nickname, String email, String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES)
            throw ApiException.badRequest("Пароль слишком длинный");
        if (repo.existsByEmail(email))
            throw ApiException.conflict("Пользователь с таким email уже существует");
        if (repo.existsByNickname(nickname))
            throw ApiException.conflict("Пользователь с таким никнеймом уже существует");

        User u = User.builder()
                .name(name)
                .nickname(nickname)
                .email(email)
                .passwordHash(encoder.encode(password))
                .role(UserRole.STUDENT)
                .build();

        try {
            return repo.saveAndFlush(u);
        } catch (DataIntegrityViolationException e) {
            throw ApiException.conflict("Email или никнейм уже заняты");
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }
}
