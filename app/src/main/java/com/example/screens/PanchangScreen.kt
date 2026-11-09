package com.example.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AstronomyEngine
import com.example.data.PanchangNames
import com.example.data.model.City
import com.example.data.model.Language
import com.example.data.model.PanchangData
import com.example.data.model.PanchangProvider
import com.example.i18n.Translations
import com.example.ui.theme.AuspiciousGreen
import com.example.ui.theme.InauspiciousRed
import com.example.ui.theme.TempleGold
import com.example.ui.theme.TempleGoldLight
import com.example.ui.theme.TempleMaroon
import com.example.ui.theme.TempleMaroonDark
import com.example.ui.theme.TempleMaroonLight
import com.example.ui.theme.TempleSaffron
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PanchangScreen(
    language: Language,
    selectedCity: City,
    panchangData: PanchangData,
    selectedDate: LocalDate,
    onSelectDate: (LocalDate) -> Unit,
    onSharePanchang: (LocalDate) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(0) } // 0 = Daily Detail, 1 = Monthly Calendar
    var dragAccumulator by remember { mutableStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(selectedDate) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        dragAccumulator += dragAmount
                    },
                    onDragEnd = {
                        if (dragAccumulator < -60) {
                            // Swiped left -> Next day
                            onSelectDate(selectedDate.plusDays(1))
                        } else if (dragAccumulator > 60) {
                            // Swiped right -> Previous day
                            onSelectDate(selectedDate.minusDays(1))
                        }
                        dragAccumulator = 0f
                    }
                )
            }
            .testTag("panchang_screen")
    ) {
        // Mode Selector: Daily Detail vs Monthly Calendar
        TabRow(
            selectedTabIndex = viewMode,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = TempleMaroon,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[viewMode]),
                    color = TempleGold
                )
            }
        ) {
            Tab(
                selected = viewMode == 0,
                onClick = { viewMode = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ViewDay,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (viewMode == 0) TempleGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == Language.TELUGU) "నిత్య పంచాంగం" else "Daily View",
                            fontWeight = if (viewMode == 0) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                },
                modifier = Modifier.testTag("panchang_tab_daily")
            )
            Tab(
                selected = viewMode == 1,
                onClick = { viewMode = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (viewMode == 1) TempleGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == Language.TELUGU) "మాస పంచాంగం" else "Monthly Calendar",
                            fontWeight = if (viewMode == 1) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                },
                modifier = Modifier.testTag("panchang_tab_monthly")
            )
        }

        if (viewMode == 0) {
            DailyPanchangView(
                language = language,
                city = selectedCity,
                panchang = panchangData,
                selectedDate = selectedDate,
                onSelectDate = onSelectDate,
                onShareDate = onSharePanchang
            )
        } else {
            MonthlyPanchangCalendar(
                language = language,
                city = selectedCity,
                selectedDate = selectedDate,
                onSelectDate = { newDate ->
                    onSelectDate(newDate)
                    viewMode = 0 // Switch to daily view of picked date
                }
            )
        }
    }
}

@Composable
private fun DailyPanchangView(
    language: Language,
    city: City,
    panchang: PanchangData,
    selectedDate: LocalDate,
    onSelectDate: (LocalDate) -> Unit,
    onShareDate: (LocalDate) -> Unit = {}
) {
    val isToday = selectedDate == LocalDate.now()
    val isTomorrow = selectedDate == LocalDate.now().plusDays(1)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("panchang_daily_scroll"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Date Navigation Bar with Small Share Icon
        item {
            DateNavigationCard(
                language = language,
                selectedDate = selectedDate,
                vaaram = if (language == Language.TELUGU) panchang.vaaramTe else panchang.vaaramEn,
                onPrev = { onSelectDate(selectedDate.minusDays(1)) },
                onNext = { onSelectDate(selectedDate.plusDays(1)) },
                onToday = { onSelectDate(LocalDate.now()) },
                onTomorrow = { onSelectDate(LocalDate.now().plusDays(1)) },
                onShareDate = { onShareDate(selectedDate) },
                isToday = isToday,
                isTomorrow = isTomorrow
            )
        }

        // Big Saffron "Share on WhatsApp" Button
        item {
            Button(
                onClick = { onShareDate(selectedDate) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("panchang_screen_share_whatsapp_btn"),
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

        // Active Rahu Kalam Warning Banner (if inside Rahu Kalam)
        if (panchang.isInsideRahuKalam) {
            item {
                ActiveRahuKalamBanner(language = language, rahuKalam = panchang.rahuKalam)
            }
        }

        // Year / Samvatsaram & Month Banner
        item {
            SamvatsaramHeaderCard(language = language, panchang = panchang)
        }

        // Big Primary Cards: Tithi & Nakshatra
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BigPanchangCard(
                    title = Translations.tr("tithi", language),
                    value = if (language == Language.TELUGU) panchang.tithiTe else panchang.tithiEn,
                    subtitle = if (language == Language.TELUGU) panchang.tithiEndTimeTe else panchang.tithiEndTimeEn,
                    badge = if (language == Language.TELUGU) panchang.pakshamTe else panchang.pakshamEn,
                    accentColor = TempleGold,
                    modifier = Modifier.weight(1f),
                    testTag = "card_tithi"
                )

                BigPanchangCard(
                    title = Translations.tr("nakshatram", language),
                    value = if (language == Language.TELUGU) panchang.nakshatramTe else panchang.nakshatramEn,
                    subtitle = if (language == Language.TELUGU) panchang.nakshatraEndTimeTe else panchang.nakshatraEndTimeEn,
                    badge = "#${panchang.nakshatraNumber}",
                    accentColor = TempleSaffron,
                    modifier = Modifier.weight(1f),
                    testTag = "card_nakshatram"
                )
            }
        }

        // Yoga & Karana Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SmallPanchangCard(
                    title = Translations.tr("yogam", language),
                    value = if (language == Language.TELUGU) panchang.yogamTe else panchang.yogamEn,
                    subtitle = "#${panchang.yogaNumber} ${if (language == Language.TELUGU) "యోగం" else "Yoga"}",
                    modifier = Modifier.weight(1f),
                    testTag = "card_yogam"
                )

                SmallPanchangCard(
                    title = Translations.tr("karanam", language),
                    value = if (language == Language.TELUGU) panchang.karanaTe else panchang.karanaEn,
                    subtitle = "${if (language == Language.TELUGU) "కరణం" else "Karana"} (${panchang.karanaIndex + 1})",
                    modifier = Modifier.weight(1f),
                    testTag = "card_karanam"
                )
            }
        }

        // Sun & Moon Timings Card
        item {
            SunAndMoonCard(language = language, panchang = panchang)
        }

        // Auspicious & Inauspicious Timings Card
        item {
            MuhurthamTimingsCard(language = language, panchang = panchang)
        }
    }
}

@Composable
private fun DateNavigationCard(
    language: Language,
    selectedDate: LocalDate,
    vaaram: String,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
    onTomorrow: () -> Unit,
    onShareDate: () -> Unit = {},
    isToday: Boolean,
    isTomorrow: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("panchang_date_nav_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, TempleGold.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onPrev,
                    modifier = Modifier.testTag("date_nav_prev_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous Day",
                        tint = TempleMaroon
                    )
                }

                val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = selectedDate.format(formatter),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Small share icon on every Panchang date
                        IconButton(
                            onClick = onShareDate,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("date_small_share_icon_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share this date Panchang",
                                tint = TempleSaffron,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = vaaram,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TempleSaffron
                    )
                }

                IconButton(
                    onClick = onNext,
                    modifier = Modifier.testTag("date_nav_next_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next Day",
                        tint = TempleMaroon
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Today / Tomorrow Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isToday) TempleGold else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onToday() }
                        .testTag("date_nav_today_btn")
                ) {
                    Text(
                        text = Translations.tr("today", language),
                        color = if (isToday) TempleMaroonDark else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isTomorrow) TempleGold else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onTomorrow() }
                        .testTag("date_nav_tomorrow_btn")
                ) {
                    Text(
                        text = Translations.tr("tomorrow", language),
                        color = if (isTomorrow) TempleMaroonDark else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveRahuKalamBanner(
    language: Language,
    rahuKalam: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rahu_kalam_active_banner"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        ),
        border = BorderStroke(1.5.dp, InauspiciousRed)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(InauspiciousRed),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = if (language == Language.TELUGU) "ప్రస్తుతం రాహుకాలం నడుస్తోంది!" else "Currently Inside Rahu Kalam!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = InauspiciousRed
                )
                Text(
                    text = if (language == Language.TELUGU) "$rahuKalam • ముఖ్యమైన కార్యాలు, ప్రయాణాలు నివారించండి"
                    else "$rahuKalam • Avoid initiating auspicious ventures",
                    fontSize = 11.sp,
                    color = Color(0xFF5C0A0A)
                )
            }
        }
    }
}

@Composable
private fun SamvatsaramHeaderCard(
    language: Language,
    panchang: PanchangData
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("panchang_samvatsaram_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = TempleMaroonDark
        ),
        border = BorderStroke(1.dp, TempleGold)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (language == Language.TELUGU) panchang.samvatsaramTe else panchang.samvatsaramEn,
                color = TempleGoldLight,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "${if (language == Language.TELUGU) panchang.ayanamTe else panchang.ayanamEn} • ${if (language == Language.TELUGU) panchang.rutuvuTe else panchang.rutuvuEn} • ${if (language == Language.TELUGU) panchang.maasamTe else panchang.maasamEn}",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun BigPanchangCard(
    title: String,
    value: String,
    subtitle: String,
    badge: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Card(
        modifier = modifier
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = accentColor,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SmallPanchangCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Card(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun SunAndMoonCard(
    language: Language,
    panchang: PanchangData
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("panchang_sun_moon_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, TempleGold.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = Translations.tr("sun_moon_timings", language),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SunMoonItem(
                    icon = Icons.Default.WbSunny,
                    label = Translations.tr("sunrise", language),
                    time = panchang.sunrise,
                    tint = TempleSaffron
                )

                SunMoonItem(
                    icon = Icons.Default.NightsStay,
                    label = Translations.tr("sunset", language),
                    time = panchang.sunset,
                    tint = TempleGold
                )

                SunMoonItem(
                    icon = Icons.Default.Flare,
                    label = Translations.tr("moonrise", language),
                    time = panchang.moonrise,
                    tint = Color(0xFF64B5F6)
                )

                SunMoonItem(
                    icon = Icons.Default.NightsStay,
                    label = Translations.tr("moonset", language),
                    time = panchang.moonset,
                    tint = Color(0xFF90A4AE)
                )
            }
        }
    }
}

@Composable
private fun SunMoonItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    time: String,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = time,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun MuhurthamTimingsCard(
    language: Language,
    panchang: PanchangData
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("panchang_muhurtham_timings_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, TempleGold.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Auspicious Section (Green)
            Text(
                text = Translations.tr("auspicious_times", language),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AuspiciousGreen
            )

            Spacer(modifier = Modifier.height(8.dp))

            TimingRow(
                title = Translations.tr("abhijit_muhurtham", language),
                time = panchang.abhijitMuhurtham,
                isAuspicious = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Inauspicious Section (Red tint)
            Text(
                text = Translations.tr("inauspicious_times", language),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = InauspiciousRed
            )

            Spacer(modifier = Modifier.height(8.dp))

            TimingRow(
                title = Translations.tr("rahu_kalam", language),
                time = panchang.rahuKalam,
                isAuspicious = false
            )

            Spacer(modifier = Modifier.height(6.dp))

            TimingRow(
                title = Translations.tr("yamagandam", language),
                time = panchang.yamagandam,
                isAuspicious = false
            )

            Spacer(modifier = Modifier.height(6.dp))

            TimingRow(
                title = Translations.tr("gulika_kalam", language),
                time = panchang.gulikaKalam,
                isAuspicious = false
            )
        }
    }
}

@Composable
private fun TimingRow(
    title: String,
    time: String,
    isAuspicious: Boolean
) {
    val bgColor = if (isAuspicious) AuspiciousGreen.copy(alpha = 0.08f) else InauspiciousRed.copy(alpha = 0.08f)
    val borderColor = if (isAuspicious) AuspiciousGreen.copy(alpha = 0.3f) else InauspiciousRed.copy(alpha = 0.3f)
    val textColor = if (isAuspicious) AuspiciousGreen else InauspiciousRed

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = time,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
private fun MonthlyPanchangCalendar(
    language: Language,
    city: City,
    selectedDate: LocalDate,
    onSelectDate: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value // 1 (Mon) - 7 (Sun)
    // Convert so Sunday is first (0..6)
    val offset = if (firstDayOfWeek == 7) 0 else firstDayOfWeek

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("panchang_monthly_calendar")
    ) {
        // Month Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = TempleMaroon)
            }

            Text(
                text = "${currentMonth.month.name} ${currentMonth.year}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = TempleMaroon)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Weekday header (Sun - Sat)
        val weekdays = if (language == Language.TELUGU) {
            listOf("ఆది", "సోమ", "మంగళ", "బుధ", "గురు", "శుక్ర", "శని")
        } else {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            weekdays.forEach { wd ->
                Text(
                    text = wd,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (wd == "ఆది" || wd == "Sun") InauspiciousRed else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days Grid
        val totalCells = offset + daysInMonth
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Empty placeholder cells before 1st of month
            items(offset) {
                Box(modifier = Modifier.size(44.dp))
            }

            // Actual days
            items(daysInMonth) { index ->
                val day = index + 1
                val cellDate = currentMonth.atDay(day)
                val isSelected = cellDate == selectedDate
                val isToday = cellDate == LocalDate.now()

                // Calculate tithi for this day
                val pData = PanchangProvider.getPanchangForDate(cellDate, city)
                val tithiNum = pData.tithiNumber // 1..30
                val isPurnima = tithiNum == 15
                val isAmavasya = tithiNum == 30

                Card(
                    modifier = Modifier
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelectDate(cellDate) },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isSelected -> TempleMaroon
                            isToday -> TempleGold.copy(alpha = 0.2f)
                            isPurnima -> Color(0xFFFFF8E1)
                            isAmavasya -> Color(0xFFECEFF1)
                            else -> MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = BorderStroke(
                        width = if (isSelected || isToday) 1.5.dp else 0.5.dp,
                        color = when {
                            isSelected -> TempleGold
                            isToday -> TempleSaffron
                            isPurnima -> TempleGold
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = day.toString(),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            if (isPurnima) {
                                Text(text = " 🌕", fontSize = 9.sp)
                            } else if (isAmavasya) {
                                Text(text = " 🌑", fontSize = 9.sp)
                            }
                        }

                        // Short Tithi Name
                        val tithiShortName = PanchangNames.getTithi(tithiNum).nameTe
                            .replace("శుక్ల ", "శు.")
                            .replace("కృష్ణ ", "కృ.")
                            .replace("పూర్ణిమ (పౌర్ణమి)", "పౌర్ణమి")

                        Text(
                            text = if (language == Language.TELUGU) tithiShortName else PanchangNames.getTithi(tithiNum).nameEn.take(6),
                            fontSize = 8.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (isSelected) TempleGoldLight else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
