package com.studyenglish.backend.users;

import com.studyenglish.backend.common.error.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private static final int MAX_PASSWORD_BYTES = 72;

    private final UserRepo repo;
    private final PasswordEncoder encoder;
    private final ApplicationEventPublisher events;

    public User createStudent(String name, String nickname, String email, String password) {
        requirePasswordFitsBCrypt(password);
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

    @Transactional(readOnly = true)
    public User getCurrent(UUID id) {
        return repo.findById(id).orElseThrow(ApiException::invalidToken);
    }

    public User updateProfile(UUID id, String name, String nickname) {
        User u = getCurrent(id);
        if (name != null)
            u.setName(name);
        if (nickname != null && !nickname.equals(u.getNickname())) {
            if (!nickname.equalsIgnoreCase(u.getNickname()) && repo.existsByNickname(nickname))
                throw ApiException.conflict("Пользователь с таким никнеймом уже существует");

            u.setNickname(nickname);
        }

        try {
            return repo.saveAndFlush(u);
        } catch (DataIntegrityViolationException e) {
            throw ApiException.conflict("Этот никнейм уже занят");
        }
    }

    public void changePassword(UUID id, String curPassword, String newPassword) {
        User u = getCurrent(id);
        if (!encoder.matches(curPassword, u.getPasswordHash()))
            throw ApiException.badRequest("Текущий пароль неправильный");

        requirePasswordFitsBCrypt(newPassword);
        u.setPasswordHash(encoder.encode(newPassword));
        events.publishEvent(new UserPasswordChangedEvent(u.getId()));
    }

    @Transactional(readOnly = true)
    public Page<User> search(UserRole role, String name, int page, int size) {
        String pattern = likePattern(name);
        Pageable p = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by("id")));
        return role == null ? repo.search(pattern, p) : repo.searchByRole(role, pattern, p);
    }

    public User changeRole(UUID id, UserRole newRole) {
        if (newRole == UserRole.ADMIN)
            throw ApiException.badRequest("Нельзя сделать пользователя Администратором");
        User u = repo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Пользователь не найден"));
        if (u.getRole() == UserRole.ADMIN)
            throw ApiException.badRequest("Нельзя изменить роль Администратора");

        u.setRole(newRole);
        return u;
    }

    private static void requirePasswordFitsBCrypt(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES)
            throw ApiException.badRequest("Пароль слишком длинный");
    }

    private static String likePattern(String name) {
        if (name == null || name.isBlank())
            return "%";
        String esc = name.trim()
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        return "%" + esc + "%";
    }
}
