package com.studyenglish.backend.dtos.responses;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SentenceCheckTranslationResponse {
    private boolean correct;
    private String correctAnswer;
    private String explanation;

    public static SentenceCheckTranslationResponse success() {
        return new SentenceCheckTranslationResponse(
                true,
                null,
                null
        );
    }

    public static SentenceCheckTranslationResponse failure(String correctAnswer, String explanation) {
        return new SentenceCheckTranslationResponse(
                false,
                correctAnswer,
                explanation
        );
    }
}
