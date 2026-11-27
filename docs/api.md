# REST API к StudyEnglish

## Общие правила
- Базовый путь: `/api`. Формат: JSON, UTF-8. Идентификаторы типа `UUID`. Дата/время - по UTC, ISO-8601
- Авторизация:
    - Заголовок: `Authorization: Bearer <access-токен>`
    - Refresh-токен хранится в HttpOnly-куках `refresh_token` (Path = `/api/auth`)
- Колонка "Доступ":
    - **Публичный** - без токена
    - **Опционально** - без токена будет работать, но с токеном другое поведение
    - **Авторизованный** - любая роль
    - **STUDENT**, **ADMIN** - только эта роль, иначе 403 Forbidden
- Пагинация: параметры `page` (от 0) и `size` (по умолчанию 20, максимум = 100). Ответ приходит в виде: `{items, page, size, totalItems, totalPages}`
- Коды ответов:
    - 200 OK
    - 201 Created
    - 204 No Content
    - 400 Bad Request
    - 401 Unauthorized
    - 403 Forbidden
    - 404 Not Found
    - 409 Conflict

### Формат ошибки
Все ошибки возвращаются в следующем формате:
```json
{
    "status": 400,
    "code": "VALIDATION_ERROR",
    "message": "Проверьте введенные данные",
    "path": "/api/auth/register",
    "timestamp": "2026-11-21T04:47:00Z",
    "fieldErrors": [
        {
            "field": "email",
            "message": "Некорректный email"
        }
    ]
}
```
`fieldErrors` присутствует только при ошибке валидации. Поле `message` всегда на русском языке и показывается пользователю

Коды для `code`:
- `VALIDATION_ERROR`
- `INVALID_CREDS`
- `INVALID_TOKEN`
- `ACCESS_DENIED`
- `NOT_FOUND`
- `CONFLICT`

## 1. Авторизация
| Метод | Путь | Доступ | Запрос | Успешный ответ | Ошибки |
|---|---|---|---|---|---|
| POST | `/api/auth/register` | Публичный | `{name, nickname, email, password}` | 201 `{id, name, nickname, email, role}` | 400; 409 (email или никнейм заняты) |
| POST | `/api/auth/login` | Публичный | `{email, password, rememberMe}` | 200 `{accessToken, expiresIn}` + cookie `refresh_token` | 400; 401 |
| POST | `/api/auth/refresh` | Публичный (нужна cookie) | — | 200 `{accessToken, expiresIn}` + новая cookie | 401 |
| POST | `/api/auth/logout` | Публичный (нужна cookie) | — | 204; refresh-токен отозван, cookie удалена | — |

Правила валидации:
- `name` 1–100 символов;
- `nickname` 3–50 символов, только латиница, цифры и `_`;
- `email` корректный, до 255 символов;
- `password` 8–72 символа

Email и никнейм сравниваются без учёта регистра.

Cookie `refresh_token`: при `rememberMe = true` живёт 30 дней, иначе сессионная.

## 2. Профиль

| Метод | Путь | Доступ | Запрос | Успешный ответ | Ошибки |
|---|---|---|---|---|---|
| GET | `/api/users/me` | Авторизованный | — | 200 `{id, name, nickname, email, role, createdAt}` | 401 |
| PATCH | `/api/users/me` | Авторизованный | `{name?, nickname?}` | 200 профиль | 400; 401; 409 (никнейм занят) |
| PUT | `/api/users/me/password` | Авторизованный | `{currentPassword, newPassword}` | 204; все refresh-токены пользователя отозваны | 400 (текущий пароль неверен или новый не проходит валидацию); 401 |

## 3. Словарь

| Метод | Путь | Доступ | Запрос | Успешный ответ | Ошибки |
|---|---|---|---|---|---|
| GET | `/api/dictionary/words` | Публичный | `query` (1–100 символов, обязателен), `page`, `size` | 200 страница из `{id, engText, partOfSpeech, transcription, audioUrl, level, translations: [rusText]}` | 400 |

Язык запроса определяется автоматически: есть кириллица — ищем среди переводов, иначе среди английских слов. Поиск по началу слова, без учёта регистра. Сначала идут точные совпадения.

## 4. Тест на уровень

| Метод | Путь | Доступ | Запрос | Успешный ответ | Ошибки |
|---|---|---|---|---|---|
| POST | `/api/placement/next` | Опционально | `{answers: [{questionId, optionId}]}` (пустой список в начале теста) | 200 `{done: false, number, question: {id, text, options: [{id, text}]}}` или 200 `{done: true, level, correct, total}` | 400 |
| GET | `/api/placement/results/latest` | STUDENT | — | 200 `{level, correct, total, completedAt}` | 404 (тест не пройден) |

Тест хранит состояние в браузере: в каждом запросе отправляется весь список ответов. В вариантах ответа признака `correct` нет. Ошибка 400 возвращается, если ответов больше 20, вопрос повторяется или вариант не принадлежит вопросу. Если в запросе есть валидный токен, результат (при `done: true`) сохраняется.

## 5. Тренировка перевода

| Метод | Путь | Доступ | Запрос | Успешный ответ | Ошибки |
|---|---|---|---|---|---|
| GET | `/api/translation/sentences` | Публичный | `level?` (A1, A2, B1), `count?` (1–20, по умолчанию 20) | 200 `[{id, rusText, level}]` | 400 |
| POST | `/api/translation/check` | Публичный | `{sentenceId, answer}` | 200 `{correct: true}` или `{correct: false, correctAnswer, explanation?}` | 400; 404 |

Предложения выдаются в случайном порядке без повторов внутри одной порции. Лимит гостя (20 предложений за сессию) обеспечивает интерфейс, сервер ограничивает размер порции. Ответ проверяется после нормализации (регистр, пробелы, пунктуация).

## 6. Темы, упражнения, прогресс

| Метод | Путь | Доступ | Запрос | Успешный ответ | Ошибки |
|---|---|---|---|---|---|
| GET | `/api/topics` | STUDENT | `name?`, `type?` (GRAMMAR, VOCABULARY), `level?` | 200 `[{id, title, type, level, status, solvedCount, totalCount}]` | 401; 403 |
| GET | `/api/topics/{id}` | STUDENT | — | 200 `{id, title, type, level, theory, status, exercises: [{id, type, prompt, position, solved, options?: [{id, text}]}]}` | 401; 403; 404 |
| POST | `/api/exercises/{id}/answer` | STUDENT | `{optionId}` для Choice, `{text}` для GapFill и Translation | 200 `{correct: true, topicStatus}` или `{correct: false, explanation, correctAnswer}` | 400; 401; 403; 404 |
| GET | `/api/progress` | STUDENT | — | 200 `{totalTopics, completedTopics, inProgressTopics, notStartedTopics}` | 401; 403 |

`status` темы: `NOT_STARTED`, `IN_PROGRESS`, `COMPLETED`. Каталог тем возвращается целиком (без пагинации), отсортированный по уровню и названию, фильтр по `name` ищет по подстроке без учёта регистра. Упражнения в теме идут по `position`. В пропущенном слове (GapFill) пропуск в `prompt` обозначается `___`. Варианты (`options`) есть только у упражнений типа Choice и не содержат признака `correct`.

## 7. Администрирование

| Метод | Путь | Доступ | Запрос | Успешный ответ | Ошибки |
|---|---|---|---|---|---|
| GET | `/api/admin/users` | ADMIN | `role?` (STUDENT, TEACHER, ADMIN), `name?`, `page`, `size` | 200 страница из `{id, name, nickname, email, role, createdAt}` | 401; 403 |
| PATCH | `/api/admin/users/{id}/role` | ADMIN | `{role}` (STUDENT или TEACHER) | 200 пользователь | 400 (недопустимая роль, либо у пользователя роль ADMIN); 401; 403; 404 |

Фильтр `name` ищет по подстроке в имени или никнейме без учёта регистра.

## 8. Тренажёр слов

| Метод | Путь | Доступ | Запрос | Успешный ответ | Ошибки |
|---|---|---|---|---|---|
| GET | `/api/trainer/words` | Публичный | `level?` (A1, A2, B1), `count?` (1–20, по умолчанию 10) | 200 `[{id, direction, prompt, transcription, partOfSpeech, level, options: [string]}]` | 400; 404 |
| POST | `/api/trainer/words/check` | Публичный | `{wordId, direction, answer}` | 200 `{correct: true}` или `{correct: false, correctAnswer, translations}` | 400; 404 |

`direction`: `ENG_TO_RUS` (prompt — английское слово, options — русские переводы) или `RUS_TO_ENG` (prompt — русский перевод, options — английские слова; transcription при этом null, чтобы не подсказывать ответ). Карточки в порции не повторяются, направления перемешаны, options — 4 варианта без признака правильности. Ответ проверяется после нормализации (регистр, ё/е, пробелы, пунктуация): для ENG_TO_RUS верным считается любой из переводов слова, для RUS_TO_ENG — совпадение с engText.