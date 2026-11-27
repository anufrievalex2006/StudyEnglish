package com.studyenglish.backend.dtos.responses;

import com.studyenglish.backend.models.api.PlacementResult;
import com.studyenglish.backend.models.enums.Level;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlacementResultResponse {
    private Level level;
    private int correct;
    private int total;
    private Instant completedAt;

    public static PlacementResultResponse from(PlacementResult r) {
        return new PlacementResultResponse(
                r.getLevel(),
                r.getCorrectAnswersCount(),
                r.getTotalQuestionsCount(),
                r.getCompletedAt()
        );
    }
}
