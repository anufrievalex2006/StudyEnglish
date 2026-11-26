package com.studyenglish.backend.controllers;

import com.studyenglish.backend.dtos.responses.PageResponse;
import com.studyenglish.backend.dtos.responses.WordResponse;
import com.studyenglish.backend.services.DictionaryService;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dictionary")
@RequiredArgsConstructor
public class DictionaryController {
    private final DictionaryService service;

    @GetMapping("/words")
    public PageResponse<WordResponse> search(
            @RequestParam @NotBlank @Size(max = 100) String query,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return service.search(query, page, size);
    }
}
