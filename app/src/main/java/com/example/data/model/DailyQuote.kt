package com.example.data.model

data class DailyQuote(
    val id: Int,
    val telugu: String,
    val english: String,
    val source: String? = null
)

object AppConfig {
    const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.aistudio.shubhodaya"
}

object DailyQuotesData {
    val quotes = listOf(
        DailyQuote(1, "సత్యమేవ జయతే నానృతం — సత్యమే ఎల్లప్పుడూ జయిస్తుంది.", "Truth alone triumphs, not falsehood.", "ముండకోపనిషత్"),
        DailyQuote(2, "ధర్మో రక్షతి రక్షితః — మనం ధర్మాన్ని రక్షిస్తే, ఆ ధర్మమే మనలను కాపాడుతుంది.", "Dharma protects those who uphold dharma.", "మహాభారతం"),
        DailyQuote(3, "కర్మణ్యేవాధికారస్తే మా ఫలేషు కదాచన — కర్మ చేయడం వరకే నీ అధికారం, ఫలితంపై కాదు.", "You have a right to perform your duties, but not to the fruits thereof.", "భగవద్గీత"),
        DailyQuote(4, "విద్యా దదాతి వినయం — నిజమైన విద్య వినయాన్ని మరియు సద్గుణాన్ని ప్రసాదిస్తుంది.", "True knowledge yields humility and noble character.", "హితోపదేశం"),
        DailyQuote(5, "మాతృదేవో భవ, పితృదేవో భవ — తల్లిదండ్రులను ప్రత్యక్ష దైవాలుగా పూజించండి.", "Revere mother and father as living embodiments of God.", "తైత్తిరీయోపనిషత్"),
        DailyQuote(6, "ఆచార్యదేవో భవ — జ్ఞానాన్నిచ్చే గురువును భగవంతునిగా భావించండి.", "Revere your teacher as the divine bringer of light.", "తైత్తిరీయోపనిషత్"),
        DailyQuote(7, "అహింసా పరమో ధర్మః — అన్ని జీవుల పట్ల దయ మరియు అహింసయే శ్రేష్ఠమైన ధర్మం.", "Non-violence and compassion are the supreme duties.", "మహాభారతం"),
        DailyQuote(8, "ఉద్యోగినం పురుషసింహముపైతి లక్ష్మీః — ప్రయత్నశీలుడైన మానవుణ్ణే లక్ష్మీదేవి వరిస్తుంది.", "Prosperity embraces the diligent and resolute.", "సుభాషితం"),
        DailyQuote(9, "వసుధైవ కుటుంబకం — ఈ సమస్త జగత్తు ఒకే కుటుంబం.", "The whole world is one single family.", "మహోపనిషత్"),
        DailyQuote(10, "సర్వే భవంతు సుఖినః సర్వే సంతు నిరామయాః — సమస్త ప్రాణులు ఆనందారోగ్యాలతో వర్ధిల్లుగాక.", "May all beings everywhere be happy and free from illness.", "బృహదారణ్యకోపనిషత్"),
        DailyQuote(11, "యోగా కర్మసు కౌశలం — ఏ పనినైనా నిష్కామంగా, నైపుణ్యంతో చేయడమే యోగం.", "Yoga is exquisite skill and equanimity in action.", "భగవద్గీత"),
        DailyQuote(12, "శ్రద్ధావాన్ లభతే జ్ఞానం — నిరంతర ఆసక్తి, శ్రద్ధ ఉన్నవారికే ఉత్తమ జ్ఞానం లభిస్తుంది.", "The sincere and devoted seeker attains true wisdom.", "భగవద్గీత"),
        DailyQuote(13, "సంతోషం పరమం సుఖం — సంతృప్తిని మించిన నిరంతర సంపద మరియు సుఖం లేదు.", "Contentment is the greatest treasure and true happiness.", "పంచతంత్రం"),
        DailyQuote(14, "గుణైః గౌరవం ప్రాప్యతే నాసనార్హైః — మానవుడికి గౌరవం సద్గుణాల వల్లనే వస్తుంది.", "Honor is earned through virtue, not mere high status.", "నీతిశాస్త్రం"),
        DailyQuote(15, "ఆత్మవత్ సర్వభూతేషు — ఇతరులలో కూడా నీ ఆత్మను చూడు, సమానంగా ప్రేమించు.", "See your own soul reflected in all living beings.", "వేదవాణి"),
        DailyQuote(16, "నారాయణేతి సమర్పయామి — సమస్త కర్మలను శ్రీమన్నారాయణునికి శరణాగతితో అర్పించుము.", "Dedicate all deeds selflessly to the Supreme Divine.", "భాగవతం"),
        DailyQuote(17, "మన ఏవ మనుష్యాణాం కారణం బంధమోక్షయోః — మనస్సే బంధానికి, మోక్షానికి మూలకారణం.", "The mind alone is the cause of both bondage and liberation.", "అమృతబిందూపనిషత్"),
        DailyQuote(18, "సహనాభవతు సహనౌ భునక్తు — మనమంతా కలిసి వర్ధిల్లుదాం, కలిసి జ్ఞానాన్ని పంచుకుందాం.", "May we be protected and nourished together in harmony.", "కఠోపనిషత్"),
        DailyQuote(19, "ఉత్థాతవ్యం జాగృతవ్యం — లేవండి, మేల్కొనండి, గమ్యం చేరేవరకు విశ్రమించకండి.", "Arise, awake, and stop not till the noble goal is achieved.", "కఠోపనిషత్"),
        DailyQuote(20, "కామోధ్భవతి సంకల్పాత్ — మంచి సంకల్పమే ఉత్తమ కార్యాలకు బీజం.", "Pure and noble intentions lay the foundation for righteous deeds.", "మనుస్మృతి"),
        DailyQuote(21, "పరోపకారాయ ఫలంతి వృక్షాః — పరోపకారం కోసమే వృక్షాలు ఫలాలను ఇస్తాయి.", "Nature exists to give; live generously to serve others.", "సుభాషితం"),
        DailyQuote(22, "శాంతే సమత్వాత్ పరం నాస్తి — మనశ్శాంతిని మించిన దివ్యమైన తపస్సు లేదు.", "There is no austerity superior to calm peace of mind.", "శ్రీరామగీత"),
        DailyQuote(23, "వచనాలలో తియ్యదనం, చేతలలో నిజాయితీ ఉన్నవాడే సజ్జనుడు.", "A noble soul speaks with gentle sweetness and acts with integrity.", "వేమన శతకం"),
        DailyQuote(24, "నిండు మనసుతో చేసే ప్రార్థన సకల సంకటాలను నివారిస్తుంది.", "Heartfelt, surrendered prayer dispels all distress.", "భాగవతం"),
        DailyQuote(25, "అనభ్యాసే విషం విద్యా — సాధన చేయని విద్య కాలక్రమేణా నశిస్తుంది.", "Knowledge unpracticed becomes dull like unused tools.", "హితోపదేశం"),
        DailyQuote(26, "సహనం సముద్రమంత ఉండాలి, సంకల్పం మేరుపర్వతమంత స్థిరంగా ఉండాలి.", "Let patience be as deep as the sea and resolve steadfast.", "నీతిచంద్రిక"),
        DailyQuote(27, "లోకాః సమస్తాః సుఖినో భవంతు — విశ్వంలోని ప్రాణులన్నీ క్షేమంగా ఉండుగాక.", "May peace, auspiciousness, and well-being prevail.", "మంగళ శాసనం"),
        DailyQuote(28, "కృతజ్ఞత లేని హృదయం ఎడారి వంటిది, కృతజ్ఞత కల హృదయం నందనవనం.", "A grateful heart is a blooming garden of divine grace.", "సుభాషిత రత్నావళి"),
        DailyQuote(29, "మౌనం సర్వార్థ సాధనం — అపరిమితమైన వాక్చాతుర్యం కన్నా సందర్భోచిత మౌనం మేలు.", "Mindful silence is often the wisest accomplishment.", "భర్తృహరి నీతిశతకం"),
        DailyQuote(30, "ఆపదలో ఆదుకునేవాడే నిజమైన బంధువు, ధర్మమే శాశ్వత మిత్రుడు.", "He who stands with you in crisis is true kin; dharma is your eternal friend.", "హితోపదేశం"),
        DailyQuote(31, "కోపం శత్రువు, శాంతం రక్ష, క్షమయే అమృతం.", "Anger is your greatest enemy; patience protects; forgiveness is nectar.", "భారత నీతి"),
        DailyQuote(32, "ఉదయాన్నే సూర్య నమస్కారం ఆయురారోగ్యాలను, తేజస్సును ప్రసాదిస్తుంది.", "Morning adoration of the Sun bestows longevity, radiant vigor, and health.", "సూర్యోపనిషత్"),
        DailyQuote(33, "దానాలలో అన్నదానం, జ్ఞానదానం అత్యంత పుణ్యప్రదమైనవి.", "Giving food and sharing sacred knowledge are the highest forms of charity.", "పురాణ సూక్తి"),
        DailyQuote(34, "మంచి ఆలోచన మంచి జీవన విధానాన్ని నిర్మిస్తుంది.", "Right thinking shapes an inspiring and noble way of life.", "సుభాషితం"),
        DailyQuote(35, "సత్సంగత్వే నిస్సంగత్వం — మంచివారి సాంగత్యం నిర్మలమైన మనస్సును ఇస్తుంది.", "Good company naturally detaches the mind from destructive thoughts.", "భజ గోవిందం"),
        DailyQuote(36, "కాలం అమూల్యమైనది, గడిచిన క్షణం తిరిగిరాదు; సద్వినియోగం చేసుకోండి.", "Time is irreplaceable; consecrate every moment with purposeful action.", "నీతి సూక్తి"),
        DailyQuote(37, "తపస్సు అంటే అడవులకు వెళ్లడం కాదు, నిత్య జీవితంలో ఇంద్రియ నిగ్రహం పాటించడం.", "True penance is conscious mastery over one's senses in daily living.", "వేదవాణి"),
        DailyQuote(38, "నిజమైన సౌందర్యం ముఖంలో కాదు, ప్రవర్తనలో మరియు దయాగుణంలో ఉంటుంది.", "True elegance resides in graceful conduct and boundless kindness.", "నీతి శతకం"),
        DailyQuote(39, "స్వధర్మే నిధనం శ్రేయః — తన ధర్మాన్ని నిష్ఠతో ఆచరించడమే శ్రేయస్కరం.", "It is far better to live one's own duty with faith than emulate another.", "భగవద్గీత"),
        DailyQuote(40, "దీపం ప్రకాశాన్ని ఇస్తుంది, జ్ఞానం అజ్ఞానాంధకారాన్ని పారద్రోలుతుంది.", "A sacred lamp spreads glow; supreme wisdom dispels spiritual darkness.", "దీప శ్లోకం"),
        DailyQuote(41, "కష్టాలు మేఘాల వంటివి, అవి తాత్కాలికంగా సూర్యుణ్ణి దాచినా మళ్లీ వెలుగు వస్తుంది.", "Tribulations are passing clouds; your inner light will shine again.", "ధైర్య సూక్తి"),
        DailyQuote(42, "ఎవరికైతే ఆశలు తక్కువగా ఉంటాయో, వారి ఆనందం అంత ఎక్కువగా ఉంటుంది.", "Freedom from excessive cravings is the direct doorway to serenity.", "విదుర నీతి"),
        DailyQuote(43, "ఓర్పు అనేది చేదుగా ఉండవచ్చు, కానీ దాని ఫలం అమృతతుల్యమైనది.", "Endurance may test the spirit, but its harvest is ever sweet.", "చాణక్య నీతి"),
        DailyQuote(44, "ప్రతి ఉదయం భగవంతుడు మనకిచ్చే నూతన అవకాశాల వరప్రసాదం.", "Each sunrise is a divine gift of renewed purpose and fresh beginnings.", "శుభోదయ సందేశం"),
        DailyQuote(45, "మృదువైన మాట కోపాన్ని తగ్గిస్తుంది, కఠినమైన మాట ద్వేషాన్ని పెంచుతుంది.", "A gentle word soothes fury, while harsh speech inflames discord.", "హితోపదేశం"),
        DailyQuote(46, "పవిత్రమైన కర్మలే మానవుని నిజమైన సంపద మరియు పుణ్యఫలాలు.", "Noble righteous actions are human life's true spiritual wealth.", "గరుడ పురాణం"),
        DailyQuote(47, "తనను తాను గెలిచినవాడు సమస్త జగత్తును గెలిచినట్లే.", "Conquering one's own mind is the greatest victory in creation.", "యోగ వాశిష్టం"),
        DailyQuote(48, "దేవాలయం ఎంత పవిత్రమో, మన హృదయం కూడా అంత నిర్మలంగా ఉండాలి.", "Keep the inner sanctuary of your heart as pure as a consecrated temple.", "అధ్యాత్మ రామాయణం"),
        DailyQuote(49, "వినయం లేని పాండిత్యం వాసన లేని కాగితపు పువ్వు వంటిది.", "Scholarly learning without humility is like a scentless paper flower.", "వేమన"),
        DailyQuote(50, "ఆపదలో అధైర్యపడకు, సంపదలో పొంగిపోకు; స్థితప్రజ్ఞుడవై ఉండు.", "Despair not in adversity, nor boast in fortune; maintain steadfast equanimity.", "భగవద్గీత"),
        DailyQuote(51, "నిరంతరం ఈశ్వర చింతన కలవారికి భయము, శోకము ఉండవు.", "He whose consciousness is anchored in the Divine transcends all fear and grief.", "విష్ణు సహస్రనామం"),
        DailyQuote(52, "ఆహార శుద్ధౌ సత్త్వశుద్ధిః — సాత్త్విక ఆహారమే మనస్సుకు పవిత్రతను ఇస్తుంది.", "Purity in food and thoughts fosters pure spiritual clarity.", "ఛాందోగ్యోపనిషత్"),
        DailyQuote(53, "స్వార్థం లేని సేవయే మానవ జన్మకు సార్థకత.", "Selfless service to humanity gives sacred meaning to human birth.", "సేవా సూక్తి"),
        DailyQuote(54, "మనం చేసే చిన్న మంచి పని కూడా ఎవరి జీవితంలోనో పెద్ద వెలుగును నింపవచ్చు.", "Even a small gentle act of kindness can kindle immense light in someone's day.", "సుభాషితం"),
        DailyQuote(55, "అసూయ, ద్వేషాలను వీడడమే అంతరంగ ప్రక్షాళన.", "Releasing malice and jealousy is the truest inner purification.", "నారద భక్తి సూత్రాలు"),
        DailyQuote(56, "మంగళకరమైన సంకల్పంతో ప్రారంభించే ప్రతి కార్యం విజయవంతమవుతుంది.", "Any undertaking begun with an auspicious mindset and pure prayers succeeds.", "ముహూర్త సూత్రం"),
        DailyQuote(57, "క్షమతో శత్రువును కూడా మిత్రునిగా మార్చవచ్చు.", "Forgiveness has the divine power to transform adversity into enduring friendship.", "సుభాషితం"),
        DailyQuote(58, "నమ్రతతో కూడిన ప్రవర్తన సకల దేవతల అనుగ్రహాన్ని తెస్తుంది.", "A humble disposition attracts the benevolent grace of all deities.", "దేవీ భాగవతం"),
        DailyQuote(59, "నేటి సత్కర్మలే రేపటి ఆనందకరమైన భవితవ్యానికి పునాదులు.", "Today's pious deeds are the indestructible foundation for a blissful tomorrow.", "కర్మ సిద్ధాంతం"),
        DailyQuote(60, "ఓం తత్సత్ — చేసిన ప్రతి సత్కార్యం పరబ్రహ్మార్పణమగుగాక.", "Om Tat Sat — may all goodness be offered unconditionally to the Divine.", "భగవద్గీత")
    )

    fun getDailyQuote(dayOfYear: Int): DailyQuote {
        val index = Math.abs(dayOfYear) % quotes.size
        return quotes[index]
    }
}
