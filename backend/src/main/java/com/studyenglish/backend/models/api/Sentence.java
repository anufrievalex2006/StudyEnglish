package com.studyenglish.backend.models.api;

import com.studyenglish.backend.models.enums.Level;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sentences")
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "rus_text", nullable = false, length = 500)
    private String rusText;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 2)
    private Level level;
    @Column(columnDefinition = "TEXT")
    private String explanation;
    @Column(name = "external_key", nullable = false)
    private String externalKey;
    @OneToMany(mappedBy = "sentence", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private final List<SentenceTranslation> translations = new ArrayList<>();

    public void addTranslation(String engText, String normalizedText) {
        translations.add(SentenceTranslation.builder()
                .sentence(this)
                .engText(engText)
                .normalizedText(normalizedText)
                .build());
    }
}
