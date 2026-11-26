package com.studyenglish.backend.dtos.responses;

import com.studyenglish.backend.models.enums.Level;
import com.studyenglish.backend.models.enums.PartOfSpeech;
import com.studyenglish.backend.models.api.Word;
import com.studyenglish.backend.models.api.WordTranslation;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WordResponse {
    private UUID id;
    private String engText;
    private PartOfSpeech partOfSpeech;
    private String transcription;
    private String audioUrl;
    private Level level;
    private List<String> translations;

    public static WordResponse from(Word w) {
        List<String> translations = w.getTranslations().stream()
                .map(WordTranslation::getRusText)
                .sorted()
                .toList();
        return new WordResponse(
                w.getId(),
                w.getEngText(),
                w.getPartOfSpeech(),
                w.getTranscription(),
                w.getAudioUrl(),
                w.getLevel(),
                translations
        );
    }
}
