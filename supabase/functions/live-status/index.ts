import { createClient } from "npm:@supabase/supabase-js@2";

const CACHE_MS = 10 * 60 * 1000;
const RECENT_UPLOADS = 10;
const YOUTUBE_API = "https://www.googleapis.com/youtube/v3";

// Keep in sync with youtubeChannelId in app/src/main/java/com/example/data/model/Channel.kt.
// The list lives on the server so callers can't spend quota on arbitrary channels.
const CHANNEL_IDS = [
  "UCSzOZ97LOpU-_AVlGfmD4rQ", // Bhakthi TV
  "UCS2Y83GD-fc7qqgNW5uj41g", // SVBC TTD
  "UCPXTXMecYqnRKNdqdVOGSFg", // TV9 Telugu Live
  "UCumtYpCY26F6Jr3satUgMvA", // NTV Telugu
  "UC_2irx_BQR7RsBKmUV9fePQ", // ABN Telugu
  "UCDCMjD1XIAsCZsYHNMGVcog", // V6 News Telugu
  "UCQ_FATLW83q-4xJ2fsi8qAw", // Sakshi TV Live
  "UCGDQNvybfDDeGTf4GtigXaw", // DD India
  "UCISgnSNwqQ2i8lhCun3KtQg", // Sansad TV
  "UC9CYT9gSNLevX5ey2_6CK0Q", // NDTV India
  "UC3Dvbf4eEov7YORy5nEioTA", // ETV Cinema
];

interface ChannelStatus {
  channelId: string;
  isLive: boolean;
  videoId: string | null;
  title: string | null;
  thumbnail: string | null;
}

interface YouTubeList {
  items?: Array<Record<string, any>>;
}

// A missing playlist returns null so one bad channel can't fail the whole refresh.
async function youtube(
  resource: string,
  params: Record<string, string>,
  apiKey: string,
): Promise<YouTubeList | null> {
  const query = new URLSearchParams({ ...params, key: apiKey });
  const response = await fetch(`${YOUTUBE_API}/${resource}?${query}`);
  if (response.status === 404) {
    await response.body?.cancel();
    return null;
  }
  if (!response.ok) {
    await response.body?.cancel();
    throw new Error(`YouTube ${resource} returned HTTP ${response.status}`);
  }
  return await response.json();
}

async function checkChannel(channelId: string, apiKey: string): Promise<ChannelStatus> {
  const offline: ChannelStatus = {
    channelId,
    isLive: false,
    videoId: null,
    title: null,
    thumbnail: null,
  };

  const uploads = await youtube("playlistItems", {
    part: "contentDetails",
    playlistId: `UU${channelId.slice(2)}`,
    maxResults: String(RECENT_UPLOADS),
  }, apiKey);
  const videoIds = uploads?.items?.map((item) => item.contentDetails.videoId) ?? [];
  if (videoIds.length === 0) return offline;

  const videos = await youtube("videos", {
    part: "snippet,liveStreamingDetails",
    id: videoIds.join(","),
  }, apiKey);
  const live = videos?.items?.find((video) => video.snippet.liveBroadcastContent === "live");
  if (!live) return offline;

  const thumbnails = live.snippet.thumbnails ?? {};
  return {
    channelId,
    isLive: true,
    videoId: live.id,
    title: live.snippet.title,
    thumbnail: (thumbnails.high ?? thumbnails.medium ?? thumbnails.default)?.url ?? null,
  };
}

function json(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { "Content-Type": "application/json" },
  });
}

Deno.serve(async () => {
  // The cache is a table because each function instance has its own memory.
  const db = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
  );
  const { data: cached } = await db
    .from("live_status_cache")
    .select("payload, fetched_at")
    .eq("id", 1)
    .maybeSingle();

  if (cached && Date.now() - Date.parse(cached.fetched_at) < CACHE_MS) {
    return json(cached.payload);
  }

  const apiKey = Deno.env.get("YOUTUBE_API_KEY");
  if (!apiKey) return json({ error: "YOUTUBE_API_KEY is not set" }, 500);

  try {
    const payload = await Promise.all(CHANNEL_IDS.map((id) => checkChannel(id, apiKey)));
    const { error } = await db.from("live_status_cache").upsert({
      id: 1,
      payload,
      fetched_at: new Date().toISOString(),
    });
    if (error) console.error("Could not save live status cache", error.message);
    return json(payload);
  } catch (error) {
    console.error("Live status refresh failed", error instanceof Error ? error.message : error);
    // Older data beats none, and a failed check must never be reported as "Offline".
    return cached ? json(cached.payload) : json({ error: "Live status unavailable" }, 502);
  }
});
