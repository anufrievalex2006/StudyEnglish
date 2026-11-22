CREATE TABLE placement_questions
(
    id           uuid         NOT NULL,
    text         text         NOT NULL,
    level        varchar(2)   NOT NULL,
    explanation  text,
    external_key varchar(255) NOT NULL,
    CONSTRAINT pk_placement_questions PRIMARY KEY (id),
    CONSTRAINT uq_placement_questions_external_key UNIQUE (external_key),
    CONSTRAINT chk_placement_questions_level CHECK (level IN ('A1', 'A2', 'B1'))
);

CREATE INDEX idx_placement_questions_level ON placement_questions (level);

CREATE TABLE placement_options
(
    id          uuid         NOT NULL,
    question_id uuid         NOT NULL,
    text        varchar(300) NOT NULL,
    correct     boolean      NOT NULL,
    CONSTRAINT pk_placement_options PRIMARY KEY (id),
    CONSTRAINT fk_placement_options_question FOREIGN KEY (question_id)
        REFERENCES placement_questions (id) ON DELETE CASCADE
);

CREATE INDEX idx_placement_options_question_id ON placement_options (question_id);

CREATE UNIQUE INDEX uq_placement_options_one_correct
    ON placement_options (question_id) WHERE correct;

CREATE TABLE placement_results
(
    id                    uuid        NOT NULL,
    user_id               uuid        NOT NULL,
    level                 varchar(2)  NOT NULL,
    correct_answers_count int         NOT NULL,
    total_questions_count int         NOT NULL,
    completed_at          timestamptz NOT NULL,
    CONSTRAINT pk_placement_results PRIMARY KEY (id),
    CONSTRAINT fk_placement_results_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_placement_results_level CHECK (level IN ('A1', 'A2', 'B1')),
    CONSTRAINT chk_placement_results_counts CHECK (
        correct_answers_count >= 0
            AND total_questions_count > 0
            AND correct_answers_count <= total_questions_count
        )
);

CREATE INDEX idx_placement_results_user_completed
    ON placement_results (user_id, completed_at DESC);