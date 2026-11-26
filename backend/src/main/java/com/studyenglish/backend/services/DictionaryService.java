package com.studyenglish.backend.services;

import com.studyenglish.backend.dtos.responses.PageResponse;
import com.studyenglish.backend.exceptions.ApiException;
import com.studyenglish.backend.dtos.responses.WordResponse;
import com.studyenglish.backend.models.api.Word;
import com.studyenglish.backend.repos.WordRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DictionaryService {
    private final WordRepo repo;

    public PageResponse<WordResponse> search(String query, int page, int size) {
        String t = query.trim();
        if (t.isEmpty())
            throw ApiException.badRequest("Введите слово для поиска");

        String exact = t.toLowerCase(Locale.ROOT);
        String pattern = escapeLike(exact) + "%";
        PageRequest pageable = PageRequest.of(page, size);

        Page<Word> res = containsCyrillic(t)
                ? repo.searchByTranslation(exact, pattern, pageable)
                : repo.searchByEnglish(exact, pattern, pageable);
        return PageResponse.from(res, WordResponse::from);
    }

    private static boolean containsCyrillic(String txt) {
        return txt.codePoints()
                .anyMatch(c -> Character.UnicodeScript.of(c) == Character.UnicodeScript.CYRILLIC);
    }

    private static String escapeLike(String txt) {
        return txt
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
    }
}
