package com.studyenglish.backend.services;

import com.studyenglish.backend.dtos.requests.sentence.SentenceCheckTranslationDto;
import com.studyenglish.backend.dtos.responses.SentenceCheckTranslationResponse;
import com.studyenglish.backend.dtos.responses.SentenceResponse;
import com.studyenglish.backend.exceptions.ApiException;
import com.studyenglish.backend.models.api.Sentence;
import com.studyenglish.backend.models.api.SentenceTranslation;
import com.studyenglish.backend.models.enums.Level;
import com.studyenglish.backend.repos.SentenceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TranslationService {
    private static final Pattern NORMALIZE_PATTERN = Pattern.compile("[^a-zA-Zа-яА-ЯёЁ0-9\\s]");

    private final SentenceRepo repo;

    public List<SentenceResponse> getSentences(Level level, int count) {
        List<Sentence> sentences = (level != null)
                ? repo.findByLevelRandom(level)
                : repo.findAllRandom();

        return sentences.stream()
                .limit(count)
                .map(s -> new SentenceResponse(
                        s.getId(),
                        s.getRusText(),
                        s.getLevel()
                ))
                .toList();
    }

    @Transactional
    public SentenceCheckTranslationResponse check(SentenceCheckTranslationDto req) {
        Sentence s = repo.findById(req.getSentenceId())
                .orElseThrow(() -> ApiException.notFound("Предложение не найдено"));

        String normalized = normalize(req.getAnswer());
        boolean isCorrect = s.getTranslations().stream()
                .anyMatch(t -> t.getNormalizedText().equals(normalized));
        if (isCorrect)
            return SentenceCheckTranslationResponse.success();

        String firstCorrect = s.getTranslations().stream()
                .map(SentenceTranslation::getEngText)
                .findFirst()
                .orElse("-");

        return SentenceCheckTranslationResponse.failure(firstCorrect, s.getExplanation());
    }

    private String normalize(String text) {
        if (text == null)
            return "";

        String lower = text.toLowerCase();
        String noPunct = NORMALIZE_PATTERN.matcher(lower).replaceAll("");
        return noPunct.trim().replaceAll("\\s+", "");
    }
}
