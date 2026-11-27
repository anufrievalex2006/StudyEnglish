package com.studyenglish.backend.dtos.responses;

import com.studyenglish.backend.models.api.PlacementOption;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlacementOptionResponse {
    private UUID id;
    private String text;

    public static PlacementOptionResponse from(PlacementOption o) {
        return new PlacementOptionResponse(o.getId(), o.getText());
    }
}
