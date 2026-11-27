"""Генерирует V6__seed_words.sql из words.json под схему миграции V2."""
import argparse, json, pathlib, uuid

POS_OK = {"NOUN", "VERB", "ADJECTIVE", "ADVERB", "PRONOUN",
          "PREPOSITION", "CONJUNCTION", "INTERJECTION", "OTHER"}
LEVELS = {"A1", "A2", "B1"}
NS = uuid.uuid5(uuid.NAMESPACE_URL, "https://studyenglish.local/words-v1")

def esc(s: str) -> str:
    return s.replace("'", "''")

def uid(seed: str) -> str:
    return str(uuid.uuid5(NS, seed))

def chunked_insert(table: str, cols: str, rows: list[str], chunk: int = 500) -> str:
    parts = []
    for i in range(0, len(rows), chunk):
        values = ",\n".join("    " + r for r in rows[i:i + chunk])
        parts.append(f"INSERT INTO {table} ({cols}) VALUES\n{values};")
    return "\n\n".join(parts)

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--in", dest="src", default="words.json")
    ap.add_argument("--out", default="V6__seed_words.sql")
    a = ap.parse_args()

    words = json.loads(pathlib.Path(a.src).read_text(encoding="utf-8"))

    errors, seen = [], set()
    for w in words:
        k = w["externalKey"]
        if k in seen:
            errors.append(f"дубль externalKey: {k}")
        seen.add(k)
        if w["partOfSpeech"] not in POS_OK:
            errors.append(f"{k}: неожиданный POS {w['partOfSpeech']}")
        if w["level"] not in LEVELS:
            errors.append(f"{k}: уровень {w['level']} вне A1-B1")
        if not w.get("translations"):
            errors.append(f"{k}: нет переводов")
        for t in w["translations"]:
            if len(t) > 200:
                errors.append(f"{k}: перевод длиннее 200: {t[:60]}...")
    if errors:
        raise SystemExit("Миграция не сгенерирована, исправь данные:\n" + "\n".join(errors[:30]))

    words.sort(key=lambda w: (w["level"], w["engText"].lower(), w["partOfSpeech"]))

    word_rows, tr_rows = [], []
    for w in words:
        wid = uid(w["externalKey"])
        tr = (w.get("transcription") or "").strip()
        transcription = f"'{esc(tr)}'" if tr else "NULL"
        word_rows.append(
            f"('{wid}', '{esc(w['engText'])}', '{w['partOfSpeech']}', "
            f"{transcription}, NULL, '{w['level']}', '{esc(w['externalKey'])}')"
        )
        for t in w["translations"]:
            tr_rows.append(f"('{uid(wid + ':' + t)}', '{wid}', '{esc(t)}')")

    header = (
        "-- V6: справочный словарь английских слов A1–B1 с русскими переводами.\n"
        "-- Источники: CEFR-J Vocabulary Profile 1.5 (Tono Laboratory, TUFS);\n"
        "-- FreeDict eng-rus / WikDict (данные Викисловаря через DBnary), CC BY-SA 3.0;\n"
        "-- плюс ручные дополнения. Сгенерировано tools/gen_v6_seed.py — не редактировать руками.\n"
    )
    sql = (
        header + "\n\n"
        + chunked_insert("words",
                         "id, eng_text, part_of_speech, transcription, audio_url, level, external_key",
                         word_rows)
        + "\n\n"
        + chunked_insert("word_translations", "id, word_id, rus_text", tr_rows)
        + "\n"
    )
    pathlib.Path(a.out).write_text(sql, encoding="utf-8")
    print(f"слов: {len(word_rows)}, переводов: {len(tr_rows)}")
    print(f"файл: {a.out}")

if __name__ == "__main__":
    main()