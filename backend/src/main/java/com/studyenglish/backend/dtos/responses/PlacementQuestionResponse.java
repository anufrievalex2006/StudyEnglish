package com.studyenglish.backend.dtos.responses;

import com.studyenglish.backend.models.api.PlacementQuestion;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlacementQuestionResponse {
    private UUID id;
    private String text;
    private List<PlacementOptionResponse> options;

    public static PlacementQuestionResponse from(PlacementQuestion q) {
        return new PlacementQuestionResponse(
                q.getId(),
                q.getText(),
                q.getOptions().stream().map(PlacementOptionResponse::from).toList()
        );
    }
}
