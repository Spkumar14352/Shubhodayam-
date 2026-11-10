// Verified muhurthams, entered by hand from a purohit or a printed panchangam.
// These show a gold "Verified" badge and replace the automatic suggestion for the same day and type.
// The Android app reads this file when it is built. Keep the array JSON-like (no trailing commas).
//
// start/end are the muhurtham window in IST (24-hour "HH:mm"); leave them out for an all-day entry.
// Example:
//   { "date": "2026-11-26", "type": "Marriage", "start": "10:24", "end": "11:12",
//     "lagnam": "Makara", "notes": "From the family purohit" }

export interface VerifiedMuhurtham {
  date: string;
  type: "Marriage" | "Griha Pravesam" | "Vehicle" | "Upanayanam" | "Annaprasana" | "Naming";
  start?: string;
  end?: string;
  lagnam?: string;
  notes?: string;
}

export const muhurthams: VerifiedMuhurtham[] = [
];
