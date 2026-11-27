package com.studyenglish.backend.dtos.requests.placement;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlacementAnswerDto {
    @NotNull(message = "Укажите вопрос")
    private UUID questionId;
    @NotNull(message = "Укажите вариант ответа")
    private UUID optionId;
}
