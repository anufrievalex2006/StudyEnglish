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
@Table(name = "placement_questions")
public class PlacementQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String text;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 2)
    private Level level;
    @Column
    private String explanation;
    @Column(name = "external_key", nullable = false, unique = true)
    private String externalKey;
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private final List<PlacementOption> options = new ArrayList<>();

    public void addOption(String text, boolean correct) {
        options.add(PlacementOption.builder()
                .text(text)
                .correct(correct)
                .question(this)
                .build());
    }
}
