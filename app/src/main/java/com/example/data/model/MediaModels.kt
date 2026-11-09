package com.example.data.model

data class LiveChannel(
    val id: String,
    val nameTe: String,
    val nameEn: String,
    val categoryTe: String,
    val categoryEn: String,
    val isDevotional: Boolean,
    val currentProgramTe: String,
    val currentProgramEn: String,
    val nextProgramTe: String,
    val nextProgramEn: String,
    val viewersCount: String,
    val descriptionTe: String,
    val descriptionEn: String
)

data class NewsItem(
    val id: String,
    val titleTe: String,
    val titleEn: String,
    val summaryTe: String,
    val summaryEn: String,
    val categoryTe: String,
    val categoryEn: String,
    val timeAgoTe: String,
    val timeAgoEn: String,
    val source: String,
    val readMinutes: Int,
    val isBookmarked: Boolean = false
)

data class Festival(
    val id: String,
    val nameTe: String,
    val nameEn: String,
    val dateDisplayTe: String,
    val dateDisplayEn: String,
    val daysLeft: Int,
    val tithiTe: String,
    val tithiEn: String,
    val significanceTe: String,
    val significanceEn: String,
    val ritualsTe: String,
    val ritualsEn: String
)

data class RadioStation(
    val id: String,
    val nameTe: String,
    val nameEn: String,
    val categoryTe: String,
    val categoryEn: String,
    val nowPlayingTe: String,
    val nowPlayingEn: String,
    val artistTe: String,
    val artistEn: String,
    val bitrate: String
)

data class CricketMatch(
    val id: String,
    val title: String,
    val tournament: String,
    val team1: String,
    val team1Flag: String,
    val team1Score: String,
    val team1Overs: String,
    val team2: String,
    val team2Flag: String,
    val team2Score: String,
    val team2Overs: String,
    val statusTe: String,
    val statusEn: String,
    val highlightTe: String,
    val highlightEn: String,
    val venue: String
)

object MediaSampleData {
    val channels = listOf(
        LiveChannel(
            id = "bhakthi_tv",
            nameTe = "భక్తి టీవీ",
            nameEn = "Bhakthi TV",
            categoryTe = "భక్తి & ఆధ్యాత్మికం",
            categoryEn = "Devotional",
            isDevotional = true,
            currentProgramTe = "శ్రీ కాశీ విశ్వనాథ్ ప్రత్యక్ష హారతి",
            currentProgramEn = "Sri Kashi Vishwanath Live Aarati",
            nextProgramTe = "గరుడ పురాణం ప్రవచనం - బ్రహ్మశ్రీ చాగంటి",
            nextProgramEn = "Garuda Puranam Discourse by Brahmasri Chaganti",
            viewersCount = "48.2K",
            descriptionTe = "దక్షిణ భారతదేశపు అగ్రశ్రేణి ఆధ్యాత్మిక ఛానల్. నిత్య పూజలు, ప్రవచనాలు.",
            descriptionEn = "South India's premier 24x7 spiritual & devotional broadcast."
        ),
        LiveChannel(
            id = "svbc",
            nameTe = "ఎస్వీబీసీ (తిరుమల లైవ్)",
            nameEn = "SVBC TTD Live",
            categoryTe = "తిరుమల ప్రత్యక్ష ప్రసారం",
            categoryEn = "Tirumala Live",
            isDevotional = true,
            currentProgramTe = "తిరుమల శ్రీవారి నిత్య కల్యాణోత్సవం",
            currentProgramEn = "Tirumala Srivari Nitya Kalyanotsavam",
            nextProgramTe = "శ్రీ వేంకటేశ్వర సహస్రనామార్చన",
            nextProgramEn = "Sri Venkateswara Sahasranamarchana",
            viewersCount = "124.5K",
            descriptionTe = "తిరుమల తిరుపతి దేవస్థానం అధికారిక ఆధ్యాత్మిక ఛానల్.",
            descriptionEn = "Official devotional channel of Tirumala Tirupati Devasthanams."
        ),
        LiveChannel(
            id = "tv9",
            nameTe = "టీవీ9 తెలుగు",
            nameEn = "TV9 Telugu",
            categoryTe = "వార్తలు & విశ్లేషణ",
            categoryEn = "News & Analysis",
            isDevotional = false,
            currentProgramTe = "న్యూస్ అవర్: తెలుగు రాష్ట్రాల తాజా వార్తలు",
            currentProgramEn = "News Hour: Telugu States Latest Updates",
            nextProgramTe = "బిగ్ న్యూస్ బిగ్ డిబేట్",
            nextProgramEn = "Big News Big Debate with Special Guests",
            viewersCount = "89.1K",
            descriptionTe = "తెలుగు నాట అత్యధిక వీక్షకాదరణ కలిగిన వార్తా ఛానల్.",
            descriptionEn = "Leading Telugu 24/7 news and investigative journalism."
        ),
        LiveChannel(
            id = "ntv",
            nameTe = "ఎన్‌టీవీ న్యూస్",
            nameEn = "NTV News",
            categoryTe = "వార్తలు",
            categoryEn = "24x7 News",
            isDevotional = false,
            currentProgramTe = "ప్రైమ్ టైమ్ బులెటిన్ & విశ్లేషణ",
            currentProgramEn = "Prime Time Bulletin & Ground Reports",
            nextProgramTe = "స్పీడ్ న్యూస్ 100",
            nextProgramEn = "Speed News 100 Non-Stop",
            viewersCount = "65.4K",
            descriptionTe = "నిష్పాక్షిక వార్తలు, జాతీయ మరియు ప్రాంతీయ సమగ్ర కథనాలు.",
            descriptionEn = "Unbiased breaking news, Andhra Pradesh & Telangana focus."
        ),
        LiveChannel(
            id = "dd_saptagiri",
            nameTe = "డీడీ సప్తగిరి",
            nameEn = "DD Saptagiri",
            categoryTe = "దూరదర్శన్ & సంస్కృతి",
            categoryEn = "Culture & Heritage",
            isDevotional = true,
            currentProgramTe = "గ్రామీణ రైతు నేస్తం & జానపద కళలు",
            currentProgramEn = "Rythu Nestham & Telugu Folk Arts",
            nextProgramTe = "తెలుగు ప్రాచీన ఆలయాల వైభవం",
            nextProgramEn = "Glory of Ancient Telugu Temples",
            viewersCount = "22.0K",
            descriptionTe = "భారత ప్రభుత్వ అధికారిక దూరదర్శన్ తెలుగు ప్రసారాలు.",
            descriptionEn = "Official public broadcaster showcasing Telugu heritage."
        ),
        LiveChannel(
            id = "v6_news",
            nameTe = "వి6 న్యూస్",
            nameEn = "V6 News",
            categoryTe = "తెలంగాణ వార్తలు & జానపదం",
            categoryEn = "Telangana News",
            isDevotional = false,
            currentProgramTe = "తీన్మార్ వార్తలు విత్ బిత్తిరి సత్తి",
            currentProgramEn = "Teenmaar News Special Satire",
            nextProgramTe = "తెలంగాణ టాప్ స్టోరీస్",
            nextProgramEn = "Telangana Top Stories Tonight",
            viewersCount = "41.8K",
            descriptionTe = "తెలంగాణ యాస, సంస్కృతి, రాజకీయ ప్రత్యక్ష విశ్లేషణలు.",
            descriptionEn = "Telangana native perspective, grassroots news and humor."
        )
    )

    val newsList = listOf(
        NewsItem(
            id = "n1",
            titleTe = "కార్తీక సోమవారం పురస్కరించుకుని శ్రీశైలంలో భక్తుల రద్దీ",
            titleEn = "Heavy Pilgrim Influx at Srisailam on Auspicious Karthika Somavaram",
            summaryTe = "పవిత్ర కార్తీక మాసం సందర్భంగా మల్లికార్జున స్వామి దర్శనానికి తెలుగు రాష్ట్రాల నుంచి వేలాదిగా తరలివచ్చిన భక్తులు. పాతాళగంగలో పుణ్యస్నానాలు.",
            summaryEn = "Thousands of devotees from Andhra Pradesh and Telangana throng Srisailam temple for holy bath in Pathalaganga and Mallikarjuna Swamy darshan.",
            categoryTe = "ఆధ్యాత్మికం",
            categoryEn = "Devotional",
            timeAgoTe = "15 నిమిషాల క్రితం",
            timeAgoEn = "15 mins ago",
            source = "Shubhodaya Bhakthi Desk",
            readMinutes = 2
        ),
        NewsItem(
            id = "n2",
            titleTe = "రైతులకు సకాలంలో మద్దతు ధర, ధాన్యం కొనుగోలు కేంద్రాలు ప్రారంభం",
            titleEn = "Grain Procurement Centers Inaugurated to Support Telugu Farmers",
            summaryTe = "ఖరీఫ్ ధాన్యం సేకరణకు ప్రభుత్వం అన్ని ఏర్పాట్లు పూర్తి చేసింది. రైతు ఖాతాల్లో 48 గంటల్లో నేరుగా నగదు జమ చేసేలా చర్యలు.",
            summaryEn = "Government expedites kharif paddy procurement with direct bank transfers within 48 hours for paddy farmers across major districts.",
            categoryTe = "ఆంధ్రప్రదేశ్",
            categoryEn = "Andhra Pradesh",
            timeAgoTe = "1 గంట క్రితం",
            timeAgoEn = "1 hour ago",
            source = "Telugu Vartha Desk",
            readMinutes = 3
        ),
        NewsItem(
            id = "n3",
            titleTe = "హైదరాబాద్ మెట్రో రెండవ దశ విస్తరణకు కేంద్ర కేబినెట్ ఆమోదం",
            titleEn = "Cabinet Approves Hyderabad Metro Phase 2 Connectivity Expansion",
            summaryTe = "శంషాబాద్ విమానాశ్రయం మరియు హైటెక్ సిటీ కారిడార్ల అనుసంధానానికి లైన్ క్లియర్. ప్రజారవాణాలో నూతన మైలురాయి.",
            summaryEn = "Clearance granted for Hyderabad Metro Phase 2 corridors linking Shamshabad Airport and financial district to enhance rapid transit.",
            categoryTe = "తెలంగాణ",
            categoryEn = "Telangana",
            timeAgoTe = "2 గంటల క్రితం",
            timeAgoEn = "2 hours ago",
            source = "Metro Express",
            readMinutes = 2
        ),
        NewsItem(
            id = "n4",
            titleTe = "ఆస్ట్రేలియాతో బోర్డర్-గవాస్కర్ ట్రోఫీ: భారత తుది జట్టు ఖరారు",
            titleEn = "Border-Gavaskar Trophy: Team India Final Playing XI Confirmed",
            summaryTe = "పెర్త్ టెస్టుకు పటిష్టమైన కూర్పుతో బరిలోకి దిగుతున్న భారత్. నెట్స్ లో చెమటోడుస్తున్న యంగ్ బ్యాటర్లు మరియు బుమ్రా.",
            summaryEn = "Indian squad finalized for the test series opener with fierce net sessions and Jasprit Bumrah spearheading the bowling attack.",
            categoryTe = "క్రీడలు",
            categoryEn = "Sports",
            timeAgoTe = "3 గంటల క్రితం",
            timeAgoEn = "3 hours ago",
            source = "CricTelugu Updates",
            readMinutes = 3
        ),
        NewsItem(
            id = "n5",
            titleTe = "తిరుమల శ్రీవారి లడ్డూ ప్రసాదం నాణ్యత పెంపునకు ప్రత్యేక సాంకేతిక కమిటీ",
            titleEn = "Special Technical Panel Instituted for Srivari Laddu Prasadam Quality",
            summaryTe = "స్వచ్ఛమైన ఆవు నెయ్యి మరియు సాంప్రదాయ సుగంధ ద్రవ్యాల సేకరణపై నిరంతర నాణ్యతా పరీక్షలు నిర్వహించాలని నిర్ణయం.",
            summaryEn = "TTD establishes an expert oversight committee to ensure purest desi cow ghee and authentic ingredients for divine laddu preparation.",
            categoryTe = "ఆధ్యాత్మికం",
            categoryEn = "Devotional",
            timeAgoTe = "4 గంటల క్రితం",
            timeAgoEn = "4 hours ago",
            source = "Tirumala News",
            readMinutes = 2
        )
    )

    val festivals = listOf(
        Festival(
            id = "f1",
            nameTe = "కార్తీక దీపోత్సవం",
            nameEn = "Karthika Deepotsavam",
            dateDisplayTe = "కార్తీక పౌర్ణమి (నవంబర్ 15)",
            dateDisplayEn = "Karthika Pournami (Nov 15)",
            daysLeft = 9,
            tithiTe = "కార్తీక శుక్ల పూర్ణిమ",
            tithiEn = "Karthika Shukla Pournami",
            significanceTe = "శివాలయాల్లో జ్వాలా తోరణం, 365 వత్తులతో దీపారాధన. కోటి జన్మల పాపాలు హరిస్తాయని విశ్వాసం.",
            significanceEn = "Lighting 365 wicks and lamps in Shiva shrines, Jwala Toranam and sacred deepam rituals.",
            ritualsTe = "ఉదయం నదీ స్నానం, సాయంత్రం తులసి కోట వద్ద దీపాలు, ఉసిరి దీప సమర్పణ.",
            ritualsEn = "Holy bath at sunrise, lighting amla lamps and offering worship at the sacred Tulasi altar."
        ),
        Festival(
            id = "f2",
            nameTe = "వైకుంఠ ఏకాదశి",
            nameEn = "Vaikunta Ekadasi",
            dateDisplayTe = "మార్గశిర శుక్ల ఏకాదశి (డిసెంబర్ 23)",
            dateDisplayEn = "Margashirsha Shukla Ekadasi (Dec 23)",
            daysLeft = 47,
            tithiTe = "మార్గశిర శుక్ల ఏకాదశి (ముక్కోటి)",
            tithiEn = "Margashirsha Shukla Ekadashi (Mukkoti)",
            significanceTe = "ఉత్తర ద్వార దర్శనం ద్వారా మోక్ష ప్రాప్తి. ముక్కోటి దేవతలు శ్రీ మహావిష్ణువును దర్శించుకునే పుణ్యదినం.",
            significanceEn = "Passing through the North Gate (Uttara Dwara) bestows eternal liberation and divine grace.",
            ritualsTe = "పూర్తి ఉపవాసం, రాత్రి జాగరణ, విష్ణు సహస్రనామ పారాయణ, గీతా జయంతి పఠనం.",
            ritualsEn = "Complete fasting, night vigil, chanting Sri Vishnu Sahasranama and Bhagavad Gita recitation."
        ),
        Festival(
            id = "f3",
            nameTe = "మకర సంక్రాంతి",
            nameEn = "Makara Sankranti",
            dateDisplayTe = "పుష్య మాసం (జనవరి 14-16)",
            dateDisplayEn = "Pushya Maasam (Jan 14-16)",
            daysLeft = 69,
            tithiTe = "భోగి, సంక్రాంతి, కనుమ",
            tithiEn = "Bhogi, Sankranti, Kanuma",
            significanceTe = "సూర్యుడి ఉత్తరాయణ పుణ్యకాల ప్రవేశం. తెలుగు వారి అతి పెద్ద పంట పండుగ.",
            significanceEn = "Sun's transition into Capricorn marking Uttarayana, the harvest festival of Telugu households.",
            ritualsTe = "భోగి మంటలు, ముగ్గులు, గొబ్బెమ్మలు, హరిదాసు కీర్తనలు, పిండివంటలు.",
            ritualsEn = "Bhogi bonfire, ornate rangolis, Gobbemmalu, Haridasu hymns, and traditional festive delicacies."
        ),
        Festival(
            id = "f4",
            nameTe = "మహా శివరాత్రి",
            nameEn = "Maha Shivaratri",
            dateDisplayTe = "మాఘ బహుళ చతుర్దశి (ఫిబ్రవరి 26)",
            dateDisplayEn = "Magha Bahula Chaturdashi (Feb 26)",
            daysLeft = 112,
            tithiTe = "మాఘ కృష్ణ చతుర్దశి",
            tithiEn = "Magha Krishna Chaturdashi",
            significanceTe = "పరమశివుడు లింగోద్భవమైన పరమ పవిత్ర రాత్రి. లింగాష్టకం, రుద్రాభిషేకాలు.",
            significanceEn = "Auspicious divine night of Shiva's cosmic Lingodbhava dance and supreme spiritual awakening.",
            ritualsTe = "రోజంతా ఉపవాసం, లింగోద్భవ కాలంలో అభిషేకం, రాత్రంతా జాగరణ.",
            ritualsEn = "Day-long fast, Rudrabhishekam with bilva leaves during Lingodbhava kalam, and blissful night vigil."
        ),
        Festival(
            id = "f5",
            nameTe = "శ్రీ ఉగాది (తెలుగు నూతన సంవత్సరం)",
            nameEn = "Sri Ugadi (Telugu New Year)",
            dateDisplayTe = "చైత్ర శుద్ధ పాడ్యమి (మార్చి 29)",
            dateDisplayEn = "Chaitra Shuddha Padyami (Mar 29)",
            daysLeft = 143,
            tithiTe = "చైత్ర శుక్ల పాడ్యమి",
            tithiEn = "Chaitra Shukla Padyami",
            significanceTe = "తెలుగు వారి సంవత్సరాది. షడ్రుచుల ఉగాది పచ్చడి మరియు పంచాంగ శ్రవణం.",
            significanceEn = "Telugu New Year marking fresh cosmic cycle, Panchanga Sravanam and six-taste festive chutney.",
            ritualsTe = "నూతన వస్త్రధారణ, ఉగాది పచ్చడి సేవనం, రాశిఫలాలు మరియు పంచాంగ శ్రవణం.",
            ritualsEn = "Wearing fresh attire, tasting the symbolic Ugadi Pachadi, and gathering for royal Panchanga Sravanam."
        )
    )

    val radioStations = listOf(
        RadioStation(
            id = "r1",
            nameTe = "శ్రీవారి సుప్రభాతం & స్తోత్రాలు",
            nameEn = "Srivari Suprabhatam & Chants",
            categoryTe = "నిత్య ప్రార్థనలు",
            categoryEn = "Daily Chants",
            nowPlayingTe = "కౌసల్యా సుప్రజా రామ పూర్వా సంధ్యా ప్రవర్తతే",
            nowPlayingEn = "Kausalya Supraja Rama Poorva Sandhya Pravartate",
            artistTe = "ఎం.ఎస్. సుబ్బులక్ష్మి",
            artistEn = "M.S. Subbulakshmi",
            bitrate = "320 kbps HD"
        ),
        RadioStation(
            id = "r2",
            nameTe = "భక్తి తరంగిణి (అన్నమయ్య కీర్తనలు)",
            nameEn = "Bhakti Tarangini (Annamayya)",
            categoryTe = "సంకీర్తనలు",
            categoryEn = "Sacred Hymns",
            nowPlayingTe = "బ్రహ్మ కడిగిన పాదము బ్రహ్మమ్యె ఈ పాదము",
            nowPlayingEn = "Brahma Kadigina Paadamu",
            artistTe = "డాక్టర్ బాలమురళీకృష్ణ",
            artistEn = "Dr. M. Balamuralikrishna",
            bitrate = "256 kbps"
        ),
        RadioStation(
            id = "r3",
            nameTe = "తెలుగు గోల్డెన్ క్లాసిక్స్ రేడియో",
            nameEn = "Telugu Golden Classics FM",
            categoryTe = "పాత మధుర గీతాలు",
            categoryEn = "Vintage Melodies",
            nowPlayingTe = "జగదానంద కారక జయ జానకీ ప్రాణ నాయక",
            nowPlayingEn = "Jagadananda Karaka Jaya Janaki",
            artistTe = "ఘంటసాల & పి.సుశీల",
            artistEn = "Ghantasala & P. Susheela",
            bitrate = "320 kbps HD"
        ),
        RadioStation(
            id = "r4",
            nameTe = "శివ తత్వ నాదం (శివ స్తోత్రాలు)",
            nameEn = "Shiva Tatva Nadam",
            categoryTe = "మహా రుద్రం",
            categoryEn = "Maha Rudram",
            nowPlayingTe = "నమస్తే అస్తు భగవన్ విశ్వేశ్వరాయ మహాదేవాయ",
            nowPlayingEn = "Sri Rudram Chamakam Namakam",
            artistTe = "వేద పండితుల బృందం",
            artistEn = "Vedic Scholars Troupe",
            bitrate = "320 kbps HD"
        )
    )

    val cricketMatches = listOf(
        CricketMatch(
            id = "c1",
            title = "IND vs AUS - 1st Test (Border-Gavaskar Trophy)",
            tournament = "Border-Gavaskar Trophy 2026",
            team1 = "India",
            team1Flag = "🇮🇳",
            team1Score = "284/5",
            team1Overs = "78.2 ov",
            team2 = "Australia",
            team2Flag = "🇦🇺",
            team2Score = "Yet to bat",
            team2Overs = "",
            statusTe = "లైవ్: మొదటి రోజు టీ బ్రేక్",
            statusEn = "Live: Day 1 - Tea Break",
            highlightTe = "భారత బ్యాటర్ల సంయమనం. విరాట్ & పంత్ అర్ధ శతకాలు.",
            highlightEn = "Composed partnership. Kohli and Pant steady the innings in Perth.",
            venue = "Optus Stadium, Perth"
        ),
        CricketMatch(
            id = "c2",
            title = "Hyderabad vs Andhra - Ranji Trophy",
            tournament = "Ranji Trophy Elite Group",
            team1 = "Hyderabad",
            team1Flag = "🏏",
            team1Score = "342",
            team1Overs = "92.4 ov",
            team2 = "Andhra",
            team2Flag = "🏏",
            team2Score = "128/2",
            team2Overs = "38.0 ov",
            statusTe = "రెండో రోజు ఆట కొనసాగుతోంది",
            statusEn = "Day 2 - Stumps Approaching",
            highlightTe = "ఆంధ్రా ఓపెనర్ల నిలకడైన బ్యాటింగ్. హైదరాబాద్ బౌలర్ల పోరాటం.",
            highlightEn = "Andhra top-order shows resilience against Hyderabad pace attack.",
            venue = "Gymkhana Ground, Secunderabad"
        )
    )
}
