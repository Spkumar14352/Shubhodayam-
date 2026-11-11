export type ChannelCategory = "Devotional" | "Telugu News" | "National News" | "Entertainment";

export interface Channel {
  id: string;
  name: string;
  nameTelugu: string;
  category: ChannelCategory;
  youtubeChannelId: string; // YouTube channel ID (used in https://www.youtube.com/embed/live_stream?channel=CHANNEL_ID)
  logoUrl: string; // If empty, an initials badge is displayed
}

// TODO: add official YouTube channel IDs
// Replace the placeholder IDs below with actual YouTube Channel IDs (e.g., "UCxxxxxx...")
export const channels: Channel[] = [
  // --- Devotional ---
  {
    id: "bhakthi_tv",
    name: "Bhakthi TV",
    nameTelugu: "భక్తి టీవీ",
    category: "Devotional",
    youtubeChannelId: "d0dB3kSCMmM",
    logoUrl: ""
  },
  {
    id: "svbc_ttd",
    name: "SVBC TTD Live",
    nameTelugu: "ఎస్వీబీసీ (తిరుమల లైవ్)",
    category: "Devotional",
    youtubeChannelId: "XxdarKTmJ8c",
    logoUrl: ""
  },
  {
    id: "hindu_dharmam",
    name: "Hindu Dharmam TV",
    nameTelugu: "హిందూ ధర్మం టీవీ",
    category: "Devotional",
    youtubeChannelId: "", // TODO: add official YouTube channel ID for Hindu Dharmam TV
    logoUrl: ""
  },

  // --- Telugu News ---
  {
    id: "tv9_telugu",
    name: "TV9 Telugu",
    nameTelugu: "టీవీ9 తెలుగు",
    category: "Telugu News",
    youtubeChannelId: "UCPXTXMecYqnRKNdqdVOGSFg",
    logoUrl: ""
  },
  {
    id: "ntv_telugu",
    name: "NTV Telugu",
    nameTelugu: "ఎన్‌టీవీ తెలుగు",
    category: "Telugu News",
    youtubeChannelId: "UCSzOZ97LOpU-_AVlGfmD4rQ",
    logoUrl: ""
  },
  {
    id: "abn_andhrajyothy",
    name: "ABN Andhra Jyothi",
    nameTelugu: "ఏబీఎన్ ఆంధ్రజ్యోతి",
    category: "Telugu News",
    youtubeChannelId: "UC_2irx_BQR7RsBKmUV9fePQ",
    logoUrl: ""
  },
  {
    id: "v6_news",
    name: "V6 News",
    nameTelugu: "వి6 న్యూస్",
    category: "Telugu News",
    youtubeChannelId: "UCDCMjD1XIAsCZsYHNMGVcog",
    logoUrl: ""
  },
  {
    id: "sakshi_tv",
    name: "Sakshi TV",
    nameTelugu: "సాక్షి టీవీ",
    category: "Telugu News",
    youtubeChannelId: "UCQ_FATLW83q-4xJ2fsi8qAw",
    logoUrl: ""
  },

  // --- National News ---
  {
    id: "dd_india",
    name: "DD India Live",
    nameTelugu: "డీడీ ఇండియా",
    category: "National News",
    youtubeChannelId: "UCGDQNvybfDDeGTf4GtigXaw",
    logoUrl: ""
  },
  {
    id: "sansad_tv",
    name: "Sansad TV",
    nameTelugu: "సంసద్ టీవీ",
    category: "National News",
    youtubeChannelId: "UCISgnSNwqQ2i8lhCun3KtQg",
    logoUrl: ""
  },
  {
    id: "ndtv_india",
    name: "NDTV India",
    nameTelugu: "ఎన్డీటీవీ ఇండియా",
    category: "National News",
    youtubeChannelId: "UC9CYT9gSNLevX5ey2_6CK0Q",
    logoUrl: ""
  },

  // --- Entertainment ---
  {
    id: "etv_cinema",
    name: "ETV Cinema",
    nameTelugu: "ఈటీవీ సినిమా",
    category: "Entertainment",
    youtubeChannelId: "UC3Dvbf4eEov7YORy5nEioTA",
    logoUrl: ""
  },
  {
    id: "gemini_comedy",
    name: "Gemini Comedy",
    nameTelugu: "జెమిని కామెడీ",
    category: "Entertainment",
    youtubeChannelId: "", // TODO: add official YouTube channel ID for Gemini Comedy
    logoUrl: ""
  },
  {
    id: "maa_gold",
    name: "Star Maa Gold",
    nameTelugu: "స్టార్ మా గోల్డ్",
    category: "Entertainment",
    youtubeChannelId: "", // TODO: add official YouTube channel ID for Star Maa Gold
    logoUrl: ""
  }
];
