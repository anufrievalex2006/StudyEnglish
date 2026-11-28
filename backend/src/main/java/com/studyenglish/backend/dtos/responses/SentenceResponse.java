package com.studyenglish.backend.dtos.responses;

import com.studyenglish.backend.models.enums.Level;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SentenceResponse {
    private UUID id;
    private String rusText;
    private Level level;
}
