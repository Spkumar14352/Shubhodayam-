package com.example.data.model

data class CricScore(
    val r: Int = 0,
    val w: Int = 0,
    val o: Double = 0.0,
    val inning: String = ""
)

data class LiveCricketMatch(
    val id: String,
    val name: String,
    val matchType: String = "",
    val status: String = "",
    val venue: String = "",
    val date: String = "",
    val dateTimeGMT: String = "",
    val teams: List<String> = emptyList(),
    val teamInfo: List<CricTeamInfo> = emptyList(),
    val score: List<CricScore> = emptyList(),
    val matchStarted: Boolean = false,
    val matchEnded: Boolean = false,
    val liveScoreText: String = "",
    val tournament: String = ""
) {
    val isLive: Boolean
        get() = matchStarted && !matchEnded

    val isUpcoming: Boolean
        get() = !matchStarted && !matchEnded

    val isRecent: Boolean
        get() = matchEnded

    fun getTeam1Name(): String = teams.getOrNull(0) ?: "Team 1"
    fun getTeam2Name(): String = teams.getOrNull(1) ?: "Team 2"

    fun getTeam1ScoreFormatted(): String {
        val s1 = score.getOrNull(0) ?: return "Yet to bat"
        return "${s1.r}/${s1.w} (${s1.o} ov)"
    }

    fun getTeam2ScoreFormatted(): String {
        val s2 = score.getOrNull(1) ?: return "Yet to bat"
        return "${s2.r}/${s2.w} (${s2.o} ov)"
    }
}

data class CricTeamInfo(
    val name: String,
    val shortname: String = "",
    val img: String = ""
)

object CricketSampleData {
    val sampleMatches = listOf(
        LiveCricketMatch(
            id = "sample_1",
            name = "India vs Australia, 3rd ODI",
            matchType = "odi",
            status = "India need 42 runs in 38 balls",
            venue = "Wankhede Stadium, Mumbai",
            date = "2026-11-09",
            teams = listOf("Australia", "India"),
            score = listOf(
                CricScore(r = 284, w = 8, o = 50.0, inning = "Australia Inning 1"),
                CricScore(r = 243, w = 4, o = 43.4, inning = "India Inning 1")
            ),
            matchStarted = true,
            matchEnded = false,
            liveScoreText = "IND 243/4 (43.4 ov) vs AUS 284/8",
            tournament = "Australia Tour of India 2026"
        ),
        LiveCricketMatch(
            id = "sample_2",
            name = "Sunrisers Hyderabad vs Chennai Super Kings",
            matchType = "t20",
            status = "SRH won by 6 wickets",
            venue = "Rajiv Gandhi Intl Stadium, Hyderabad",
            date = "2026-11-08",
            teams = listOf("CSK", "SRH"),
            score = listOf(
                CricScore(r = 178, w = 6, o = 20.0, inning = "CSK Inning 1"),
                CricScore(r = 182, w = 4, o = 18.2, inning = "SRH Inning 1")
            ),
            matchStarted = true,
            matchEnded = true,
            liveScoreText = "SRH 182/4 (18.2 ov) beat CSK 178/6",
            tournament = "Indian T20 League"
        ),
        LiveCricketMatch(
            id = "sample_3",
            name = "South Africa vs England, 1st Test",
            matchType = "test",
            status = "Starts tomorrow at 1:30 PM IST",
            venue = "SuperSport Park, Centurion",
            date = "2026-11-10",
            teams = listOf("South Africa", "England"),
            score = emptyList(),
            matchStarted = false,
            matchEnded = false,
            liveScoreText = "Match yet to begin",
            tournament = "England Tour of South Africa"
        )
    )
}
