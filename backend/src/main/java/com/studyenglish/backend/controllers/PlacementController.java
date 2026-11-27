package com.studyenglish.backend.controllers;

import com.studyenglish.backend.dtos.requests.placement.PlacementNextDto;
import com.studyenglish.backend.dtos.responses.PlacementNextResponse;
import com.studyenglish.backend.dtos.responses.PlacementResultResponse;
import com.studyenglish.backend.services.PlacementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/placement")
@RequiredArgsConstructor
public class PlacementController {
    private final PlacementService service;

    @PostMapping("/next")
    public PlacementNextResponse next(@Valid @RequestBody PlacementNextDto req, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = jwt != null ? UUID.fromString(jwt.getSubject()) : null;
        return service.next(req.getAnswers(), userId);
    }

    @GetMapping("/results/latest")
    public PlacementResultResponse latest(@AuthenticationPrincipal Jwt jwt) {
        return service.latest(UUID.fromString(jwt.getSubject()));
    }
}
