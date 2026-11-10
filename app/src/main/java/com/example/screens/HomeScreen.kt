package com.example.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Channel
import com.example.data.model.ChannelsData
import com.example.data.model.City
import com.example.data.festivals.FestivalCalendar
import com.example.data.model.Festival
import com.example.data.model.FestivalCalendarItem
import com.example.data.model.Language
import com.example.data.model.MediaSampleData
import com.example.data.model.MoreDestination
import com.example.data.model.NewsItem
import com.example.data.model.PanchangData
import com.example.data.model.Rasi
import com.example.data.model.TabItem
import com.example.i18n.Translations
import com.example.ui.theme.AuspiciousGreen
import com.example.ui.theme.InauspiciousRed
import com.example.ui.theme.LiveRed
import com.example.ui.theme.StarGold
import com.example.ui.theme.TempleGold
import com.example.ui.theme.TempleGoldLight
import com.example.ui.theme.TempleMaroon
import com.example.ui.theme.TempleMaroonDark
import com.example.ui.theme.TempleSaffron
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    language: Language,
    selectedCity: City,
    panchangData: PanchangData,
    selectedRasi: Rasi,
    onNavigateTab: (TabItem) -> Unit,
    onNavigateMore: (MoreDestination) -> Unit,
    onSelectChannel: (Channel) -> Unit,
    onOpenNewsDetail: (NewsItem) -> Unit,
    onToggleBookmark: (String) -> Unit,
    bookmarkedIds: Set<String>,
    onOpenFestivalDetail: (Festival) -> Unit,
    onSharePanchang: () -> Unit = {},
    eclipses: List<com.example.data.eclipses.Eclipse> = emptyList(),
    modifier: Modifier = Modifier
) {
    val topNews = MediaSampleData.newsList.take(3)
    val context = LocalContext.current
    val today = java.time.LocalDate.now()
    val upcomingFestival = remember(today, selectedCity) {
        FestivalCalendar.nextMajor(context, today, selectedCity.latitude, selectedCity.longitude)
            ?.toHomeFestival(today)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_scroll"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Banner with Temple at Dusk artwork
        item {
            HomeHeroHeader(
                language = language,
                selectedCity = selectedCity,
                panchangData = panchangData
            )
        }

        // Smart Suggestions for Greetings (Festival within 3 days or Morning before 11 AM)
        item {
            SmartGreetingSuggestionsBanner(
                language = language,
                panchangDate = panchangData.date,
                selectedCity = selectedCity,
                onOpenGreetings = { onNavigateMore(MoreDestination.GREETINGS) }
            )
        }

        // Eclipse within the next week
        val now = java.time.LocalDateTime.now(com.example.data.eclipses.EclipseCalculator.IST)
        upcomingEclipseWithin(eclipses, now, com.example.data.eclipses.EclipseConstants.HOME_BANNER_DAYS)?.let { eclipse ->
            item {
                EclipseHomeBanner(
                    language = language,
                    eclipse = eclipse,
                    today = now.toLocalDate(),
                    onOpen = { onNavigateMore(MoreDestination.GRAHANAM) }
                )
            }
        }

        // Today's Panchang Card
        item {
            TodayPanchangCard(
                language = language,
                panchang = panchangData,
                selectedCity = selectedCity,
                onViewFullPanchang = { onNavigateTab(TabItem.PANCHANG) },
                onSharePanchang = onSharePanchang
            )
        }

        // Your Rasi Today Card
        item {
            YourRasiTodayCard(
                language = language,
                rasi = selectedRasi,
                onViewAllRasis = { onNavigateMore(MoreDestination.HOROSCOPE) }
            )
        }

        // Planning a function? Shubhalekha + Muhurthams
        item {
            PlanningFunctionCard(
                language = language,
                onShubhalekha = { onNavigateMore(MoreDestination.SHUBHALEKHA) },
                onMuhurthams = { onNavigateMore(MoreDestination.MUHURTHAMS) }
            )
        }

        // Baby Names Entry Card
        item {
            BabyNamesHomeCard(
                language = language,
                onOpen = { onNavigateMore(MoreDestination.BABY_NAMES) }
            )
        }

        // Live Now Horizontal Scroll
        item {
            LiveNowChannelsSection(
                language = language,
                channels = ChannelsData.allChannels,
                onSelectChannel = { channel ->
                    onSelectChannel(channel)
                    onNavigateTab(TabItem.LIVE_TV)
                },
                onViewAll = { onNavigateTab(TabItem.LIVE_TV) }
            )
        }

        // Top News Section
        item {
            TopNewsSection(
                language = language,
                newsItems = topNews,
                bookmarkedIds = bookmarkedIds,
                onOpenNews = onOpenNewsDetail,
                onToggleBookmark = onToggleBookmark,
                onViewAll = { onNavigateTab(TabItem.NEWS) }
            )
        }

        // Upcoming Festival Card with Countdown
        upcomingFestival?.let { festival ->
            item {
                UpcomingFestivalCard(
                    language = language,
                    festival = festival,
                    onViewAllFestivals = { onNavigateMore(MoreDestination.FESTIVALS) },
                    onClickFestival = { onOpenFestivalDetail(festival) }
                )
            }
        }
    }
}

@Composable
private fun EclipseHomeBanner(
    language: Language,
    eclipse: com.example.data.eclipses.Eclipse,
    today: java.time.LocalDate,
    onOpen: () -> Unit
) {
    val isTelugu = language == Language.TELUGU
    val name = if (isTelugu) eclipse.nameTe else eclipse.nameEn
    val daysLeft = java.time.temporal.ChronoUnit.DAYS.between(today, eclipse.start.toLocalDate())
    val countdown = when {
        daysLeft <= 0 && isTelugu -> "ఈ రోజే"
        daysLeft <= 0 -> "Today"
        daysLeft == 1L && isTelugu -> "రేపు"
        daysLeft == 1L -> "Tomorrow"
        isTelugu -> "మరో $daysLeft రోజుల్లో"
        else -> "In $daysLeft days"
    }
    val time = DateTimeFormatter.ofPattern("dd MMM, hh:mm a", Locale.ENGLISH)
    val detail = eclipse.sutakStart?.let {
        if (isTelugu) "సూతకం ${it.format(time)} నుండి (సంప్రదాయం ప్రకారం)" else "Sutak from ${it.format(time)} (as per tradition)"
    } ?: if (isTelugu) "స్పర్శ ${eclipse.start.format(time)}" else "Begins ${eclipse.start.format(time)}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onOpen)
            .testTag("home_eclipse_banner"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)),
        border = BorderStroke(1.dp, TempleGold)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.NightsStay, contentDescription = null, tint = TempleGoldLight, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "$name • $countdown", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = detail, fontSize = 11.sp, color = TempleGoldLight)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TempleGold)
        }
    }
}

private fun FestivalCalendarItem.toHomeFestival(today: java.time.LocalDate): Festival {
    val shownDate = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
    return Festival(
        id = id,
        nameTe = nameTe,
        nameEn = nameEn,
        dateDisplayTe = shownDate,
        dateDisplayEn = shownDate,
        daysLeft = getDaysLeft(today).toInt(),
        tithiTe = tithiTe,
        tithiEn = tithiEn,
        significanceTe = descriptionTe,
        significanceEn = descriptionEn,
        ritualsTe = ritualsTe,
        ritualsEn = ritualsEn
    )
}

@Composable
private fun PlanningFunctionCard(language: Language, onShubhalekha: () -> Unit, onMuhurthams: () -> Unit) {
    val isTelugu = language == Language.TELUGU
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("home_planning_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TempleMaroon)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isTelugu) "శుభకార్యం ప్లాన్ చేస్తున్నారా?" else "Planning a function?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TempleGoldLight
            )
            Text(
                text = if (isTelugu) "మంచి ముహూర్తం చూసి, అందమైన శుభలేఖ తయారు చేసుకోండి." else "Find a good muhurtham and make a beautiful invitation.",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onShubhalekha,
                    colors = ButtonDefaults.buttonColors(containerColor = TempleGold, contentColor = TempleMaroonDark),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_open_shubhalekha")
                ) { Text(Translations.tr("menu_shubhalekha", language), fontWeight = FontWeight.Bold) }
                Button(
                    onClick = onMuhurthams,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f), contentColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) { Text(Translations.tr("menu_muhurthams", language)) }
            }
        }
    }
}

@Composable
private fun BabyNamesHomeCard(language: Language, onOpen: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onOpen)
            .testTag("home_baby_names_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, TempleGold.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ChildCare,
                contentDescription = null,
                tint = TempleSaffron,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = Translations.tr("menu_baby_names", language),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = Translations.tr("baby_home_subtitle", language),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TempleGold
            )
        }
    }
}

@Composable
private fun HomeHeroHeader(
    language: Language,
    selectedCity: City,
    panchangData: PanchangData
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .testTag("home_hero_banner")
    ) {
        Image(
            painter = painterResource(id = R.drawable.temple_dusk_banner),
            contentDescription = "Temple at Dusk Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            TempleMaroonDark.copy(alpha = 0.7f),
                            TempleMaroonDark.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Gregorian Date & Weekday
            val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault())
            val dateStr = panchangData.date.format(dateFormatter)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TempleGold.copy(alpha = 0.9f),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = if (language == Language.TELUGU) panchangData.vaaramTe else panchangData.vaaramEn,
                        color = TempleMaroonDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = dateStr,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Greeting
            val greeting = Translations.tr("greeting_morning", language)
            Text(
                text = "$greeting, ${if (language == Language.TELUGU) selectedCity.nameTe else selectedCity.nameEn}!",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Luni-Solar Calendar subtitle
            Text(
                text = "${if (language == Language.TELUGU) panchangData.samvatsaramTe else panchangData.samvatsaramEn} • ${if (language == Language.TELUGU) panchangData.maasamTe else panchangData.maasamEn}",
                color = TempleGoldLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TodayPanchangCard(
    language: Language,
    panchang: PanchangData,
    selectedCity: City,
    onViewFullPanchang: () -> Unit,
    onSharePanchang: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("home_panchang_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, TempleGold.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(TempleMaroon.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flare,
                            contentDescription = null,
                            tint = TempleSaffron,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Translations.tr("home_panchang_title", language),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onViewFullPanchang() }
                        .padding(4.dp)
                        .testTag("home_view_full_panchang_btn")
                ) {
                    Text(
                        text = Translations.tr("view_details", language),
                        color = TempleSaffron,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = TempleSaffron,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Tithi & Nakshatra
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Tithi Box
                PanchangHighlightBox(
                    title = Translations.tr("tithi", language),
                    value = if (language == Language.TELUGU) panchang.tithiTe else panchang.tithiEn,
                    subtitle = if (language == Language.TELUGU) panchang.tithiEndTimeTe else panchang.tithiEndTimeEn,
                    accentColor = TempleGold,
                    modifier = Modifier.weight(1f)
                )

                // Nakshatra Box
                PanchangHighlightBox(
                    title = Translations.tr("nakshatram", language),
                    value = if (language == Language.TELUGU) panchang.nakshatramTe else panchang.nakshatramEn,
                    subtitle = if (language == Language.TELUGU) panchang.nakshatraEndTimeTe else panchang.nakshatraEndTimeEn,
                    accentColor = TempleSaffron,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timings Row: Sunrise, Sunset, Rahu Kalam
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimingItem(
                    icon = Icons.Default.WbSunny,
                    label = Translations.tr("sunrise", language),
                    time = panchang.sunrise,
                    color = TempleSaffron
                )

                TimingItem(
                    icon = Icons.Default.NightsStay,
                    label = Translations.tr("sunset", language),
                    time = panchang.sunset,
                    color = TempleGold
                )

                TimingItem(
                    icon = Icons.Default.Schedule,
                    label = Translations.tr("rahu_kalam", language) + if (panchang.isInsideRahuKalam) " ⚠️" else "",
                    time = panchang.rahuKalam.substringBefore(" - "),
                    color = InauspiciousRed
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Big Saffron "Share on WhatsApp" Button
            Button(
                onClick = onSharePanchang,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("home_share_whatsapp_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TempleSaffron,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Translations.tr("share_on_whatsapp", language),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PanchangHighlightBox(
    title: String,
    value: String,
    subtitle: String? = null,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = accentColor.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            subtitle?.let { sub ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = sub,
                    fontSize = 10.sp,
                    color = accentColor,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TimingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    time: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = time,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun YourRasiTodayCard(
    language: Language,
    rasi: Rasi,
    onViewAllRasis: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("home_rasi_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, TempleGold.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = rasi.symbol,
                        fontSize = 26.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (language == Language.TELUGU) rasi.nameTe else rasi.nameEn,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(${if (language == Language.TELUGU) rasi.nameEn else rasi.nameTe})",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${if (language == Language.TELUGU) rasi.rulingPlanetTe else rasi.rulingPlanetEn} • ${if (language == Language.TELUGU) rasi.elementTe else rasi.elementEn}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onViewAllRasis() }
                        .padding(4.dp)
                        .testTag("home_view_all_rasi_btn")
                ) {
                    Text(
                        text = Translations.tr("view_all", language),
                        color = TempleSaffron,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = TempleSaffron,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Prediction text
            Text(
                text = if (language == Language.TELUGU) rasi.todayPredictionTe else rasi.todayPredictionEn,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Lucky items pill row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = TempleGold.copy(alpha = 0.15f),
                    border = BorderStroke(0.5.dp, TempleGold.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "${Translations.tr("lucky_num", language)}: ${rasi.luckyNumber}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = TempleSaffron.copy(alpha = 0.15f),
                    border = BorderStroke(0.5.dp, TempleSaffron.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "${Translations.tr("lucky_color", language)}: ${if (language == Language.TELUGU) rasi.luckyColorTe else rasi.luckyColorEn}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveNowChannelsSection(
    language: Language,
    channels: List<Channel>,
    onSelectChannel: (Channel) -> Unit,
    onViewAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(LiveRed)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = Translations.tr("home_livetv_title", language),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = Translations.tr("view_all", language),
                color = TempleSaffron,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onViewAll() }
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(channels) { channel ->
                LiveChannelCard(
                    language = language,
                    channel = channel,
                    onClick = { onSelectChannel(channel) }
                )
            }
        }
    }
}

@Composable
private fun LiveChannelCard(
    language: Language,
    channel: Channel,
    onClick: () -> Unit
) {
    val isDevotional = channel.category == "Devotional"
    Card(
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("channel_card_${channel.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, if (isDevotional) TempleGold.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column {
            // Video Thumbnail Simulation with Initials
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = if (isDevotional) listOf(TempleMaroonDark, TempleMaroon)
                            else listOf(Color(0xFF1E2638), Color(0xFF111722))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Initials Circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(TempleGold.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = channel.getInitials(),
                        color = TempleGoldLight,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Live Badge top right
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = LiveRed,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }

                // Category tag bottom left
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = channel.category,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            // Info
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = if (language == Language.TELUGU) channel.nameTelugu else channel.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = channel.name,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TopNewsSection(
    language: Language,
    newsItems: List<NewsItem>,
    bookmarkedIds: Set<String>,
    onOpenNews: (NewsItem) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onViewAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = Translations.tr("home_news_title", language),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = Translations.tr("view_all", language),
                color = TempleSaffron,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onViewAll() }
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            newsItems.forEach { item ->
                NewsRowItem(
                    language = language,
                    item = item,
                    isBookmarked = bookmarkedIds.contains(item.id),
                    onClick = { onOpenNews(item) },
                    onToggleBookmark = { onToggleBookmark(item.id) }
                )
            }
        }
    }
}

@Composable
private fun NewsRowItem(
    language: Language,
    item: NewsItem,
    isBookmarked: Boolean,
    onClick: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("home_news_item_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = TempleMaroon.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = if (language == Language.TELUGU) item.categoryTe else item.categoryEn,
                            color = TempleMaroon,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = if (language == Language.TELUGU) item.timeAgoTe else item.timeAgoEn,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (language == Language.TELUGU) item.titleTe else item.titleEn,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onToggleBookmark,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (isBookmarked) TempleGold else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun UpcomingFestivalCard(
    language: Language,
    festival: Festival,
    onViewAllFestivals: () -> Unit,
    onClickFestival: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClickFestival() }
            .testTag("home_festival_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        ),
        border = BorderStroke(1.dp, TempleGold)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Celebration,
                        contentDescription = null,
                        tint = TempleGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Translations.tr("home_festival_title", language),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Countdown badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = TempleSaffron,
                    border = BorderStroke(1.dp, TempleGold)
                ) {
                    Text(
                        text = "${festival.daysLeft} ${Translations.tr("days_left", language)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (language == Language.TELUGU) festival.nameTe else festival.nameEn,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = if (language == Language.TELUGU) festival.dateDisplayTe else festival.dateDisplayEn,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TempleSaffron
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (language == Language.TELUGU) festival.significanceTe else festival.significanceEn,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = Translations.tr("festivals_title", language) + " →",
                    color = TempleGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onViewAllFestivals() }
                )
            }
        }
    }
}

@Composable
private fun SmartGreetingSuggestionsBanner(
    language: Language,
    panchangDate: java.time.LocalDate,
    selectedCity: City,
    onOpenGreetings: () -> Unit
) {
    val currentHour = remember { java.time.LocalTime.now().hour }
    val isMorning = currentHour < 11
    val isTelugu = language == Language.TELUGU

    val context = LocalContext.current
    val nextFest = remember(panchangDate, selectedCity) {
        FestivalCalendar.nextMajor(context, panchangDate, selectedCity.latitude, selectedCity.longitude)
    }
    val daysUntilFestival: Long = remember(nextFest, panchangDate) {
        nextFest?.getDaysLeft(panchangDate) ?: -1L
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Festival Near Banner
        if (nextFest != null && daysUntilFestival in 0L..3L) {
            val festName = if (isTelugu) nextFest.nameTe else nextFest.nameEn
            val festText = if (daysUntilFestival == 0L) {
                if (isTelugu) "$festName నేడే! – మీ శుభాకాంక్షల కార్డును తయారు చేసుకోండి"
                else "$festName is today! – make your greeting card now"
            } else {
                if (isTelugu) "$festName మరో $daysUntilFestival రోజుల్లో – మీ శుభాకాంక్షల కార్డును తయారు చేసుకోండి"
                else "$festName in $daysUntilFestival days – make your greeting card now"
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, TempleGold),
                colors = CardDefaults.cardColors(
                    containerColor = TempleMaroon
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenGreetings() }
                    .testTag("home_festival_greeting_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = TempleSaffron,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Celebration,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = festText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFF7EC),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isTelugu) "వాట్సాప్ కార్డు తయారు చేయండి →" else "Make WhatsApp Card →",
                                fontSize = 11.sp,
                                color = TempleGold
                            )
                        }
                    }
                }
            }
        }

        // 2. Morning Greeting Card (Before 11 AM)
        if (isMorning) {
            Card(
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, TempleSaffron.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(
                    containerColor = TempleMaroonDark
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenGreetings() }
                    .testTag("home_morning_greeting_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = TempleGold,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = TempleMaroonDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isTelugu) "ఉదయపు శుభాకాంక్షలు పంపండి" else "Send Good Morning wishes",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFF7EC)
                            )
                            Text(
                                text = if (isTelugu)
                                    "మీ పేరు, ఫోటోతో శుభోదయం కార్డును పంపండి →"
                                else "Share customized morning wishes with your name & photo →",
                                fontSize = 11.sp,
                                color = TempleSaffron
                            )
                        }
                    }
                }
            }
        }
    }
}
