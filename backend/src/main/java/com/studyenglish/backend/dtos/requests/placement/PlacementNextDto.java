package com.studyenglish.backend.dtos.requests.placement;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlacementNextDto {
    @Valid
    @NotNull(message = "Список ответов обязателен (P.S. в начале теста он пуст)")
    @Size(max = 20, message = "Ответов не может быть больше 20")
    private List<PlacementAnswerDto> answers;
}
