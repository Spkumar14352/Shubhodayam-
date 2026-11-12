package com.example

import com.example.data.model.FallbackRadioStations
import com.example.data.model.RadioBrowserStation
import com.example.data.repository.RadioRepository
import com.example.services.RadioPlayerState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RadioPlayerTest {

    @Test
    fun testFallbackTeluguStationsNotEmpty() {
        val stations = FallbackRadioStations.popularTeluguStations
        assertTrue("Telugu stations should not be empty", stations.isNotEmpty())
        assertTrue("Should have at least 10 popular Telugu channels", stations.size >= 10)

        // Ensure key popular stations exist
        val stationUuids = stations.map { it.stationuuid }
        assertTrue(stationUuids.contains("telugu_akashvani_hyd"))
        assertTrue(stationUuids.contains("telugu_vb_hyd"))
        assertTrue(stationUuids.contains("telugu_svbc_ttd"))
        assertTrue(stationUuids.contains("telugu_vb_vijayawada"))
        assertTrue(stationUuids.contains("telugu_oye_radio"))
    }

    @Test
    fun testStationStreamUrlsAndInitials() {
        val stations = FallbackRadioStations.popularTeluguStations
        for (station in stations) {
            assertTrue("Stream URL must be non-blank for ${station.name}", station.getStreamUrl().isNotBlank())
            assertTrue("Stream URL must start with http for ${station.name}", station.getStreamUrl().startsWith("http"))
            assertTrue("Initials must be non-blank for ${station.name}", station.getInitials().isNotBlank())
            assertEquals("Initials must be uppercase", station.getInitials(), station.getInitials().uppercase())
        }
    }

    @Test
    fun testRadioRepositoryCategoryFiltering() = runTest {
        val repo = RadioRepository()

        val devotionalStations = repo.getStations("DEVOTIONAL")
        assertTrue("Devotional stations should not be empty", devotionalStations.isNotEmpty())
        for (st in devotionalStations) {
            if (st.category.isNotBlank() && st.category != "ALL") {
                assertEquals("DEVOTIONAL", st.category)
            }
        }

        val melodiesStations = repo.getStations("MELODIES")
        assertTrue("Melodies stations should not be empty", melodiesStations.isNotEmpty())

        val popularStations = repo.getStations("POPULAR")
        assertTrue("Popular stations should not be empty", popularStations.isNotEmpty())
    }

    @Test
    fun testRadioPlayerStateTransitions() {
        val initial = RadioPlayerState()
        assertFalse(initial.isPlaying)
        assertFalse(initial.isLoading)
        assertEquals(null, initial.currentStation)
        assertEquals(null, initial.errorMessage)

        val station = FallbackRadioStations.popularTeluguStations.first()
        val loadingState = initial.copy(currentStation = station, isLoading = true)
        assertTrue(loadingState.isLoading)
        assertEquals(station, loadingState.currentStation)

        val playingState = loadingState.copy(isLoading = false, isPlaying = true)
        assertTrue(playingState.isPlaying)
        assertFalse(playingState.isLoading)

        val pausedState = playingState.copy(isPlaying = false)
        assertFalse(pausedState.isPlaying)

        val errorState = playingState.copy(isPlaying = false, errorMessage = "Station offline")
        assertNotNull(errorState.errorMessage)
        assertEquals("Station offline", errorState.errorMessage)
    }
}
