package com.studyenglish.backend.services;

import com.studyenglish.backend.dtos.responses.TrainerCardResponse;
import com.studyenglish.backend.dtos.responses.WordCheckResponse;
import com.studyenglish.backend.exceptions.ApiException;
import com.studyenglish.backend.models.api.Word;
import com.studyenglish.backend.models.api.WordTranslation;
import com.studyenglish.backend.models.enums.Level;
import com.studyenglish.backend.models.enums.TrainerDirection;
import com.studyenglish.backend.repos.WordRepo;
import com.studyenglish.backend.utilities.AnswerNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WordTrainerService {
    private static final int OPTIONS_PER_CARD = 4;
    private static final int DISTRACTOR_POOL_SIZE = 40;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final WordRepo repo;

    public List<TrainerCardResponse> quiz(Level level, int count) {
        List<UUID> ids = level == null ? repo.findAllIds() : repo.findIdsByLevel(level);
        if (ids.isEmpty())
            throw ApiException.notFound("Для этого уровня не нашлось слов в словаре");

        Collections.shuffle(ids, RANDOM);
        int mainCount = Math.min(count, ids.size());
        List<UUID> mainIds = new ArrayList<>(ids.subList(0, mainCount));
        List<UUID> extraIds = ids.subList(mainCount,
                Math.min(ids.size(), mainCount + DISTRACTOR_POOL_SIZE));

        Map<UUID, Word> byId = new HashMap<>();
        List<UUID> all = new ArrayList<>(mainIds);
        all.addAll(extraIds);
        for (Word w : repo.findAllWithTranslations(all))
            byId.put(w.getId(), w);

        List<String> rusPool = new ArrayList<>(), engPool = new ArrayList<>();
        for (UUID e : extraIds) {
            Word w = byId.get(e);
            if (w == null)
                continue;

            w.getTranslations().forEach(t -> rusPool.add(t.getRusText()));
            engPool.add(w.getEngText());
        }
        Collections.shuffle(rusPool, RANDOM);
        Collections.shuffle(engPool, RANDOM);
        List<TrainerCardResponse> cards = new ArrayList<>(mainCount);
        for (UUID id : mainIds)
            cards.add(buildCard(byId.get(id), rusPool, engPool));
        return cards;
    }

    public WordCheckResponse check(UUID wordId, TrainerDirection direction, String answer) {
        Word w = repo.findWithTranslations(wordId)
                .orElseThrow(() -> ApiException.notFound("Слово не найдено"));

        List<String> translations = w.getTranslations().stream()
                .map(WordTranslation::getRusText)
                .toList();
        String given = AnswerNormalizer.normalize(answer);
        boolean isRight = switch (direction) {
            case ENG_TO_RUS -> translations.stream()
                    .anyMatch(t -> AnswerNormalizer.normalize(t).equals(given));
            case RUS_TO_ENG -> AnswerNormalizer.normalize(w.getEngText()).equals(given);
        };
        return isRight ? WordCheckResponse.right() : WordCheckResponse.wrong(
                direction == TrainerDirection.ENG_TO_RUS ? translations.getFirst() : w.getEngText(),
                translations
        );
    }

    private TrainerCardResponse buildCard(Word w, List<String> rusPool, List<String> engPool) {
        List<String> ownTranslations = w.getTranslations().stream()
                .map(WordTranslation::getRusText)
                .toList();
        boolean engToRus = RANDOM.nextBoolean();

        String prompt, transcription;
        List<String> options;
        if (engToRus) {
            String correct = ownTranslations.get(RANDOM.nextInt(ownTranslations.size()));
            prompt = w.getEngText();
            transcription = w.getTranscription();
            options = buildOptions(correct, new HashSet<>(ownTranslations), rusPool);
        } else {
            prompt = ownTranslations.get(RANDOM.nextInt(ownTranslations.size()));
            transcription = null;
            options = buildOptions(w.getEngText(), Set.of(w.getEngText()), engPool);
        }
        return TrainerCardResponse.builder()
                .id(w.getId())
                .direction(engToRus ? TrainerDirection.ENG_TO_RUS : TrainerDirection.RUS_TO_ENG)
                .prompt(prompt)
                .transcription(transcription)
                .partOfSpeech(w.getPartOfSpeech())
                .level(w.getLevel())
                .options(options)
                .build();
    }

    private List<String> buildOptions(String correct, Set<String> excluded, List<String> pool) {
        Set<String> options = new LinkedHashSet<>();
        options.add(correct);
        for (String cand : pool) {
            if (options.size() == OPTIONS_PER_CARD)
                break;

            boolean ownTranslation = excluded.stream()
                    .anyMatch(t -> t.equalsIgnoreCase(cand));
            if (ownTranslation || cand.equalsIgnoreCase(correct))
                continue;
            options.add(cand);
        }
        List<String> list = new ArrayList<>(options);
        Collections.shuffle(list, RANDOM);
        return list;
    }
}
