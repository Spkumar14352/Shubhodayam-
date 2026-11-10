// Rules for the automatic "Generally good days" finder. Everything is checked at sunrise.
// The Android app reads this file when it is built. Keep the array JSON-like: quoted strings, no trailing commas.
//
// avoidTithis uses 1-30: 1-15 Shukla paksha, 16-30 Krishna paksha (19, 24, 29 are Krishna Chavithi,
// Navami, Chaturdashi; 30 is Amavasya).
// avoidKrishnaAfterAshtami skips Krishna Navami to Amavasya (tithis 24-30).
// Types not listed here (Upanayanam, Annaprasana, Naming) show only the verified dates in muhurthams.ts.

export interface MuhurthaRule {
  type: "Marriage" | "Griha Pravesam" | "Vehicle" | "Upanayanam" | "Annaprasana" | "Naming";
  avoidTithis: number[];
  avoidKrishnaAfterAshtami?: boolean;
  nakshatras: string[];
  avoidWeekdays?: ("monday" | "tuesday" | "wednesday" | "thursday" | "friday" | "saturday" | "sunday")[];
}

export const muhurthaRules: MuhurthaRule[] = [
  {
    "type": "Marriage",
    "avoidTithis": [4, 9, 14, 19, 24, 29, 30],
    "avoidKrishnaAfterAshtami": true,
    "nakshatras": [
      "Rohini", "Mrigashira", "Magha", "Uttara Phalguni", "Hasta", "Swati",
      "Anuradha", "Mula", "Uttara Ashadha", "Uttara Bhadrapada", "Revati"
    ],
    "avoidWeekdays": ["tuesday", "saturday"]
  },
  {
    "type": "Griha Pravesam",
    "avoidTithis": [4, 9, 14, 19, 24, 29, 30],
    "nakshatras": [
      "Rohini", "Mrigashira", "Uttara Phalguni", "Uttara Ashadha", "Uttara Bhadrapada",
      "Chitra", "Anuradha", "Revati"
    ],
    "avoidWeekdays": ["tuesday", "saturday"]
  },
  {
    "type": "Vehicle",
    "avoidTithis": [4, 9, 14, 19, 24, 29, 30],
    "nakshatras": [
      "Ashwini", "Rohini", "Mrigashira", "Punarvasu", "Pushya", "Hasta", "Chitra",
      "Swati", "Anuradha", "Shravana", "Dhanishta", "Revati"
    ]
  }
];
