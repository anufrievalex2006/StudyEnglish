package com.studyenglish.backend.dtos.responses;

import com.studyenglish.backend.models.enums.Level;
import com.studyenglish.backend.models.enums.PartOfSpeech;
import com.studyenglish.backend.models.enums.TrainerDirection;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainerCardResponse {
    private UUID id;
    private TrainerDirection direction;
    private String prompt;
    private String transcription;
    private PartOfSpeech partOfSpeech;
    private Level level;
    private List<String> options;
}
