-- V7: чистка вики-разметки WikDict из переводов и транскрипций.

DELETE
FROM word_translations a
    USING word_translations b
WHERE a.word_id = b.word_id
  AND a.id > b.id
  AND (a.rus_text LIKE '%[[%' OR b.rus_text LIKE '%[[%')
  AND btrim(regexp_replace(
        regexp_replace(a.rus_text, '\[\[[^|\]]*\|([^\]]*)\]\]', '\1', 'g'),
        '\[\[([^\]]*)\]\]', '\1', 'g'))
    = btrim(regexp_replace(
            regexp_replace(b.rus_text, '\[\[[^|\]]*\|([^\]]*)\]\]', '\1', 'g'),
            '\[\[([^\]]*)\]\]', '\1', 'g'));

UPDATE word_translations
SET rus_text = btrim(regexp_replace(
        regexp_replace(rus_text, '\[\[[^|\]]*\|([^\]]*)\]\]', '\1', 'g'),
        '\[\[([^\]]*)\]\]', '\1', 'g'))
WHERE rus_text LIKE '%[[%';

DELETE
FROM word_translations
WHERE rus_text = '';

UPDATE words
SET transcription = regexp_replace(transcription, '^(/[^/]*/).*$', '\1')
WHERE transcription LIKE '%<%'
  AND transcription ~ '^/';