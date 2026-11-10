// Shubhalekha (invitation) templates. Every design is drawn by the app from these settings:
// no images are downloaded. The Android app reads this file when it is built.
// Keep the array JSON-like: quoted strings, no trailing commas.
//
// border:  "kalyanam" | "temple" | "floral" | "line" | "royal" | "kolam"
// motifs:  any of "kalasam", "toranam", "kolam", "lotus", "diya", "marigold", "balloons", "stars"
// fonts:   "serif" | "sans" | "condensed" | "cursive"
// slots:   the order of content on each page. Cover: deity, title, photo, names, date.
//          Details: text, names, muhurtham, venue, qr, compliments. Events: events, compliments.

export type Occasion = "wedding" | "griha_pravesam" | "upanayanam" | "namakaranam" | "half_saree" | "birthday";

export interface InvitationTemplate {
  id: string;
  occasion: Occasion;
  nameEn: string;
  nameTe: string;
  background: [string, string];
  border: "kalyanam" | "temple" | "floral" | "line" | "royal" | "kolam";
  borderColor: string;
  motifs: string[];
  headingFont: "serif" | "sans" | "condensed" | "cursive";
  bodyFont: "serif" | "sans" | "condensed" | "cursive";
  headingColor: string;
  textColor: string;
  accentColor: string;
  slots: { cover: string[]; details: string[]; events: string[] };
}

export const invitationTemplates: InvitationTemplate[] = [
  {
    "id": "kalyanam_classic", "occasion": "wedding", "nameEn": "Kalyanam Classic", "nameTe": "కల్యాణం సంప్రదాయం",
    "background": ["#FFF6DD", "#F7DFA8"], "border": "kalyanam", "borderColor": "#A8761A",
    "motifs": ["kalasam", "toranam"], "headingFont": "serif", "bodyFont": "serif",
    "headingColor": "#7A0F1F", "textColor": "#3B1F0F", "accentColor": "#C28A16",
    "slots": { "cover": ["deity", "title", "photo", "names", "date"], "details": ["text", "names", "muhurtham", "venue", "qr", "compliments"], "events": ["events", "compliments"] }
  },
  {
    "id": "mango_toranam", "occasion": "wedding", "nameEn": "Mango Leaf Toranam", "nameTe": "మామిడి తోరణం",
    "background": ["#FFFBEA", "#FFF0B8"], "border": "kalyanam", "borderColor": "#2E7D32",
    "motifs": ["toranam", "kolam"], "headingFont": "serif", "bodyFont": "serif",
    "headingColor": "#1B5E20", "textColor": "#33291A", "accentColor": "#E0A100",
    "slots": { "cover": ["deity", "title", "names", "photo", "date"], "details": ["text", "names", "muhurtham", "venue", "qr", "compliments"], "events": ["events", "compliments"] }
  },
  {
    "id": "kolam_white", "occasion": "wedding", "nameEn": "Kolam White", "nameTe": "ముగ్గు తెలుపు",
    "background": ["#FFFFFF", "#F4F1EA"], "border": "kolam", "borderColor": "#8C2F39",
    "motifs": ["kolam"], "headingFont": "serif", "bodyFont": "sans",
    "headingColor": "#8C2F39", "textColor": "#2B2B2B", "accentColor": "#B08D57",
    "slots": { "cover": ["deity", "title", "photo", "names", "date"], "details": ["text", "names", "muhurtham", "venue", "qr", "compliments"], "events": ["events", "compliments"] }
  },
  {
    "id": "temple_gold", "occasion": "wedding", "nameEn": "Temple Gold", "nameTe": "ఆలయ బంగారు",
    "background": ["#5B0E1E", "#380712"], "border": "temple", "borderColor": "#D4A537",
    "motifs": ["kalasam", "diya"], "headingFont": "serif", "bodyFont": "serif",
    "headingColor": "#F2D37A", "textColor": "#FFF7EC", "accentColor": "#D4A537",
    "slots": { "cover": ["deity", "title", "photo", "names", "date"], "details": ["text", "names", "muhurtham", "venue", "qr", "compliments"], "events": ["events", "compliments"] }
  },
  {
    "id": "temple_night", "occasion": "wedding", "nameEn": "Temple Night", "nameTe": "ఆలయ రాత్రి",
    "background": ["#14213D", "#0B1326"], "border": "temple", "borderColor": "#E0B84F",
    "motifs": ["kalasam", "diya"], "headingFont": "serif", "bodyFont": "serif",
    "headingColor": "#F5D67B", "textColor": "#F4F1E8", "accentColor": "#E0B84F",
    "slots": { "cover": ["deity", "title", "photo", "names", "date"], "details": ["text", "names", "muhurtham", "venue", "qr", "compliments"], "events": ["events", "compliments"] }
  },
  {
    "id": "floral_blush", "occasion": "wedding", "nameEn": "Floral Blush", "nameTe": "పూల గులాబీ",
    "background": ["#FFF1F3", "#FAD4DA"], "border": "floral", "borderColor": "#C2185B",
    "motifs": ["lotus"], "headingFont": "cursive", "bodyFont": "serif",
    "headingColor": "#AD1457", "textColor": "#4A2330", "accentColor": "#E91E63",
    "slots": { "cover": ["deity", "title", "photo", "names", "date"], "details": ["text", "names", "muhurtham", "venue", "qr", "compliments"], "events": ["events", "compliments"] }
  },
  {
    "id": "marigold_garland", "occasion": "wedding", "nameEn": "Marigold Garland", "nameTe": "బంతిపూల దండ",
    "background": ["#FFF3E0", "#FFD8A8"], "border": "floral", "borderColor": "#E65100",
    "motifs": ["marigold", "kalasam"], "headingFont": "serif", "bodyFont": "serif",
    "headingColor": "#BF360C", "textColor": "#3E2723", "accentColor": "#F57C00",
    "slots": { "cover": ["deity", "title", "photo", "names", "date"], "details": ["text", "names", "muhurtham", "venue", "qr", "compliments"], "events": ["events", "compliments"] }
  },
  {
    "id": "modern_minimal", "occasion": "wedding", "nameEn": "Modern Minimal", "nameTe": "ఆధునిక సరళం",
    "background": ["#FFFFFF", "#FFFFFF"], "border": "line", "borderColor": "#222222",
    "motifs": [], "headingFont": "sans", "bodyFont": "sans",
    "headingColor": "#111111", "textColor": "#333333", "accentColor": "#B8860B",
    "slots": { "cover": ["deity", "photo", "names", "title", "date"], "details": ["text", "names", "muhurtham", "venue", "qr", "compliments"], "events": ["events", "compliments"] }
  },
  {
    "id": "modern_sage", "occasion": "wedding", "nameEn": "Sage Minimal", "nameTe": "లేత ఆకుపచ్చ",
    "background": ["#F1F5EE", "#DDE7D6"], "border": "line", "borderColor": "#5F7A55",
    "motifs": ["lotus"], "headingFont": "condensed", "bodyFont": "sans",
    "headingColor": "#3F5638", "textColor": "#2F3A2B", "accentColor": "#8AA67E",
    "slots": { "cover": ["deity", "title", "photo", "names", "date"], "details": ["text", "names", "muhurtham", "venue", "qr", "compliments"], "events": ["events", "compliments"] }
  },
  {
    "id": "royal_maroon", "occasion": "wedding", "nameEn": "Royal Maroon", "nameTe": "రాజసం మెరూన్",
    "background": ["#7E182D", "#4A0A17"], "border": "royal", "borderColor": "#E8C25A",
    "motifs": ["kalasam", "kolam"], "headingFont": "serif", "bodyFont": "serif",
    "headingColor": "#F6D98A", "textColor": "#FFF3E2", "accentColor": "#E8C25A",
    "slots": { "cover": ["deity", "title", "photo", "names", "date"], "details": ["text", "names", "muhurtham", "venue", "qr", "compliments"], "events": ["events", "compliments"] }
  },
  {
    "id": "griha_pravesam_kalasam", "occasion": "griha_pravesam", "nameEn": "Griha Pravesam Kalasam", "nameTe": "గృహప్రవేశ కలశం",
    "background": ["#FFF8E1", "#FFE6A6"], "border": "kalyanam", "borderColor": "#8D6E1F",
    "motifs": ["toranam", "kalasam", "kolam"], "headingFont": "serif", "bodyFont": "serif",
    "headingColor": "#6D1B1B", "textColor": "#3B2A12", "accentColor": "#C79A1E",
    "slots": { "cover": ["deity", "title", "names", "date"], "details": ["text", "names", "muhurtham", "venue", "qr", "compliments"], "events": ["events", "compliments"] }
  },
  {
    "id": "upanayanam_saffron", "occasion": "upanayanam", "nameEn": "Upanayanam Saffron", "nameTe": "ఉపనయనం కాషాయం",
    "background": ["#FFF0DC", "#FFC98B"], "border": "temple", "borderColor": "#B34700",
    "motifs": ["diya", "kolam"], "headingFont": "serif", "bodyFont": "serif",
    "headingColor": "#8A2E00", "textColor": "#3D1E05", "accentColor": "#E07B00",
    "slots": { "cover": ["deity", "title", "photo", "names", "date"], "details": ["text", "names", "muhurtham", "venue", "qr", "compliments"], "events": ["events", "compliments"] }
  },
  {
    "id": "namakaranam_pastel", "occasion": "namakaranam", "nameEn": "Namakaranam Pastel", "nameTe": "నామకరణం లేత రంగులు",
    "background": ["#EEF7FF", "#FCEFF6"], "border": "floral", "borderColor": "#7E9CC6",
    "motifs": ["stars", "lotus"], "headingFont": "cursive", "bodyFont": "sans",
    "headingColor": "#3F5A8A", "textColor": "#38404D", "accentColor": "#E59AB7",
    "slots": { "cover": ["deity", "title", "photo", "names", "date"], "details": ["text", "names", "muhurtham", "venue", "qr", "compliments"], "events": ["events", "compliments"] }
  },
  {
    "id": "half_saree_floral", "occasion": "half_saree", "nameEn": "Half-saree / Dhoti Floral", "nameTe": "ఓణీల పంచెల వేడుక",
    "background": ["#FFF0F5", "#FFD7C2"], "border": "floral", "borderColor": "#C2185B",
    "motifs": ["marigold", "kolam"], "headingFont": "serif", "bodyFont": "serif",
    "headingColor": "#9C1458", "textColor": "#3E2430", "accentColor": "#F29F05",
    "slots": { "cover": ["deity", "title", "photo", "names", "date"], "details": ["text", "names", "muhurtham", "venue", "qr", "compliments"], "events": ["events", "compliments"] }
  },
  {
    "id": "birthday_balloons", "occasion": "birthday", "nameEn": "Birthday Balloons", "nameTe": "పుట్టినరోజు బెలూన్లు",
    "background": ["#FFF9E6", "#E6F4FF"], "border": "line", "borderColor": "#FF7043",
    "motifs": ["balloons", "stars"], "headingFont": "cursive", "bodyFont": "sans",
    "headingColor": "#E64A19", "textColor": "#263238", "accentColor": "#29B6F6",
    "slots": { "cover": ["title", "photo", "names", "date"], "details": ["text", "names", "muhurtham", "venue", "qr", "compliments"], "events": ["events", "compliments"] }
  }
];
