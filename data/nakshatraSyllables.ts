// verify with a purohit or printed panchangam
export interface PadaSyllable {
  en: string;
  te: string;
}

export interface NakshatraSyllables {
  id: number;
  nameEn: string;
  nameTe: string;
  padas: [PadaSyllable, PadaSyllable, PadaSyllable, PadaSyllable];
}

export const nakshatraSyllables: NakshatraSyllables[] = [
  { id: 1, nameEn: "Ashwini", nameTe: "అశ్విని", padas: [{ en: "Chu", te: "చు" }, { en: "Che", te: "చే" }, { en: "Cho", te: "చో" }, { en: "La", te: "ల" }] },
  { id: 2, nameEn: "Bharani", nameTe: "భరణి", padas: [{ en: "Li", te: "లీ" }, { en: "Lu", te: "లూ" }, { en: "Le", te: "లే" }, { en: "Lo", te: "లో" }] },
  { id: 3, nameEn: "Krittika", nameTe: "కృత్తిక", padas: [{ en: "A", te: "అ" }, { en: "I", te: "ఈ" }, { en: "U", te: "ఉ" }, { en: "E", te: "ఏ" }] },
  { id: 4, nameEn: "Rohini", nameTe: "రోహిణి", padas: [{ en: "O", te: "ఓ" }, { en: "Va", te: "వా" }, { en: "Vi", te: "వీ" }, { en: "Vu", te: "వూ" }] },
  { id: 5, nameEn: "Mrigashira", nameTe: "మృగశిర", padas: [{ en: "Ve", te: "వే" }, { en: "Vo", te: "వో" }, { en: "Ka", te: "కా" }, { en: "Ki", te: "కీ" }] },
  { id: 6, nameEn: "Ardra", nameTe: "ఆర్ద్ర", padas: [{ en: "Ku", te: "కూ" }, { en: "Gha", te: "ఘ" }, { en: "Ng", te: "ఙ" }, { en: "Chha", te: "ఛ" }] },
  { id: 7, nameEn: "Punarvasu", nameTe: "పునర్వసు", padas: [{ en: "Ke", te: "కే" }, { en: "Ko", te: "కో" }, { en: "Ha", te: "హా" }, { en: "Hi", te: "హీ" }] },
  { id: 8, nameEn: "Pushya", nameTe: "పుష్యమి", padas: [{ en: "Hu", te: "హూ" }, { en: "He", te: "హే" }, { en: "Ho", te: "హో" }, { en: "Da", te: "డా" }] },
  { id: 9, nameEn: "Ashlesha", nameTe: "ఆశ్లేష", padas: [{ en: "Di", te: "డీ" }, { en: "Du", te: "డూ" }, { en: "De", te: "డే" }, { en: "Do", te: "డో" }] },
  { id: 10, nameEn: "Magha", nameTe: "మఖ", padas: [{ en: "Ma", te: "మా" }, { en: "Mi", te: "మీ" }, { en: "Mu", te: "మూ" }, { en: "Me", te: "మే" }] },
  { id: 11, nameEn: "Purva Phalguni", nameTe: "పూర్వ ఫల్గుణి", padas: [{ en: "Mo", te: "మో" }, { en: "Ta", te: "టా" }, { en: "Ti", te: "టీ" }, { en: "Tu", te: "టూ" }] },
  { id: 12, nameEn: "Uttara Phalguni", nameTe: "ఉత్తర ఫల్గుణి", padas: [{ en: "Te", te: "టే" }, { en: "To", te: "టో" }, { en: "Pa", te: "పా" }, { en: "Pi", te: "పీ" }] },
  { id: 13, nameEn: "Hasta", nameTe: "హస్త", padas: [{ en: "Pu", te: "పూ" }, { en: "Sha", te: "ష" }, { en: "Na", te: "ణ" }, { en: "Tha", te: "ఠ" }] },
  { id: 14, nameEn: "Chitra", nameTe: "చిత్త", padas: [{ en: "Pe", te: "పే" }, { en: "Po", te: "పో" }, { en: "Ra", te: "రా" }, { en: "Ri", te: "రీ" }] },
  { id: 15, nameEn: "Swati", nameTe: "స్వాతి", padas: [{ en: "Ru", te: "రూ" }, { en: "Re", te: "రే" }, { en: "Ro", te: "రో" }, { en: "Ta", te: "తా" }] },
  { id: 16, nameEn: "Vishakha", nameTe: "విశాఖ", padas: [{ en: "Ti", te: "తీ" }, { en: "Tu", te: "తూ" }, { en: "Te", te: "తే" }, { en: "To", te: "తో" }] },
  { id: 17, nameEn: "Anuradha", nameTe: "అనూరాధ", padas: [{ en: "Na", te: "నా" }, { en: "Ni", te: "నీ" }, { en: "Nu", te: "నూ" }, { en: "Ne", te: "నే" }] },
  { id: 18, nameEn: "Jyeshtha", nameTe: "జ్యేష్ఠ", padas: [{ en: "No", te: "నో" }, { en: "Ya", te: "యా" }, { en: "Yi", te: "యీ" }, { en: "Yu", te: "యూ" }] },
  { id: 19, nameEn: "Mula", nameTe: "మూల", padas: [{ en: "Ye", te: "యే" }, { en: "Yo", te: "యో" }, { en: "Bha", te: "భా" }, { en: "Bhi", te: "భీ" }] },
  { id: 20, nameEn: "Purva Ashadha", nameTe: "పూర్వాషాఢ", padas: [{ en: "Bhu", te: "భూ" }, { en: "Dha", te: "ధ" }, { en: "Pha", te: "ఫ" }, { en: "Dha", te: "ఢ" }] },
  { id: 21, nameEn: "Uttara Ashadha", nameTe: "ఉత్తరాషాఢ", padas: [{ en: "Bhe", te: "భే" }, { en: "Bho", te: "భో" }, { en: "Ja", te: "జా" }, { en: "Ji", te: "జీ" }] },
  { id: 22, nameEn: "Shravana", nameTe: "శ్రవణం", padas: [{ en: "Ju", te: "జూ" }, { en: "Je", te: "జే" }, { en: "Jo", te: "జో" }, { en: "Gha", te: "ఘ" }] },
  { id: 23, nameEn: "Dhanishta", nameTe: "ధనిష్ఠ", padas: [{ en: "Ga", te: "గా" }, { en: "Gi", te: "గీ" }, { en: "Gu", te: "గూ" }, { en: "Ge", te: "గే" }] },
  { id: 24, nameEn: "Shatabhisha", nameTe: "శతభిషం", padas: [{ en: "Go", te: "గో" }, { en: "Sa", te: "సా" }, { en: "Si", te: "సీ" }, { en: "Su", te: "సూ" }] },
  { id: 25, nameEn: "Purva Bhadrapada", nameTe: "పూర్వాభాద్ర", padas: [{ en: "Se", te: "సే" }, { en: "So", te: "సో" }, { en: "Da", te: "దా" }, { en: "Di", te: "దీ" }] },
  { id: 26, nameEn: "Uttara Bhadrapada", nameTe: "ఉత్తరాభాద్ర", padas: [{ en: "Du", te: "దూ" }, { en: "Tha", te: "థ" }, { en: "Jha", te: "ఝ" }, { en: "Na", te: "ఞ" }] },
  { id: 27, nameEn: "Revati", nameTe: "రేవతి", padas: [{ en: "De", te: "దే" }, { en: "Do", te: "దో" }, { en: "Cha", te: "చా" }, { en: "Chi", te: "చీ" }] }
];
