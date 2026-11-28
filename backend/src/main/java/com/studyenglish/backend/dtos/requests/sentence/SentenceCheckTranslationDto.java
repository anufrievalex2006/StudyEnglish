package com.studyenglish.backend.dtos.requests.sentence;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SentenceCheckTranslationDto {
    @NotNull
    private UUID sentenceId;
    @NotNull
    private String answer;
}
