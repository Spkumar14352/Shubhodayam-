package com.example

import com.example.data.model.Channel
import com.example.data.model.ChannelsData
import com.example.data.repository.LiveSource
import com.example.data.repository.LiveStatus
import com.example.data.repository.LiveStatusSource
import com.example.screens.LiveTvUiState
import com.example.screens.LiveTvViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private class FakeSource(
    override val source: LiveSource,
    var all: Map<String, LiveStatus>? = emptyMap(),
    var channel: (String) -> LiveStatus? = { null },
    val throwError: Boolean = false
) : LiveStatusSource {
    val allCalls = mutableListOf<Unit>()
    val channelCalls = mutableListOf<String>()

    override suspend fun fetchAll(): Map<String, LiveStatus>? {
        allCalls += Unit
        if (throwError) throw IllegalStateException("boom")
        return all
    }

    override suspend fun fetchChannel(channelId: String): LiveStatus? {
        channelCalls += channelId
        if (throwError) throw IllegalStateException("boom")
        return channel(channelId)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LiveTvViewModelTest {
    private val bhakthi = ChannelsData.allChannels.first { it.id == "bhakthi_tv" }
    private val noId = ChannelsData.allChannels.first { it.youtubeChannelId.isBlank() }
    private val live = LiveStatus(bhakthi.youtubeChannelId, true, "abcDEF12345", "Live", null)

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun noKeyShowsChannelsWithoutCallingAnything() {
        val source = FakeSource(LiveSource.NONE)
        val vm = LiveTvViewModel(source)
        vm.onChannelShown(bhakthi)

        val state = vm.state.value as LiveTvUiState.Success
        assertFalse(state.liveCheckAvailable)
        assertTrue(source.allCalls.isEmpty() && source.channelCalls.isEmpty())
    }

    @Test
    fun serverSuccessMarksUnmentionedChannelsOffline() {
        val vm = LiveTvViewModel(FakeSource(LiveSource.SERVER, all = mapOf(live.channelId to live)))
        vm.onChannelShown(bhakthi)

        val state = vm.state.value as LiveTvUiState.Success
        assertTrue(state.statuses.getValue(bhakthi.youtubeChannelId).isLive)
        assertFalse(state.statuses.getValue("UCS2Y83GD-fc7qqgNW5uj41g").isLive)
    }

    @Test
    fun serverFailureIsAnErrorState() {
        val vm = LiveTvViewModel(FakeSource(LiveSource.SERVER, all = null))
        vm.onChannelShown(bhakthi)
        assertEquals(LiveTvUiState.Error, vm.state.value)
    }

    @Test
    fun exceptionsBecomeAnErrorStateInsteadOfACrash() {
        val vm = LiveTvViewModel(FakeSource(LiveSource.YOUTUBE_API, throwError = true))
        vm.onChannelShown(bhakthi)
        assertEquals(LiveTvUiState.Error, vm.state.value)
    }

    @Test
    fun apiModeChecksOnlyTheShownChannelAndCachesIt() {
        var time = 0L
        val source = FakeSource(LiveSource.YOUTUBE_API, channel = { live })
        val vm = LiveTvViewModel(source, now = { time })

        vm.onChannelShown(bhakthi)
        vm.onChannelShown(bhakthi)
        assertEquals(listOf(bhakthi.youtubeChannelId), source.channelCalls)
        assertTrue((vm.state.value as LiveTvUiState.Success).statuses.getValue(bhakthi.youtubeChannelId).isLive)

        time += LiveTvViewModel.MAX_AGE_MS + 1
        vm.onChannelShown(bhakthi)
        assertEquals(2, source.channelCalls.size)
    }

    @Test
    fun channelWithoutAnIdIsNeverSentToTheApi() {
        val source = FakeSource(LiveSource.YOUTUBE_API, channel = { live })
        val vm = LiveTvViewModel(source)
        vm.onChannelShown(noId)

        assertTrue(source.channelCalls.isEmpty())
        assertTrue(vm.state.value is LiveTvUiState.Success)
    }

    @Test
    fun retryAfterAnErrorKeepsWhatWasAlreadyKnown() {
        val other = Channel("x", "Other", "", "Devotional", "UCS2Y83GD-fc7qqgNW5uj41g")
        val source = FakeSource(LiveSource.YOUTUBE_API, channel = { if (it == bhakthi.youtubeChannelId) live else null })
        val vm = LiveTvViewModel(source)

        vm.onChannelShown(bhakthi)
        vm.onChannelShown(other)
        assertEquals(LiveTvUiState.Error, vm.state.value)

        vm.onChannelShown(bhakthi)
        assertTrue((vm.state.value as LiveTvUiState.Success).statuses.getValue(bhakthi.youtubeChannelId).isLive)
    }
}
