package com.studyenglish.backend.repos;

import com.studyenglish.backend.models.api.PlacementQuestion;
import com.studyenglish.backend.models.enums.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlacementQuestionsRepo extends JpaRepository<PlacementQuestion, UUID> {
    @Query("""
            select distinct q from PlacementQuestion q
            left join fetch q.options
            where q.id in :ids
            """)
    List<PlacementQuestion> findAllWithOptions(@Param("ids") Collection<UUID> ids);
    @Query("""
            select q from PlacementQuestion q
            left join fetch q.options
            where q.id = :id
            """)
    Optional<PlacementQuestion> findWithOptions(@Param("id") UUID id);
    @Query("select q.id from PlacementQuestion q where q.level = :level")
    List<UUID> findIdsByLevel(@Param("level") Level level);
}
