package com.example.data.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class FestivalCalendarItem(
    val id: String,
    val nameEn: String,
    val nameTe: String,
    val dateString: String, // YYYY-MM-DD
    val monthGroupEn: String,
    val monthGroupTe: String,
    val tithiEn: String,
    val tithiTe: String,
    val descriptionEn: String,
    val descriptionTe: String,
    val ritualsEn: String,
    val ritualsTe: String
) {
    val date: LocalDate by lazy {
        try {
            LocalDate.parse(dateString)
        } catch (_: Exception) {
            LocalDate.now()
        }
    }

    fun getDaysLeft(fromDate: LocalDate = LocalDate.now()): Long {
        return ChronoUnit.DAYS.between(fromDate, date)
    }
}

object FestivalCalendarData {
    // TODO: verify dates against a printed Telugu calendar
    val allFestivals = listOf(
        FestivalCalendarItem(
            id = "sankranti_2026",
            nameEn = "Bhogi & Makara Sankranti",
            nameTe = "భోగి & మకర సంక్రాంతి",
            dateString = "2026-01-14",
            monthGroupEn = "January 2026",
            monthGroupTe = "జనవరి 2026",
            tithiEn = "Makara Sankranti Transition",
            tithiTe = "మకర సంక్రమణం, పుష్య బహుళ ఏకాదశి",
            descriptionEn = "The harvest festival marking the Sun's transit into Capricorn. Celebrated with Bhogi fires, colorful muggulu, and sweet Pongal.",
            descriptionTe = "సూర్యుడు మకర రాశిలోకి ప్రవేశించే పుణ్యకాలం. భోగి మంటలు, రంగురంగుల ముగ్గులు, గొబ్బెమ్మలు, హరిదాసుల కీర్తనలతో తెలుగు లోగిళ్లు కళకళలాడే పెద్ద పండుగ.",
            ritualsEn = "Bhogi mantalu at dawn, preparing Pongal, kite flying, and Bommala Koluvu.",
            ritualsTe = "తెల్లవారుజామునే భోగి మంటలు, చక్కెర పొంగలి నివేదన, గాలిపటాల సందడి, బొమ్మల కొలువు."
        ),
        FestivalCalendarItem(
            id = "kanuma_2026",
            nameEn = "Kanuma & Mukkanuma",
            nameTe = "కనుమ (పశువుల పండుగ)",
            dateString = "2026-01-15",
            monthGroupEn = "January 2026",
            monthGroupTe = "జనవరి 2026",
            tithiEn = "Pushya Bahula Dwadasi",
            tithiTe = "పుష్య బహుళ ద్వాదశి",
            descriptionEn = "A dedicated festival to honor cattle and agriculture tools that assist farmers throughout the year.",
            descriptionTe = "వ్యవసాయంలో తోడ్పడే పశువులను, గోవులను పూజించే పండుగ. సంక్రాంతి ఉత్సవాల్లో రైతన్నలకు అత్యంత ప్రీతిపాత్రమైన దినం.",
            ritualsEn = "Worshipping decorated cattle with turmeric and vermilion, distributing sweet pongali, family gatherings.",
            ritualsTe = "గోవులను శుభ్రపరిచి పసుపు కుంకుమలతో అలంకరించి పూజించడం, గారెలు, పిండివంటలు ఆరగించడం."
        ),
        FestivalCalendarItem(
            id = "maha_shivaratri_2026",
            nameEn = "Maha Shivaratri",
            nameTe = "మహా శివరాత్రి",
            dateString = "2026-02-15",
            monthGroupEn = "February 2026",
            monthGroupTe = "ఫిబ్రవరి 2026",
            tithiEn = "Magha Bahula Chaturdasi",
            tithiTe = "మాఘ బహుళ చతుర్దశి",
            descriptionEn = "The great cosmic night of Lord Shiva. Devotees observe strict fasting (Upavasam) and night-long vigil (Jagaram).",
            descriptionTe = "సదాశివుని లింగోద్భవ పుణ్యకాలం. పరమేశ్వరుని కృప కోసం రోజంతా ఉపవాసం, రాత్రంతా జాగరణతో శివనామస్మరణ చేసే పరమ పవిత్ర పర్వదినం.",
            ritualsEn = "Maha Abhishekam with Panchamrutam, bilva leaves offering, Rudrabhishekam, and Lingodbhava puja at midnight.",
            ritualsTe = "మారేడు దళాలతో పూజ, పంచామృతాభిషేకం, అర్ధరాత్రి లింగోద్భవ కాలంలో మహాన్యాసపూర్వక రుద్రాభిషేకం, జాగరణ."
        ),
        FestivalCalendarItem(
            id = "ugadi_2026",
            nameEn = "Ugadi (Telugu New Year)",
            nameTe = "ఉగాది (తెలుగు నూతన సంవత్సరం)",
            dateString = "2026-03-20",
            monthGroupEn = "March 2026",
            monthGroupTe = "మార్చి 2026",
            tithiEn = "Chaitra Suddha Padyami",
            tithiTe = "చైత్ర శుద్ధ పాడ్యమి",
            descriptionEn = "The dawn of the new Telugu astronomical year. Marked by tasting Shadruchula Ugadi Pachadi and attending Panchanga Sravanam.",
            descriptionTe = "తెలుగు వారి ఆద్యంతమైన నూతన సంవత్సర ప్రారంభం. జీవితంలోని సుఖదుఃఖాల సమ్మిళితాన్ని తెలిపే షడ్రుచుల ఉగాది పచ్చడి మరియు పంచాంగ శ్రవణం ప్రధాన విశేషాలు.",
            ritualsEn = "Tasting Shadruchula Ugadi Pachadi, oil bath with sun rise prayers, Panchanga Sravanam, and wearing new traditional clothes.",
            ritualsTe = "ఉగాది పచ్చడి సేవనం, తైలాభ్యంగన స్నానం, సాయంత్రం దేవాలయాల్లో పంచాంగ శ్రవణం మరియు కవి సమ్మేళనాలు."
        ),
        FestivalCalendarItem(
            id = "sri_rama_navami_2026",
            nameEn = "Sri Rama Navami",
            nameTe = "శ్రీరామ నవమి",
            dateString = "2026-03-28",
            monthGroupEn = "March 2026",
            monthGroupTe = "మార్చి 2026",
            tithiEn = "Chaitra Suddha Navami",
            tithiTe = "చైత్ర శుద్ధ నవమి",
            descriptionEn = "Birth anniversary of Lord Sri Rama and celestial wedding (Kalyanotsavam) of Sita and Rama celebrated grandly at Bhadrachalam.",
            descriptionTe = "మర్యాదా పురుషోత్తముడు శ్రీరామచంద్రుని జన్మదినం మరియు సీతారాముల కల్యాణ మహోత్సవం. భద్రాచలంతో పాటు ప్రతి ఊరూవాడా కల్యాణ వేడుకలు.",
            ritualsEn = "Sita Rama Kalyanam ceremony, preparation and distribution of Panakam and Vadapappu, recitation of Sundarakanda.",
            ritualsTe = "సీతారాముల దివ్య కల్యాణం, చల్లని పానకం, వడపప్పు నివేదన, సుందరకాండ పారాయణం."
        ),
        FestivalCalendarItem(
            id = "varalakshmi_vratham_2026",
            nameEn = "Varalakshmi Vratham",
            nameTe = "వరలక్ష్మీ వ్రతం",
            dateString = "2026-08-21",
            monthGroupEn = "August 2026",
            monthGroupTe = "ఆగస్టు 2026",
            tithiEn = "Sravana Suddha Sukravaram",
            tithiTe = "శ్రావణ శుద్ధ శుక్రవారం",
            descriptionEn = "Sacred vratham observed by married Telugu women seeking family welfare, longevity, and prosperity by worshipping Goddess Lakshmi.",
            descriptionTe = "సౌభాగ్యం, ఆయురారోగ్యాలు, సిరిసంపదల కోసం స్త్రీలు భక్తిశ్రద్ధలతో ఆచరించే శ్రావణ మాసపు విశేష వరలక్ష్మీ దేవి పూజ.",
            ritualsEn = "Kalasha sthapana with coconut and gold/silver face of Lakshmi, tying nine-knot sacred thread (Toram), offering 9 types of sweets.",
            ritualsTe = "కలశ స్థాపన, లక్ష్మీదేవి అలంకరణ, తొమ్మిది ముడుల నోము దారం (తోరం) కట్టుకోవడం, తొమ్మిది రకాల పిండివంటల నైవేద్యం."
        ),
        FestivalCalendarItem(
            id = "vinayaka_chavithi_2026",
            nameEn = "Vinayaka Chavithi (Ganesh Chaturthi)",
            nameTe = "వినాయక చవితి",
            dateString = "2026-09-14",
            monthGroupEn = "September 2026",
            monthGroupTe = "సెప్టెంబరు 2026",
            tithiEn = "Bhadrapada Suddha Chavithi",
            tithiTe = "భాద్రపద శుద్ధ చవితి",
            descriptionEn = "Celebration of Lord Ganesha, the remover of obstacles. Clay idols are worshipped with 21 varieties of leaves (Patri).",
            descriptionTe = "విఘ్ననాశకుడు గణపతి అవతరించిన పర్వదినం. మట్టి గణపతుల పూజ, ఏకవింశతి పత్ర పూజ (21 రకాల పత్రి) మరియు మోదకాలు, కుడుములు సమర్పణ.",
            ritualsEn = "21 Patri puja, offering Undrallu, Kudumulu, and Modaks; listening to Syamantakopakhyanam story.",
            ritualsTe = "21 రకాల ఆకులతో పత్రి పూజ, ఉండ్రాళ్ళు, కుడుములు, లడ్డూల నైవేద్యం, శ్యమంతకోపాఖ్యానం కథ వినడం."
        ),
        FestivalCalendarItem(
            id = "bathukamma_2026",
            nameEn = "Bathukamma Festival",
            nameTe = "బతుకమ్మ పండుగ",
            dateString = "2026-10-10",
            monthGroupEn = "October 2026",
            monthGroupTe = "అక్టోబరు 2026",
            tithiEn = "Asvayuja Suddha Padyami to Navami",
            tithiTe = "ఆశ్వయుజ శుద్ధ పాడ్యమి నుండి మహర్నవమి",
            descriptionEn = "Telangana's grand state floral festival worshipping Mother Nature and Goddess Gauri with seasonal flowers and folk songs.",
            descriptionTe = "తెలంగాణ సంస్కృతీ సంప్రదాయాల పూల పండుగ. తీరొక్క పూలతో అందంగా బతుకమ్మను పేర్చి, గౌరమ్మను కొలుస్తూ మహిళల పాటలు, కోలాటాలు.",
            ritualsEn = "Arranging flowers in concentric layers, singing folk songs around Bathukamma, offering Maleeda.",
            ritualsTe = "గునుగు, తంగేడు, బంతి పూలతో బతుకమ్మ తయారీ, చప్పట్లతో పాటలు, మలీద ప్రసాదం నివేదన."
        ),
        FestivalCalendarItem(
            id = "vijaya_dasami_2026",
            nameEn = "Vijaya Dasami (Dasara)",
            nameTe = "విజయదశమి (దసరా / శమీ పూజ)",
            dateString = "2026-10-20",
            monthGroupEn = "October 2026",
            monthGroupTe = "అక్టోబరు 2026",
            tithiEn = "Asvayuja Suddha Dasami",
            tithiTe = "ఆశ్వయుజ శుద్ధ దశమి",
            descriptionEn = "Victory of good over evil as Goddess Durga vanquished Mahishasura. Families perform Ayudha Puja and Shami (Jammi chettu) puja.",
            descriptionTe = "చెడుపై మంచి సాధించిన విజయోత్సవం. దుర్గాదేవి మహిషాసురమర్దినిగా విజయసారథ్యం వహించిన దినం. జమ్మి పూజ, ఆయుధ పూజ మరియు పాలపిట్ట దర్శనం.",
            ritualsEn = "Shami puja at sunset, exchanging Jammi leaves with elders with blessings, Ayudha puja for vehicles.",
            ritualsTe = "సాయంత్రం జమ్మి చెట్టు పూజ, 'శమీ శమయతే పాపం' అంటూ జమ్మి ఆకులు ఇచ్చి పెద్దల ఆశీస్సులు తీసుకోవడం."
        ),
        FestivalCalendarItem(
            id = "deepavali_2026",
            nameEn = "Deepavali (Diwali)",
            nameTe = "దీపావళి & నరక చతుర్దశి",
            dateString = "2026-11-08",
            monthGroupEn = "November 2026",
            monthGroupTe = "నవంబరు 2026",
            tithiEn = "Asvayuja Bahula Amavasya",
            tithiTe = "ఆశ్వయుజ బహుళ అమావాస్య",
            descriptionEn = "The festival of lights celebrating the slaying of Narakasura by Satyabhama and Krishna. Earthen oil diyas and Lakshmi puja.",
            descriptionTe = "చీకటిని పారద్రోలి వెలుగులు నింపే దివ్య దీపాల పండుగ. సత్యభామా సమేత శ్రీకృష్ణుడి నరకాసుర వధ స్మరణ, లక్ష్మీ కుబేర పూజలు, బాణాసంచా సంబరం.",
            ritualsEn = "Morning oil bath, Lakshmi-Kubera puja at dusk, lighting clay diyas, fireworks, sharing sweets.",
            ritualsTe = "నువ్వుల నూనెతో తలస్నానం, సాయంత్రం మట్టి ప్రమిదలతో దీపాలంకరణ, లక్ష్మీదేవి పూజ, బాణాసంచా కాల్చడం."
        ),
        FestivalCalendarItem(
            id = "karthika_pournami_2026",
            nameEn = "Karthika Pournami",
            nameTe = "కార్తీక పౌర్ణమి",
            dateString = "2026-11-24",
            monthGroupEn = "November 2026",
            monthGroupTe = "నవంబరు 2026",
            tithiEn = "Karthika Suddha Pournami",
            tithiTe = "కార్తీక శుద్ధ పౌర్ణమి",
            descriptionEn = "The pinnacle of the auspicious Karthika month. Devotees light 365 wicks, perform Jwala Toranam, and float lit diyas on water bodies.",
            descriptionTe = "శివకేశవులకు అత్యంత ప్రీతిపాత్రమైన కార్తీక మాసపు మహోన్నత దినం. 365 వత్తులతో దీపారాధన, జ్వాలాతోరణ దర్శనం మరియు ఉసిరి చెట్టు కింద దీపాలు.",
            ritualsEn = "365 wick lighting with cow ghee, Jwala Toranam crossing, floating diyas on rivers, Usiri deepam.",
            ritualsTe = "గోవు నెయ్యితో 365 వత్తుల దీపం, గుడుల్లో జ్వాలాతోరణం కింద నడవడం, నదీ జలాల్లో దీపాలు వదలడం."
        ),
        FestivalCalendarItem(
            id = "sankranti_2027",
            nameEn = "Makara Sankranti 2027",
            nameTe = "మకర సంక్రాంతి 2027",
            dateString = "2027-01-14",
            monthGroupEn = "January 2027",
            monthGroupTe = "జనవరి 2027",
            tithiEn = "Pushya Suddha Saptami",
            tithiTe = "పుష్య శుద్ధ సప్తమి",
            descriptionEn = "Solar transit celebration with sweet pongal, Rangoli, and family gatherings.",
            descriptionTe = "సూర్య భగవానుని పుణ్య సంక్రమణం, కొత్త బియ్యంతో పరమాన్నం, ముగ్గులు, బంధుమిత్రుల కలయికల సంక్రాంతి పండుగ.",
            ritualsEn = "Surya puja, charity, sesame seeds offerings, and Bhogi pallu for children.",
            ritualsTe = "సూర్యనమస్కారాలు, నువ్వుల లడ్డూల నైవేద్యం, పిల్లలకు భోగి పండ్లు పోయడం."
        ),
        FestivalCalendarItem(
            id = "maha_shivaratri_2027",
            nameEn = "Maha Shivaratri 2027",
            nameTe = "మహా శివరాత్రి 2027",
            dateString = "2027-03-06",
            monthGroupEn = "March 2027",
            monthGroupTe = "మార్చి 2027",
            tithiEn = "Magha Bahula Chaturdasi",
            tithiTe = "మాఘ బహుళ చతుర్దశి",
            descriptionEn = "All-night wakefulness and chanting Om Namah Shivaya for spiritual elevation.",
            descriptionTe = "శంకరుని అనుగ్రహం కోసం జాగరణ, అభిషేకాలు, ఓం నమః శివాయ నామస్మరణతో పవిత్రమైన రాత్రి.",
            ritualsEn = "Continuous bilva leaves archana, fasting, listening to Shiva Puranam.",
            ritualsTe = "మారేడు దళాలతో అష్టోత్తర పూజ, శివ పురాణ పారాయణం, జాగరణ."
        ),
        FestivalCalendarItem(
            id = "ugadi_2027",
            nameEn = "Ugadi 2027 (Telugu New Year)",
            nameTe = "ఉగాది 2027",
            dateString = "2027-04-07",
            monthGroupEn = "April 2027",
            monthGroupTe = "ఏప్రిల్ 2027",
            tithiEn = "Chaitra Suddha Padyami",
            tithiTe = "చైత్ర శుద్ధ పాడ్యమి",
            descriptionEn = "Commencement of the 2027-2028 Telugu astronomical year with Pachadi and almanac blessings.",
            descriptionTe = "నూతన సంవత్సర పంచాంగ శ్రవణం, ఉగాది పచ్చడి ఆరగింపు, రాశి ఫలాల విశ్లేషణ.",
            ritualsEn = "Ugadi Pachadi preparation, temple visit, listening to annual horoscope.",
            ritualsTe = "ఉగాది పచ్చడి నైవేద్యం, తోరణాలు కట్టడం, పంచాంగ పఠనం, ఇష్టదైవ దర్శనం."
        )
    )

    fun getUpcomingFestivals(today: LocalDate = LocalDate.now()): List<FestivalCalendarItem> {
        return allFestivals.filter { !it.date.isBefore(today) }
    }

    fun getNextFestival(today: LocalDate = LocalDate.now()): FestivalCalendarItem {
        return getUpcomingFestivals(today).firstOrNull() ?: allFestivals.first()
    }
}
