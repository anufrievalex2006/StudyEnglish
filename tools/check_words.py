"""Валидация words.json перед импортом в БД."""
import json, pathlib, re
from collections import Counter

words = json.loads(pathlib.Path("words.json").read_text(encoding="utf-8"))
problems = []

keys = Counter(w["externalKey"] for w in words)
for k, n in keys.items():
    if n > 1:
        dups = [(w["engText"], w["partOfSpeech"]) for w in words if w["externalKey"] == k]
        problems.append(f"дубликат externalKey '{k}' x{n}: {dups}")

pair = Counter((w["engText"].lower(), w["partOfSpeech"]) for w in words)
for (e, p), n in pair.items():
    if n > 1:
        problems.append(f"дубликат ('{e}', {p}) x{n}")

LEVELS = {"A1", "A2", "B1"}
POS_OK = {"NOUN", "VERB", "ADJECTIVE", "ADVERB", "PRONOUN", "PREPOSITION", "CONJUNCTION", "INTERJECTION", "OTHER"}
for w in words:
    tag = f"{w.get('engText')!r} {w.get('partOfSpeech')}"
    if not w.get("externalKey"):
        problems.append(f"{tag}: пустой externalKey")
    if not w.get("engText"):
        problems.append(f"{tag}: пустой engText")
    if w.get("partOfSpeech") not in POS_OK:
        problems.append(f"{tag}: неожиданный POS '{w.get('partOfSpeech')}'")
    if w.get("level") not in LEVELS:
        problems.append(f"{tag}: уровень '{w.get('level')}' вне A1-B1")
    tr = w.get("translation") or ""
    if not tr.strip():
        problems.append(f"{tag}: пустой перевод")
    if len(tr) > 200:
        problems.append(f"{tag}: перевод {len(tr)} симв. (>200)")
    if re.search(r"[\u0301]", w.get("engText", "") + tr):
        problems.append(f"{tag}: осталось ударение U+0301")

by_level = Counter(w["level"] for w in words)
by_pos = Counter(w["partOfSpeech"] for w in words)
print(f"всего записей: {len(words)}")
print(f"по уровням: {dict(sorted(by_level.items()))}")
print(f"по POS: {dict(by_pos.most_common())}")
print(f"без транскрипции: {sum(1 for w in words if not (w.get('transcription') or '').strip())}")
print("переводы ручные не отличить от словарных — если нужен флаг, см. ниже")

if problems:
    print(f"\nПРОБЛЕМЫ: {len(problems)}")
    for p in problems[:50]:
        print("  ", p)
    if len(problems) > 50:
        print(f"  ... и ещё {len(problems) - 50}")
else:
    print("\nОК: проблем не найдено, можно импортировать")