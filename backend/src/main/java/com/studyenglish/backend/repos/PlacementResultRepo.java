package com.studyenglish.backend.repos;

import com.studyenglish.backend.models.api.PlacementResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlacementResultRepo extends JpaRepository<PlacementResult, UUID> {
    Optional<PlacementResult> findFirstByUserIdOrderByCompletedAtDesc(UUID userId);
}
