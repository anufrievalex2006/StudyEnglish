CREATE TABLE sentences
(
    id           uuid         NOT NULL,
    rus_text     varchar(500) NOT NULL,
    level        varchar(2)   NOT NULL,
    explanation  text,
    external_key varchar(255) NOT NULL,
    CONSTRAINT pk_sentences PRIMARY KEY (id),
    CONSTRAINT uq_sentences_external_key UNIQUE (external_key),
    CONSTRAINT chk_sentences_level CHECK (level IN ('A1', 'A2', 'B1'))
);

CREATE INDEX idx_sentences_level ON sentences (level);

CREATE TABLE sentence_translations
(
    id              uuid         NOT NULL,
    sentence_id     uuid         NOT NULL,
    eng_text        varchar(500) NOT NULL,
    normalized_text varchar(500) NOT NULL,
    CONSTRAINT pk_sentence_translations PRIMARY KEY (id),
    CONSTRAINT uq_sentence_translations_sentence_normalized
        UNIQUE (sentence_id, normalized_text),
    CONSTRAINT fk_sentence_translations_sentence FOREIGN KEY (sentence_id)
        REFERENCES sentences (id) ON DELETE CASCADE
);