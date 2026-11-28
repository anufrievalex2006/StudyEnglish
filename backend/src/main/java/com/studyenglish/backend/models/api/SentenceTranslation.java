package com.studyenglish.backend.models.api;

import lombok.*;
import jakarta.persistence.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sentence_translations")
public class SentenceTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sentence_id", nullable = false)
    private Sentence sentence;
    @Column(name = "eng_text", nullable = false, length = 500)
    private String engText;
    @Column(name = "normalized_text", nullable = false, length = 500)
    private String normalizedText;
}
