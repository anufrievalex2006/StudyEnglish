package com.studyenglish.backend.utilities;

import com.studyenglish.backend.repos.RefreshTokenRepo;
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
