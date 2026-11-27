package com.studyenglish.backend.dtos.requests.trainer;

import com.studyenglish.backend.models.enums.TrainerDirection;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WordCheckDto {
    @NotNull(message = "Укажите слово")
    private UUID wordId;
    @NotNull(message = "Укажите направление перевода")
    private TrainerDirection direction;
    @NotBlank(message = "Введите ответ")
    @Size(max = 200, message = "Ответ слишком длинный")
    private String answer;
}
