package com.studyenglish.backend.users;

import com.studyenglish.backend.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {
    private final UserRepo repo;
    private final PasswordEncoder encoder;
    private final AppProperties props;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (repo.existsByRole(UserRole.ADMIN))
            return;

        AppProperties.Admin admin = props.admin();
        repo.save(User.builder()
                        .name(admin.nickname())
                        .nickname(admin.nickname())
                        .email(admin.email())
                        .passwordHash(encoder.encode(admin.password()))
                        .role(UserRole.ADMIN)
                .build());
        log.info("Создан администратор: {}", admin.email());
    }
}
