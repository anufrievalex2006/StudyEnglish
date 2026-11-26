package com.studyenglish.backend.utilities;

import java.util.UUID;

public record UserPasswordChangedEvent(UUID userId) {
}
