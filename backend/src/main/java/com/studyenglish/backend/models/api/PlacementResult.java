package com.studyenglish.backend.models.api;

import com.studyenglish.backend.models.enums.Level;
import lombok.*;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "placement_results")
public class PlacementResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 2)
    private Level level;
    @Column(nullable = false, name = "correct_answers_count")
    private int correctAnswersCount;
    @Column(nullable = false, name = "total_questions_count")
    private int totalQuestionsCount;
    @Column(nullable = false, name = "completed_at")
    private Instant completedAt;
}
