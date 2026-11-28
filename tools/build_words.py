"""Собирает words.json из CEFR-J (слова, часть речи, уровень) и FreeDict eng-rus (переводы, транскрипция)."""
import csv, argparse, json, re, unicodedata, xml.etree.ElementTree as ET
from collections import Counter

NS = "{http://www.tei-c.org/ns/1.0}"
LEVELS = {"A1", "A2", "B1"}
MAX_TRANSLATIONS = 8
MAIN_POS = {"NOUN", "VERB", "ADJECTIVE", "ADVERB"}
CEFR_POS = {
    "noun": "NOUN", "verb": "VERB", "adjective": "ADJECTIVE", "adverb": "ADVERB",
    "pronoun": "PRONOUN", "preposition": "PREPOSITION", "conjunction": "CONJUNCTION",
    "interjection": "INTERJECTION",
    "modal auxiliary": "VERB", "be-verb": "VERB", "do-verb": "VERB", "have-verb": "VERB",
    "determiner": "OTHER", "number": "OTHER", "infinitive-to": "OTHER",
}
TEI_POS = {
    "n": "NOUN", "v": "VERB", "adj": "ADJECTIVE", "adv": "ADVERB",
    "pn": "NOUN", "properNoun": "NOUN",
    "pronoun": "PRONOUN", "preposition": "PREPOSITION", "postposition": "PREPOSITION",
    "conjunction": "CONJUNCTION", "interjection": "INTERJECTION",
    "determiner": "OTHER", "numeral": "OTHER", "particle": "OTHER",
}

def read_cefrj(path):
    words = {}
    for r in csv.DictReader(open(path, encoding="utf-8")):
        level = r["CEFR"].strip()
        pos = CEFR_POS.get(r["pos"].strip(), "OTHER")
        if level not in LEVELS:
            continue

        vs = [x.strip() for x in r["headword"].split("/") if x.strip()]
        if not vs:
            continue

        key = (vs[0].lower(), pos)
        if key in words:
            continue

        words[key] = {
            "eng": vs[0],
            "variants": [x.lower() for x in vs],
            "level": level,
            "pos": pos
        }
    return words

def clean(text):
    text = unicodedata.normalize("NFC", text.replace("\u0301", "")).strip()
    text = re.sub(r"\[\[[^\]]*\]\]", lambda m: m.group(0)[2:-2].split("|")[-1].strip(), text)
    return text

def read_tei(path, needed):
    """needed: множество слов (lower), нужных нам. Возвращает {(слово, POS|None): {...}}."""
    id = {}
    unknown = Counter()
    for _, el in ET.iterparse(path, events=("end",)):
        if el.tag != NS + "entry":
            continue

        orth = el.findtext(f"{NS}form/{NS}orth")
        if orth and orth.strip().lower() in needed:
            w = orth.strip().lower()
            pos = el.findtext(f"{NS}gramGrp/{NS}pos")
            p = TEI_POS.get(pos)
            if pos and p is None:
                unknown[pos] += 1

            pron = el.findtext(f"{NS}form/{NS}pron")
            quotes = [clean(q.text) for q in el.iterfind(f".//{NS}cit/{NS}quote") if q.text and q.text.strip()]
            item = id.setdefault((w,p), {
                "pron": None,
                "trans": []
            })
            if item["pron"] is None and pron:
                m = re.search(r"/[^/]+/", pron)
                item["pron"] = m.group(0) if m else None
            item["trans"].extend(quotes)
        el.clear()
    return id, unknown

def pick_translations(items):
    seen, res = set(), []
    for t in items:
        low = t.lower()
        if not t or low in seen or len(t) > 200:
            continue

        seen.add(low)
        res.append(t)
        if len(res) == MAX_TRANSLATIONS:
            break
    return res

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--cefrj", required=True)
    ap.add_argument("--tei", required=True)
    ap.add_argument("--out", default="words.json")
    ap.add_argument("--missing", default="missing.txt")
    a = ap.parse_args()

    cefr = read_cefrj(a.cefrj)
    needed = {v for w in cefr.values() for v in w["variants"]}
    id, unknown = read_tei(a.tei, needed)

    out, missing, by_pos, by_any = [], [], 0, 0
    for (word, pos), w in sorted(cefr.items(), key=lambda kv: (kv[1]["level"], kv[0])):
        entries = [id[(v, pos)] for v in w["variants"] if (v, pos) in id]
        if entries:
            by_pos += 1
        else:
            ALL_POS = ("NOUN", "VERB", "ADJECTIVE", "ADVERB", "PRONOUN", "PREPOSITION",
                    "CONJUNCTION", "INTERJECTION", "OTHER")
            fallback = (None,) + ALL_POS
            entries = [id[(v,p)] for v in w["variants"] for p in fallback if (v,p) in id]
            by_any += bool(entries)
        trans = pick_translations([t for e in entries for t in e["trans"]])
        if not trans:
            missing.append(f'{w["level"]}\t{pos}\t{w["eng"]}')
            continue

        pron = next((e["pron"] for e in entries if e["pron"]), None)
        out.append({
            "externalKey": f"cefrj:{word}:{pos}",
            "engText": w["eng"][:100],
            "partOfSpeech": pos,
            "transcription": pron[:100] if pron else None,
            "level": w["level"],
            "translations": trans
        })

    with open(a.out, "w", encoding="utf-8") as f:
        json.dump(out, f, ensure_ascii=False, indent=1)
    with open(a.missing, "w", encoding="utf-8") as f:
        f.write("\n".join(missing))

    print(f"слов в CEFR-J (A1-B1): {len(cefr)}")
    print(f"записано: {len(out)} (точно по части речи: {by_pos}, по другой части речи: {by_any})")
    print(f"без перевода: {len(missing)} -> {a.missing}")
    if unknown:
        print("неизвестные теги pos в словаре:", dict(unknown))

if __name__ == "__main__":
    main()