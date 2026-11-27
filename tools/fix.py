"""Финальная чистка words.json: формат externalKey и ключ переводов у ручных записей."""
import json, pathlib, unicodedata

DROP = {"'s"}  # клитик с мусорными переводами («ев, ов, ын») из FreeDict

path = pathlib.Path("words.json")
words = json.loads(path.read_text(encoding="utf-8"))
out, fixed_key, fixed_tr, dropped = [], 0, 0, 0

for w in words:
    if w["engText"].strip().lower() in DROP:
        dropped += 1
        continue
    if not w["externalKey"].startswith("cefrj:"):
        w["externalKey"] = f"cefrj:{unicodedata.normalize('NFC', w['engText'].strip().lower())}:{w['partOfSpeech']}"
        fixed_key += 1
    if "translation" in w:  # ручные записи: строка -> массив
        w["translations"] = [w.pop("translation")]
        fixed_tr += 1
    seen, trs = set(), []
    for t in w["translations"]:
        t = t.strip()
        if t and t.lower() not in seen:
            seen.add(t.lower())
            trs.append(t)
    w["translations"] = trs
    out.append(w)

keys = [w["externalKey"] for w in out]
assert len(keys) == len(set(keys)), "остались дубли externalKey!"
path.write_text(json.dumps(out, ensure_ascii=False, indent=1), encoding="utf-8")
print(f"было: {len(words)}, стало: {len(out)} (удалено мусорных: {dropped})")
print(f"externalKey приведён к cefrj:...: {fixed_key}, translation->translations: {fixed_tr}")
print("дубли externalKey: 0")