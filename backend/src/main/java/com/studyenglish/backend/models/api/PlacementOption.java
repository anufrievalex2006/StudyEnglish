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
@Table(name = "placement_options")
public class PlacementOption {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private PlacementQuestion question;
    @Column(nullable = false, length = 300)
    private String text;
    @Column(nullable = false)
    private boolean correct;
}
