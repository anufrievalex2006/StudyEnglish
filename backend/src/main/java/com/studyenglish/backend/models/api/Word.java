package com.studyenglish.backend.models.api;

import com.studyenglish.backend.models.enums.Level;
import com.studyenglish.backend.models.enums.PartOfSpeech;
import jakarta.persistence.*;
import lombok.*;
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
@Table(name = "words")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 100)
    private String engText;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PartOfSpeech partOfSpeech;
    @Column(length = 100)
    private String transcription;
    @Column(length = 500)
    private String audioUrl;
    @Enumerated(EnumType.STRING)
    @Column(length = 2)
    private Level level;
    @Column(nullable = false)
    private String externalKey;
    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private final List<WordTranslation> translations = new ArrayList<>();

    public void addTranslation(String rusText) {
        translations.add(WordTranslation.builder()
                .rusText(rusText)
                .word(this)
                .build());
    }
}
