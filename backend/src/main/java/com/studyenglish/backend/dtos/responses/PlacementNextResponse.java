package com.studyenglish.backend.dtos.responses;

import com.studyenglish.backend.models.enums.Level;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlacementNextResponse {
    private boolean done;
    private Integer number;
    private PlacementQuestionResponse question;
    private Level level;
    private Integer correct;
    private Integer total;

    public static PlacementNextResponse next(int number, PlacementQuestionResponse question) {
        return PlacementNextResponse.builder()
                .done(false)
                .number(number)
                .question(question)
                .build();
    }

    public static PlacementNextResponse finished(Level level, int correct, int total) {
        return PlacementNextResponse.builder()
                .done(true)
                .level(level)
                .correct(correct)
                .total(total)
                .build();
    }
}
