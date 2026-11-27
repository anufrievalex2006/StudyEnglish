package com.studyenglish.backend.dtos.responses;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WordCheckResponse {
    private boolean correct;
    private String correctAnswer;
    private List<String> translations;

    public static WordCheckResponse right() {
        return WordCheckResponse.builder()
                .correct(true)
                .build();
    }

    public static WordCheckResponse wrong(String correctAnswer, List<String> translations) {
        return WordCheckResponse.builder()
                .correct(false)
                .correctAnswer(correctAnswer)
                .translations(translations)
                .build();
    }
}
