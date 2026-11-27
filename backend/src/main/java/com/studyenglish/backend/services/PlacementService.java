package com.studyenglish.backend.services;

import com.studyenglish.backend.dtos.requests.placement.PlacementAnswerDto;
import com.studyenglish.backend.dtos.responses.PlacementNextResponse;
import com.studyenglish.backend.dtos.responses.PlacementQuestionResponse;
import com.studyenglish.backend.dtos.responses.PlacementResultResponse;
import com.studyenglish.backend.exceptions.ApiException;
import com.studyenglish.backend.models.api.PlacementQuestion;
import com.studyenglish.backend.models.api.PlacementResult;
import com.studyenglish.backend.models.enums.Level;
import com.studyenglish.backend.repos.PlacementQuestionsRepo;
import com.studyenglish.backend.repos.PlacementResultRepo;
import com.studyenglish.backend.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PlacementService {
    private static final int TOTAL_QUESTIONS = 20;
    private static final int MAX_PER_LEVEL = 10;
    private static final Level START_LEVEL = Level.A2;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PlacementQuestionsRepo questionsRepo;
    private final PlacementResultRepo resultRepo;
    private final UserRepo repo;

    @Transactional
    public PlacementNextResponse next(List<PlacementAnswerDto> answers, UUID userId) {
        Map<UUID, PlacementQuestion> answered = loadAnswered(answers);
        Map<Level, List<UUID>> pool = loadPool(answered.keySet());

        Level diff = START_LEVEL;
        Map<Level, Integer> served = new EnumMap<>(Level.class);
        int correct = 0;

        for (PlacementAnswerDto a : answers) {
            Level servedLvl = pickLevel(diff, served, pool);
            served.merge(servedLvl, 1, Integer::sum);
            boolean isRight = isCorrect(answered.get(a.getQuestionId()), a.getOptionId());
            if (isRight)
                correct++;

            diff = isRight ? diff.harder() : diff.easier();
        }

        if (answers.size() == TOTAL_QUESTIONS) {
            Level resLvl = pickLevel(diff, served, pool);
            if (userId != null)
                saveResult(userId, resLvl, correct);
            return PlacementNextResponse.finished(resLvl, correct, TOTAL_QUESTIONS);
        }

        Level nextLvl = pickLevel(diff, served, pool);
        UUID nextId = nextLvl == null ? null : randomId(pool.get(nextLvl));
        PlacementQuestion q = questionsRepo.findWithOptions(nextId)
                .orElseThrow(() -> ApiException.notFound("Вопрос не найден"));
        return PlacementNextResponse.next(answers.size() + 1, PlacementQuestionResponse.from(q));
    }

    @Transactional(readOnly = true)
    public PlacementResultResponse latest(UUID userId) {
        PlacementResult res = resultRepo.findFirstByUserIdOrderByCompletedAtDesc(userId)
                .orElseThrow(() -> ApiException.notFound("Тест на уровень еще не пройден"));
        return PlacementResultResponse.from(res);
    }

    private Map<UUID, PlacementQuestion> loadAnswered(List<PlacementAnswerDto> answers) {
        List<UUID> ids = answers.stream().map(PlacementAnswerDto::getQuestionId).toList();
        Set<UUID> uniqueIds = new HashSet<>(ids);
        if (uniqueIds.size() < ids.size())
            throw ApiException.badRequest("Один и тот же вопрос встречается в ответах дважды");

        Map<UUID, PlacementQuestion> byId = new HashMap<>();
        for (PlacementQuestion q : questionsRepo.findAllWithOptions(uniqueIds))
            byId.put(q.getId(), q);
        if (!byId.keySet().containsAll(uniqueIds))
            throw ApiException.badRequest("Вопрос не найден");

        for (PlacementAnswerDto a : answers) {
            boolean isOwned = byId.get(a.getQuestionId()).getOptions().stream()
                    .anyMatch(o -> o.getId().equals(a.getOptionId()));
            if (!isOwned)
                throw ApiException.badRequest("Вариант ответа не относится к своему вопросу");
        }
        return byId;
    }

    private Map<Level, List<UUID>> loadPool(Set<UUID> answeredIds) {
        Map<Level, List<UUID>> pool = new EnumMap<>(Level.class);
        for (Level l : Level.values()) {
            List<UUID> ids = new ArrayList<>(questionsRepo.findIdsByLevel(l));
            ids.removeAll(answeredIds);
            pool.put(l, ids);
        }
        return pool;
    }

    private Level pickLevel(Level desired, Map<Level, Integer> served, Map<Level, List<UUID>> pool) {
        for (Level l : nearestFirst(desired)) {
            if (served.getOrDefault(l, 0) >= MAX_PER_LEVEL)
                continue;
            if (pool.get(l).isEmpty())
                continue;
            return l;
        }
        throw ApiException.badRequest("Вопросы в базе закончились - не хватает данных для теста");
    }

    private static List<Level> nearestFirst(Level desired) {
        Level[] all = Level.values();
        List<Level> order = new ArrayList<>();
        order.add(desired);
        for (int d = 1; d < all.length; d++) {
            if (desired.ordinal() + d < all.length)
                order.add(all[desired.ordinal() + d]);
            if (desired.ordinal() - d >= 0)
                order.add(all[desired.ordinal() - d]);
        }
        return order;
    }

    private static boolean isCorrect(PlacementQuestion q, UUID optionId) {
        return q.getOptions().stream().anyMatch(o -> o.getId().equals(optionId) && o.isCorrect());
    }

    private static UUID randomId(List<UUID> ids) {
        return ids.get(RANDOM.nextInt(ids.size()));
    }

    private void saveResult(UUID userId, Level level, int correct) {
        resultRepo.save(PlacementResult.builder()
                .user(repo.getReferenceById(userId))
                .level(level)
                .correctAnswersCount(correct)
                .totalQuestionsCount(TOTAL_QUESTIONS)
                .completedAt(Instant.now())
                .build());
    }
}
