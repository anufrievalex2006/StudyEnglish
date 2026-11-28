package com.studyenglish.backend.controllers;

import com.studyenglish.backend.dtos.requests.sentence.SentenceCheckTranslationDto;
import com.studyenglish.backend.dtos.responses.SentenceCheckTranslationResponse;
import com.studyenglish.backend.dtos.responses.SentenceResponse;
import com.studyenglish.backend.models.enums.Level;
import com.studyenglish.backend.services.TranslationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/translation")
@RequiredArgsConstructor
public class TranslationController {
    private final TranslationService service;

    @GetMapping("/sentences")
    public List<SentenceResponse> getSentences(
            @RequestParam(required = false) Level level,
            @RequestParam(defaultValue = "20") int count
    ) {
        if (count < 1 || count > 20)
            count = 20;

        return service.getSentences(level, count);
    }

    @PostMapping("/check")
    public SentenceCheckTranslationResponse check(@Valid @RequestBody SentenceCheckTranslationDto req) {
        return service.check(req);
    }
}
