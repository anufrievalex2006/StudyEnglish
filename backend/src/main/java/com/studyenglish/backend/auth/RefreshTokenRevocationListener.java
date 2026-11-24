package com.studyenglish.backend.auth;

import com.studyenglish.backend.users.UserPasswordChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RefreshTokenRevocationListener {
    private final RefreshTokenRepo repo;

    @EventListener
    @Transactional
    public void onPasswordChange(UserPasswordChangedEvent event) {
        repo.revokeAllActiveByUserId(event.userId());
    }
}
