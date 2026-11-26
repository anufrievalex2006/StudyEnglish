package com.studyenglish.backend.models.enums;

public enum Level {
    A1,
    A2,
    B1;

    public Level harder() {
        Level[] all = values();
        return all[Math.min(ordinal() + 1, all.length - 1)];
    }

    public Level easier() {
        return values()[Math.max(ordinal() - 1, 0)];
    }
}
