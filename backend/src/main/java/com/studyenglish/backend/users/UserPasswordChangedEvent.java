package com.studyenglish.backend.users;

import java.util.UUID;

public record UserPasswordChangedEvent(UUID userId) {
}
