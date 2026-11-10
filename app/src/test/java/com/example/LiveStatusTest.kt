package com.example

import com.example.data.model.ChannelsData
import com.example.data.repository.LiveStatusRepository
import com.example.screens.buildPlayerHtml
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
        val status = LiveStatusRepository.parse(
            """[
              {"channelId":"UCa","isLive":true,"videoId":"abcDEF12345","title":"Morning","thumbnail":"https://t"},
              {"channelId":"UCb","isLive":false,"videoId":null,"title":null,"thumbnail":null}
            ]"""
        )

        assertTrue(status.getValue("UCa").isLive)
        assertEquals("abcDEF12345", status.getValue("UCa").videoId)
        assertEquals("Morning", status.getValue("UCa").title)
        assertFalse(status.getValue("UCb").isLive)
        assertNull(status.getValue("UCb").title)
    }

    @Test
    fun rejectsVideoIdsThatCouldBreakThePlayerPage() {
        val status = LiveStatusRepository.parse(
            """[{"channelId":"UCa","isLive":true,"videoId":"x\"><script>","title":"t","thumbnail":"u"}]"""
        )

        assertFalse(status.getValue("UCa").isLive)
        assertNull(status.getValue("UCa").videoId)
    }

    @Test
    fun playerHtmlIdentifiesTheAppToYouTube() {
        val html = buildPlayerHtml("abcDEF12345", "https://com.aistudio.shubhodaya.daily")

        assertTrue(html.contains("https://www.youtube.com/embed/abcDEF12345?autoplay=1&playsinline=1"))
        assertTrue(html.contains("origin=https%3A%2F%2Fcom.aistudio.shubhodaya.daily"))
        assertTrue(html.contains("""referrerpolicy="strict-origin-when-cross-origin""""))
        assertTrue(html.contains("""allow="autoplay; encrypted-media; picture-in-picture; fullscreen""""))
        assertTrue(html.contains("onError"))
    }

    @Test
    fun channelIdsAreWellFormedAndUnique() {
        val ids = ChannelsData.allChannels.map { it.youtubeChannelId }.filter { it.isNotBlank() }

        assertEquals(11, ids.size)
        assertEquals(ids.size, ids.toSet().size)
        assertTrue(ids.all { Regex("UC[A-Za-z0-9_-]{22}").matches(it) })
    }
}
