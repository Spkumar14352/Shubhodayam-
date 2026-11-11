package com.example.data.numerology

/**
 * Numerology meanings, planetary rulerships, compatibility tables and tips for numbers 1 to 9.
 * Original, positive, non-fear-based Vedic & Chaldean wisdom.
 */
data class NumerologyNumberInfo(
    val number: Int,
    val planetNameTe: String,
    val planetNameEn: String,
    val deityTe: String,
    val deityEn: String,
    val natureTe: String,
    val natureEn: String,
    val strengthsTe: List<String>,
    val strengthsEn: List<String>,
    val toWatchTe: List<String>,
    val toWatchEn: List<String>,
    val luckyDaysTe: List<String>,
    val luckyDaysEn: List<String>,
    val luckyColorsTe: List<String>,
    val luckyColorsEn: List<String>,
    val luckyNumbers: List<Int>,
    val friendlyNumbers: List<Int>,
    val neutralNumbers: List<Int>,
    val challengingNumbers: List<Int>,
    val gemstoneTe: String,
    val gemstoneEn: String
)

enum class CompatibilityStatus {
    EXCELLENT,
    GOOD,
    NEUTRAL,
    NEEDS_ATTENTION
}

data class CompatibilityResult(
    val status: CompatibilityStatus,
    val explanationTe: String,
    val explanationEn: String
)

object NumerologyData {

    val NUMBERS_MAP: Map<Int, NumerologyNumberInfo> = mapOf(
        1 to NumerologyNumberInfo(
            number = 1,
            planetNameTe = "సూర్యుడు (Sun)",
            planetNameEn = "Sun (Surya)",
            deityTe = "సూర్య భగవానుడు",
            deityEn = "Lord Surya",
            natureTe = "నాయకత్వ లక్షణాలు, ఆత్మవిశ్వాసం, సృజనాత్మక ఆలోచనలు మరియు స్వయం ప్రకాశం కలవారు. అనుకున్న లక్ష్యాన్ని సాధించడంలో అగ్రగామిగా నిలుస్తారు.",
            natureEn = "Natural leaders with immense vitality, self-confidence, inventive mind and pioneering spirit. Born to guide and inspire others.",
            strengthsTe = listOf("స్వతంత్ర భావాలు", "దృఢ సంకల్పం", "నాయకత్వం", "సమయపాలన"),
            strengthsEn = listOf("Independence", "Determination", "Leadership", "Originality"),
            toWatchTe = listOf("అతి ఆత్మవిశ్వాసం నియంత్రించుకోండి", "ఇతరుల అభిప్రాయాలను గౌరవించండి"),
            toWatchEn = listOf("Patience with subordinates", "Avoid over-assertiveness"),
            luckyDaysTe = listOf("ఆదివారం", "సోమవారం"),
            luckyDaysEn = listOf("Sunday", "Monday"),
            luckyColorsTe = listOf("బంగారు రంగు", "నారింజ (కాషాయం)", "పసుపు"),
            luckyColorsEn = listOf("Gold", "Orange", "Yellow"),
            luckyNumbers = listOf(1, 2, 3, 9),
            friendlyNumbers = listOf(1, 2, 3, 5, 9),
            neutralNumbers = listOf(4, 7),
            challengingNumbers = listOf(6, 8),
            gemstoneTe = "కెంపు (Ruby - సంప్రదాయం ప్రకారం)",
            gemstoneEn = "Ruby (Traditionally associated)"
        ),
        2 to NumerologyNumberInfo(
            number = 2,
            planetNameTe = "చంద్రుడు (Moon)",
            planetNameEn = "Moon (Chandra)",
            deityTe = "చంద్ర దేవుడు",
            deityEn = "Lord Chandra",
            natureTe = "శాంతస్వభావం, సున్నితమైన మనసు, కళాత్మక దృష్టి మరియు స్నేహశీలత వీరి ప్రత్యేకత. ఇతరుల మనోభావాలను ఇట్టే గ్రహించే అంతర్దృష్టి కలవారు.",
            natureEn = "Gentle, compassionate, highly intuitive, peace-loving and diplomatic. Excellent collaborators with deep appreciation for arts and harmony.",
            strengthsTe = listOf("సహానుభూతి", "శాంతి ప్రియత్వం", "సృజనాత్మకత", "అంతర్దృష్టి"),
            strengthsEn = listOf("Empathy", "Diplomacy", "Intuition", "Creativity"),
            toWatchTe = listOf("అతిగా ఆలోచించడం మానండి", "మానసిక ఒడుదొడుకులను అధిగమించండి"),
            toWatchEn = listOf("Avoid over-sensitivity", "Maintain emotional balance"),
            luckyDaysTe = listOf("సోమవారం", "శుక్రవారం"),
            luckyDaysEn = listOf("Monday", "Friday"),
            luckyColorsTe = listOf("తెలుపు", "వెండి రంగు", "లేత ఆకుపచ్చ"),
            luckyColorsEn = listOf("White", "Silver", "Light Green"),
            luckyNumbers = listOf(2, 1, 7),
            friendlyNumbers = listOf(1, 2, 3, 7),
            neutralNumbers = listOf(4, 8),
            challengingNumbers = listOf(5, 6, 9),
            gemstoneTe = "ముత్యం (Pearl - సంప్రదాయం ప్రకారం)",
            gemstoneEn = "Pearl (Traditionally associated)"
        ),
        3 to NumerologyNumberInfo(
            number = 3,
            planetNameTe = "గురువు (Jupiter / బృహస్పతి)",
            planetNameEn = "Jupiter (Guru / Brihaspati)",
            deityTe = "బృహస్పతి / దక్షిణామూర్తి",
            deityEn = "Lord Dakshinamurthy",
            natureTe = "జ్ఞానదాహం, సదాశయం, ఉన్నతమైన భావాలు మరియు బోధనా సామర్థ్యం కలవారు. సమాజంలో మంచి గౌరవ మర్యాదలను పొందుతారు.",
            natureEn = "Wise, optimistic, philosophical, expressive and joyful. Blessed with natural wisdom, enthusiasm and great communication prowess.",
            strengthsTe = listOf("జ్ఞానం", "సత్యసంధత", "స్పష్టమైన భావవ్యక్తీకరణ", "ఆశావాదం"),
            strengthsEn = listOf("Wisdom", "Optimism", "Eloquence", "Counseling ability"),
            toWatchTe = listOf("ఒకేసారి చాలా పనులను చేపట్టవద్దు", "లక్ష్యంపై ఏకాగ్రత ఉంచండి"),
            toWatchEn = listOf("Avoid scattering energies", "Practice focus"),
            luckyDaysTe = listOf("గురువారం", "మంగళవారం"),
            luckyDaysEn = listOf("Thursday", "Tuesday"),
            luckyColorsTe = listOf("పసుపు", "కేసరి", "బంగారు పసుపు"),
            luckyColorsEn = listOf("Yellow", "Saffron", "Golden Yellow"),
            luckyNumbers = listOf(3, 1, 9),
            friendlyNumbers = listOf(1, 2, 3, 9),
            neutralNumbers = listOf(5, 7),
            challengingNumbers = listOf(4, 6),
            gemstoneTe = "పుష్యరాగం (Yellow Sapphire - సంప్రదాయం ప్రకారం)",
            gemstoneEn = "Yellow Sapphire (Traditionally associated)"
        ),
        4 to NumerologyNumberInfo(
            number = 4,
            planetNameTe = "రాహువు (Rahu)",
            planetNameEn = "Rahu (Uranus)",
            deityTe = "రాహు దేవుడు / దుర్గా దేవి",
            deityEn = "Goddess Durga",
            natureTe = "క్రమశిక్షణ, నిర్మాణాత్మక ఆలోచన, పట్టుదల మరియు ఆచరణాత్మక దృష్టి వీరి సొంతం. సంక్లిష్ట సమస్యలను కూడా చాకచక్యంగా పరిష్కరిస్తారు.",
            natureEn = "Practical, disciplined, hard-working, inventive and unorthodox thinkers. Masters of organization, structure and systemic thinking.",
            strengthsTe = listOf("పట్టుదల", "విశ్వసనీయత", "పరిశోధనాత్మక దృష్టి", "కష్టపడే తత్వం"),
            strengthsEn = listOf("Methodical", "Resilience", "Reliability", "Technical insight"),
            toWatchTe = listOf("మొండితనం తగ్గించుకోండి", "జీవితంలో మార్పులను స్వాగతించండి"),
            toWatchEn = listOf("Avoid rigid routines", "Embrace flexibility"),
            luckyDaysTe = listOf("శనివారం", "ఆదివారం"),
            luckyDaysEn = listOf("Saturday", "Sunday"),
            luckyColorsTe = listOf("నీలం", "బూడిద రంగు", "కాకి రంగు"),
            luckyColorsEn = listOf("Electric Blue", "Grey", "Khaki"),
            luckyNumbers = listOf(4, 1, 6),
            friendlyNumbers = listOf(1, 5, 6, 7),
            neutralNumbers = listOf(2, 8),
            challengingNumbers = listOf(3, 9),
            gemstoneTe = "గోమేధికం (Hessonite - సంప్రదాయం ప్రకారం)",
            gemstoneEn = "Hessonite (Traditionally associated)"
        ),
        5 to NumerologyNumberInfo(
            number = 5,
            planetNameTe = "బుధుడు (Mercury)",
            planetNameEn = "Mercury (Budha)",
            deityTe = "బుధ భగవానుడు / మహావిష్ణువు",
            deityEn = "Lord Vishnu",
            natureTe = "చురుకైన తెలివితేటలు, హాస్యచతురత, వ్యాపార దక్షత మరియు వేగవంతమైన నిర్ణయాలు వీరి బలం. ఏ వాతావరణానికైనా సులువుగా అలవాటు పడతారు.",
            natureEn = "Dynamic, witty, communicative, adventurous and highly adaptable. Gifted in business, negotiations, learning and progressive thinking.",
            strengthsTe = listOf("వాక్చాతుర్యం", "సమయస్ఫూర్తి", "వైవిధ్యం", "వ్యాపార మెళకువలు"),
            strengthsEn = listOf("Versatility", "Quick wit", "Commercial acumen", "Adaptability"),
            toWatchTe = listOf("నిలకడలేని తనం నియంత్రించండి", "ఆందోళనను దరిచేరనీయకండి"),
            toWatchEn = listOf("Avoid restlessness", "Cultivate calm perseverance"),
            luckyDaysTe = listOf("బుధవారం", "శుక్రవారం"),
            luckyDaysEn = listOf("Wednesday", "Friday"),
            luckyColorsTe = listOf("ఆకుపచ్చ", "లేత బూడిద", "తెలుపు"),
            luckyColorsEn = listOf("Emerald Green", "Light Grey", "White"),
            luckyNumbers = listOf(5, 1, 6),
            friendlyNumbers = listOf(1, 4, 5, 6),
            neutralNumbers = listOf(3, 8),
            challengingNumbers = listOf(2),
            gemstoneTe = "మరకతం (Emerald - సంప్రదాయం ప్రకారం)",
            gemstoneEn = "Emerald (Traditionally associated)"
        ),
        6 to NumerologyNumberInfo(
            number = 6,
            planetNameTe = "శుక్రుడు (Venus)",
            planetNameEn = "Venus (Shukra)",
            deityTe = "శుక్ర భగవానుడు / లక్ష్మీ దేవి",
            deityEn = "Goddess Lakshmi",
            natureTe = "సౌందర్యారాధన, ప్రేమ, కరుణ, ఆకర్షణీయమైన వ్యక్తిత్వం మరియు కుటుంబ అనుబంధాలకు ప్రాధాన్యత ఇస్తారు. ఇల్లు, కార్యాలయం అందంగా తీర్చిదిద్దుతారు.",
            natureEn = "Charming, artistic, caring, harmonious and devoted to family and beauty. Radiate warmth, hospitality and refined aesthetic taste.",
            strengthsTe = listOf("ప్రేమపూర్వక స్వభావం", "సౌందర్య దృష్టి", "బాధ్యతాయుత ప్రవర్తన", "కళాపోషణ"),
            strengthsEn = listOf("Harmony", "Aesthetics", "Responsibility", "Compassion"),
            toWatchTe = listOf("ఇతరుల విషయాల్లో అతి జోక్యం వద్దు", "అవసరమైనప్పుడు నో చెప్పడం నేర్చుకోండి"),
            toWatchEn = listOf("Avoid self-sacrifice", "Do not micromanage loved ones"),
            luckyDaysTe = listOf("శుక్రవారం", "బుధవారం"),
            luckyDaysEn = listOf("Friday", "Wednesday"),
            luckyColorsTe = listOf("తెలుపు", "లేత గులాబీ", "నీలం"),
            luckyColorsEn = listOf("Diamond White", "Rose Pink", "Sky Blue"),
            luckyNumbers = listOf(6, 5, 8),
            friendlyNumbers = listOf(5, 6, 8, 4),
            neutralNumbers = listOf(2, 7),
            challengingNumbers = listOf(1, 3),
            gemstoneTe = "వజ్రం / ఒపాల్ (Diamond / Opal - సంప్రదాయం ప్రకారం)",
            gemstoneEn = "Diamond / White Sapphire (Traditionally associated)"
        ),
        7 to NumerologyNumberInfo(
            number = 7,
            planetNameTe = "కేతువు (Ketu / Neptune)",
            planetNameEn = "Ketu (Neptune)",
            deityTe = "కేతు దేవుడు / వినాయకుడు",
            deityEn = "Lord Ganesha",
            natureTe = "ఆధ్యాత్మిక చింతన, గంభీరమైన ఆలోచనలు, పరిశోధనా శక్తి మరియు లోతైన మనస్సు కలవారు. బాహ్య ఆడంబరాల కంటే అంతశ్శాంతికి ప్రాధాన్యమిస్తారు.",
            natureEn = "Deep thinkers, spiritual seekers, research-oriented, introspective and observant. Possess mystical intuition and analytical wisdom.",
            strengthsTe = listOf("అంతశ్చేతన", "పరిశోధన", "శాంత స్వభావం", "తాత్విక దృష్టి"),
            strengthsEn = listOf("Introspection", "Analytical mind", "Spiritual inclination", "Intuition"),
            toWatchTe = listOf("ఏకాంతంలో అతిగా మునిగిపోవద్దు", "బాహ్య ప్రపంచంతో సంభాషించండి"),
            toWatchEn = listOf("Avoid social withdrawal", "Express your feelings openly"),
            luckyDaysTe = listOf("ఆదివారం", "సోమవారం"),
            luckyDaysEn = listOf("Sunday", "Monday"),
            luckyColorsTe = listOf("తెలుపు", "లేత ఆకుపచ్చ", "లేత పసుపు"),
            luckyColorsEn = listOf("Pearl White", "Sea Green", "Light Yellow"),
            luckyNumbers = listOf(7, 2, 1),
            friendlyNumbers = listOf(1, 2, 4, 7),
            neutralNumbers = listOf(3, 5),
            challengingNumbers = listOf(9),
            gemstoneTe = "వైడూర్యం (Cat's Eye - సంప్రదాయం ప్రకారం)",
            gemstoneEn = "Cat's Eye (Traditionally associated)"
        ),
        8 to NumerologyNumberInfo(
            number = 8,
            planetNameTe = "శని (Saturn)",
            planetNameEn = "Saturn (Shani)",
            deityTe = "శనీశ్వర స్వామి",
            deityEn = "Lord Shani",
            natureTe = "తీవ్రమైన క్రమశిక్షణ, న్యాయబుద్ధి, సహనం, ఆచరణాత్మక దృష్టి మరియు దీర్ఘకాలిక విజయం కలవారు. కృషి ద్వారా అత్యున్నత స్థానాలకు చేరుకుంటారు.",
            natureEn = "Disciplined, pragmatic, patient, fair-minded and resilient. Masters of execution and long-term triumph through persistent dedicated effort.",
            strengthsTe = listOf("న్యాయం", "సహనం", "ఆర్థిక పరిజ్ఞానం", "సమర్థవంతమైన నిర్వహణ"),
            strengthsEn = listOf("Perseverance", "Justice", "Executive mastery", "Endurance"),
            toWatchTe = listOf("అనవసర ఆందోళనలను విడనాడండి", "జీవితంలో వినోదానికి చోటివ్వండి"),
            toWatchEn = listOf("Avoid pessimism", "Balance hard work with relaxation"),
            luckyDaysTe = listOf("శనివారం", "శుక్రవారం"),
            luckyDaysEn = listOf("Saturday", "Friday"),
            luckyColorsTe = listOf("ముదురు నీలం", "నలుపు", "ఊదా (పర్పుల్)"),
            luckyColorsEn = listOf("Dark Blue", "Charcoal", "Deep Purple"),
            luckyNumbers = listOf(8, 5, 6),
            friendlyNumbers = listOf(4, 5, 6),
            neutralNumbers = listOf(2, 7),
            challengingNumbers = listOf(1, 9),
            gemstoneTe = "నీలం (Blue Sapphire - సంప్రదాయం ప్రకారం)",
            gemstoneEn = "Blue Sapphire (Traditionally associated)"
        ),
        9 to NumerologyNumberInfo(
            number = 9,
            planetNameTe = "కుజుడు (Mars / అంగారకుడు)",
            planetNameEn = "Mars (Kuja / Mangal)",
            deityTe = "సుబ్రహ్మణ్య స్వామి / హనుమంతుడు",
            deityEn = "Lord Subrahmanya / Hanuman",
            natureTe = "ధైర్యం, ఉత్సాహం, సమాజ సేవ, నిస్వార్థ గుణం మరియు అలుపెరగని పోరాట పటిమ కలవారు. బలహీనులకు అండగా నిలవడంలో ముందుంటారు.",
            natureEn = "Dynamic, courageous, humanitarian, protective and energetic. High vitality with natural protective instinct and compassionate broad outlook.",
            strengthsTe = listOf("ధైర్యం", "నాయకత్వం", "ఉత్సాహం", "సహాయ గుణం"),
            strengthsEn = listOf("Courage", "Humanitarianism", "Vitality", "Generosity"),
            toWatchTe = listOf("కోపాన్ని నియంత్రించుకోండి", "ఆవేశంలో నిర్ణయాలు తీసుకోకండి"),
            toWatchEn = listOf("Control quick temper", "Avoid impulsive decisions"),
            luckyDaysTe = listOf("మంగళవారం", "గురువారం"),
            luckyDaysEn = listOf("Tuesday", "Thursday"),
            luckyColorsTe = listOf("ఎరుపు", "గులాబీ", "కాషాయం"),
            luckyColorsEn = listOf("Crimson Red", "Rose", "Saffron"),
            luckyNumbers = listOf(9, 1, 3),
            friendlyNumbers = listOf(1, 2, 3, 9),
            neutralNumbers = listOf(5),
            challengingNumbers = listOf(4, 7, 8),
            gemstoneTe = "పగడం (Red Coral - సంప్రదాయం ప్రకారం)",
            gemstoneEn = "Red Coral (Traditionally associated)"
        )
    )

    fun getInfo(num: Int): NumerologyNumberInfo {
        val safeNum = ((num - 1) % 9 + 9) % 9 + 1
        return NUMBERS_MAP[safeNum] ?: NUMBERS_MAP.getValue(1)
    }

    /**
     * Name compatibility check between Name number, Moolank and Bhagyank.
     */
    fun evaluateCompatibility(nameNumber: Int, moolank: Int, bhagyank: Int): CompatibilityResult {
        val nameInfo = getInfo(nameNumber)
        val moolankFriendly = nameInfo.friendlyNumbers.contains(moolank) || nameNumber == moolank
        val bhagyankFriendly = nameInfo.friendlyNumbers.contains(bhagyank) || nameNumber == bhagyank

        val moolankChallenging = nameInfo.challengingNumbers.contains(moolank)
        val bhagyankChallenging = nameInfo.challengingNumbers.contains(bhagyank)

        return when {
            moolankFriendly && bhagyankFriendly -> CompatibilityResult(
                status = CompatibilityStatus.EXCELLENT,
                explanationTe = "మీ నామ సంఖ్య మూలాంకం మరియు భాగ్యాంకం రెండింటితోనూ అత్యద్భుతమైన సామరస్యంలో ఉంది. ఇది మీకు శుభప్రదం.",
                explanationEn = "Your name number is in excellent harmony with both your Moolank and Bhagyank. Highly favorable."
            )
            moolankFriendly || bhagyankFriendly -> CompatibilityResult(
                status = CompatibilityStatus.GOOD,
                explanationTe = "మీ నామ సంఖ్య మీ జనన సంఖ్యలతో అనుకూలమైన మైత్రిని కలిగి ఉంది. మీ ప్రయత్నాలకు మంచి సహాయకారిగా ఉంటుంది.",
                explanationEn = "Your name number aligns positively with your birth vibrations, supporting your endeavors smoothly."
            )
            moolankChallenging || bhagyankChallenging -> CompatibilityResult(
                status = CompatibilityStatus.NEEDS_ATTENTION,
                explanationTe = "మీ నామ సంఖ్య జనన సంఖ్యతో సాధారణంగా ఉంది. చిన్న అక్షర సర్దుబాటు ద్వారా సామరస్యాన్ని మరింత పెంచుకోవచ్చు.",
                explanationEn = "Your name number has a neutral/mixed vibration with your birth numbers. A subtle spelling tweak can enhance harmony."
            )
            else -> CompatibilityResult(
                status = CompatibilityStatus.NEUTRAL,
                explanationTe = "మీ నామ సంఖ్య సమతౌల్యంగా ఉంది. శుభ ఆలోచనలు, సమర్థవంతమైన నిర్ణయాలతో సత్ఫలితాలు సాధించవచ్చు.",
                explanationEn = "Your name vibration is neutral and balanced, providing stable and dependable energy."
            )
        }
    }

    /**
     * Daily guidance tip for each Personal Day number 1 to 9.
     */
    val DAILY_TIPS: Map<Int, Pair<String, String>> = mapOf(
        1 to Pair(
            "కొత్త పనులు, ఆలోచనలు ప్రారంభించడానికి ఈ రోజు అత్యంత అనుకూలం. ఆత్మవిశ్వాసంతో ముందడుగు వేయండి.",
            "A prime day for new beginnings and bold initiatives. Lead with confidence."
        ),
        2 to Pair(
            "సహోద్యోగులు, కుటుంబ సభ్యులతో సమన్వయం సాధించండి. ప్రశాంతమైన సంభాషణలు సత్ఫలితాలనిస్తాయి.",
            "Focus on teamwork, gentle collaboration, and peaceful communications."
        ),
        3 to Pair(
            "సృజనాత్మక ఆలోచనలు, జ్ఞాన సముపార్జన మరియు ఉల్లాసానికి అనుకూలమైన రోజు. మీ నైపుణ్యాలను ప్రదర్శించండి.",
            "Express creativity and optimism. Excellent for self-expression and learning."
        ),
        4 to Pair(
            "క్రమశిక్షణతో కూడిన ప్రణాళికలు రూపొందించండి. దీర్ఘకాలిక పనులకు గట్టి పునాది వేయడానికి మంచి సమయం.",
            "Organize details, attend to routines, and build solid practical foundations."
        ),
        5 to Pair(
            "వేగవంతమైన నిర్ణయాలు, కొత్త పరిచయాలు మరియు అవకాశాలు ఆహ్వానించండి. ఉత్సాహంగా ఉండండి.",
            "Expect dynamic shifts and fresh opportunities. Stay flexible and open-minded."
        ),
        6 to Pair(
            "కుటుంబ సంబంధాలు, గృహ వాతావరణం మరియు సేవలకు ప్రాధాన్యమివ్వండి. ప్రేమ, కరుణలను పంచండి.",
            "Dedicate time to home, loved ones, and harmony. Generosity brings deep joy."
        ),
        7 to Pair(
            "అంతర్దృష్టి, ధ్యానం, పుస్తక పఠనానికి అనువైన రోజు. ఏకాగ్రతతో చేసే పరిశోధనలలో విజయం లభిస్తుంది.",
            "A day for contemplation, study, and quiet inner clarity. Trust your intuition."
        ),
        8 to Pair(
            "ఆర్థిక నిర్ణయాలు, నిర్వహణ మరియు పట్టుదలతో కూడిన శ్రమకు అత్యుత్తమ ఫలితాలు లభిస్తాయి.",
            "Focus on efficiency, practical achievements, and sound financial judgment."
        ),
        9 to Pair(
            "పెండింగ్ పనులను పూర్తి చేయండి. ఇతరులకు సహాయం చేయడం ద్వారా మానసిక సంతృప్తిని పొందుతారు.",
            "Wrap up loose ends and extend a helping hand. Compassion lights your path."
        )
    )

    fun getDailyTip(personalDay: Int): Pair<String, String> {
        val safeDay = ((personalDay - 1) % 9 + 9) % 9 + 1
        return DAILY_TIPS[safeDay] ?: DAILY_TIPS.getValue(1)
    }
}
