// Manual festival dates for a given year, from the AP/Telangana government holiday list or a
// printed panchangam. An override always replaces the calculated date and shows a "Verified" badge.
// The Android app reads this file when it is built. Keep the array JSON-like (no trailing commas).
//
// "id" is a festival id from festivalRules.ts (including offset ids like "kanuma")
// or fixedFestivals.ts. Example:
//   { "id": "ugadi", "year": 2026, "date": "2026-03-19", "source": "Telangana holiday list 2026" }

export interface FestivalOverride {
  id: string;
  year: number;
  date: string;
  source?: string;
}

export const festivalOverrides: FestivalOverride[] = [
  { id: "karthika_masam_start", year: 2026, date: "2026-11-10", source: "ధృవీకరించబడిన పంచాంగం" },
  { id: "karthika_pournami", year: 2026, date: "2026-11-24", source: "ధృవీకరించబడిన పంచాంగం" },
  { id: "karthika_amavasya", year: 2026, date: "2026-12-08", source: "ధృవీకరించబడిన పంచాంగం" },
  { id: "deepavali", year: 2026, date: "2026-11-08", source: "ధృవీకరించబడిన పంచాంగం" }
];
