// News Feeds Configuration for Shubhodaya
// RSS feeds fetched through free rss2json endpoint: https://api.rss2json.com/v1/api.json?rss_url=ENCODED_FEED_URL

export interface NewsFeed {
  id: string;
  name: string;
  nameTelugu: string;
  category: 'Telugu' | 'India' | 'Sports' | 'Devotional';
  feedUrl: string;
}

export const newsFeeds: NewsFeed[] = [
  // Telugu News Feeds
  // TODO: verify feeds
  {
    id: 'eenadu_main',
    name: 'Eenadu',
    nameTelugu: 'ఈనాడు',
    category: 'Telugu',
    feedUrl: 'https://www.eenadu.net/rss/andhra-pradesh.xml'
  },
  {
    id: 'sakshi_telangana',
    name: 'Sakshi',
    nameTelugu: 'సాక్షి',
    category: 'Telugu',
    feedUrl: 'https://www.sakshi.com/rss/telangana.xml'
  },
  {
    id: 'andhrajyothy_main',
    name: 'Andhra Jyothy',
    nameTelugu: 'ఆంధ్రజ్యోతి',
    category: 'Telugu',
    feedUrl: 'https://www.andhrajyothy.com/rss/ap-telangana.xml'
  },

  // India / National News Feeds
  // TODO: verify feeds
  {
    id: 'hindu_national',
    name: 'The Hindu',
    nameTelugu: 'ది హిందూ',
    category: 'India',
    feedUrl: 'https://www.thehindu.com/news/national/feeder/default.rss'
  },
  {
    id: 'ndtv_india',
    name: 'NDTV India',
    nameTelugu: 'ఎన్డీటీవీ ఇండియా',
    category: 'India',
    feedUrl: 'https://feeds.feedburner.com/ndtvnews-india-news'
  },
  {
    id: 'times_india',
    name: 'Times of India',
    nameTelugu: 'టైమ్స్ ఆఫ్ ఇండియా',
    category: 'India',
    feedUrl: 'https://timesofindia.indiatimes.com/rssfeeds/-2128936835.cms'
  },

  // Sports Feeds
  // TODO: verify feeds
  {
    id: 'cricinfo_news',
    name: 'ESPNCricinfo',
    nameTelugu: 'ఇఎస్పీఎన్ క్రిక్‌ఇన్ఫో',
    category: 'Sports',
    feedUrl: 'https://www.espncricinfo.com/rss/content/story/feeds/0.xml'
  },
  {
    id: 'ndtv_cricket',
    name: 'NDTV Cricket & Sports',
    nameTelugu: 'ఎన్డీటీవీ క్రీడలు',
    category: 'Sports',
    feedUrl: 'https://feeds.feedburner.com/ndtvsports-cricket'
  },
  {
    id: 'hindu_sport',
    name: 'The Hindu Sport',
    nameTelugu: 'ది హిందూ స్పోర్ట్స్',
    category: 'Sports',
    feedUrl: 'https://www.thehindu.com/sport/feeder/default.rss'
  },

  // Devotional Feeds
  // TODO: verify feeds
  {
    id: 'speaking_tree',
    name: 'Speaking Tree',
    nameTelugu: 'స్పీకింగ్ ట్రీ (ఆధ్యాత్మికం)',
    category: 'Devotional',
    feedUrl: 'https://www.speakingtree.in/rss/allblogs.xml'
  },
  {
    id: 'bhakthi_articles',
    name: 'Sanatana Dharma',
    nameTelugu: 'సనాతన ధర్మం & భక్తి విశేషాలు',
    category: 'Devotional',
    feedUrl: 'https://hinduismtoday.com/feed/'
  }
];
