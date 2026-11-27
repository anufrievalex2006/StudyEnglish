import json, pathlib, unicodedata

MANUAL = {
    # --- предыдущие 36 ---
    "'m": "сокр. от am — быть, являться",
    "'re": "сокр. от are — быть, являться",
    "am": "быть (форма 1 л. ед. ч. наст. вр.)",
    "an": "неопределённый артикль (перед гласным звуком)",
    "bookstore": "книжный магазин",
    "cd": "компакт-диск",
    "could": "мог, могла; прош. вр. от can",
    "dancing": "танцы; танцевальный",
    "did": "прош. вр. от do — делать",
    "does": "делает (3 л. ед. ч. от do)",
    "glasses": "очки",
    "had": "прош. вр. от have — иметь",
    "has": "имеет (3 л. ед. ч. от have)",
    "is": "есть, является (3 л. ед. ч. от be)",
    "last name": "фамилия",
    "mom": "мама",
    "mommy": "мамочка",
    "photo": "фотография, фото",
    "trousers": "брюки",
    "was": "был, была (прош. вр. от be)",
    "were": "были (прош. вр. от be)",
    "would": "бы; прош. вр. от will",
    "aged": "в возрасте; пожилой",
    "alright": "в порядке, нормально (разг. = all right)",
    "amused": "развеселённый, позабавленный",
    "anymore": "больше (не), теперь",
    "been": "причастие прош. вр. от be",
    "blonde": "блондин(ка); светловолосый",
    "brainstorm": "мозговой штурм",
    "car park": "автостоянка, парковка",
    "climbing": "скалолазание",
    "drugstore": "аптека (амер.)",
    "elsewhere": "где-то в другом месте",
    "exam": "экзамен",
    "first name": "имя (до фамилии)",
    # --- A2, оставшиеся 27 ---
    "fridge": "холодильник",
    "granddad": "дедушка",
    "grocery store": "продуктовый магазин",
    "guidebook": "путеводитель",
    "half-price": "за полцены, со скидкой 50%",
    "headteacher": "директор школы (брит.)",
    "id card": "удостоверение личности",
    "importantly": "что важно; существенно",
    "latest": "самый последний, новейший",
    "left-hand": "левый, находящийся слева",
    "lyric": "лирический; (обычно lyrics) текст песни",
    "max": "максимум",
    "midday": "полдень",
    "pal": "приятель, дружок",
    "penfriend": "друг по переписке",
    "petrol": "бензин (брит.)",
    "petrol station": "автозаправочная станция, заправка",
    "policeman": "полицейский",
    "possibly": "возможно, может быть",
    "right-hand": "правый, находящийся справа",
    "schoolwork": "школьные занятия, учёба",
    "sitting room": "гостиная (брит.)",
    "snowboarding": "сноуборд, катание на сноуборде",
    "sports center": "спортивный центр (амер.; брит. centre)",
    "swimming costume": "купальник",
    "uneasy": "неспокойный, тревожный",
    "washing-up": "мытьё посуды",
    # --- B1, оставшиеся 114 ---
    "advert": "реклама, объявление (брит.)",
    "afterward": "потом, затем (амер. = afterwards)",
    "amazed": "поражённый, изумлённый",
    "anti": "против; противник (чего-либо)",
    "anxiously": "тревожно, с тревогой",
    "baggage": "багаж",
    "barman": "бармен",
    "bedside": "прикроватный; у постели (bedside table — тумбочка)",
    "behalf": "от имени (on behalf of — от имени, в интересах)",
    "brand-new": "совершенно новый, с иголочки",
    "cashpoint": "банкомат (брит.)",
    "chat show": "ток-шоу (брит.)",
    "columnist": "обозреватель, автор колонки",
    "contrary": "противоположный; on the contrary — наоборот",
    "counseling": "консультирование, психологическая помощь (амер.; брит. counselling)",
    "curiously": "с любопытством; как ни странно",
    "cv": "резюме, автобиография",
    "damaged": "повреждённый",
    "devastating": "разрушительный, сокрушительный",
    "dj": "диджей",
    "doc": "врач, доктор (разг.)",
    "dustbin": "мусорный бак, мусорное ведро (брит.)",
    "eagerness": "рвение, горячее желание",
    "eco": "экологичный, экологический",
    "electrical": "электрический",
    "engage": "вовлекать, привлекать; нанимать",
    "excitedly": "взволнованно, с воодушевлением",
    "extreme sports": "экстремальные виды спорта",
    "face-to-face": "лицом к лицу, лично",
    "faraway": "далёкий, отдалённый",
    "farming": "сельское хозяйство, фермерство",
    "farther": "дальше, далее",
    "farthest": "самый дальний; дальше всего",
    "film-maker": "кинорежиссёр, кинематографист",
    "finding": "находка; (обычно findings) выводы, результаты",
    "first-floor": "на первом этаже (амер.; брит. first floor — наш второй)",
    "frustrated": "разочарованный, досадующий",
    "furthest": "самый далёкий; дальше всего",
    "geographical": "географический",
    "gifted": "одарённый, талантливый",
    "grouping": "группировка, группирование",
    "guidance": "руководство, наставление",
    "hand-held": "ручной, портативный",
    "hard-working": "трудолюбивый, усердный",
    "hearted": "(в сочетаниях) ...-сердечный (kind-hearted — добрый)",
    "horrify": "ужасать, приводить в ужас",
    "hurriedly": "второпях, поспешно",
    "innermost": "самый сокровенный; внутренний",
    "isle": "остров (поэт.)",
    "lab": "лаборатория (разг.)",
    "lasting": "прочный, долговременный",
    "lately": "недавно, в последнее время",
    "likeness": "сходство; портрет",
    "lorry": "грузовик (брит.)",
    "mailbox": "почтовый ящик (амер.)",
    "marked": "заметный, выраженный; отмеченный",
    "mixing bowl": "миска для смешивания",
    "next-door": "соседний; живущий по соседству",
    "noticeboard": "доска объявлений (брит.)",
    "onstage": "на сцене",
    "ought to": "следует, должен (модальный глагол)",
    "out-of-date": "устаревший, просроченный",
    "outweigh": "перевешивать, превышать (по значимости)",
    "overjoyed": "вне себя от радости",
    "packing": "упаковка; укладка (вещей)",
    "paid": "оплаченный; платный",
    "permanently": "навсегда, постоянно",
    "pleasantly": "приятно",
    "positively": "положительно; определённо",
    "postman": "почтальон (брит.)",
    "quake": "землетрясение (разг.); трясти(сь)",
    "railroad": "железная дорога (амер.)",
    "reclaim": "возвращать себе; перерабатывать (отходы)",
    "recycled": "переработанный (об отходах)",
    "refreshments": "лёгкое угощение, напитки и закуски",
    "regain": "возвращать себе, вновь обретать",
    "relate": "соотносить; рассказывать; быть связанным (relate to)",
    "repay": "возвращать (долг); отплачивать",
    "reunify": "воссоединять, объединять вновь",
    "rolling": "катящийся; волнистый (rolling hills)",
    "schoolteacher": "школьный учитель",
    "second-hand": "подержанный, б/у; из вторых рук",
    "social networking": "социальные сети, общение в соцсетях",
    "socially": "социально; в обществе",
    "someplace": "где-то, где-нибудь (амер.)",
    "sports": "спортивный (в сочетаниях: sports club)",
    "sportsmanship": "спортивное мастерство; дух честной игры",
    "steadily": "устойчиво, неуклонно",
    "strangely": "странно",
    "stricken": "поражённый, охваченный (бедой)",
    "surrounding": "окружающий",
    "takeaway": "еда навынос; кафе навынос (брит.)",
    "teammate": "товарищ по команде",
    "telecommunications": "телекоммуникации, электросвязь",
    "terrified": "напуганный, в ужасе",
    "totally": "полностью, совершенно",
    "travel agent": "турагент; туристическое агентство",
    "turning": "поворот (turning point — переломный момент)",
    "typically": "обычно, как правило",
    "unfairly": "несправедливо",
    "unfit": "непригодный; нетренированный",
    "uninterested": "незаинтересованный, равнодушный",
    "unrelated": "не связанный (с чем-либо)",
    "uplifted": "приподнятый; воодушевлённый",
    "vary": "различаться, варьироваться; менять",
    "viewpoint": "точка зрения",
    "violently": "бурно, яростно",
    "visually": "визуально, зрительно",
    "well-dressed": "элегантно одетый",
    "whichever": "какой бы ни; любой из",
    "wildly": "бешено, дико; чрезвычайно",
    "willingness": "готовность, желание",
    "windscreen": "лобовое стекло (брит.)",
    "worn": "изношенный, поношенный; worn out — измотанный",
}

def norm(s: str) -> str:
    return unicodedata.normalize("NFC", s.replace("\u0301", "")).strip().lower()

words_path = pathlib.Path("words.json")
missing_path = pathlib.Path("missing.txt")

words = json.loads(words_path.read_text(encoding="utf-8"))
id = {(w["engText"].strip().lower(), w["partOfSpeech"]): w for w in words}

patched, missing = [], []
for l in missing_path.read_text(encoding="utf-8").splitlines():
    l = l.strip()
    if not l:
        continue

    level, pos, word = l.split("\t")
    tr = MANUAL.get(norm(word))
    if tr is None:
        missing.append(l)
        continue

    w = id.get((norm(word), pos))
    if w is None:
        w = {
            "externalKey": norm(word),
            "engText": word,
            "partOfSpeech": pos,
            "transcription": "",
            "level": level,
            "translation": tr
        }
        words.append(w)
    else:
        w["translation"] = tr
    patched.append(word)

words_path.write_text(json.dumps(words, ensure_ascii=False, indent=2), encoding="utf-8")
missing_path.write_text("\n".join(missing) + ("\n" if missing else ""), encoding="utf-8")
print(f"дописано вручную: {len(patched)}")
print(f"осталось без перевода: {len(missing)}")
for l in missing:
    print("  ", l)