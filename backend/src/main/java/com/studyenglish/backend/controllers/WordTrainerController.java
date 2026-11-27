package com.studyenglish.backend.controllers;

import com.studyenglish.backend.dtos.requests.trainer.WordCheckDto;
import com.studyenglish.backend.dtos.responses.TrainerCardResponse;
import com.studyenglish.backend.dtos.responses.WordCheckResponse;
import com.studyenglish.backend.models.enums.Level;
import com.studyenglish.backend.services.WordTrainerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainer/words")
@RequiredArgsConstructor
public class WordTrainerController {
    private final WordTrainerService service;

    @GetMapping
    public List<TrainerCardResponse> quiz(
            @RequestParam(required = false) Level level,
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) int count
    ) {
        return service.quiz(level, count);
    }

    @PostMapping("/check")
    public WordCheckResponse check(@Valid @RequestBody WordCheckDto req) {
        return service.check(req.getWordId(), req.getDirection(), req.getAnswer());
    }
}
