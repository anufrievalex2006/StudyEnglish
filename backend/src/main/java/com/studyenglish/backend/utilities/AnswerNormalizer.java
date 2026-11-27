package com.studyenglish.backend.utilities;

import java.util.Locale;

public final class AnswerNormalizer {
    private AnswerNormalizer() {

    }

    public static String normalize(String text) {
        if (text == null)
            return "";

        return text
                .strip()
                .toLowerCase(Locale.ROOT)
                .replace('ё', 'е')
                .replaceAll("[^\\p{L}\\p{N}]+", " ")
                .strip();
    }
}
