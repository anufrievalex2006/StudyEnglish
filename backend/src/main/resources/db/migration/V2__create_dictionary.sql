CREATE TABLE words
(
    id             uuid         NOT NULL,
    eng_text       varchar(100) NOT NULL,
    part_of_speech varchar(20)  NOT NULL,
    transcription  varchar(100),
    audio_url      varchar(500),
    level          varchar(2),
    external_key   varchar(255) NOT NULL,
    CONSTRAINT pk_words PRIMARY KEY (id),
    CONSTRAINT uq_words_external_key UNIQUE (external_key),
    CONSTRAINT chk_words_part_of_speech CHECK (part_of_speech IN (
                                                                  'NOUN', 'VERB', 'ADJECTIVE', 'ADVERB', 'PRONOUN',
                                                                  'PREPOSITION', 'CONJUNCTION', 'INTERJECTION', 'OTHER'
        )),
    CONSTRAINT chk_words_level CHECK (level IN ('A1', 'A2', 'B1'))
);

CREATE INDEX idx_words_eng_text_lower ON words (lower(eng_text) text_pattern_ops);

CREATE TABLE word_translations
(
    id       uuid         NOT NULL,
    word_id  uuid         NOT NULL,
    rus_text varchar(200) NOT NULL,
    CONSTRAINT pk_word_translations PRIMARY KEY (id),
    CONSTRAINT uq_word_translations_word_rus UNIQUE (word_id, rus_text),
    CONSTRAINT fk_word_translations_word FOREIGN KEY (word_id)
        REFERENCES words (id) ON DELETE CASCADE
);

CREATE INDEX idx_word_translations_rus_text_lower
    ON word_translations (lower(rus_text) text_pattern_ops);