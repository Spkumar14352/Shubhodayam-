// Festival rules for the Telugu (amanta) lunar calendar.
// The Android app reads this file when it is built, so editing a rule here changes the app's dates.
// Keep the array JSON-like: quoted string values and no trailing commas. // comments are fine.
//
// type "tithi":              a tithi in a nija (non-adhika) lunar month
// type "weekdayBeforeTithi": the given weekday just before that tithi's day, inside the same paksha
// type "tithiWithSunSign":   the tithi in whichever month has the Sun in sunSign that day
// type "everyTithi":         every occurrence (paksha "both" means shukla and krishna)
// type "sankranti":          the day the Sun enters sign (after sunset counts as the next day)
// type "everySankranti":     every sankranti not covered by a "sankranti" rule
//
// tithi is 1-15 within the paksha (15 = Purnima in shukla, Amavasya in krishna).
// kala decides which part of the day must contain the tithi:
//   sunrise (default), madhyahna, aparahna, pradosha, nishita
// offsets add related days, e.g. Bhogi the day before Makara Sankranti.
// In names, {month}, {tithi} and {sign} are filled in for recurring rules.

export type LunarMonthName =
  | "Chaitra" | "Vaishakha" | "Jyeshtha" | "Ashadha" | "Shravana" | "Bhadrapada"
  | "Ashwayuja" | "Kartika" | "Margashira" | "Pushya" | "Magha" | "Phalguna";
export type SignName =
  | "Mesha" | "Vrishabha" | "Mithuna" | "Karka" | "Simha" | "Kanya"
  | "Tula" | "Vrischika" | "Dhanu" | "Makara" | "Kumbha" | "Meena";
export type Kala = "sunrise" | "madhyahna" | "aparahna" | "pradosha" | "nishita";

export interface FestivalOffset {
  id: string;
  days: number;
  nameEn: string;
  nameTe: string;
  descriptionEn?: string;
  descriptionTe?: string;
}

export interface FestivalRule {
  id: string;
  type: "tithi" | "weekdayBeforeTithi" | "tithiWithSunSign" | "everyTithi" | "sankranti" | "everySankranti";
  nameEn: string;
  nameTe: string;
  month?: LunarMonthName;
  paksha?: "shukla" | "krishna" | "both";
  tithi?: number;
  kala?: Kala;
  weekday?: "monday" | "tuesday" | "wednesday" | "thursday" | "friday" | "saturday" | "sunday";
  sunSign?: SignName;
  sign?: SignName;
  major?: boolean;
  offsets?: FestivalOffset[];
  descriptionEn?: string;
  descriptionTe?: string;
  ritualsEn?: string;
  ritualsTe?: string;
}

export const festivalRules: FestivalRule[] = [
  {
    "id": "ugadi", "type": "tithi", "month": "Chaitra", "paksha": "shukla", "tithi": 1, "kala": "sunrise", "major": true,
    "nameEn": "Ugadi (Telugu New Year)", "nameTe": "ఉగాది (తెలుగు నూతన సంవత్సరం)",
    "descriptionEn": "The dawn of the new Telugu astronomical year. Marked by tasting Shadruchula Ugadi Pachadi and attending Panchanga Sravanam.",
    "descriptionTe": "తెలుగు వారి ఆద్యంతమైన నూతన సంవత్సర ప్రారంభం. జీవితంలోని సుఖదుఃఖాల సమ్మిళితాన్ని తెలిపే షడ్రుచుల ఉగాది పచ్చడి మరియు పంచాంగ శ్రవణం ప్రధాన విశేషాలు.",
    "ritualsEn": "Tasting Shadruchula Ugadi Pachadi, oil bath with sun rise prayers, Panchanga Sravanam, and wearing new traditional clothes.",
    "ritualsTe": "ఉగాది పచ్చడి సేవనం, తైలాభ్యంగన స్నానం, సాయంత్రం దేవాలయాల్లో పంచాంగ శ్రవణం మరియు కవి సమ్మేళనాలు."
  },
  {
    "id": "sri_rama_navami", "type": "tithi", "month": "Chaitra", "paksha": "shukla", "tithi": 9, "kala": "madhyahna", "major": true,
    "nameEn": "Sri Rama Navami", "nameTe": "శ్రీరామ నవమి",
    "descriptionEn": "Birth anniversary of Lord Sri Rama and celestial wedding (Kalyanotsavam) of Sita and Rama celebrated grandly at Bhadrachalam.",
    "descriptionTe": "మర్యాదా పురుషోత్తముడు శ్రీరామచంద్రుని జన్మదినం మరియు సీతారాముల కల్యాణ మహోత్సవం. భద్రాచలంతో పాటు ప్రతి ఊరూవాడా కల్యాణ వేడుకలు.",
    "ritualsEn": "Sita Rama Kalyanam ceremony, preparation and distribution of Panakam and Vadapappu, recitation of Sundarakanda.",
    "ritualsTe": "సీతారాముల దివ్య కల్యాణం, చల్లని పానకం, వడపప్పు నివేదన, సుందరకాండ పారాయణం."
  },
  {
    "id": "akshaya_tritiya", "type": "tithi", "month": "Vaishakha", "paksha": "shukla", "tithi": 3, "kala": "sunrise", "major": true,
    "nameEn": "Akshaya Tritiya", "nameTe": "అక్షయ తృతీయ",
    "descriptionEn": "A day held auspicious for new beginnings, charity and buying gold.",
    "descriptionTe": "శుభకార్యాల ప్రారంభానికి, దానధర్మాలకు, బంగారం కొనుగోలుకు శ్రేష్ఠమైన దినం."
  },
  {
    "id": "hanuman_jayanti", "type": "tithi", "month": "Vaishakha", "paksha": "krishna", "tithi": 10, "kala": "sunrise", "major": true,
    "nameEn": "Hanuman Jayanti", "nameTe": "హనుమాన్ జయంతి",
    "descriptionEn": "Birth anniversary of Lord Hanuman as observed in the Telugu states.",
    "descriptionTe": "తెలుగు రాష్ట్రాల్లో జరుపుకునే ఆంజనేయ స్వామి జయంతి."
  },
  {
    "id": "varalakshmi_vratham", "type": "weekdayBeforeTithi", "month": "Shravana", "paksha": "shukla", "tithi": 15, "weekday": "friday", "major": true,
    "nameEn": "Varalakshmi Vratham", "nameTe": "వరలక్ష్మీ వ్రతం",
    "descriptionEn": "Sacred vratham observed by married Telugu women seeking family welfare, longevity, and prosperity by worshipping Goddess Lakshmi.",
    "descriptionTe": "సౌభాగ్యం, ఆయురారోగ్యాలు, సిరిసంపదల కోసం స్త్రీలు భక్తిశ్రద్ధలతో ఆచరించే శ్రావణ మాసపు విశేష వరలక్ష్మీ దేవి పూజ.",
    "ritualsEn": "Kalasha sthapana with coconut and gold/silver face of Lakshmi, tying nine-knot sacred thread (Toram), offering 9 types of sweets.",
    "ritualsTe": "కలశ స్థాపన, లక్ష్మీదేవి అలంకరణ, తొమ్మిది ముడుల నోము దారం (తోరం) కట్టుకోవడం, తొమ్మిది రకాల పిండివంటల నైవేద్యం."
  },
  {
    "id": "raksha_bandhan", "type": "tithi", "month": "Shravana", "paksha": "shukla", "tithi": 15, "kala": "sunrise", "major": true,
    "nameEn": "Raksha Bandhan (Rakhi Pournami)", "nameTe": "రాఖీ పౌర్ణమి",
    "descriptionEn": "Sisters tie rakhi on their brothers' wrists, praying for their wellbeing.",
    "descriptionTe": "సోదరుల క్షేమం కోరుతూ సోదరీమణులు రాఖీ కట్టే పండుగ."
  },
  {
    "id": "krishnashtami", "type": "tithi", "month": "Shravana", "paksha": "krishna", "tithi": 8, "kala": "nishita", "major": true,
    "nameEn": "Sri Krishnashtami", "nameTe": "శ్రీకృష్ణాష్టమి",
    "descriptionEn": "Birth of Lord Krishna, celebrated at midnight with fasting, bhajans and Uttiyadi.",
    "descriptionTe": "అర్ధరాత్రి శ్రీకృష్ణ జననం. ఉపవాసం, భజనలు, ఉట్టి కొట్టడం విశేషాలు."
  },
  {
    "id": "vinayaka_chavithi", "type": "tithi", "month": "Bhadrapada", "paksha": "shukla", "tithi": 4, "kala": "madhyahna", "major": true,
    "nameEn": "Vinayaka Chavithi (Ganesh Chaturthi)", "nameTe": "వినాయక చవితి",
    "descriptionEn": "Celebration of Lord Ganesha, the remover of obstacles. Clay idols are worshipped with 21 varieties of leaves (Patri).",
    "descriptionTe": "విఘ్ననాశకుడు గణపతి అవతరించిన పర్వదినం. మట్టి గణపతుల పూజ, ఏకవింశతి పత్ర పూజ (21 రకాల పత్రి) మరియు మోదకాలు, కుడుములు సమర్పణ.",
    "ritualsEn": "21 Patri puja, offering Undrallu, Kudumulu, and Modaks; listening to Syamantakopakhyanam story.",
    "ritualsTe": "21 రకాల ఆకులతో పత్రి పూజ, ఉండ్రాళ్ళు, కుడుములు, లడ్డూల నైవేద్యం, శ్యమంతకోపాఖ్యానం కథ వినడం."
  },
  {
    "id": "bathukamma", "type": "tithi", "month": "Bhadrapada", "paksha": "krishna", "tithi": 15, "kala": "sunrise", "major": true,
    "nameEn": "Bathukamma begins (Engili Pula)", "nameTe": "బతుకమ్మ ప్రారంభం (ఎంగిలిపూల బతుకమ్మ)",
    "descriptionEn": "Telangana's grand state floral festival worshipping Mother Nature and Goddess Gauri with seasonal flowers and folk songs.",
    "descriptionTe": "తెలంగాణ సంస్కృతీ సంప్రదాయాల పూల పండుగ. తీరొక్క పూలతో అందంగా బతుకమ్మను పేర్చి, గౌరమ్మను కొలుస్తూ మహిళల పాటలు, కోలాటాలు.",
    "ritualsEn": "Arranging flowers in concentric layers, singing folk songs around Bathukamma, offering Maleeda.",
    "ritualsTe": "గునుగు, తంగేడు, బంతి పూలతో బతుకమ్మ తయారీ, చప్పట్లతో పాటలు, మలీద ప్రసాదం నివేదన.",
    "offsets": [
      {
        "id": "saddula_bathukamma", "days": 9, "nameEn": "Saddula Bathukamma", "nameTe": "సద్దుల బతుకమ్మ",
        "descriptionEn": "The grand final day of Bathukamma, when the flower stacks are immersed in water.",
        "descriptionTe": "బతుకమ్మ పండుగ చివరి రోజు. బతుకమ్మలను నీటిలో నిమజ్జనం చేస్తారు."
      }
    ]
  },
  {
    "id": "dasara", "type": "tithi", "month": "Ashwayuja", "paksha": "shukla", "tithi": 10, "kala": "aparahna", "major": true,
    "nameEn": "Vijaya Dasami (Dasara)", "nameTe": "విజయదశమి (దసరా / శమీ పూజ)",
    "descriptionEn": "Victory of good over evil as Goddess Durga vanquished Mahishasura. Families perform Ayudha Puja and Shami (Jammi chettu) puja.",
    "descriptionTe": "చెడుపై మంచి సాధించిన విజయోత్సవం. దుర్గాదేవి మహిషాసురమర్దినిగా విజయసారథ్యం వహించిన దినం. జమ్మి పూజ, ఆయుధ పూజ మరియు పాలపిట్ట దర్శనం.",
    "ritualsEn": "Shami puja at sunset, exchanging Jammi leaves with elders with blessings, Ayudha puja for vehicles.",
    "ritualsTe": "సాయంత్రం జమ్మి చెట్టు పూజ, 'శమీ శమయతే పాపం' అంటూ జమ్మి ఆకులు ఇచ్చి పెద్దల ఆశీస్సులు తీసుకోవడం."
  },
  {
    "id": "naraka_chaturdashi", "type": "tithi", "month": "Ashwayuja", "paksha": "krishna", "tithi": 14, "kala": "sunrise", "major": true,
    "nameEn": "Naraka Chaturdashi", "nameTe": "నరక చతుర్దశి",
    "descriptionEn": "Remembers Satyabhama and Krishna's victory over Narakasura; an early-morning oil bath begins Deepavali.",
    "descriptionTe": "సత్యభామా సమేత శ్రీకృష్ణుడు నరకాసురుని సంహరించిన దినం. తెల్లవారుజామున అభ్యంగన స్నానం."
  },
  {
    "id": "deepavali", "type": "tithi", "month": "Ashwayuja", "paksha": "krishna", "tithi": 15, "kala": "pradosha", "major": true,
    "nameEn": "Deepavali (Diwali)", "nameTe": "దీపావళి",
    "descriptionEn": "The festival of lights celebrating the slaying of Narakasura by Satyabhama and Krishna. Earthen oil diyas and Lakshmi puja.",
    "descriptionTe": "చీకటిని పారద్రోలి వెలుగులు నింపే దివ్య దీపాల పండుగ. సత్యభామా సమేత శ్రీకృష్ణుడి నరకాసుర వధ స్మరణ, లక్ష్మీ కుబేర పూజలు, బాణాసంచా సంబరం.",
    "ritualsEn": "Morning oil bath, Lakshmi-Kubera puja at dusk, lighting clay diyas, fireworks, sharing sweets.",
    "ritualsTe": "నువ్వుల నూనెతో తలస్నానం, సాయంత్రం మట్టి ప్రమిదలతో దీపాలంకరణ, లక్ష్మీదేవి పూజ, బాణాసంచా కాల్చడం."
  },
  {
    "id": "karthika_masam_start", "type": "tithi", "month": "Kartika", "paksha": "shukla", "tithi": 1, "kala": "sunrise", "major": true,
    "nameEn": "Karthika Masam begins", "nameTe": "కార్తీక మాసారంభం",
    "descriptionEn": "The beginning of the auspicious Karthika month, dedicated to the worship of Lord Shiva and Lord Vishnu.",
    "descriptionTe": "శివకేశవుల ఆరాధనకు అత్యంత విశిష్టమైన కార్తీక మాసారంభం. తెల్లవారుజామున నదీ స్నానాలు, నిత్య దీపారాధనలు ప్రారంభం.",
    "ritualsEn": "Early morning bath, daily lighting of earthen lamps, Akasa Deepam.",
    "ritualsTe": "నదీ స్నానం లేదా పుణ్యస్నానం, నిత్యం ఉదయ సాయంత్రాల్లో ఆవు నెయ్యితో దీపారాధన, ఆకాశ దీప దర్శనం."
  },
  {
    "id": "bhagini_hasta_bhojanam", "type": "tithi", "month": "Kartika", "paksha": "shukla", "tithi": 2, "kala": "sunrise", "major": true,
    "nameEn": "Bhagini Hasta Bhojanam (Bhatru Ditiya)", "nameTe": "భగినీ హస్త భోజనం (యమ ద్వితీయ / భ్రాతృ విదియ)",
    "descriptionEn": "Brothers dine at their sisters' homes and receive blessings and gifts; prayers offered to Yama Dharmaraja.",
    "descriptionTe": "సోదరి చేతి భోజనం చేసి సోదరులు కానుకలు సమర్పించే పవిత్ర దినం. అపమృత్యు భయ నివారణకు యమ ధర్మరాజు ఆరాధన.",
    "ritualsEn": "Brothers visit sisters, meal prepared by sister, exchange of gifts, lighting lamps for Yama.",
    "ritualsTe": "తోబుట్టువుల అనురాగ బంధం, సోదరి స్వయంగా వండిన వంటల భోజనం, కానుకలు ఇవ్వడం, యమ దీపదానం."
  },
  {
    "id": "nagula_chavithi", "type": "tithi", "month": "Kartika", "paksha": "shukla", "tithi": 4, "kala": "sunrise", "major": true,
    "nameEn": "Nagula Chavithi", "nameTe": "నాగుల చవితి",
    "descriptionEn": "Devotees, especially women, worship the serpent god (Nagendra) at ant-hills with milk, chalimidi, and thil laddus.",
    "descriptionTe": "సంతాన సౌభాగ్యం, వంశాభివృద్ధి కొరకు మహిళలు పుట్టలో పాలు, చలిమిడి, చిమ్మిలి సమర్పించి నాగేంద్రుని ఆరాధించే పుణ్యదినం.",
    "ritualsEn": "Offering cow milk, thil-jaggery (Chimmidi), chalimidi at ant-hills, lighting oil lamps.",
    "ritualsTe": "నాగుల పుట్ట వద్ద పసుపు, కుంకుమలతో పూజ, ఆవు పాలు, చలిమిడి, చిమ్మిలి నైవేద్యం, నాగస్తుతి పారాయణం."
  },
  {
    "id": "utthana_ekadashi", "type": "tithi", "month": "Kartika", "paksha": "shukla", "tithi": 11, "kala": "sunrise", "major": true,
    "nameEn": "Utthana (Prabodhini) Ekadashi", "nameTe": "ఉత్థాన ఏకాదశి (ప్రబోధినీ ఏకాదశి)",
    "descriptionEn": "Lord Vishnu awakens from his four-month cosmic yogic sleep (Chaturmasya). A sacred day of complete fasting.",
    "descriptionTe": "శ్రీమహావిష్ణువు చాతుర్మాస్య యోగనిద్ర నుండి మేల్కొనే పరమ పవిత్ర ఏకాదశి. భక్తులు రోజంతా ఉపవాసం ఉండి హరినామ సంకీర్తన చేస్తారు.",
    "ritualsEn": "Complete fasting (Upavasam), Vishnu Sahasranama parayana, night vigil (Jagaram).",
    "ritualsTe": "నిరాహార ఉపవాసం, రాత్రి జాగరణ, విష్ణు సహస్రనామ పారాయణం, హరి కథా శ్రవణం."
  },
  {
    "id": "ksheerabdi_dwadashi", "type": "tithi", "month": "Kartika", "paksha": "shukla", "tithi": 12, "kala": "sunrise", "major": true,
    "nameEn": "Ksheerabdi Dwadashi (Chiluka Dwadashi)", "nameTe": "క్షీరాబ్ది ద్వాదశి (చిలుక ద్వాదశి)",
    "descriptionEn": "Celebration of Tulasi-Damodara celestial wedding with gooseberry (amla) branch and sugarcane puja at dusk.",
    "descriptionTe": "తులసి కోటలో ఉసిరి కొమ్మను ఉంచి, చెరుకుగడలతో మండపం నిర్మించి తులసీ-ధాత్రీ సమేత దామోదర కల్యాణం జరిపించే విశేష పర్వదినం.",
    "ritualsEn": "Tulasi Damodara Kalyanam, lighting amla deepams, offering Poha and sweets.",
    "ritualsTe": "తులసి దామోదర కల్యాణం, ఉసిరి కాయలపై నేతి దీపాలు వెలిగించడం, చెరుకు ముక్కలు, అటుకులు నైవేద్యం."
  },
  {
    "id": "karthika_pournami", "type": "tithi", "month": "Kartika", "paksha": "shukla", "tithi": 15, "kala": "sunrise", "major": true,
    "nameEn": "Karthika Pournami", "nameTe": "కార్తీక పౌర్ణమి",
    "descriptionEn": "The pinnacle of the auspicious Karthika month. Devotees light 365 wicks, perform Jwala Toranam, and float lit diyas on water bodies.",
    "descriptionTe": "శివకేశవులకు అత్యంత ప్రీతిపాత్రమైన కార్తీక మాసపు మహోన్నత దినం. 365 వత్తులతో దీపారాధన, జ్వాలాతోరణ దర్శనం మరియు ఉసిరి చెట్టు కింద దీపాలు.",
    "ritualsEn": "365 wick lighting with cow ghee, Jwala Toranam crossing, floating diyas on rivers, Usiri deepam.",
    "ritualsTe": "గోవు నెయ్యితో 365 వత్తుల దీపం, గుడుల్లో జ్వాలాతోరణం కింద నడవడం, నదీ జలాల్లో దీపాలు వదలడం."
  },
  {
    "id": "karthika_amavasya", "type": "tithi", "month": "Kartika", "paksha": "krishna", "tithi": 15, "kala": "sunrise", "major": true,
    "nameEn": "Karthika Amavasya (Poli Swargam)", "nameTe": "కార్తీక అమావాస్య (కార్తీక మాస సమాప్తి / పోలి స్వర్గం)",
    "descriptionEn": "The concluding day of Karthika month. Women observe Poli Swargam vratham by floating 30-wick diyas on water bodies at dawn.",
    "descriptionTe": "కార్తీక మాసపు ముగింపు దినం. తెల్లవారుజామునే అరటి దొప్పలలో 30 వత్తుల దీపాలను వెలిగించి నదులు, చెరువుల్లో వదిలే పోలిపాడ్యమి/పోలి స్వర్గం నోము.",
    "ritualsEn": "Floating Poli deepams in water bodies at dawn, conclusion of Karthika Snana-Deepam vratham.",
    "ritualsTe": "తెల్లవారుజామున నదీ స్నానం, అరటి దొప్పలలో దీపాలు వెలిగించి వదలడం, బ్రాహ్మణ సమారాధన, కార్తీక మాస వ్రత సమాప్తి."
  },
  {
    "id": "vaikunta_ekadashi", "type": "tithiWithSunSign", "paksha": "shukla", "tithi": 11, "sunSign": "Dhanu", "kala": "sunrise", "major": true,
    "nameEn": "Vaikunta (Mukkoti) Ekadashi", "nameTe": "వైకుంఠ (ముక్కోటి) ఏకాదశి",
    "descriptionEn": "Devotees fast and pass through the Vaikunta Dwaram (northern gate) of Vishnu temples.",
    "descriptionTe": "ఉపవాసం ఉండి విష్ణు ఆలయాల ఉత్తర ద్వార (వైకుంఠ ద్వార) దర్శనం చేసుకునే పవిత్ర దినం."
  },
  {
    "id": "maha_shivaratri", "type": "tithi", "month": "Magha", "paksha": "krishna", "tithi": 14, "kala": "nishita", "major": true,
    "nameEn": "Maha Shivaratri", "nameTe": "మహా శివరాత్రి",
    "descriptionEn": "The great cosmic night of Lord Shiva. Devotees observe strict fasting (Upavasam) and night-long vigil (Jagaram).",
    "descriptionTe": "సదాశివుని లింగోద్భవ పుణ్యకాలం. పరమేశ్వరుని కృప కోసం రోజంతా ఉపవాసం, రాత్రంతా జాగరణతో శివనామస్మరణ చేసే పరమ పవిత్ర పర్వదినం.",
    "ritualsEn": "Maha Abhishekam with Panchamrutam, bilva leaves offering, Rudrabhishekam, and Lingodbhava puja at midnight.",
    "ritualsTe": "మారేడు దళాలతో పూజ, పంచామృతాభిషేకం, అర్ధరాత్రి లింగోద్భవ కాలంలో మహాన్యాసపూర్వక రుద్రాభిషేకం, జాగరణ."
  },
  {
    "id": "holi", "type": "tithi", "month": "Phalguna", "paksha": "shukla", "tithi": 15, "kala": "sunrise", "major": true,
    "nameEn": "Holi (Kamuni Pournami)", "nameTe": "హోలీ (కాముని పౌర్ణమి)",
    "descriptionEn": "The festival of colours on Phalguna Purnima.",
    "descriptionTe": "ఫాల్గుణ పౌర్ణమి నాడు జరుపుకునే రంగుల పండుగ."
  },
  {
    "id": "makara_sankranti", "type": "sankranti", "sign": "Makara", "major": true,
    "nameEn": "Makara Sankranti", "nameTe": "మకర సంక్రాంతి",
    "descriptionEn": "The harvest festival marking the Sun's transit into Capricorn. Celebrated with Bhogi fires, colorful muggulu, and sweet Pongal.",
    "descriptionTe": "సూర్యుడు మకర రాశిలోకి ప్రవేశించే పుణ్యకాలం. భోగి మంటలు, రంగురంగుల ముగ్గులు, గొబ్బెమ్మలు, హరిదాసుల కీర్తనలతో తెలుగు లోగిళ్లు కళకళలాడే పెద్ద పండుగ.",
    "ritualsEn": "Bhogi mantalu at dawn, preparing Pongal, kite flying, and Bommala Koluvu.",
    "ritualsTe": "తెల్లవారుజామునే భోగి మంటలు, చక్కెర పొంగలి నివేదన, గాలిపటాల సందడి, బొమ్మల కొలువు.",
    "offsets": [
      {
        "id": "bhogi", "days": -1, "nameEn": "Bhogi", "nameTe": "భోగి",
        "descriptionEn": "The first day of Sankranti, with Bhogi bonfires at dawn and Bhogi pallu for children.",
        "descriptionTe": "సంక్రాంతి మొదటి రోజు. తెల్లవారుజామున భోగి మంటలు, పిల్లలకు భోగి పళ్లు."
      },
      {
        "id": "kanuma", "days": 1, "nameEn": "Kanuma", "nameTe": "కనుమ (పశువుల పండుగ)",
        "descriptionEn": "A dedicated festival to honor cattle and agriculture tools that assist farmers throughout the year.",
        "descriptionTe": "వ్యవసాయంలో తోడ్పడే పశువులను, గోవులను పూజించే పండుగ. సంక్రాంతి ఉత్సవాల్లో రైతన్నలకు అత్యంత ప్రీతిపాత్రమైన దినం."
      }
    ]
  },
  {
    "id": "monthly_sankranti", "type": "everySankranti", "major": false,
    "nameEn": "{sign} Sankranti", "nameTe": "{sign} సంక్రమణం"
  },
  {
    "id": "ekadashi", "type": "everyTithi", "paksha": "both", "tithi": 11, "kala": "sunrise", "major": false,
    "nameEn": "{month} {tithi}", "nameTe": "{month} {tithi}"
  },
  {
    "id": "purnima", "type": "everyTithi", "paksha": "shukla", "tithi": 15, "kala": "sunrise", "major": false,
    "nameEn": "{month} Purnima", "nameTe": "{month} పౌర్ణమి"
  },
  {
    "id": "amavasya", "type": "everyTithi", "paksha": "krishna", "tithi": 15, "kala": "sunrise", "major": false,
    "nameEn": "{month} Amavasya", "nameTe": "{month} అమావాస్య"
  },
  {
    "id": "sankashti_chaturthi", "type": "everyTithi", "paksha": "krishna", "tithi": 4, "kala": "sunrise", "major": false,
    "nameEn": "{month} Sankashti Chaturthi", "nameTe": "{month} సంకష్టహర చతుర్థి"
  }
];
