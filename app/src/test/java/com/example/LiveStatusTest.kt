package com.example

import com.example.data.model.Channel
import com.example.data.model.ChannelsData
import com.example.data.repository.LiveSource
import com.example.data.repository.LiveStatusRepository
import com.example.screens.YouTubeLinks
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LiveStatusTest {
    @Test
    fun parsesLiveAndOfflineChannels() {
        val status = requireNotNull(LiveStatusRepository.parseServer(
            """[
              {"channelId":"UCa","isLive":true,"videoId":"abcDEF12345","title":"Morning","thumbnail":"https://t"},
              {"channelId":"UCb","isLive":false,"videoId":null,"title":null,"thumbnail":null}
            ]"""
        ))

        assertTrue(status.getValue("UCa").isLive)
        assertEquals("abcDEF12345", status.getValue("UCa").videoId)
        assertEquals("Morning", status.getValue("UCa").title)
        assertFalse(status.getValue("UCb").isLive)
        assertNull(status.getValue("UCb").title)
    }

    @Test
    fun rejectsVideoIdsThatAreNotRealIds() {
        val status = requireNotNull(LiveStatusRepository.parseServer(
            """[{"channelId":"UCa","isLive":true,"videoId":"x\"><script>","title":"t","thumbnail":"u"}]"""
        ))

        assertFalse(status.getValue("UCa").isLive)
        assertNull(status.getValue("UCa").videoId)
    }

    @Test
    fun serverErrorsAndJunkGiveNullInsteadOfThrowing() {
        assertNull(LiveStatusRepository.parseServer("""{"error":"quota exceeded"}"""))
        assertNull(LiveStatusRepository.parseServer("<html>502 Bad Gateway</html>"))
        assertNull(LiveStatusRepository.parseServer(""))
        // Bad items are skipped rather than failing everything.
        val partial = requireNotNull(LiveStatusRepository.parseServer("""[42, {"isLive":true}, {"channelId":"UCa","isLive":false}]"""))
        assertEquals(setOf("UCa"), partial.keys)
    }

    @Test
    fun parsesYouTubeSearchResponses() {
        val live = requireNotNull(LiveStatusRepository.parseYouTubeSearch(
            """{"items":[{"id":{"kind":"youtube#video","videoId":"abcDEF12345"},
               "snippet":{"title":"Live now","thumbnails":{"high":{"url":"https://i.ytimg.com/x.jpg"}}}}]}""",
            "UCa"
        ))
        assertTrue(live.isLive)
        assertEquals("abcDEF12345", live.videoId)
        assertEquals("Live now", live.title)

        val notLive = requireNotNull(LiveStatusRepository.parseYouTubeSearch("""{"kind":"youtube#searchListResponse","items":[]}""", "UCa"))
        assertFalse(notLive.isLive)
        assertNull(notLive.videoId)
    }

    @Test
    fun youTubeErrorsAndUnexpectedJsonGiveNull() {
        assertNull(LiveStatusRepository.parseYouTubeSearch("""{"error":{"code":403,"message":"quotaExceeded"}}""", "UCa"))
        assertNull(LiveStatusRepository.parseYouTubeSearch("""{"kind":"something else"}""", "UCa"))
        assertNull(LiveStatusRepository.parseYouTubeSearch("not json", "UCa"))
        assertNull(LiveStatusRepository.parseYouTubeSearch("[]", "UCa"))
    }

    @Test
    fun sourceDependsOnWhatIsConfigured() {
        assertEquals(LiveSource.NONE, LiveStatusRepository(serverUrl = "", apiKey = "").source)
        assertEquals(LiveSource.NONE, LiveStatusRepository(serverUrl = "MY_LIVE_STATUS_URL", apiKey = "  ").source)
        // A malformed URL is ignored instead of crashing OkHttp later.
        assertEquals(LiveSource.NONE, LiveStatusRepository(serverUrl = "not a url", apiKey = "").source)
        assertEquals(LiveSource.YOUTUBE_API, LiveStatusRepository(serverUrl = "", apiKey = "AIzaKey").source)
        assertEquals(LiveSource.SERVER, LiveStatusRepository(serverUrl = "https://x.supabase.co/functions/v1/live-status", apiKey = "AIzaKey").source)
    }

    @Test
    fun noKeyOrBadChannelIdMeansNoNetworkCall() = runBlocking {
        assertNull(LiveStatusRepository(serverUrl = "", apiKey = "").fetchChannel("UCSzOZ97LOpU-_AVlGfmD4rQ"))
        assertNull(LiveStatusRepository(serverUrl = "", apiKey = "AIzaKey").fetchChannel(""))
        assertNull(LiveStatusRepository(serverUrl = "", apiKey = "").fetchAll())
    }

    @Test
    fun youTubeSearchUrlAsksForLiveVideosOnly() {
        val url = LiveStatusRepository.youTubeSearchUrl("UCSzOZ97LOpU-_AVlGfmD4rQ", "AIzaKey")
        assertEquals("www.googleapis.com", url.host)
        assertEquals("live", url.queryParameter("eventType"))
        assertEquals("video", url.queryParameter("type"))
        assertEquals("UCSzOZ97LOpU-_AVlGfmD4rQ", url.queryParameter("channelId"))
        assertEquals("AIzaKey", url.queryParameter("key"))
    }

    @Test
    fun watchOnYouTubeLinks() {
        assertEquals("https://www.youtube.com/watch?v=abcDEF12345", YouTubeLinks.watch("abcDEF12345"))
        val withId = Channel("a", "Bhakthi TV", "", "Devotional", "UCSzOZ97LOpU-_AVlGfmD4rQ")
        assertEquals("https://www.youtube.com/channel/UCSzOZ97LOpU-_AVlGfmD4rQ/live", YouTubeLinks.channel(withId))
        val withoutId = Channel("b", "Gemini Comedy", "", "Entertainment", "")
        assertEquals("https://www.youtube.com/results?search_query=Gemini+Comedy+live", YouTubeLinks.channel(withoutId))
    }

    @Test
    fun channelIdsAreWellFormedAndUnique() {
        val ids = ChannelsData.allChannels.map { it.youtubeChannelId }.filter { it.isNotBlank() }

        assertEquals(11, ids.size)
        assertEquals(ids.size, ids.toSet().size)
        assertTrue(ids.all { LiveStatusRepository.CHANNEL_ID.matches(it) })
    }
}
