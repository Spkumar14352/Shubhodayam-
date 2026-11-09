// Greeting Templates for Shubhodaya App
// Categories: Good Morning, Good Night, Festivals, Birthday, Anniversary, Devotional
// Original artwork patterns drawn with SVGs and CSS gradients (no external images)

export type GreetingCategory =
  | 'GOOD_MORNING'
  | 'GOOD_NIGHT'
  | 'FESTIVALS'
  | 'BIRTHDAY'
  | 'ANNIVERSARY'
  | 'DEVOTIONAL';

export interface GreetingTemplate {
  id: string;
  category: GreetingCategory;
  titleEn: string;
  titleTe: string;
  bgGradient: string; // CSS gradient
  primaryColor: string;
  accentColor: string;
  textColor: string;
  patternType: 'mandala' | 'diya' | 'rangoli' | 'flower' | 'stars' | 'sun';
  defaultMessageTe: string;
  defaultMessageEn: string;
  readyMessagesTe: string[];
  readyMessagesEn: string[];
}

export const greetingTemplates: GreetingTemplate[] = [
  // --- GOOD MORNING (6 templates) ---
  {
    id: 'gm_golden_dawn',
    category: 'GOOD_MORNING',
    titleEn: 'Golden Dawn',
    titleTe: 'స్వర్ణోదయం',
    bgGradient: 'linear-gradient(135deg, #4A0E17 0%, #7B1123 50%, #D47A1E 100%)',
    primaryColor: '#D4A537',
    accentColor: '#F28C28',
    textColor: '#FFF7EC',
    patternType: 'sun',
    defaultMessageTe: 'ఈ శుభోదయం మీ జీవితంలో నూతన ఉత్సాహాన్ని, సుఖశాంతులను నింపాలని మనస్ఫూర్తిగా కోరుకుంటున్నాను.',
    defaultMessageEn: 'Wishing you a bright morning filled with renewed joy, good health, and peace.',
    readyMessagesTe: [
      'ఈ శుభోదయం మీ జీవితంలో నూతన ఉత్సాహాన్ని, సుఖశాంతులను నింపాలని మనస్ఫూర్తిగా కోరుకుంటున్నాను.',
      'ప్రతి ఉదయం ఒక వరం. సూర్యకాంతిలా మీ జీవితం సదా ప్రకాశించుగాక. శుభోదయం!',
      'నేటి దినం మీకు సకల కార్యసిద్ధిని, మానసిక ప్రశాంతతను ప్రసాదించాలని ప్రార్థిస్తున్నాను.',
      'శుభోదయం! చిరునవ్వుతో రోజును ప్రారంభించండి, విజయం మీ వెంటే ఉంటుంది.',
      'ఉదయించే సూర్యభగవానుని దివ్య కిరణాలు మీకు ఆయురారోగ్యాలు, ఐశ్వర్యాన్ని ప్రసాదించుగాక.'
    ],
    readyMessagesEn: [
      'Wishing you a bright morning filled with renewed joy, good health, and peace.',
      'May the morning sunshine bring boundless inspiration and success to your day. Good morning!',
      'Begin this day with a grateful heart and peaceful mind. Have an auspicious day ahead!',
      'May Lord Surya bless you with vigor, prosperity, and joy today and always.',
      'Rise and shine with positive thoughts. Wishing you a blessed and productive morning!'
    ]
  },
  {
    id: 'gm_temple_dusk',
    category: 'GOOD_MORNING',
    titleEn: 'Temple Radiance',
    titleTe: 'దేవాలయ శోభ',
    bgGradient: 'linear-gradient(135deg, #2B0610 0%, #5B0E1E 60%, #9E2A2B 100%)',
    primaryColor: '#F28C28',
    accentColor: '#D4A537',
    textColor: '#FFF7EC',
    patternType: 'mandala',
    defaultMessageTe: 'దైవానుగ్రహంతో నేటి దినం మీకు సర్వ శుభాలను కలిగించుగాక. శుభోదయం!',
    defaultMessageEn: 'May divine grace brighten your day with boundless blessings. Good morning!',
    readyMessagesTe: [
      'దైవానుగ్రహంతో నేటి దినం మీకు సర్వ శుభాలను కలిగించుగాక. శుభోదయం!',
      'నిర్మలమైన మనస్సు, పవిత్రమైన ఆలోచనలే సుఖకరమైన రోజుకు సోపానాలు. శుభోదయం!',
      'మీ ఇష్టదైవం అనుగ్రహంతో చేపట్టిన ప్రతి పని విజయవంతం కావాలని ఆకాంక్షిస్తూ...',
      'ఓం సూర్యాయ నమః! మీ దినచర్య శుభప్రదంగా, ఆనందమయంగా సాగిపోవుగాక.',
      'ఆనందం మీ ఇంట నిండాలని, ఆరోగ్యం తోడుగా ఉండాలని మనసారా కోరుకుంటూ శుభోదయం.'
    ],
    readyMessagesEn: [
      'May divine grace brighten your day with boundless blessings. Good morning!',
      'Pure thoughts and calm determination make every morning auspicious. Have a great day!',
      'May your day be filled with warm smiles, divine protection, and fruitful deeds.',
      'Sending peaceful morning prayers and auspicious wishes for your family.',
      'May all your endeavors flourish today. Good morning!'
    ]
  },
  {
    id: 'gm_lotus_blessing',
    category: 'GOOD_MORNING',
    titleEn: 'Lotus Serenity',
    titleTe: 'కమల కాంతి',
    bgGradient: 'linear-gradient(135deg, #1C0A15 0%, #4D1234 60%, #87255B 100%)',
    primaryColor: '#FFB703',
    accentColor: '#FB8500',
    textColor: '#FFFFFF',
    patternType: 'flower',
    defaultMessageTe: 'వికసించే తామర పువ్వులా మీ జీవితం పవిత్ర సుగంధాలతో నిండాలని కోరుకుంటూ శుభోదయం!',
    defaultMessageEn: 'May your day unfold like a blossoming lotus, pure, calm, and full of grace.',
    readyMessagesTe: [
      'వికసించే తామర పువ్వులా మీ జీవితం పవిత్ర సుగంధాలతో నిండాలని కోరుకుంటూ శుభోదయం!',
      'ప్రతి క్షణం ఆనందమయం, ప్రతి అడుగు విజయపథం కావాలని ఆశిస్తూ శుభోదయం.',
      'మంచి మనసుకు మంచి ఆలోచనలు తోడైతే ప్రతిరోజూ పండుగే. శుభదినం!',
      'మీ ముఖంలో చిరునవ్వు సదా చిగురించాలని ప్రార్థిస్తున్నాను. శుభోదయం!',
      'ప్రకృతి సౌందర్యాన్ని ఆస్వాదిస్తూ, ప్రశాంతంగా ఈ రోజును గడపండి. శుభోదయం.'
    ],
    readyMessagesEn: [
      'May your day unfold like a blossoming lotus, pure, calm, and full of grace.',
      'May every moment today lead you closer to happiness and contentment.',
      'Greet the new dawn with confidence and a cheerful smile. Good morning!',
      'May purity of purpose guide your path today. Have a peaceful day!',
      'Wishing you a day brimming with harmony, good energy, and joy.'
    ]
  },
  {
    id: 'gm_rangoli_breeze',
    category: 'GOOD_MORNING',
    titleEn: 'Rangoli Sunrise',
    titleTe: 'ముగ్గుల మురిపెం',
    bgGradient: 'linear-gradient(135deg, #2D1300 0%, #663300 50%, #C66900 100%)',
    primaryColor: '#FFE082',
    accentColor: '#FFB300',
    textColor: '#FFFDE7',
    patternType: 'rangoli',
    defaultMessageTe: 'ముంగిట ముగ్గుల రంగుల్లా మీ దినచర్య వైవిధ్యభరితంగా, ఆనందదాయకంగా సాగాలి.',
    defaultMessageEn: 'May your morning be as cheerful and vibrant as a colorful morning rangoli.',
    readyMessagesTe: [
      'ముంగిట ముగ్గుల రంగుల్లా మీ దినచర్య వైవిధ్యభరితంగా, ఆనందదాయకంగా సాగాలి.',
      'శుభోదయం! నేటి మీ ప్రతి ప్రయత్నంలో విజయలక్ష్మి తోడుండాలని ప్రార్థిస్తున్నాను.',
      'కల్మషం లేని చిరునవ్వుతో ఈ రోజును ఆహ్వానించండి. శుభ దినం!',
      'ఉదయపు చల్లని గాలి మీ మనస్సును ప్రశాంతంగా ఉంచాలని ఆశిస్తూ...',
      'ఆనందకరమైన వార్తలతో మీ ఇల్లు కళకళలాడాలని కోరుకుంటూ శుభోదయం.'
    ],
    readyMessagesEn: [
      'May your morning be as cheerful and vibrant as a colorful morning rangoli.',
      'Wishing you productive endeavors and joyful tidings throughout today.',
      'Start the day with positivity and quiet confidence. Good morning!',
      'May good fortune and warmth attend every step you take today.',
      'Wishing you and your dear ones a very serene and joyful morning.'
    ]
  },
  {
    id: 'gm_saffron_glow',
    category: 'GOOD_MORNING',
    titleEn: 'Saffron Aura',
    titleTe: 'కుంకుమ ప్రభ',
    bgGradient: 'linear-gradient(135deg, #38040E 0%, #700B1A 60%, #E85D04 100%)',
    primaryColor: '#FAA307',
    accentColor: '#FFBA08',
    textColor: '#FFF7EC',
    patternType: 'diya',
    defaultMessageTe: 'ప్రాతఃకాల దీపకాంతి మీ ఇంట నిరంతర సౌభాగ్యాన్ని, ఐశ్వర్యాన్ని నింపాలి.',
    defaultMessageEn: 'May the gentle morning light illuminate your path with prosperity and auspiciousness.',
    readyMessagesTe: [
      'ప్రాతఃకాల దీపకాంతి మీ ఇంట నిరంతర సౌభాగ్యాన్ని, ఐశ్వర్యాన్ని నింపాలి.',
      'ఆత్మవిశ్వాసమే మీ విజయానికి బలమైన ఆయుధం. శుభోదయం!',
      'నేటి దినం మీకు సరికొత్త అవకాశాలను తీసుకురావాలని ఆకాంక్షిస్తూ...',
      'ధర్మమార్గంలో నడిచేవారికి విజయం ఎల్లప్పుడూ తథ్యం. శుభోదయం!',
      'మీ కుటుంబంలో నిత్యం సంతోషం, ఐకమత్యం వెల్లివిరియాలని ప్రార్థిస్తూ...'
    ],
    readyMessagesEn: [
      'May the gentle morning light illuminate your path with prosperity and auspiciousness.',
      'Confidence and virtue will conquer all hurdles. Have an inspired morning!',
      'May new avenues of growth and joy open up for you today. Good morning!',
      'Sending heartfelt prayers for your continuous progress and well-being.',
      'May harmony and sweet laughter fill your home today. Good morning!'
    ]
  },
  {
    id: 'gm_vedic_chant',
    category: 'GOOD_MORNING',
    titleEn: 'Vedic Peace',
    titleTe: 'వేద శాంతి',
    bgGradient: 'linear-gradient(135deg, #120B24 0%, #291B4E 60%, #6A4C93 100%)',
    primaryColor: '#E0AAFF',
    accentColor: '#C77DFF',
    textColor: '#F8F9FA',
    patternType: 'mandala',
    defaultMessageTe: 'ఓం శాంతిః శాంతిః శాంతిః — ప్రశాంతమైన మనస్సుతో ఈ నూతన దినాన్ని ప్రారంభించండి.',
    defaultMessageEn: 'Om Shanti! Begin this fresh dawn with peace, mindfulness, and inner clarity.',
    readyMessagesTe: [
      'ఓం శాంతిః శాంతిః శాంతిః — ప్రశాంతమైన మనస్సుతో ఈ నూతన దినాన్ని ప్రారంభించండి.',
      'సకల శుభములు మీకు కలగాలని, దైవచింతనతో రోజు సాగాలని కోరుకుంటూ శుభోదయం.',
      'గాయత్రీ మంత్ర తేజస్సు మీ బుద్ధిని సన్మార్గంలో నడిపించుగాక. శుభోదయం!',
      'కష్టాలు తొలగి, సుఖ సంతోషాలు వెల్లివిరిసే శుభోదయం మీకిది.',
      'శాంతియుతమైన హృదయమే భగవంతుని నివాసం. ప్రశాంతమైన శుభోదయం.'
    ],
    readyMessagesEn: [
      'Om Shanti! Begin this fresh dawn with peace, mindfulness, and inner clarity.',
      'May serene contemplation guide your decisions and bless your day.',
      'Wishing you freedom from stress and abundant clarity this morning.',
      'May the divine presence envelop you in solace and warmth. Good morning!',
      'Start the day centered in calm grace and joy. Blessed morning to you!'
    ]
  },

  // --- GOOD NIGHT (6 templates) ---
  {
    id: 'gn_starlit_temple',
    category: 'GOOD_NIGHT',
    titleEn: 'Starlit Haven',
    titleTe: 'తారా తోరణం',
    bgGradient: 'linear-gradient(135deg, #0A0A16 0%, #15162C 60%, #26294A 100%)',
    primaryColor: '#C0C0E8',
    accentColor: '#9D9DF2',
    textColor: '#F0F0FF',
    patternType: 'stars',
    defaultMessageTe: 'రోజంతా చేసిన శ్రమను మరచి, ప్రశాంతమైన నిద్రలోకి జారుకోండి. శుభరాత్రి!',
    defaultMessageEn: 'Release the day’s toil and drift into peaceful, restful slumber. Good night!',
    readyMessagesTe: [
      'రోజంతా చేసిన శ్రమను మరచి, ప్రశాంతమైన నిద్రలోకి జారుకోండి. శుభరాత్రి!',
      'చల్లని వెన్నెల మీ మనస్సుకు ఉపశమనాన్ని ఇవ్వాలని కోరుకుంటూ శుభరాత్రి.',
      'నేటిని కృతజ్ఞతతో ముగించండి, రేపటి నూతన ఉదయానికి సిద్ధంకండి. శుభరాత్రి!',
      'కలలన్నీ శుభప్రదంగా, నిద్ర ప్రశాంతంగా ఉండాలని ఆకాంక్షిస్తూ...',
      'భగవంతుని రక్షణలో సుఖంగా నిద్రించండి. శుభరాత్రి!'
    ],
    readyMessagesEn: [
      'Release the day’s toil and drift into peaceful, restful slumber. Good night!',
      'May gentle moonlight soothe your mind and renew your spirit. Good night!',
      'End today with gratitude; welcome tomorrow with renewed hope. Sleep well!',
      'May sweet and peaceful dreams embrace you tonight. Good night!',
      'Rest in calm assurance and comfort. Wishing you a blissful night.'
    ]
  },
  {
    id: 'gn_moonlit_lotus',
    category: 'GOOD_NIGHT',
    titleEn: 'Moonlit Lotus',
    titleTe: 'చంద్రకాంతి కమలం',
    bgGradient: 'linear-gradient(135deg, #0B0E14 0%, #1A233A 60%, #2E3E64 100%)',
    primaryColor: '#D4AF37',
    accentColor: '#8ECAE6',
    textColor: '#E8F1F5',
    patternType: 'flower',
    defaultMessageTe: 'చందమామ వెన్నెల్లాంటి చల్లని నిద్ర మీ సొంతం కావాలి. రేపటి ఉదయం సరికొత్త కాంతులు తేవాలి. శుభరాత్రి!',
    defaultMessageEn: 'May the cool moonbeams cradle you in restorative peace. Good night!',
    readyMessagesTe: [
      'చందమామ వెన్నెల్లాంటి చల్లని నిద్ర మీ సొంతం కావాలి. రేపటి ఉదయం సరికొత్త కాంతులు తేవాలి. శుభరాత్రి!',
      'భారమైన ఆలోచనలను వదిలేయండి, ప్రశాంతంగా విశ్రాంతి తీసుకోండి. శుభరాత్రి.',
      'నేటి విజయం మీదే, రేపటి ఉదయం మీ కోసమే వేచి ఉంది. శుభరాత్రి!',
      'దేవుని కృపతో హాయిగా నిద్రించండి, సరికొత్త ఉత్సాహంతో మేల్కొనండి.',
      'శుభరాత్రి! మనస్సు ప్రశాంతంగా ఉంటే రేపటి రోజు మరింత అందంగా ఉంటుంది.'
    ],
    readyMessagesEn: [
      'May the cool moonbeams cradle you in restorative peace. Good night!',
      'Let go of all burdens and rest your weary thoughts. Sleep peacefully!',
      'You did your best today. Rest well for a vibrant tomorrow. Good night!',
      'May calm serenity embrace you and your family tonight.',
      'Wishing you silent hours of quiet healing and beautiful dreams.'
    ]
  },
  {
    id: 'gn_divine_rest',
    category: 'GOOD_NIGHT',
    titleEn: 'Divine Slumber',
    titleTe: 'ప్రశాంత నిశి',
    bgGradient: 'linear-gradient(135deg, #0F051D 0%, #23123D 60%, #432371 100%)',
    primaryColor: '#E2D9F3',
    accentColor: '#C77DFF',
    textColor: '#FFFFFF',
    patternType: 'mandala',
    defaultMessageTe: 'అన్ని చింతలను ఈశ్వరుని పాదాల వద్ద ఉంచి, హాయిగా నిద్రపోండి. శుభరాత్రి!',
    defaultMessageEn: 'Surrender all worries to the divine and rest in serene confidence. Good night!',
    readyMessagesTe: [
      'అన్ని చింతలను ఈశ్వరుని పాదాల వద్ద ఉంచి, హాయిగా నిద్రపోండి. శుభరాత్రి!',
      'నిద్ర అనేది భగవంతుడు మనకిచ్చిన అమూల్యమైన ఉపశమనం. శుభరాత్రి.',
      'రేపటి శుభోదయానికి స్వాగతం పలికేందుకు ప్రశాంతంగా నిద్రించండి.',
      'మీ కలలు సాకారమయ్యే దిశగా నూతన ఆలోచనలు కలగాలని ఆశిస్తూ శుభరాత్రి.',
      'ఓం శాంతిః — మనస్సు నిర్మలంగా, శరీరం విశ్రాంతిగా ఉండాలని కోరుకుంటూ.'
    ],
    readyMessagesEn: [
      'Surrender all worries to the divine and rest in serene confidence. Good night!',
      'Rest is nature’s sacred medicine. Relax completely and sleep deeply.',
      'May peaceful energy renew your mind and body tonight. Good night!',
      'Let calm thoughts quiet your spirit. Tomorrow is full of promise.',
      'Wishing you untroubled sleep and gentle, inspiring dreams.'
    ]
  },
  {
    id: 'gn_midnight_diya',
    category: 'GOOD_NIGHT',
    titleEn: 'Midnight Glow',
    titleTe: 'నిశీథి దీపం',
    bgGradient: 'linear-gradient(135deg, #1C0A00 0%, #381A05 60%, #613009 100%)',
    primaryColor: '#F4A261',
    accentColor: '#E76F51',
    textColor: '#FFF3E0',
    patternType: 'diya',
    defaultMessageTe: 'ఒక చిన్న దీపంలా మీ ఆశలు ఎప్పుడూ వెలుగుతూనే ఉండాలి. శుభరాత్రి!',
    defaultMessageEn: 'Like an enduring flame, may hopeful thoughts keep glowing inside you. Good night!',
    readyMessagesTe: [
      'ఒక చిన్న దీపంలా మీ ఆశలు ఎప్పుడూ వెలుగుతూనే ఉండాలి. శుభరాత్రి!',
      'రోజంతా పడిన శ్రమకు చక్కని విశ్రాంతి దొరకాలని కోరుకుంటూ శుభరాత్రి.',
      'చీకటి ఎంత గాఢంగా ఉన్నా ఉదయించే సూర్యుడు తప్పక వస్తాడు. శుభరాత్రి!',
      'మీ కుటుంబ సభ్యులందరికీ ప్రశాంతమైన నిద్ర చేకూరాలని ఆశిస్తూ...',
      'ఈ రాత్రి మీకు సుఖనిద్రను, మధుర స్వప్నాలను ప్రసాదించుగాక.'
    ],
    readyMessagesEn: [
      'Like an enduring flame, may hopeful thoughts keep glowing inside you. Good night!',
      'No matter how dark the night, the bright dawn is always assured. Sleep well!',
      'Wishing comfortable rest and gentle restoration for you and your family.',
      'May your dreams be sweet and your awakening joyous. Good night!',
      'Close your eyes with peace and wake up with passion. Sleep tight!'
    ]
  },
  {
    id: 'gn_mystic_rangoli',
    category: 'GOOD_NIGHT',
    titleEn: 'Silent Rangoli',
    titleTe: 'మౌన రంగవల్లి',
    bgGradient: 'linear-gradient(135deg, #09131C 0%, #132A3E 60%, #1E4663 100%)',
    primaryColor: '#A8DADC',
    accentColor: '#457B9D',
    textColor: '#F1FAEE',
    patternType: 'rangoli',
    defaultMessageTe: 'నిశ్శబ్ద రాత్రిలో మనస్సును ప్రశాంతంగా ఉంచుకుని, హాయిగా నిద్రించండి.',
    defaultMessageEn: 'In the tranquil quiet of the night, find deep rejuvenation. Good night!',
    readyMessagesTe: [
      'నిశ్శబ్ద రాత్రిలో మనస్సును ప్రశాంతంగా ఉంచుకుని, హాయిగా నిద్రించండి.',
      'రేపటి రోజు మీ జీవితంలో నూతన వెలుగులు నింపాలని ఆకాంక్షిస్తూ శుభరాత్రి.',
      'నిశ్చింతగా నిద్రపోండి, భగవంతుని ఆశీస్సులు మీకు రక్షగా ఉంటాయి.',
      'మంచి ఆలోచనలతో నిద్రించండి, మంచి ఫలితాలతో మేల్కొనండి.',
      'కంటినిండా నిద్ర, మనస్సునిండా శాంతి మీ సొంతం కావాలి. శుభరాత్రి.'
    ],
    readyMessagesEn: [
      'In the tranquil quiet of the night, find deep rejuvenation. Good night!',
      'Sleep peacefully under the watchful care of the divine. Good night!',
      'May tranquility surround your home tonight. Wake up revitalized!',
      'Peaceful sleep tonight sets the foundation for tomorrow’s triumph.',
      'Wishing you unbroken sleep and cheerful morning energy.'
    ]
  },
  {
    id: 'gn_cosmic_mandala',
    category: 'GOOD_NIGHT',
    titleEn: 'Cosmic Slumber',
    titleTe: 'విశ్వ శాంతి',
    bgGradient: 'linear-gradient(135deg, #12031B 0%, #2B0D3B 60%, #4A1966 100%)',
    primaryColor: '#D8BBFF',
    accentColor: '#B088F9',
    textColor: '#F5EEF8',
    patternType: 'mandala',
    defaultMessageTe: 'విశ్వాంతరాళంలో ఒక భాగాన్ని ధ్యానిస్తూ, నిశ్చింతగా విశ్రాంతి తీసుకోండి. శుభరాత్రి.',
    defaultMessageEn: 'Rest in harmony with the cosmos. Tomorrow brings infinite fresh beginnings.',
    readyMessagesTe: [
      'విశ్వాంతరాళంలో ఒక భాగాన్ని ధ్యానిస్తూ, నిశ్చింతగా విశ్రాంతి తీసుకోండి. శుభరాత్రి.',
      'ఈ రాత్రి మీకు అమృతతుల్యమైన నిద్రను అందించాలని ప్రార్థిస్తున్నాను.',
      'గడిచిన రోజులోని మంచిని గుర్తుంచుకోండి, ప్రశాంతంగా నిద్రించండి.',
      'శుభరాత్రి! రేపటి ఉదయం మీ కోసం మరిన్ని సంతోషాలను సిద్ధం చేయాలి.',
      'దైవ కృపతో మీ కలలన్నీ సుందరంగా మారాలని ఆశిస్తూ శుభరాత్రి.'
    ],
    readyMessagesEn: [
      'Rest in harmony with the cosmos. Tomorrow brings infinite fresh beginnings.',
      'Treasuring today’s gentle moments, drift into restful dreams.',
      'May all tension melt away in tonight’s comforting quiet. Good night!',
      'Awaken tomorrow with high spirits and renewed stamina. Sleep well!',
      'Wishing you a peaceful and sacred slumber. Good night!'
    ]
  },

  // --- FESTIVALS (13 required festivals) ---
  {
    id: 'fest_sankranti',
    category: 'FESTIVALS',
    titleEn: 'Makara Sankranti',
    titleTe: 'మకర సంక్రాంతి',
    bgGradient: 'linear-gradient(135deg, #4A1202 0%, #8C2503 50%, #E85D04 100%)',
    primaryColor: '#FFD166',
    accentColor: '#F77F00',
    textColor: '#FFF7EC',
    patternType: 'rangoli',
    defaultMessageTe: 'భోగి భాగ్యాలు, సంక్రాంతి సంబరాలు, కనుమ కనువిందులతో మీ ఇల్లు ఆనందాల హరివిల్లవ్వాలని కోరుకుంటూ మకర సంక్రాంతి శుభాకాంక్షలు!',
    defaultMessageEn: 'May the harvest festival of Makara Sankranti bring boundless joy, prosperity, and sweet memories to your family!',
    readyMessagesTe: [
      'భోగి భాగ్యాలు, సంక్రాంతి సంబరాలు, కనుమ కనువిందులతో మీ ఇల్లు ఆనందాల హరివిల్లవ్వాలని కోరుకుంటూ మకర సంక్రాంతి శుభాకాంక్షలు!',
      'సూర్యుని ఉత్తరాయణ పుణ్యకాలం మీ జీవితంలో సరికొత్త వెలుగులు నింపాలని ఆకాంక్షిస్తూ సంక్రాంతి శుభాకాంక్షలు.',
      'పిండివంటల ఘుమఘుమలు, గొబ్బెమ్మల ముగ్గులు, హరిదాసుల కీర్తనలతో పల్లెసీమల శోభ మీ ఇంట వెల్లివిరియాలి.',
      'చేలల్లో బంగారు పంటలు, ఇళ్లలో సిరిసంపదలు వర్ధిల్లాలని కోరుకుంటూ సంక్రాంతి పండుగ శుభాకాంక్షలు!',
      'మీ కుటుంబ సభ్యులందరికీ భోగి, సంక్రాంతి, కనుమ పండుగల హృదయపూర్వక శుభాకాంక్షలు.'
    ],
    readyMessagesEn: [
      'May the harvest festival of Makara Sankranti bring boundless joy, prosperity, and sweet memories to your family!',
      'Wishing you auspicious beginnings as the Sun enters the northern solstice. Happy Sankranti!',
      'May your home be filled with sweet delicacies, colorful kites, and warm festive laughter.',
      'Praying for plentiful harvests and overflowing abundance in your life this Sankranti.',
      'Warmest wishes for Bhogi, Makara Sankranti, and Kanuma to you and your loved ones!'
    ]
  },
  {
    id: 'fest_ugadi',
    category: 'FESTIVALS',
    titleEn: 'Ugadi (Telugu New Year)',
    titleTe: 'ఉగాది పండుగ',
    bgGradient: 'linear-gradient(135deg, #1C3B0E 0%, #386618 50%, #70A22D 100%)',
    primaryColor: '#FFE169',
    accentColor: '#99D98C',
    textColor: '#F8FFF0',
    patternType: 'flower',
    defaultMessageTe: 'తీపి, చేదు, పులుపుల షడ్రుచుల సమ్మేళనంలాంటి ఈ నూతన సంవత్సరంలో మీ జీవితం సదా సుఖసంతోషాలతో వర్ధిల్లాలని కోరుకుంటూ ఉగాది శుభాకాంక్షలు!',
    defaultMessageEn: 'Wishing you a prosperous Ugadi! May the New Year blend all flavors of life into peace, good health, and success.',
    readyMessagesTe: [
      'తీపి, చేదు, పులుపుల షడ్రుచుల సమ్మేళనంలాంటి ఈ నూతన సంవత్సరంలో మీ జీవితం సదా సుఖసంతోషాలతో వర్ధిల్లాలని కోరుకుంటూ ఉగాది శుభాకాంక్షలు!',
      'మామిడి తోరణాలు, వేపపూత పచ్చడితో ఆరంభమయ్యే ఈ ఉగాది మీకు సకల శుభాలను చేకూర్చుగాక.',
      'నూతన సంవత్సరం మీ జీవితంలో సరికొత్త ఆశలను, ఉన్నత శిఖరాలను తీసుకురావాలని ఆకాంక్షిస్తూ ఉగాది శుభాకాంక్షలు.',
      'పంచాంగ శ్రవణం, ఇష్టదేవతారాధనలతో ఈ ఉగాది దినం మీ ఇంట శాంతిని నెలకొల్పాలి.',
      'తెలుగు వారి నూతన సంవత్సర పర్వదినమైన ఉగాది పండుగ శుభాకాంక్షలు!'
    ],
    readyMessagesEn: [
      'Wishing you a prosperous Ugadi! May the New Year blend all flavors of life into peace, good health, and success.',
      'May the fresh neem-mango pachadi bring resilience, good cheer, and auspicious tidings.',
      'May this Telugu New Year usher in joyous accomplishments and prosperity. Happy Ugadi!',
      'Wishing you and your household abundance and delightful memories this Ugadi.',
      'Warm greetings on our traditional Telugu New Year festival of Ugadi!'
    ]
  },
  {
    id: 'fest_sri_rama_navami',
    category: 'FESTIVALS',
    titleEn: 'Sri Rama Navami',
    titleTe: 'శ్రీరామనవమి',
    bgGradient: 'linear-gradient(135deg, #4A0E17 0%, #7E1528 50%, #C93B2B 100%)',
    primaryColor: '#FFD700',
    accentColor: '#FFA500',
    textColor: '#FFF7EC',
    patternType: 'mandala',
    defaultMessageTe: 'మర్యాదా పురుషోత్తముడైన శ్రీరామచంద్రుని దివ్య అనుగ్రహం మీ కుటుంబంపై సదా ఉండాలని కోరుకుంటూ శ్రీరామనవమి శుభాకాంక్షలు!',
    defaultMessageEn: 'May the righteous grace and virtues of Lord Rama protect, guide, and bless your family. Happy Sri Rama Navami!',
    readyMessagesTe: [
      'మర్యాదా పురుషోత్తముడైన శ్రీరామచంద్రుని దివ్య అనుగ్రహం మీ కుటుంబంపై సదా ఉండాలని కోరుకుంటూ శ్రీరామనవమి శుభాకాంక్షలు!',
      'శ్రీ సీతారాముల కల్యాణ మహోత్సవం మీ ఇంట సదా సుఖసంతోషాలను నింపాలని ప్రార్థిస్తున్నాను.',
      'రామో విగ్రహవాన్ ధర్మః — ధర్మ స్వరూపుడైన శ్రీరాముని ఆశీస్సులతో మీ జీవితం ధన్యమగుగాక.',
      'చల్లని వడపప్పు, పానకంతో సీతారామచంద్రులను పూజించి తరించండి. శ్రీరామనవమి శుభాకాంక్షలు!',
      'మీకు, మీ కుటుంబ సభ్యులకు భక్తిపూర్వక శ్రీరామనవమి పర్వదిన శుభాకాంక్షలు.'
    ],
    readyMessagesEn: [
      'May the righteous grace and virtues of Lord Rama protect, guide, and bless your family. Happy Sri Rama Navami!',
      'May the divine celestial wedding of Sita-Rama bestow lasting peace and harmony in your home.',
      'Celebrating righteousness, truth, and devotion. Wishing you a blessed Sri Rama Navami!',
      'May Lord Rama dispel all tribulations and grant steady wisdom to you and your loved ones.',
      'Hearty greetings on the auspicious occasion of Sri Rama Navami!'
    ]
  },
  {
    id: 'fest_vinayaka_chavithi',
    category: 'FESTIVALS',
    titleEn: 'Vinayaka Chavithi',
    titleTe: 'వినాయక చవితి',
    bgGradient: 'linear-gradient(135deg, #3D1308 0%, #7F260F 50%, #D45B12 100%)',
    primaryColor: '#FFE066',
    accentColor: '#F77F00',
    textColor: '#FFF9E6',
    patternType: 'diya',
    defaultMessageTe: 'విఘ్నహర్త అయిన శ్రీ వరసిద్ధి వినాయకుని కటాక్షంతో మీ ఆటంకాలన్నీ తొలగి, సకల విజయాలు చేకూరాలని కోరుకుంటూ వినాయక చవితి శుభాకాంక్షలు!',
    defaultMessageEn: 'May Lord Ganesha remove every hurdle from your path and bestow immense wisdom, prosperity, and victory!',
    readyMessagesTe: [
      'విఘ్నహర్త అయిన శ్రీ వరసిద్ధి వినాయకుని కటాక్షంతో మీ ఆటంకాలన్నీ తొలగి, సకల విజయాలు చేకూరాలని కోరుకుంటూ వినాయక చవితి శుభాకాంక్షలు!',
      'గణపతి బప్పా మోరియా! గణనాథుని దివ్య అనుగ్రహం మీ ఇంట నిరంతర ఆనందాన్ని నింపాలి.',
      'ఏకదంతాయ వక్రతుండాయ గౌరీతనయాయ ధీమహి — వినాయకుని పూజతో మీ పనులన్నీ నిర్విఘ్నంగా పూర్తవ్వాలి.',
      'మోదకాలు, ఉండ్రాళ్లతో గణనాథుని కొలిచి సర్వ సౌభాగ్యాలను అందుకోండి. వినాయక చవితి శుభాకాంక్షలు!',
      'మీకు, మీ కుటుంబ సభ్యులందరికీ వినాయక చవితి పండుగ హృదయపూర్వక శుభాకాంక్షలు.'
    ],
    readyMessagesEn: [
      'May Lord Ganesha remove every hurdle from your path and bestow immense wisdom, prosperity, and victory!',
      'Wishing you beginnings that blossom into glorious success. Happy Vinayaka Chavithi!',
      'May Lord Ganapati bless your studies, career, and family with auspicious harmony.',
      'Celebrating the joyful arrival of Bappa! Warm wishes on Ganesh Chaturthi to all.',
      'May obstacles dissolve and noble paths open up before you. Happy Ganesha Festival!'
    ]
  },
  {
    id: 'fest_dasara',
    category: 'FESTIVALS',
    titleEn: 'Vijayadasami / Dasara',
    titleTe: 'విజయదశమి / దసరా',
    bgGradient: 'linear-gradient(135deg, #4A0512 0%, #850E23 50%, #D00000 100%)',
    primaryColor: '#FFD60A',
    accentColor: '#FF9E00',
    textColor: '#FFF7EC',
    patternType: 'rangoli',
    defaultMessageTe: 'చెడుపై మంచి సాధించిన దివ్య విజయానికి ప్రతీక అయిన విజయదశమి పర్వదినం మీ జీవితంలో సర్వదా విజయాలను చేకూర్చాలని కోరుకుంటూ దసరా శుభాకాంక్షలు!',
    defaultMessageEn: 'May the triumph of good over evil bring lasting victory, righteous courage, and joyous prosperity. Happy Vijayadasami!',
    readyMessagesTe: [
      'చెడుపై మంచి సాధించిన దివ్య విజయానికి ప్రతీక అయిన విజయదశమి పర్వదినం మీ జీవితంలో సర్వదా విజయాలను చేకూర్చాలని కోరుకుంటూ దసరా శుభాకాంక్షలు!',
      'శమీ శమయతే పాపం — జమ్మి ఆకులు పంచుకుంటూ బంధుమిత్రులతో ఆనందంగా దసరా జరుపుకోండి.',
      'దుర్గామాత దివ్య ఆశీస్సులతో మీకు సకల అభీష్టసిద్ధి, విజయాలు కలగాలని ప్రార్థిస్తున్నాను.',
      'ఆయుధ పూజ, వాహన పూజలతో శ్రమను గౌరవించే పవిత్ర పండుగ. దసరా శుభాకాంక్షలు!',
      'మీకు మరియు మీ కుటుంబ సభ్యులకు విజయదశమి పర్వదిన శుభాకాంక్షలు.'
    ],
    readyMessagesEn: [
      'May the triumph of good over evil bring lasting victory, righteous courage, and joyous prosperity. Happy Vijayadasami!',
      'May Goddess Durga shower strength, fortitude, and success upon all your good pursuits.',
      'Sharing sweet festive greetings on this auspicious occasion of Dasara and Jammi puja.',
      'May every challenge in your life turn into an inspiring victory. Happy Dussehra!',
      'Hearty congratulations and best wishes for a triumphant Vijayadasami!'
    ]
  },
  {
    id: 'fest_deepavali',
    category: 'FESTIVALS',
    titleEn: 'Deepavali',
    titleTe: 'దీపావళి పండుగ',
    bgGradient: 'linear-gradient(135deg, #2D0814 0%, #5E0B20 50%, #B5179E 100%)',
    primaryColor: '#FFD166',
    accentColor: '#F72585',
    textColor: '#FFF7EC',
    patternType: 'diya',
    defaultMessageTe: 'దీపాల కాంతులు మీ ఇంట అజ్ఞానాన్ని పారద్రోలి, సుఖశాంతులు, సిరిసంపదలను నింపాలని మనసారా కోరుకుంటూ దీపావళి శుభాకాంక్షలు!',
    defaultMessageEn: 'May the divine glow of diyas brighten your home with supreme peace, joy, and prosperity. Happy Deepavali!',
    readyMessagesTe: [
      'దీపాల కాంతులు మీ ఇంట అజ్ఞానాన్ని పారద్రోలి, సుఖశాంతులు, సిరిసంపదలను నింపాలని మనసారా కోరుకుంటూ దీపావళి శుభాకాంక్షలు!',
      'లక్ష్మీదేవి కటాక్షం మీ కుటుంబంపై సదా ఉండాలని, ధనధాన్యాలు వర్ధిల్లాలని కోరుకుంటున్నాను.',
      'టపాసుల వెలుగులు, మిఠాయిల మాధుర్యంతో మీ దీపావళి వేడుకలు చిరస్మరణీయంగా సాగాలి.',
      'తమసోమా జ్యోతిర్గమయ — చీకటిని చీల్చి వెలుగును నింపే దివ్య దీపావళి శుభాకాంక్షలు!',
      'మీకు, మీ ఆత్మీయులకు వెలుగుల పండుగ దీపావళి శుభాకాంక్షలు.'
    ],
    readyMessagesEn: [
      'May the divine glow of diyas brighten your home with supreme peace, joy, and prosperity. Happy Deepavali!',
      'May Goddess Lakshmi bless your household with abundance, harmony, and radiant good health.',
      'Wishing you a dazzling, safe, and joy-filled Diwali with sweet family moments.',
      'From darkness to illumination — may divine radiance shine through your life always.',
      'Warmest festive greetings on the Festival of Lights to you and your loved ones!'
    ]
  },
  {
    id: 'fest_karthika_pournami',
    category: 'FESTIVALS',
    titleEn: 'Karthika Pournami',
    titleTe: 'కార్తీక పౌర్ణమి',
    bgGradient: 'linear-gradient(135deg, #1A0D38 0%, #321666 50%, #5E32A8 100%)',
    primaryColor: '#FFDE59',
    accentColor: '#C77DFF',
    textColor: '#F8F9FA',
    patternType: 'diya',
    defaultMessageTe: 'శివకేశవుల అనుగ్రహం ప్రసాదించే కార్తీక పౌర్ణమి పుణ్యదినాన దీపారాధనతో మీ జీవితం దివ్య కాంతులతో ప్రకాశించాలని కోరుకుంటూ శుభాకాంక్షలు!',
    defaultMessageEn: 'May the holy full moon of Karthika Pournami bring profound spiritual peace, divine glow, and joy to your family!',
    readyMessagesTe: [
      'శివకేశవుల అనుగ్రహం ప్రసాదించే కార్తీక పౌర్ణమి పుణ్యదినాన దీపారాధనతో మీ జీవితం దివ్య కాంతులతో ప్రకాశించాలని కోరుకుంటూ శుభాకాంక్షలు!',
      'జ్వాలాతోరణం, ఉసిరి దీపం, నదీ స్నానాల పుణ్యఫలాలు మీ సొంతం కావాలి. కార్తీక పౌర్ణమి శుభాకాంక్షలు.',
      'శివాలయాల్లో జ్యోతిర్దర్శనం సకల పాపాలను పోగొట్టి మోక్షాన్ని ప్రసాదిస్తుంది.',
      'కార్తీక దామోదరుని అనుగ్రహంతో మీ ఇంట సదా శుభములు కలగాలని ప్రార్థిస్తున్నాను.',
      'మీకు మరియు మీ కుటుంబ సభ్యులకు పవిత్ర కార్తీక పౌర్ణమి శుభాకాంక్షలు.'
    ],
    readyMessagesEn: [
      'May the holy full moon of Karthika Pournami bring profound spiritual peace, divine glow, and joy to your family!',
      'May lit earthen lamps by the Tulasi plant bring calm solace and divine benediction.',
      'Wishing you auspicious blessings during this sacred Karthika month of devotion.',
      'May Shiva and Keshava protect you and illuminate your spiritual journey.',
      'Blessed Karthika Pournami greetings to you and your dear ones!'
    ]
  },
  {
    id: 'fest_maha_shivaratri',
    category: 'FESTIVALS',
    titleEn: 'Maha Shivaratri',
    titleTe: 'మహా శివరాత్రి',
    bgGradient: 'linear-gradient(135deg, #0C1222 0%, #162447 50%, #1F4068 100%)',
    primaryColor: '#E0AAFF',
    accentColor: '#00F5D4',
    textColor: '#F0F8FF',
    patternType: 'mandala',
    defaultMessageTe: 'ఓం నమః శివాయ! పరమేశ్వరుని దివ్య లింగోద్భవ పుణ్యకాలాన ఉపవాస, జాగరణలతో శివసాయుజ్యాన్ని పొందాలని కోరుకుంటూ మహా శివరాత్రి శుభాకాంక్షలు!',
    defaultMessageEn: 'Om Namah Shivaya! May Lord Shiva bless you with inner calm, fearlessness, and divine liberation on Maha Shivaratri!',
    readyMessagesTe: [
      'ఓం నమః శివాయ! పరమేశ్వరుని దివ్య లింగోద్భవ పుణ్యకాలాన ఉపవాస, జాగరణలతో శివసాయుజ్యాన్ని పొందాలని కోరుకుంటూ మహా శివరాత్రి శుభాకాంక్షలు!',
      'మృత్యుంజయ మహాదేవుని కృపతో మీకు ఆయురారోగ్యాలు, మనశ్శాంతి కలగాలని ప్రార్థిస్తున్నాను.',
      'బిల్వపత్రాలతో సదాశివుని అర్చించి, త్రయంబక మంత్ర జపంతో పునీతులవండి.',
      'శివరాత్రి జాగరణ మీ అంతరంగాన్ని మేల్కొలిపి సత్యమార్గంలో నడిపించుగాక.',
      'మీకు, మీ కుటుంబ సభ్యులకు భక్తిపూర్వక మహా శివరాత్రి శుభాకాంక్షలు.'
    ],
    readyMessagesEn: [
      'Om Namah Shivaya! May Lord Shiva bless you with inner calm, fearlessness, and divine liberation on Maha Shivaratri!',
      'May Mahadeva dissolve all negative energies and fill your heart with timeless tranquility.',
      'Wishing you a spiritually uplifting and deeply fulfilling Maha Shivaratri night.',
      'May the auspicious Bilva offerings and chanting of Shiva hymns bring eternal peace.',
      'Warm and sacred Maha Shivaratri greetings to all!'
    ]
  },
  {
    id: 'fest_varalakshmi_vratham',
    category: 'FESTIVALS',
    titleEn: 'Varalakshmi Vratham',
    titleTe: 'వరలక్ష్మీ వ్రతం',
    bgGradient: 'linear-gradient(135deg, #3B0D18 0%, #6E1A2D 50%, #A82440 100%)',
    primaryColor: '#FFE066',
    accentColor: '#FFB703',
    textColor: '#FFF7EC',
    patternType: 'flower',
    defaultMessageTe: 'శ్రీ వరలక్ష్మీ దేవి దివ్య కటాక్షం మీ ఇంట నిత్య సుమంగళి సౌభాగ్యాన్ని, ఐశ్వర్యాన్ని, ఆనందాన్ని నింపాలని కోరుకుంటూ వరలక్ష్మీ వ్రత శుభాకాంక్షలు!',
    defaultMessageEn: 'May Goddess Varalakshmi bless your family with enduring prosperity, health, and blissful harmony!',
    readyMessagesTe: [
      'శ్రీ వరలక్ష్మీ దేవి దివ్య కటాక్షం మీ ఇంట నిత్య సుమంగళి సౌభాగ్యాన్ని, ఐశ్వర్యాన్ని, ఆనందాన్ని నింపాలని కోరుకుంటూ వరలక్ష్మీ వ్రత శుభాకాంక్షలు!',
      'పసుపు కుంకుమలు, తోరాల పూజతో అమ్మవారి అనుగ్రహం మీ ఇంట వెల్లివిరియాలి.',
      'అష్టలక్ష్ముల రూపమైన వరలక్ష్మీ మాత మీ కోరికలన్నీ నెరవేర్చాలని ప్రార్థిస్తున్నాను.',
      'మీ కుటుంబంలో నిరంతర ఆనందం, సౌభాగ్యం వర్ధిల్లాలని కోరుకుంటూ...',
      'భక్తిశ్రద్ధలతో జరుపుకునే వరలక్ష్మీ వ్రత శుభాకాంక్షలు!'
    ],
    readyMessagesEn: [
      'May Goddess Varalakshmi bless your family with enduring prosperity, health, and blissful harmony!',
      'May your worship and sacred sacred thread rituals bring peace and joy to your household.',
      'Wishing divine grace, contentment, and abundant happiness on Varalakshmi Vratham.',
      'May all eight forms of divine wealth bless your home with sweet stability.',
      'Heartfelt greetings on the auspicious occasion of Varalakshmi Vratham!'
    ]
  },
  {
    id: 'fest_bathukamma',
    category: 'FESTIVALS',
    titleEn: 'Bathukamma Festival',
    titleTe: 'బతుకమ్మ పండుగ',
    bgGradient: 'linear-gradient(135deg, #2B0715 0%, #5C1230 50%, #9E1F52 100%)',
    primaryColor: '#FFD166',
    accentColor: '#06D6A0',
    textColor: '#FFF8F0',
    patternType: 'flower',
    defaultMessageTe: 'గునుగు, తంగేడు పూల అందాలతో ఆడపడుచులు జరుపుకునే ప్రకృతి పండుగ బతుకమ్మ శుభాకాంక్షలు! గౌరమ్మ తల్లి మీకు సకల సౌభాగ్యాలు ఇవ్వాలి.',
    defaultMessageEn: 'May the floral colors and joy of Bathukamma bring prosperity, vitality, and sisterhood to your home!',
    readyMessagesTe: [
      'గునుగు, తంగేడు పూల అందాలతో ఆడపడుచులు జరుపుకునే ప్రకృతి పండుగ బతుకమ్మ శుభాకాంక్షలు! గౌరమ్మ తల్లి మీకు సకల సౌభాగ్యాలు ఇవ్వాలి.',
      'బతుకమ్మ బతుకమ్మ ఉయ్యాలో — తెలంగాణ సంస్కృతి సంప్రదాయాల పండుగ శుభాకాంక్షలు.',
      'తీరొక్క పూలతో పేర్చిన బతుకమ్మ మీ జీవితంలో ఆనందాల హరివిల్లును పూయించాలి.',
      'గౌరమ్మ తల్లి కరుణాకటాక్షాలు మీ కుటుంబంపై సదా ఉండాలని ప్రార్థిస్తూ...',
      'ఆడపడుచులందరికీ సద్దుల బతుకమ్మ పండుగ హృదయపూర్వక శుభాకాంక్షలు.'
    ],
    readyMessagesEn: [
      'May the floral colors and joy of Bathukamma bring prosperity, vitality, and sisterhood to your home!',
      'Celebrating the sacred bond between nature, flowers, and motherhood. Happy Bathukamma!',
      'May the vibrant blossoms of Gunugu and Tangedu spread joy in your household.',
      'Wishing all sisters and families an enchanting and joyful Bathukamma celebration.',
      'Hearty Bathukamma festival greetings to you and your loved ones!'
    ]
  },
  {
    id: 'fest_christmas',
    category: 'FESTIVALS',
    titleEn: 'Merry Christmas',
    titleTe: 'క్రిస్మస్ పండుగ',
    bgGradient: 'linear-gradient(135deg, #0A2614 0%, #154726 50%, #2D7A44 100%)',
    primaryColor: '#FFD700',
    accentColor: '#E63946',
    textColor: '#FFFFFF',
    patternType: 'stars',
    defaultMessageTe: 'ప్రేమ, కరుణ, శాంతిని బోధించిన ఏసుక్రీస్తు జన్మదిన పర్వదినమైన క్రిస్మస్ శుభాకాంక్షలు! మీ ఇంట సంతోషం వెల్లివిరియాలి.',
    defaultMessageEn: 'May the divine spirit of Christmas fill your home with peace, goodwill, warm smiles, and endless love!',
    readyMessagesTe: [
      'ప్రేమ, కరుణ, శాంతిని బోధించిన ఏసుక్రీస్తు జన్మదిన పర్వదినమైన క్రిస్మస్ శుభాకాంక్షలు! మీ ఇంట సంతోషం వెల్లివిరియాలి.',
      'మీకు, మీ కుటుంబ సభ్యులందరికీ మెర్రీ క్రిస్మస్! ఈ పండుగ మీ జీవితంలో ఆనందాన్ని నింపాలి.',
      'క్రిస్మస్ నక్షత్రంలా మీ జీవితం ఎల్లప్పుడూ ఆశతో, ప్రేమతో ప్రకాశించుగాక.',
      'శాంతిదూత ఏసుక్రీస్తు దీవెనలు మీకు సదా తోడుండాలని ప్రార్థిస్తున్నాను.',
      'క్రిస్మస్ పర్వదినం సందర్భంగా మీకు హృదయపూర్వక శుభాకాంక్షలు.'
    ],
    readyMessagesEn: [
      'May the divine spirit of Christmas fill your home with peace, goodwill, warm smiles, and endless love!',
      'Wishing you a joyous and blessed Christmas season surrounded by cherished family.',
      'May hope, faith, and radiant joy abide in your heart throughout this season.',
      'Sending warm holiday wishes and festive cheer for Christmas and the coming year.',
      'Merry Christmas to you and your beloved ones!'
    ]
  },
  {
    id: 'fest_ramzan',
    category: 'FESTIVALS',
    titleEn: 'Eid Mubarak / Ramzan',
    titleTe: 'రంజాన్ / ఈద్ ముబారక్',
    bgGradient: 'linear-gradient(135deg, #041B15 0%, #0A3A2F 50%, #126351 100%)',
    primaryColor: '#E9C46A',
    accentColor: '#2A9D8F',
    textColor: '#F4F1DE',
    patternType: 'stars',
    defaultMessageTe: 'పవిత్ర రంజాన్ పర్వదినం సందర్భంగా మీకు, మీ కుటుంబానికి ఈద్ ముబారక్! అల్లాహ్ కటాక్షంతో మీ ఇంట శాంతి, ఐశ్వర్యం వెల్లివిరియాలి.',
    defaultMessageEn: 'Eid Mubarak! May this blessed day bring boundless peace, compassion, good health, and prosperity to your home.',
    readyMessagesTe: [
      'పవిత్ర రంజాన్ పర్వదినం సందర్భంగా మీకు, మీ కుటుంబానికి ఈద్ ముబారక్! అల్లాహ్ కటాక్షంతో మీ ఇంట శాంతి, ఐశ్వర్యం వెల్లివిరియాలి.',
      'ఉపవాస దీక్షల పుణ్యఫలాలు మీ సొంతమై, మీ ఇంట సుఖసంతోషాలు నిండాలని కోరుకుంటూ ఈద్ శుభాకాంక్షలు.',
      'సేవ, దానధర్మాల పవిత్ర సంప్రదాయం మీ జీవితాన్ని ఆనందమయం చేయాలి.',
      'ఈద్ ముబారక్! మీ ప్రార్థనలన్నీ సఫలమవ్వాలని ఆకాంక్షిస్తూ...',
      'మీకు, మీ బంధుమిత్రులకు పవిత్ర రంజాన్ పండుగ శుభాకాంక్షలు.'
    ],
    readyMessagesEn: [
      'Eid Mubarak! May this blessed day bring boundless peace, compassion, good health, and prosperity to your home.',
      'May the divine blessings of Ramadan illuminate your life with serenity and gratitude.',
      'Wishing you and your family a festive and joy-filled Eid celebration.',
      'May your prayers, charity, and fasts be accepted with divine benevolence.',
      'Warmest greetings of Eid-ul-Fitr to you and your loved ones!'
    ]
  },
  {
    id: 'fest_new_year',
    category: 'FESTIVALS',
    titleEn: 'Happy New Year',
    titleTe: 'నూతన సంవత్సర శుభాకాంక్షలు',
    bgGradient: 'linear-gradient(135deg, #1A0526 0%, #350A4E 50%, #62148F 100%)',
    primaryColor: '#FFD700',
    accentColor: '#FF007F',
    textColor: '#FFFFFF',
    patternType: 'stars',
    defaultMessageTe: 'ఈ నూతన సంవత్సరం మీ జీవితంలో సరికొత్త విజయాలను, ఆయురారోగ్యాలను, అంతులేని ఆనందాన్ని తీసుకురావాలని ఆశిస్తూ నూతన సంవత్సర శుభాకాంక్షలు!',
    defaultMessageEn: 'Wishing you a joyful and triumphant New Year! May each day bring fresh opportunities, happiness, and prosperity.',
    readyMessagesTe: [
      'ఈ నూతన సంవత్సరం మీ జీవితంలో సరికొత్త విజయాలను, ఆయురారోగ్యాలను, అంతులేని ఆనందాన్ని తీసుకురావాలని ఆశిస్తూ నూతన సంవత్సర శుభాకాంక్షలు!',
      'నూతన ఆశలు, సరికొత్త సంకల్పాలతో ఈ సంవత్సరాన్ని ఘనంగా ప్రారంభించండి.',
      'ప్రతి నెల, ప్రతి వారం, ప్రతి రోజు మీకు విజయవంతంగా గడవాలని ప్రార్థిస్తున్నాను.',
      'కష్టాలు తొలగి, కొత్త సంవత్సరంలో మీ కలలన్నీ సాకారం కావాలి. హ్యాపీ న్యూ ఇయర్!',
      'మీకు, మీ కుటుంబ సభ్యులకు ఆంగ్ల నూతన సంవత్సర శుభాకాంక్షలు.'
    ],
    readyMessagesEn: [
      'Wishing you a joyful and triumphant New Year! May each day bring fresh opportunities, happiness, and prosperity.',
      'Step boldly into the new year with courage, enthusiasm, and hope.',
      'May 365 days of good fortune and peace accompany you throughout this year.',
      'Wishing you grand milestones and sweet family memories in the coming year.',
      'Happy New Year to you and your dear family!'
    ]
  },

  // --- BIRTHDAY (6 templates) ---
  {
    id: 'bday_golden_celebration',
    category: 'BIRTHDAY',
    titleEn: 'Golden Birthday',
    titleTe: 'స్వర్ణ జన్మదినం',
    bgGradient: 'linear-gradient(135deg, #3B0918 0%, #6E1230 50%, #B8224A 100%)',
    primaryColor: '#FFD700',
    accentColor: '#FF9E00',
    textColor: '#FFF7EC',
    patternType: 'flower',
    defaultMessageTe: 'మీకు హృదయపూర్వక జన్మదిన శుభాకాంక్షలు! ఆయురారోగ్యాలతో, అష్టైశ్వర్యాలతో నూరేళ్లు సంతోషంగా వర్ధిల్లాలని మనసారా కోరుకుంటున్నాను.',
    defaultMessageEn: 'Wishing you a very Happy Birthday! May you be blessed with longevity, vibrant health, peace, and splendid success.',
    readyMessagesTe: [
      'మీకు హృదయపూర్వక జన్మదిన శుభాకాంక్షలు! ఆయురారోగ్యాలతో, అష్టైశ్వర్యాలతో నూరేళ్లు సంతోషంగా వర్ధిల్లాలని మనసారా కోరుకుంటున్నాను.',
      'జీవితంలో మీరు కోరుకున్న ప్రతి లక్ష్యాన్ని సాధించాలని దైవాన్ని ప్రార్థిస్తూ జన్మదిన శుభాకాంక్షలు!',
      'ఈ పుట్టినరోజు మీ జీవితంలో సరికొత్త సంతోషాలను, విజయాలను నింపాలి. హ్యాపీ బర్త్‌డే!',
      'నిండు నూరేళ్లు చిరునవ్వుతో, ఆరోగ్యంగా జీవించాలని మనసారా దీవిస్తూ...',
      'పుట్టినరోజు శుభాకాంక్షలు! మీ ప్రతి అడుగులోనూ విజయలక్ష్మి తోడుండాలి.'
    ],
    readyMessagesEn: [
      'Wishing you a very Happy Birthday! May you be blessed with longevity, vibrant health, peace, and splendid success.',
      'May all your cherished dreams and noble aspirations come true this year. Happy Birthday!',
      'Wishing you another year filled with delightful laughter, love, and wonderful milestones.',
      'May divine grace shield and guide you on your birthday and all the days ahead.',
      'Warmest birthday wishes to an incredible person! Enjoy your special day!'
    ]
  },
  {
    id: 'bday_vedic_ayush',
    category: 'BIRTHDAY',
    titleEn: 'Vedic Blessings',
    titleTe: 'ఆయుష్షు ప్రాప్తి',
    bgGradient: 'linear-gradient(135deg, #1C0F38 0%, #3A206B 50%, #653A9E 100%)',
    primaryColor: '#FFE066',
    accentColor: '#D8BBFF',
    textColor: '#F8F9FA',
    patternType: 'mandala',
    defaultMessageTe: 'శతమానం భవతి శతాయుః పురుషః శతేంద్రియః — ఆయుష్మాన్ భవ! దైవానుగ్రహంతో నిండు నూరేళ్లు ఆనందంగా జీవించాలని ప్రార్థిస్తున్నాను.',
    defaultMessageEn: 'Shatamanam Bhavati! May the Supreme Divine bless you with a hundred years of health, wisdom, and virtuous happiness.',
    readyMessagesTe: [
      'శతమానం భవతి శతాయుః పురుషః శతేంద్రియః — ఆయుష్మాన్ భవ! దైవానుగ్రహంతో నిండు నూరేళ్లు ఆనందంగా జీవించాలని ప్రార్థిస్తున్నాను.',
      'మీ జన్మదినం సందర్భంగా కులదైవ ఆశీస్సులు మీపై సదా ఉండాలని కోరుకుంటున్నాను.',
      'విద్యా, వినయాలు, కీర్తి ప్రతిష్టలు మీ సొంతం కావాలని ఆకాంక్షిస్తూ పుట్టినరోజు శుభాకాంక్షలు.',
      'ధర్మబద్ధమైన జీవితం గడుపుతూ సమాజానికి ఆదర్శంగా నిలవాలని ఆశిస్తూ...',
      'జన్మదిన శుభాకాంక్షలు! భగవంతుని రక్షణ మీ చుట్టూ సదా ఉండాలి.'
    ],
    readyMessagesEn: [
      'Shatamanam Bhavati! May the Supreme Divine bless you with a hundred years of health, wisdom, and virtuous happiness.',
      'May ancient blessings of health and noble renown accompany your sacred journey.',
      'Wishing you spiritual peace, intellectual brilliance, and prosperity on your birthday.',
      'May you continue to inspire everyone with your gentleness and good conduct.',
      'Hearty birthday blessings for a bright and purposeful future!'
    ]
  },
  {
    id: 'bday_festive_rangoli',
    category: 'BIRTHDAY',
    titleEn: 'Rangoli Celebration',
    titleTe: 'సంబరాల ముగ్గు',
    bgGradient: 'linear-gradient(135deg, #2D1405 0%, #66320C 50%, #AB5A18 100%)',
    primaryColor: '#FFE6A7',
    accentColor: '#FFB703',
    textColor: '#FFFBF0',
    patternType: 'rangoli',
    defaultMessageTe: 'ఈ ప్రత్యేక దినం మీ జీవితంలో చిరకాలం గుర్తుండిపోయే మధుర జ్ఞాపకాలను ఇవ్వాలని ఆశిస్తూ పుట్టినరోజు శుభాకాంక్షలు!',
    defaultMessageEn: 'May this special birthday shower you with memorable joy and cherished blessings!',
    readyMessagesTe: [
      'ఈ ప్రత్యేక దినం మీ జీవితంలో చిరకాలం గుర్తుండిపోయే మధుర జ్ఞాపకాలను ఇవ్వాలని ఆశిస్తూ పుట్టినరోజు శుభాకాంక్షలు!',
      'ప్రతి పుట్టినరోజూ మీ ఎదుగుదలకు మరింత బలాన్ని ఇవ్వాలని కోరుకుంటున్నాను.',
      'ఆత్మీయుల ప్రేమాభిమానాలు మీ వెంటే ఉండాలని ప్రార్థిస్తూ జన్మదిన శుభాకాంక్షలు.',
      'మీ ముఖంలో విరిసే చిరునవ్వే మీ అసలైన సంపద. హ్యాపీ బర్త్‌డే!',
      'సకల శుభములు చేకూరాలని మనసారా ఆశీర్వదిస్తూ జన్మదిన శుభాకాంక్షలు.'
    ],
    readyMessagesEn: [
      'May this special birthday shower you with memorable joy and cherished blessings!',
      'Celebrate today with gratitude and enthusiasm. Happy Birthday!',
      'May the year ahead bring thrilling breakthroughs and continuous contentment.',
      'Wishing you abundant strength and good fortune on your milestone day.',
      'Warmest thoughts and happiest wishes on your birthday!'
    ]
  },
  {
    id: 'bday_floral_wishes',
    category: 'BIRTHDAY',
    titleEn: 'Floral Harmony',
    titleTe: 'కుసుమ శుభాలు',
    bgGradient: 'linear-gradient(135deg, #1C331B 0%, #305831 50%, #4D8B4E 100%)',
    primaryColor: '#D8F3DC',
    accentColor: '#95D5B2',
    textColor: '#FFFFFF',
    patternType: 'flower',
    defaultMessageTe: 'విరిసిన పూల సువాసనల వలె మీ జీవితం ఎల్లప్పుడూ ఆనంద సుగంధాలతో పరిమళించాలని కోరుకుంటూ జన్మదిన శుభాకాంక్షలు!',
    defaultMessageEn: 'May your life stay fragrant with happiness, loving companions, and serenity. Happy Birthday!',
    readyMessagesTe: [
      'విరిసిన పూల సువాసనల వలె మీ జీవితం ఎల్లప్పుడూ ఆనంద సుగంధాలతో పరిమళించాలని కోరుకుంటూ జన్మదిన శుభాకాంక్షలు!',
      'మీ కృషికి తగిన సముచిత గౌరవం లభించాలని ప్రార్థిస్తూ హ్యాపీ బర్త్‌డే.',
      'నిరంతరం ఉత్సాహంగా, ఆరోగ్యంగా ఉండాలని ఆకాంక్షిస్తూ...',
      'పుట్టినరోజు శుభాకాంక్షలు! ప్రతి రోజూ ఒక నూతన ఉదయమే.',
      'మీ కలలన్నీ తీరి, మీ జీవితం సంతోషంగా సాగాలని కోరుకుంటూ...'
    ],
    readyMessagesEn: [
      'May your life stay fragrant with happiness, loving companions, and serenity. Happy Birthday!',
      'May your steadfast endeavors be rewarded with highest recognition.',
      'Wishing you vibrant stamina, peace, and prosperity throughout this year.',
      'Stay joyful and hopeful every single day. Happy Birthday!',
      'May love and warm laughter brighten your path today and always.'
    ]
  },
  {
    id: 'bday_crimson_joy',
    category: 'BIRTHDAY',
    titleEn: 'Crimson Joy',
    titleTe: 'రక్తచందన కాంతి',
    bgGradient: 'linear-gradient(135deg, #4A0E1A 0%, #871930 50%, #C42749 100%)',
    primaryColor: '#FFCCD5',
    accentColor: '#FF758F',
    textColor: '#FFF0F3',
    patternType: 'diya',
    defaultMessageTe: 'మీ హృదయంలోని సద్గుణాలు మీకు నిరంతర శాంతిని, గౌరవాన్ని అందించాలి. పుట్టినరోజు శుభాకాంక్షలు!',
    defaultMessageEn: 'May your virtues continue to earn you affection, respect, and deep peace. Happy Birthday!',
    readyMessagesTe: [
      'మీ హృదయంలోని సద్గుణాలు మీకు నిరంతర శాంతిని, గౌరవాన్ని అందించాలి. పుట్టినరోజు శుభాకాంక్షలు!',
      'మీతో స్నేహం ఎంతో అమూల్యమైనది. హ్యాపీ బర్త్‌డే మిత్రమా!',
      'కుటుంబంతో కలిసి ఆనందంగా ఈ పుట్టినరోజును జరుపుకోండి.',
      'ఎల్లప్పుడూ ఇతరులకు మేలు చేసే మీ మంచి మనసు చల్లగా ఉండాలి.',
      'జన్మదిన శుభాకాంక్షలు! మీ జీవితం సుఖసంతోషాలతో నిండాలి.'
    ],
    readyMessagesEn: [
      'May your virtues continue to earn you affection, respect, and deep peace. Happy Birthday!',
      'Grateful for your precious presence in our lives. Happy Birthday!',
      'Enjoy this memorable day surrounded by fond family and dear friends.',
      'May your kind and generous heart always be blessed with joy.',
      'Wishing you a vibrant, heartwarming, and joyful birthday!'
    ]
  },
  {
    id: 'bday_sapphire_sparkle',
    category: 'BIRTHDAY',
    titleEn: 'Sapphire Sparkle',
    titleTe: 'నీలమణి దీవెన',
    bgGradient: 'linear-gradient(135deg, #051923 0%, #003554 50%, #006494 100%)',
    primaryColor: '#A8DADC',
    accentColor: '#00A6FB',
    textColor: '#FFFFFF',
    patternType: 'stars',
    defaultMessageTe: 'ఆకాశమంత ఎత్తుకు మీరు ఎదగాలని, చుక్కల వలె మీ కీర్తి ప్రకాశించాలని కోరుకుంటూ జన్మదిన శుభాకాంక్షలు!',
    defaultMessageEn: 'May your accomplishments soar to sky-high heights and shine like radiant stars. Happy Birthday!',
    readyMessagesTe: [
      'ఆకాశమంత ఎత్తుకు మీరు ఎదగాలని, చుక్కల వలె మీ కీర్తి ప్రకాశించాలని కోరుకుంటూ జన్మదిన శుభాకాంక్షలు!',
      'మీ ఆత్మవిశ్వాసమే మీ విజయానికి బాటలు వేయాలి. హ్యాపీ బర్త్‌డే!',
      'ఆరోగ్యంగా, ఆనందంగా నూరేళ్లు జీవించాలని ప్రార్థిస్తూ...',
      'పుట్టినరోజు శుభాకాంక్షలు! మీ ప్రతి కోరిక నెరవేరాలి.',
      'మీ రాబోయే కాలం ఎంతో ఉజ్వలంగా ఉండాలని ఆకాంక్షిస్తూ...'
    ],
    readyMessagesEn: [
      'May your accomplishments soar to sky-high heights and shine like radiant stars. Happy Birthday!',
      'May unshakeable self-belief lead you to your highest summits. Happy Birthday!',
      'Praying for your continuous good health and wholesome contentment.',
      'May all your noble aspirations find fulfillment this year.',
      'Wishing you a brilliant and triumphant year ahead!'
    ]
  },

  // --- ANNIVERSARY (6 templates) ---
  {
    id: 'anniv_sita_rama',
    category: 'ANNIVERSARY',
    titleEn: 'Divine Pair (Sita Rama)',
    titleTe: 'సీతారాముల జంట',
    bgGradient: 'linear-gradient(135deg, #4A0E17 0%, #7E1528 50%, #C93B2B 100%)',
    primaryColor: '#FFD700',
    accentColor: '#FFA500',
    textColor: '#FFF7EC',
    patternType: 'mandala',
    defaultMessageTe: 'సీతారాముల జంట వలె మీ దాంపత్య జీవితం అన్యోన్యంగా, ఆదర్శప్రాయంగా నిలవాలని కోరుకుంటూ వివాహ వార్షికోత్సవ శుభాకాంక్షలు!',
    defaultMessageEn: 'May your marriage be blessed like Sita-Rama, bonded with unshakeable love, mutual devotion, and happiness!',
    readyMessagesTe: [
      'సీతారాముల జంట వలె మీ దాంపత్య జీవితం అన్యోన్యంగా, ఆదర్శప్రాయంగా నిలవాలని కోరుకుంటూ వివాహ వార్షికోత్సవ శుభాకాంక్షలు!',
      'ఏడడుగుల బంధం నూరేళ్లు పచ్చగా వర్ధిల్లాలని ఆకాంక్షిస్తూ పెళ్లిరోజు శుభాకాంక్షలు.',
      'మీ అనురాగం, ఆప్యాయతలు కలకాలం నిలిచి ఉండాలని మనసారా ఆశీర్వదిస్తూ...',
      'లక్ష్మీనారాయణుల అనుగ్రహంతో మీ దాంపత్యం సదా ఆనందమయంగా ఉండాలి.',
      'మీ ఆదర్శ దాంపత్యానికి మా హృదయపూర్వక వివాహ వార్షికోత్సవ శుభాకాంక్షలు!'
    ],
    readyMessagesEn: [
      'May your marriage be blessed like Sita-Rama, bonded with unshakeable love, mutual devotion, and happiness!',
      'May the seven sacred vows flourish into an eternal bond of joy. Happy Wedding Anniversary!',
      'Wishing you decades of sweet togetherness, shared triumphs, and peaceful companionship.',
      'May divine grace enrich your home with perpetual laughter and harmony.',
      'Hearty congratulations to an exemplary couple on your anniversary!'
    ]
  },
  {
    id: 'anniv_golden_knot',
    category: 'ANNIVERSARY',
    titleEn: 'Golden Knot',
    titleTe: 'బంగారు ముడి',
    bgGradient: 'linear-gradient(135deg, #2D1400 0%, #5E2B02 50%, #9C4B03 100%)',
    primaryColor: '#FFE082',
    accentColor: '#FFB300',
    textColor: '#FFFDE7',
    patternType: 'flower',
    defaultMessageTe: 'ఒకరికొకరు తోడై, నీడై సాగుతున్న మీ దాంపత్య ప్రయాణం ఎందరికో స్ఫూర్తిదాయకం. పెళ్లిరోజు శుభాకాంక్షలు!',
    defaultMessageEn: 'Walking hand in hand through every season, your union inspires everyone. Happy Anniversary!',
    readyMessagesTe: [
      'ఒకరికొకరు తోడై, నీడై సాగుతున్న మీ దాంపత్య ప్రయాణం ఎందరికో స్ఫూర్తిదాయకం. పెళ్లిరోజు శుభాకాంక్షలు!',
      'ప్రేమ, అవగాహనలతో కూడిన మీ అనుబంధం నిరంతరం వికసించాలి.',
      'కలిసి గడిపే ప్రతి క్షణం మీ జీవితంలో తీపి జ్ఞాపకంగా మారాలి. వివాహ వార్షికోత్సవ శుభాకాంక్షలు.',
      'మీ ఇంట సుఖశాంతులు, సిరిసంపదలు వెల్లివిరియాలని కోరుకుంటూ...',
      'ఆలుమగల అన్యోన్యతకు నిలువెత్తు రూపమైన మీకు పెళ్లిరోజు శుభాకాంక్షలు!'
    ],
    readyMessagesEn: [
      'Walking hand in hand through every season, your union inspires everyone. Happy Anniversary!',
      'May deep affection and understanding keep growing with every passing sunrise.',
      'May every shared moment turn into an everlasting sweet memory. Happy Anniversary!',
      'Wishing your family peace, good health, and abundant blessings.',
      'Cheers to your beautiful bond of companionship and love!'
    ]
  },
  {
    id: 'anniv_rangoli_celebration',
    category: 'ANNIVERSARY',
    titleEn: 'Rangoli of Love',
    titleTe: 'అనురాగ రంగవల్లి',
    bgGradient: 'linear-gradient(135deg, #3B0D23 0%, #69173E 50%, #A62463 100%)',
    primaryColor: '#F8EDEB',
    accentColor: '#F4A261',
    textColor: '#FFFFFF',
    patternType: 'rangoli',
    defaultMessageTe: 'రంగురంగుల రంగవల్లిలా మీ జీవితం ఎల్లప్పుడూ ఆనందమయంగా ఉండాలని ఆకాంక్షిస్తూ వివాహ వార్షికోత్సవ శుభాకాంక్షలు!',
    defaultMessageEn: 'May your shared life be as vibrant and joyful as a celebratory festival rangoli. Happy Anniversary!',
    readyMessagesTe: [
      'రంగురంగుల రంగవల్లిలా మీ జీవితం ఎల్లప్పుడూ ఆనందమయంగా ఉండాలని ఆకాంక్షిస్తూ వివాహ వార్షికోత్సవ శుభాకాంక్షలు!',
      'మీ ఇద్దరి మధ్య బంధం కాలంతో పాటు మరింత దృఢంగా మారాలని ఆశిస్తూ...',
      'పెళ్లిరోజు శుభాకాంక్షలు! మీ కుటుంబం సదా ఆనందాల హరివిల్లవ్వాలి.',
      'కలిసి వేసిన అడుగులు విజయతీరాలకు చేరాలని కోరుకుంటూ...',
      'సకల శుభాలు మీ దంపతులకు కలగాలని మనసారా దీవిస్తున్నాను.'
    ],
    readyMessagesEn: [
      'May your shared life be as vibrant and joyful as a celebratory festival rangoli. Happy Anniversary!',
      'May the harmony between your hearts grow firmer with every passing year.',
      'Happy Anniversary! May your home always be an abode of sweet laughter and peace.',
      'Wishing you smooth pathways to all your shared aspirations and dreams.',
      'Abundant blessings and joy to a truly wonderful couple!'
    ]
  },
  {
    id: 'anniv_diya_radiance',
    category: 'ANNIVERSARY',
    titleEn: 'Diya of Togetherness',
    titleTe: 'ఐక్యత దీపం',
    bgGradient: 'linear-gradient(135deg, #2A0815 0%, #520F2A 50%, #851945 100%)',
    primaryColor: '#FFD166',
    accentColor: '#EF476F',
    textColor: '#FFF7EC',
    patternType: 'diya',
    defaultMessageTe: 'ఒకరి కంటికి ఒకరు వెలుగై, ఒకరి మనసుకు ఒకరు తోడై నూరేళ్లు సుఖంగా జీవించాలని కోరుకుంటూ పెళ్లిరోజు శుభాకాంక్షలు!',
    defaultMessageEn: 'Lighting each other’s life with warmth and quiet support, may you thrive for a hundred years. Happy Anniversary!',
    readyMessagesTe: [
      'ఒకరి కంటికి ఒకరు వెలుగై, ఒకరి మనసుకు ఒకరు తోడై నూరేళ్లు సుఖంగా జీవించాలని కోరుకుంటూ పెళ్లిరోజు శుభాకాంక్షలు!',
      'మీ అన్యోన్యతకు దైవకటాక్షం తోడవ్వాలని ప్రార్థిస్తూ వివాహ వార్షికోత్సవ శుభాకాంక్షలు.',
      'మీ జీవితంలో అనురాగం ఎప్పటికీ తరగని గనిలా ఉండాలి.',
      'కలిసి సాగే ఈ సుదీర్ఘ ప్రయాణంలో ప్రతి క్షణాన్ని ఆస్వాదించండి.',
      'వివాహ వార్షికోత్సవ శుభాకాంక్షలు! మీ జంట ఎల్లప్పుడూ కలకాలం వర్ధిల్లాలి.'
    ],
    readyMessagesEn: [
      'Lighting each other’s life with warmth and quiet support, may you thrive for a hundred years. Happy Anniversary!',
      'May divine benevolence crown your shared devotion with tranquility.',
      'May genuine care and patience remain the bedrock of your home.',
      'Cherish each moment of this profound sacred journey together.',
      'Warmest wishes on your wedding anniversary!'
    ]
  },
  {
    id: 'anniv_silver_stream',
    category: 'ANNIVERSARY',
    titleEn: 'Silver Stream',
    titleTe: 'రజత వాహిని',
    bgGradient: 'linear-gradient(135deg, #10192A 0%, #1C2D4B 50%, #2A4573 100%)',
    primaryColor: '#E0E1DD',
    accentColor: '#778DA9',
    textColor: '#FFFFFF',
    patternType: 'stars',
    defaultMessageTe: 'గంగా-యమునల కలయికలా మీ దాంపత్యం పవిత్రంగా, శాంతియుతంగా సాగిపోవాలని కోరుకుంటూ వివాహ వార్షికోత్సవ శుభాకాంక్షలు!',
    defaultMessageEn: 'Like the confluence of sacred rivers, may your union flow in eternal purity and calm. Happy Anniversary!',
    readyMessagesTe: [
      'గంగా-యమునల కలయికలా మీ దాంపత్యం పవిత్రంగా, శాంతియుతంగా సాగిపోవాలని కోరుకుంటూ వివాహ వార్షికోత్సవ శుభాకాంక్షలు!',
      'ఎన్నో మధుర జ్ఞాపకాలను నెమరువేసుకుంటూ ఈ వార్షికోత్సవాన్ని జరుపుకోండి.',
      'మీరిద్దరూ ఎల్లప్పుడూ ఆయురారోగ్యాలతో, ఐశ్వర్యంతో వర్ధిల్లాలి.',
      'పెళ్లిరోజు శుభాకాంక్షలు! మీ అనుబంధం కలకాలం ఆదర్శంగా నిలవాలి.',
      'ఆనందం మీ ఇంట నిండిపోవాలని, దైవకృప సదా తోడుండాలని ప్రార్థిస్తూ...'
    ],
    readyMessagesEn: [
      'Like the confluence of sacred rivers, may your union flow in eternal purity and calm. Happy Anniversary!',
      'Celebrate this day reflecting on all the heartwarming memories you built.',
      'Wishing you both vibrant wellness, laughter, and prosperity.',
      'May your union remain an inspiring model of devotion and grace.',
      'Hearty congratulations and best blessings on your anniversary!'
    ]
  },
  {
    id: 'anniv_lotus_bond',
    category: 'ANNIVERSARY',
    titleEn: 'Eternal Lotus Bond',
    titleTe: 'కమల బంధం',
    bgGradient: 'linear-gradient(135deg, #1C051F 0%, #3D0D43 50%, #681772 100%)',
    primaryColor: '#F7C6D0',
    accentColor: '#C77DFF',
    textColor: '#FFFFFF',
    patternType: 'flower',
    defaultMessageTe: 'ప్రేమ, త్యాగం, నమ్మకాలతో ముడిపడిన మీ బంధం కలకాలం వర్ధిల్లాలని మనసారా కోరుకుంటూ పెళ్లిరోజు శుభాకాంక్షలు!',
    defaultMessageEn: 'Bound by noble trust, patience, and tenderness, may your bond flourish endlessly. Happy Anniversary!',
    readyMessagesTe: [
      'ప్రేమ, త్యాగం, నమ్మకాలతో ముడిపడిన మీ బంధం కలకాలం వర్ధిల్లాలని మనసారా కోరుకుంటూ పెళ్లిరోజు శుభాకాంక్షలు!',
      'ఒకరికొకరు ఆధారమై, సంతోషకరమైన జీవితాన్ని గడపాలని ఆశిస్తూ...',
      'వివాహ వార్షికోత్సవ శుభాకాంక్షలు! మీ కుటుంబం సదా సుఖసంతోషాలతో ఉండాలి.',
      'మీ దాంపత్యం సకల శుభములతో, ఐశ్వర్యంతో శోభిల్లాలి.',
      'మీ ఇద్దరి ప్రేమానురాగాలు ఎప్పటికీ చిరస్థాయిగా నిలిచిపోవాలని ప్రార్థిస్తూ...'
    ],
    readyMessagesEn: [
      'Bound by noble trust, patience, and tenderness, may your bond flourish endlessly. Happy Anniversary!',
      'Supporting each other’s journeys with warmth and mutual dignity. Happy Anniversary!',
      'May peace, good cheer, and noble virtues reside in your home always.',
      'Wishing you ongoing happiness and strength through every milestone.',
      'Warmest congratulations on another blessed year of marriage!'
    ]
  },

  // --- DEVOTIONAL (6 templates) ---
  {
    id: 'devo_tirumala_balaji',
    category: 'DEVOTIONAL',
    titleEn: 'Sri Venkateswara Swamy',
    titleTe: 'శ్రీ వేంకటేశ్వర స్వామి',
    bgGradient: 'linear-gradient(135deg, #2D0814 0%, #5B0E1E 50%, #9C182F 100%)',
    primaryColor: '#FFD700',
    accentColor: '#FFA500',
    textColor: '#FFF7EC',
    patternType: 'mandala',
    defaultMessageTe: 'కలియుగ వైకుంఠనాథుడైన శ్రీనివాసుని దివ్య కృపాకటాక్షాలు మీ కుటుంబంపై సదా ఉండాలని, ఆయురారోగ్యాలు ప్రసాదించాలని ప్రార్థిస్తున్నాను.',
    defaultMessageEn: 'May Lord Venkateswara of Tirumala shower eternal grace, protection, and prosperity upon your household.',
    readyMessagesTe: [
      'కలియుగ వైకుంఠనాథుడైన శ్రీనివాసుని దివ్య కృపాకటాక్షాలు మీ కుటుంబంపై సదా ఉండాలని, ఆయురారోగ్యాలు ప్రసాదించాలని ప్రార్థిస్తున్నాను.',
      'ఓం నమో వేంకటేశాయ! గోవిందుని నామస్మరణతో మీ సకల పాపాలు, కష్టాలు తొలగిపోవాలి.',
      'శ్రీవారి దివ్య పాదపద్మాలకు నమస్కరిస్తూ, మీకు సకల అభీష్టసిద్ధి కలగాలని కోరుకుంటున్నాను.',
      'ఏడుకొండలవాడా వెంకటరమణా గోవిందా గోవిందా! తిరుమలేశుని అనుగ్రహం మీకు రక్ష.',
      'నిత్యం దైవచింతనతో గడుపుతూ ప్రశాంతమైన జీవితాన్ని గడపాలని ఆశిస్తూ...'
    ],
    readyMessagesEn: [
      'May Lord Venkateswara of Tirumala shower eternal grace, protection, and prosperity upon your household.',
      'Om Namo Venkatesaya! Chanting Govinda’s sacred names dispels all adversity and sorrow.',
      'May the lotus feet of Sri Balaji guide you through every crossroad in life.',
      'Praying for the divine blessing of the Lord of Seven Hills to shield your home.',
      'May devotion bring pristine peace and enduring wisdom to you and your family.'
    ]
  },
  {
    id: 'devo_shiva_shambho',
    category: 'DEVOTIONAL',
    titleEn: 'Shiva Shambho',
    titleTe: 'శివ శంభో',
    bgGradient: 'linear-gradient(135deg, #09111E 0%, #15253F 50%, #203A62 100%)',
    primaryColor: '#BEE3F8',
    accentColor: '#90CDF4',
    textColor: '#F7FAFC',
    patternType: 'mandala',
    defaultMessageTe: 'ఓం త్రయంబకం యజామహే సుగంధిం పుష్టివర్ధనమ్ — మృత్యుంజయ మహాదేవుడు మీకు పూర్ణాయుష్షును, మనశ్శాంతిని ప్రసాదించుగాక.',
    defaultMessageEn: 'Om Tryambakam Yajamahe! May Lord Shiva bestow long life, liberation from ailments, and serene tranquility.',
    readyMessagesTe: [
      'ఓం త్రయంబకం యజామహే సుగంధిం పుష్టివర్ధనమ్ — మృత్యుంజయ మహాదేవుడు మీకు పూర్ణాయుష్షును, మనశ్శాంతిని ప్రసాదించుగాక.',
      'హర హర మహాదేవ శంభో శంకర! సదాశివుని కరుణతో మీ ఇంట శాంతి వెల్లివిరియాలి.',
      'గంగాధరుడైన పరమేశ్వరుని ఆశీస్సులతో మీ జీవితంలోని అశాంతి తొలగిపోవుగాక.',
      'నిత్యం శివనామస్మరణ చేయండి, నిర్భయంగా ఆనందంగా జీవించండి.',
      'భోళాశంకరుని అనుగ్రహంతో మీ సత్సంకల్పాలన్నీ నెరవేరాలని ప్రార్థిస్తూ...'
    ],
    readyMessagesEn: [
      'Om Tryambakam Yajamahe! May Lord Shiva bestow long life, liberation from ailments, and serene tranquility.',
      'Hara Hara Mahadeva! May Lord Shiva’s benevolence dissolve all fears and sorrow.',
      'May the sacred waters of Gangadhara purify your thoughts and grant equanimity.',
      'Anchored in pure devotion to Mahadeva, may you walk in courage and integrity.',
      'Wishing you quiet moments of contemplation and profound spiritual strength.'
    ]
  },
  {
    id: 'devo_lakshmi_kataksham',
    category: 'DEVOTIONAL',
    titleEn: 'Maha Lakshmi Kataksham',
    titleTe: 'మహాలక్ష్మీ కటాక్షం',
    bgGradient: 'linear-gradient(135deg, #380812 0%, #691024 50%, #A31837 100%)',
    primaryColor: '#FFD700',
    accentColor: '#FFA500',
    textColor: '#FFF7EC',
    patternType: 'diya',
    defaultMessageTe: 'సర్వమంగళ మాంగల్యే శివే సర్వార్థ సాధికే — జగన్మాత మహాలక్ష్మి అనుగ్రహంతో మీ ఇల్లు అష్టైశ్వర్యాలతో, సౌభాగ్యంతో తులతూగాలి.',
    defaultMessageEn: 'May Supreme Mother Maha Lakshmi illuminate your dwelling with all eightfold forms of divine prosperity and health.',
    readyMessagesTe: [
      'సర్వమంగళ మాంగల్యే శివే సర్వార్థ సాధికే — జగన్మాత మహాలక్ష్మి అనుగ్రహంతో మీ ఇల్లు అష్టైశ్వర్యాలతో, సౌభాగ్యంతో తులతూగాలి.',
      'దీపలక్ష్మిని పూజించండి, దారిద్ర్యాన్ని పారద్రోలి సిరిసంపదలను ఆహ్వానించండి.',
      'అమ్మవారి అమృత దృష్టి మీపై సదా ఉండాలని, మీ కుటుంబం క్షేమంగా ఉండాలని కోరుకుంటూ...',
      'కనకధారా స్తోత్ర పఠనంతో మీ ఇంట దైవిక సంపదలు నిండాలి.',
      'ధన, ధాన్య, విద్యా, విజయ లక్ష్ముల అనుగ్రహం మీకు లభించాలని ప్రార్థిస్తూ...'
    ],
    readyMessagesEn: [
      'May Supreme Mother Maha Lakshmi illuminate your dwelling with all eightfold forms of divine prosperity and health.',
      'May the golden shower of Goddess Lakshmi’s grace nourish every honest effort you make.',
      'Praying for stability, peace, and spiritual wealth to fill your blessed home.',
      'May reverence for the Divine Mother keep your family protected and content.',
      'Wishing you ongoing auspiciousness and harmonious abundance.'
    ]
  },
  {
    id: 'devo_hanuman_raksha',
    category: 'DEVOTIONAL',
    titleEn: 'Sri Anjaneya Swamy',
    titleTe: 'శ్రీ ఆంజనేయ స్వామి',
    bgGradient: 'linear-gradient(135deg, #3D1005 0%, #752008 50%, #B8340E 100%)',
    primaryColor: '#FFE066',
    accentColor: '#F77F00',
    textColor: '#FFFBF0',
    patternType: 'diya',
    defaultMessageTe: 'బుద్ధిర్బలం యశోధైర్యం నిర్భయత్వమరోగతా — శ్రీ హనుమంతుని దివ్య రక్షతో మీకు అపార ధైర్యం, ఆరోగ్యం, విజయం కలగాలి.',
    defaultMessageEn: 'May Lord Hanuman bestow profound intellect, stamina, fearlessness, health, and steadfast courage upon you.',
    readyMessagesTe: [
      'బుద్ధిర్బలం యశోధైర్యం నిర్భయత్వమరోగతా — శ్రీ హనుమంతుని దివ్య రక్షతో మీకు అపార ధైర్యం, ఆరోగ్యం, విజయం కలగాలి.',
      'జయ హనుమాన్ జ్ఞాన గుణ సాగర! ఆంజనేయుని కృపతో సకల గ్రహదోషాలు, భయాలు పటాపంచలు కావాలి.',
      'రామదూత హనుమాన్ మీ ఇంట సదా రక్షగా నిలవాలని ప్రార్థిస్తున్నాను.',
      'హనుమాన్ చాలీసా పఠనంతో మీకు ఆత్మస్థైర్యం, కార్యసిద్ధి లభించుగాక.',
      'భక్తశిరోమణి అయిన ఆంజనేయ స్వామి అనుగ్రహం మీపై సదా ఉండాలని కోరుకుంటూ...'
    ],
    readyMessagesEn: [
      'May Lord Hanuman bestow profound intellect, stamina, fearlessness, health, and steadfast courage upon you.',
      'Jai Hanuman! May the mighty guardian dispel all anxiety, fear, and negativity from your life.',
      'May Rama’s supreme devotee stand as an impenetrable shield around you and your dear ones.',
      'Chanting the sacred Hanuman Chalisa awakens boundless inner power and peaceful resolve.',
      'May Lord Anjaneya bless you with victory over every difficulty!'
    ]
  },
  {
    id: 'devo_gayatri_radiance',
    category: 'DEVOTIONAL',
    titleEn: 'Gayatri Mantram',
    titleTe: 'గాయత్రీ దివ్య ప్రభ',
    bgGradient: 'linear-gradient(135deg, #2D1400 0%, #663300 50%, #A85A05 100%)',
    primaryColor: '#FFEAA7',
    accentColor: '#F39C12',
    textColor: '#FFF9E6',
    patternType: 'sun',
    defaultMessageTe: 'ఓం భూర్భువః స్వః తత్సవితుర్వరేణ్యం భర్గో దేవస్య ధీమహి ధియో యో నః ప్రచోదయాత్ — గాయత్రీ మాత మీ బుద్ధిని సన్మార్గంలో నడిపించుగాక.',
    defaultMessageEn: 'May the supreme primordial solar radiance of Mother Gayatri purify your intellect and guide your footsteps in truth.',
    readyMessagesTe: [
      'ఓం భూర్భువః స్వః తత్సవితుర్వరేణ్యం భర్గో దేవస్య ధీమహి ధియో యో నః ప్రచోదయాత్ — గాయత్రీ మాత మీ బుద్ధిని సన్మార్గంలో నడిపించుగాక.',
      'వేదమాత గాయత్రీ ఆశీస్సులతో మీకు వివేకం, తేజస్సు, మనశ్శాంతి చేకూరాలి.',
      'సత్యం, ధర్మం, శాంతియే మానవ జీవితానికి శాశ్వత వెలుగులు.',
      'సూర్యుని సాక్షిగా నిత్యం గాయత్రీ జపం చేయండి, అమృతత్వాన్ని పొందండి.',
      'మీకు, మీ కుటుంబ సభ్యులకు దివ్య గాయత్రీ ఆశీర్వచనములు.'
    ],
    readyMessagesEn: [
      'May the supreme primordial solar radiance of Mother Gayatri purify your intellect and guide your footsteps in truth.',
      'May Vedic illumination illuminate your intellect with discernment and peaceful joy.',
      'Living in righteousness and truth reflects the divine presence in every action.',
      'May daily Gayatri meditation kindle the sacred inner flame of spiritual clarity.',
      'Praying for divine discernment and steady fortitude in all your walks of life.'
    ]
  },
  {
    id: 'devo_durga_shakthi',
    category: 'DEVOTIONAL',
    titleEn: 'Maha Shakthi Durga',
    titleTe: 'శ్రీ దుర్గా శక్తి',
    bgGradient: 'linear-gradient(135deg, #3B000C 0%, #6E0017 50%, #A60023 100%)',
    primaryColor: '#FFD700',
    accentColor: '#FF6B6B',
    textColor: '#FFF7EC',
    patternType: 'flower',
    defaultMessageTe: 'జగన్మాత దుర్గాదేవి దివ్య శక్తి మీ ఇంట సకల శత్రువులను, కష్టాలను తొలగించి, రక్షణగా నిలవాలని కోరుకుంటూ శుభాకాంక్షలు!',
    defaultMessageEn: 'May Supreme Mother Durga shield you from all adversities and empower your journey with unwavering strength and peace.',
    readyMessagesTe: [
      'జగన్మాత దుర్గాదేవి దివ్య శక్తి మీ ఇంట సకల శత్రువులను, కష్టాలను తొలగించి, రక్షణగా నిలవాలని కోరుకుంటూ శుభాకాంక్షలు!',
      'దుర్గే స్మృతా హరసి భీతిమశేషజంతోః — అమ్మవారి నామస్మరణ సర్వ భయాలను హరిస్తుంది.',
      'కనకదుర్గమ్మ దివ్య కటాక్షం మీ కుటుంబంపై సదా ఉండాలని ప్రార్థిస్తున్నాను.',
      'అధర్మంపై విజయం సాధించే సంకల్పాన్ని, ధైర్యాన్ని అమ్మవారు మీకు ప్రసాదించుగాక.',
      'శక్తిస్వరూపిణి అయిన దుర్గాభవాని అనుగ్రహం మీకు సదా తోడుండాలని కోరుకుంటూ...'
    ],
    readyMessagesEn: [
      'May Supreme Mother Durga shield you from all adversities and empower your journey with unwavering strength and peace.',
      'Remembering the Divine Mother dispels all deep-rooted doubts and anxieties.',
      'May the benevolent grace of Kanaka Durga preserve wellness and light in your home.',
      'May your spirit be blessed with quiet courage to overcome life’s trials.',
      'Heartfelt devotional greetings on the lotus feet of Mother Durga!'
    ]
  }
];

export function getTemplatesByCategory(category: GreetingCategory): GreetingTemplate[] {
  return greetingTemplates.filter((t) => t.category === category);
}

export function getTemplateById(id: string): GreetingTemplate | undefined {
  return greetingTemplates.find((t) => t.id === id);
}
