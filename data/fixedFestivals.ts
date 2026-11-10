// Festivals that can't be calculated from the Panchang: civil holidays on fixed dates,
// and festivals that depend on moon sighting.
// The Android app reads this file when it is built. Keep the array JSON-like (no trailing commas).
//
// "date": "MM-DD" repeats every year. "dates": ["YYYY-MM-DD", ...] lists specific years.
// "moonSighting": true shows "subject to moon sighting" next to the date.

export interface FixedFestival {
  id: string;
  nameEn: string;
  nameTe: string;
  date?: string;
  dates?: string[];
  moonSighting?: boolean;
  major?: boolean;
  descriptionEn?: string;
  descriptionTe?: string;
}

export const fixedFestivals: FixedFestival[] = [
  { "id": "new_year", "nameEn": "New Year's Day", "nameTe": "ఆంగ్ల నూతన సంవత్సరం", "date": "01-01" },
  { "id": "republic_day", "nameEn": "Republic Day", "nameTe": "గణతంత్ర దినోత్సవం", "date": "01-26" },
  { "id": "telangana_formation_day", "nameEn": "Telangana Formation Day", "nameTe": "తెలంగాణ రాష్ట్ర ఆవిర్భావ దినోత్సవం", "date": "06-02" },
  { "id": "independence_day", "nameEn": "Independence Day", "nameTe": "స్వాతంత్ర్య దినోత్సవం", "date": "08-15" },
  { "id": "gandhi_jayanti", "nameEn": "Gandhi Jayanti", "nameTe": "గాంధీ జయంతి", "date": "10-02" },
  { "id": "christmas", "nameEn": "Christmas", "nameTe": "క్రిస్మస్", "date": "12-25" },

  // Estimated dates. Replace them with the government holiday list once it is announced.
  {
    "id": "ramzan", "nameEn": "Ramzan (Eid ul-Fitr)", "nameTe": "రంజాన్", "moonSighting": true,
    "dates": ["2026-03-21", "2027-03-10"]
  },
  {
    "id": "bakrid", "nameEn": "Bakrid (Eid ul-Adha)", "nameTe": "బక్రీద్", "moonSighting": true,
    "dates": ["2026-05-28", "2027-05-17"]
  }

  // Bonalu: add the dates from the Telangana government holiday list, for example:
  // , { "id": "bonalu", "nameEn": "Bonalu", "nameTe": "బోనాలు", "dates": ["YYYY-MM-DD"] }
];
