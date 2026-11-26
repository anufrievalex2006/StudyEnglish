package com.studyenglish.backend.repos;

import com.studyenglish.backend.models.api.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface WordRepo extends JpaRepository<Word, UUID> {
    @Query(value = """
            select w from Word w
            where lower(w.engText) like :pattern escape '!'
            order by case when lower(w.engText) = :exact then 0 else 1 end,
                     lower(w.engText), w.partOfSpeech, w.id
            """,
            countQuery = """
                    select count(w) from Word w
                    where lower(w.engText) like :pattern escape '!'
                    """)
    Page<Word> searchByEnglish(@Param("exact") String exact,
                               @Param("pattern") String pattern,
                               Pageable pageable);

    @Query(value = """
            select w from Word w
            where exists (select 1 from WordTranslation t
                          where t.word = w and lower(t.rusText) like :pattern escape '!')
            order by case when exists (select 1 from WordTranslation t2
                                       where t2.word = w and lower(t2.rusText) = :exact)
                          then 0 else 1 end,
                     lower(w.engText), w.partOfSpeech, w.id
            """,
            countQuery = """
                    select count(w) from Word w
                    where exists (select 1 from WordTranslation t
                                  where t.word = w and lower(t.rusText) like :pattern escape '!')
                    """)
    Page<Word> searchByTranslation(@Param("exact") String exact,
                                   @Param("pattern") String pattern,
                                   Pageable pageable);
}
