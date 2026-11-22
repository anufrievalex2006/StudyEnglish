CREATE TABLE topics
(
    id           uuid         NOT NULL,
    title        varchar(200) NOT NULL,
    type         varchar(20)  NOT NULL,
    level        varchar(2)   NOT NULL,
    theory       text         NOT NULL,
    external_key varchar(255) NOT NULL,
    CONSTRAINT pk_topics PRIMARY KEY (id),
    CONSTRAINT uq_topics_external_key UNIQUE (external_key),
    CONSTRAINT chk_topics_type CHECK (type IN ('GRAMMAR', 'VOCABULARY')),
    CONSTRAINT chk_topics_level CHECK (level IN ('A1', 'A2', 'B1'))
);

CREATE TABLE exercises
(
    id           uuid         NOT NULL,
    topic_id     uuid         NOT NULL,
    type         varchar(20)  NOT NULL,
    prompt       text         NOT NULL,
    explanation  text         NOT NULL,
    position     int          NOT NULL,
    external_key varchar(255) NOT NULL,
    CONSTRAINT pk_exercises PRIMARY KEY (id),
    CONSTRAINT uq_exercises_external_key UNIQUE (external_key),
    CONSTRAINT uq_exercises_topic_position UNIQUE (topic_id, position),
    CONSTRAINT chk_exercises_type CHECK (type IN ('CHOICE', 'GAP_FILL', 'TRANSLATION')),
    CONSTRAINT chk_exercises_position CHECK (position > 0),
    CONSTRAINT fk_exercises_topic FOREIGN KEY (topic_id)
        REFERENCES topics (id) ON DELETE CASCADE
);

CREATE TABLE exercise_options
(
    id          uuid         NOT NULL,
    exercise_id uuid         NOT NULL,
    text        varchar(300) NOT NULL,
    correct     boolean      NOT NULL,
    CONSTRAINT pk_exercise_options PRIMARY KEY (id),
    CONSTRAINT fk_exercise_options_exercise FOREIGN KEY (exercise_id)
        REFERENCES exercises (id) ON DELETE CASCADE
);

CREATE INDEX idx_exercise_options_exercise_id ON exercise_options (exercise_id);

CREATE UNIQUE INDEX uq_exercise_options_one_correct
    ON exercise_options (exercise_id) WHERE correct;

CREATE TABLE accepted_answers
(
    id              uuid         NOT NULL,
    exercise_id     uuid         NOT NULL,
    text            varchar(300) NOT NULL,
    normalized_text varchar(300) NOT NULL,
    CONSTRAINT pk_accepted_answers PRIMARY KEY (id),
    CONSTRAINT uq_accepted_answers_exercise_normalized
        UNIQUE (exercise_id, normalized_text),
    CONSTRAINT fk_accepted_answers_exercise FOREIGN KEY (exercise_id)
        REFERENCES exercises (id) ON DELETE CASCADE
);

CREATE TABLE solved_exercises
(
    id          uuid        NOT NULL,
    user_id     uuid        NOT NULL,
    exercise_id uuid        NOT NULL,
    solved_at   timestamptz NOT NULL,
    CONSTRAINT pk_solved_exercises PRIMARY KEY (id),
    CONSTRAINT uq_solved_exercises_user_exercise UNIQUE (user_id, exercise_id),
    CONSTRAINT fk_solved_exercises_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_solved_exercises_exercise FOREIGN KEY (exercise_id)
        REFERENCES exercises (id) ON DELETE CASCADE
);

CREATE INDEX idx_solved_exercises_exercise_id ON solved_exercises (exercise_id);