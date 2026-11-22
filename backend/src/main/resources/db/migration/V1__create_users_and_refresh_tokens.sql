CREATE TABLE users
(
    id            uuid         NOT NULL,
    name          varchar(100) NOT NULL,
    nickname      varchar(50)  NOT NULL,
    email         varchar(255) NOT NULL,
    password_hash varchar(255) NOT NULL,
    role          varchar(20)  NOT NULL,
    created_at    timestamptz  NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT chk_users_role CHECK ( role IN ('STUDENT', 'TEACHER', 'ADMIN'))
);

CREATE UNIQUE INDEX uq_users_email_lower ON users (lower(email));
CREATE UNIQUE INDEX uq_users_nickname_lower ON users (lower(nickname));

CREATE TABLE refresh_tokens
(
    id         uuid        NOT NULL,
    user_id    uuid        NOT NULL,
    token_hash varchar(64) NOT NULL,
    expires_at timestamptz NOT NULL,
    persistent boolean     NOT NULL,
    revoked    boolean     NOT NULL DEFAULT false,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uq_refresh_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);