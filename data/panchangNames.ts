export interface PanchangNameEntry {
  id: number;
  nameTe: string;
  nameEn: string;
}

// 30 Tithis (1–15: Shukla Paksha, 16–30: Krishna Paksha)
export const tithis: PanchangNameEntry[] = [
  { id: 1, nameTe: "శుక్ల పాడ్యమి", nameEn: "Shukla Padyami" },
  { id: 2, nameTe: "శుక్ల విదియ", nameEn: "Shukla Vidiya" },
  { id: 3, nameTe: "శుక్ల తదియ", nameEn: "Shukla Tadiya" },
  { id: 4, nameTe: "శుక్ల చవితి", nameEn: "Shukla Chavithi" },
  { id: 5, nameTe: "శుక్ల పంచమి", nameEn: "Shukla Panchami" },
  { id: 6, nameTe: "శుక్ల షష్ఠి", nameEn: "Shukla Shasthi" },
  { id: 7, nameTe: "శుక్ల సప్తమి", nameEn: "Shukla Saptami" },
  { id: 8, nameTe: "శుక్ల అష్టమి", nameEn: "Shukla Ashtami" },
  { id: 9, nameTe: "శుక్ల నవమి", nameEn: "Shukla Navami" },
  { id: 10, nameTe: "శుక్ల దశమి", nameEn: "Shukla Dashami" },
  { id: 11, nameTe: "శుక్ల ఏకాదశి", nameEn: "Shukla Ekadashi" },
  { id: 12, nameTe: "శుక్ల ద్వాదశి", nameEn: "Shukla Dwadashi" },
  { id: 13, nameTe: "శుక్ల త్రయోదశి", nameEn: "Shukla Trayodashi" },
  { id: 14, nameTe: "శుక్ల చతుర్దశి", nameEn: "Shukla Chaturdashi" },
  { id: 15, nameTe: "పూర్ణిమ (పౌర్ణమి)", nameEn: "Purnima (Full Moon)" },
  { id: 16, nameTe: "కృష్ణ పాడ్యమి", nameEn: "Krishna Padyami" },
  { id: 17, nameTe: "కృష్ణ విదియ", nameEn: "Krishna Vidiya" },
  { id: 18, nameTe: "కృష్ణ తదియ", nameEn: "Krishna Tadiya" },
  { id: 19, nameTe: "కృష్ణ చవితి", nameEn: "Krishna Chavithi" },
  { id: 20, nameTe: "కృష్ణ పంచమి", nameEn: "Krishna Panchami" },
  { id: 21, nameTe: "కృష్ణ షష్ఠి", nameEn: "Krishna Shasthi" },
  { id: 22, nameTe: "కృష్ణ సప్తమి", nameEn: "Krishna Saptami" },
  { id: 23, nameTe: "కృష్ణ అష్టమి", nameEn: "Krishna Ashtami" },
  { id: 24, nameTe: "కృష్ణ నవమి", nameEn: "Krishna Navami" },
  { id: 25, nameTe: "కృష్ణ దశమి", nameEn: "Krishna Dashami" },
  { id: 26, nameTe: "కృష్ణ ఏకాదశి", nameEn: "Krishna Ekadashi" },
  { id: 27, nameTe: "కృష్ణ ద్వాదశి", nameEn: "Krishna Dwadashi" },
  { id: 28, nameTe: "కృష్ణ త్రయోదశి", nameEn: "Krishna Trayodashi" },
  { id: 29, nameTe: "కృష్ణ చతుర్దశి", nameEn: "Krishna Chaturdashi" },
  { id: 30, nameTe: "అమావాస్య", nameEn: "Amavasya (New Moon)" }
];

// 27 Nakshatras
export const nakshatras: PanchangNameEntry[] = [
  { id: 1, nameTe: "అశ్విని", nameEn: "Ashwini" },
  { id: 2, nameTe: "భరణి", nameEn: "Bharani" },
  { id: 3, nameTe: "కృత్తిక", nameEn: "Krittika" },
  { id: 4, nameTe: "రోహిణి", nameEn: "Rohini" },
  { id: 5, nameTe: "మృగశిర", nameEn: "Mrigashira" },
  { id: 6, nameTe: "ఆర్ద్ర", nameEn: "Ardra" },
  { id: 7, nameTe: "పునర్వసు", nameEn: "Punarvasu" },
  { id: 8, nameTe: "పుష్యమి", nameEn: "Pushyami" },
  { id: 9, nameTe: "ఆశ్లేష", nameEn: "Ashlesha" },
  { id: 10, nameTe: "మఖ", nameEn: "Magha" },
  { id: 11, nameTe: "పూర్వ ఫల్గుణి (పుబ్బ)", nameEn: "Purva Phalguni (Pubba)" },
  { id: 12, nameTe: "ఉత్తర ఫల్గుణి (ఉత్తర)", nameEn: "Uttara Phalguni" },
  { id: 13, nameTe: "హస్త", nameEn: "Hasta" },
  { id: 14, nameTe: "చిత్త", nameEn: "Chitra" },
  { id: 15, nameTe: "స్వాతి", nameEn: "Swati" },
  { id: 16, nameTe: "విశాఖ", nameEn: "Vishakha" },
  { id: 17, nameTe: "అనూరాధ", nameEn: "Anuradha" },
  { id: 18, nameTe: "జ్యేష్ఠ", nameEn: "Jyeshtha" },
  { id: 19, nameTe: "మూల", nameEn: "Mula" },
  { id: 20, nameTe: "పూర్వాషాఢ", nameEn: "Purvashadha" },
  { id: 21, nameTe: "ఉత్తరాషాఢ", nameEn: "Uttarashadha" },
  { id: 22, nameTe: "శ్రవణం", nameEn: "Shravana" },
  { id: 23, nameTe: "ధనిష్ఠ", nameEn: "Dhanishta" },
  { id: 24, nameTe: "శతభిషం", nameEn: "Shatabhisha" },
  { id: 25, nameTe: "పూర్వాభాద్ర", nameEn: "Purvabhadra" },
  { id: 26, nameTe: "ఉత్తరాభాద్ర", nameEn: "Uttarabhadra" },
  { id: 27, nameTe: "రేవతి", nameEn: "Revati" }
];

// 27 Yogas
export const yogas: PanchangNameEntry[] = [
  { id: 1, nameTe: "విష్కంభ", nameEn: "Vishkambha" },
  { id: 2, nameTe: "ప్రీతి", nameEn: "Priti" },
  { id: 3, nameTe: "ఆయుష్మాన్", nameEn: "Ayushman" },
  { id: 4, nameTe: "సౌభాగ్య", nameEn: "Saubhagya" },
  { id: 5, nameTe: "శోభన", nameEn: "Shobhana" },
  { id: 6, nameTe: "అతిగండ", nameEn: "Atiganda" },
  { id: 7, nameTe: "సుకర్మ", nameEn: "Sukarma" },
  { id: 8, nameTe: "ధృతి", nameEn: "Dhriti" },
  { id: 9, nameTe: "శూల", nameEn: "Shula" },
  { id: 10, nameTe: "గండ", nameEn: "Ganda" },
  { id: 11, nameTe: "వృద్ధి", nameEn: "Vriddhi" },
  { id: 12, nameTe: "ధ్రువ", nameEn: "Dhruva" },
  { id: 13, nameTe: "వ్యాఘాత", nameEn: "Vyaghata" },
  { id: 14, nameTe: "హర్షణ", nameEn: "Harshana" },
  { id: 15, nameTe: "వజ్ర", nameEn: "Vajra" },
  { id: 16, nameTe: "సిద్ధి", nameEn: "Siddhi" },
  { id: 17, nameTe: "వ్యతీపాత", nameEn: "Vyatipata" },
  { id: 18, nameTe: "వరీయాన్", nameEn: "Variyan" },
  { id: 19, nameTe: "పరిఘ", nameEn: "Parigha" },
  { id: 20, nameTe: "శివ", nameEn: "Shiva" },
  { id: 21, nameTe: "సిద్ధ", nameEn: "Siddha" },
  { id: 22, nameTe: "సాధ్య", nameEn: "Sadhya" },
  { id: 23, nameTe: "శుభ", nameEn: "Shubha" },
  { id: 24, nameTe: "శుభ్ర", nameEn: "Shubhra" },
  { id: 25, nameTe: "బ్రహ్మ", nameEn: "Brahma" },
  { id: 26, nameTe: "ఐంద్ర", nameEn: "Aindra" },
  { id: 27, nameTe: "వైధృతి", nameEn: "Vaidhriti" }
];

// 11 Karanas (4 Fixed + 7 Repeating)
export const repeatingKaranas: PanchangNameEntry[] = [
  { id: 1, nameTe: "బవ", nameEn: "Bava" },
  { id: 2, nameTe: "బాలవ", nameEn: "Balava" },
  { id: 3, nameTe: "కౌలవ", nameEn: "Kaulava" },
  { id: 4, nameTe: "తైతిల", nameEn: "Taitila" },
  { id: 5, nameTe: "గరజ", nameEn: "Garaja" },
  { id: 6, nameTe: "వణిజ", nameEn: "Vanija" },
  { id: 7, nameTe: "విష్టి (భద్ర)", nameEn: "Vishti (Bhadra)" }
];

export const fixedKaranas: Record<number, PanchangNameEntry> = {
  0: { id: 0, nameTe: "కింస్తుఘ్న", nameEn: "Kimstughna" },
  57: { id: 57, nameTe: "శకుని", nameEn: "Shakuni" },
  58: { id: 58, nameTe: "చతుష్పాద", nameEn: "Chatushpada" },
  59: { id: 59, nameTe: "నాగవ", nameEn: "Naga" }
};

export const vaaramNames = {
  te: ["ఆదివారం", "సోమవారం", "మంగళవారం", "బుధవారం", "గురువారం", "శుక్రవారం", "శనివారం"],
  en: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"]
};
