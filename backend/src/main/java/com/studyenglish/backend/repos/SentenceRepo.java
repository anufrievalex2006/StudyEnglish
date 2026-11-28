package com.studyenglish.backend.repos;

import com.studyenglish.backend.models.api.Sentence;
import com.studyenglish.backend.models.enums.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SentenceRepo extends JpaRepository<Sentence, UUID> {
    @Query("SELECT s FROM Sentence s ORDER BY function('RANDOM')")
    List<Sentence> findAllRandom();

    @Query("SELECT s FROM Sentence s WHERE s.level = :level ORDER BY function('RANDOM')")
    List<Sentence> findByLevelRandom(@Param("level") Level level);
}
