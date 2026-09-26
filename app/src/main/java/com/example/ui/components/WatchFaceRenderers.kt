package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.GregorianDate
import com.example.calendar.LunarHijriDate
import com.example.calendar.PersianOccasion
import com.example.calendar.SolarDate
import com.example.calendar.SolarHijriCalendar
import com.example.model.WatchComplicationType
import com.example.model.WatchCustomSettings
import com.example.model.WatchFaceLayout
import com.example.model.WatchFontType
import com.example.model.WatchThemePalette
import java.util.Calendar
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/**
 * Formats live clock into hour:minute and optional second string.
 */
fun formatLiveClock(
    currentTimeMillis: Long,
    usePersianDigits: Boolean,
    is24Hour: Boolean,
    showSeconds: Boolean,
    isAmbient: Boolean
): Pair<String, String?> {
    val cal = Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
    val hour = if (is24Hour) cal.get(Calendar.HOUR_OF_DAY) else {
        val h = cal.get(Calendar.HOUR)
        if (h == 0) 12 else h
    }
    val min = cal.get(Calendar.MINUTE)
    val sec = cal.get(Calendar.SECOND)

    val timeRaw = String.format(Locale.US, "%02d:%02d", hour, min)
    val timeFormatted = if (usePersianDigits) SolarHijriCalendar.toPersianDigits(timeRaw) else timeRaw

    val secFormatted = if (showSeconds && !isAmbient) {
        val secRaw = String.format(Locale.US, ":%02d", sec)
        if (usePersianDigits) SolarHijriCalendar.toPersianDigits(secRaw) else secRaw
    } else null

    return Pair(timeFormatted, secFormatted)
}

@Composable
fun WatchFaceRenderer(
    settings: WatchCustomSettings,
    solarDate: SolarDate,
    gregorianDate: GregorianDate,
    lunarDate: LunarHijriDate,
    occasions: List<PersianOccasion>,
    currentTimeMillis: Long,
    modifier: Modifier = Modifier
) {
    val theme = settings.theme
    val isAmbient = settings.isAmbientAodMode

    // Background color: Pure black in AOD for OLED subpixel off
    val bgColor = if (isAmbient) Color.Black else theme.backgroundColor

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        when (settings.layout) {
            WatchFaceLayout.BIG_CLOCK_MODULAR -> BigClockModularFace(
                settings = settings,
                solarDate = solarDate,
                gregorianDate = gregorianDate,
                lunarDate = lunarDate,
                occasions = occasions,
                currentTimeMillis = currentTimeMillis
            )
            WatchFaceLayout.HEALTH_DASHBOARD -> HealthDashboardFace(
                settings = settings,
                solarDate = solarDate,
                occasions = occasions,
                currentTimeMillis = currentTimeMillis
            )
            WatchFaceLayout.SOLAR_MINIMAL -> SolarMinimalFace(
                settings = settings,
                solarDate = solarDate,
                gregorianDate = gregorianDate,
                lunarDate = lunarDate,
                occasions = occasions,
                currentTimeMillis = currentTimeMillis
            )
            WatchFaceLayout.CHRONO_DIAL -> ChronoDialFace(
                settings = settings,
                solarDate = solarDate,
                currentTimeMillis = currentTimeMillis
            )
            WatchFaceLayout.PROGRESS_RINGS -> ProgressRingsFace(
                settings = settings,
                solarDate = solarDate,
                occasions = occasions,
                currentTimeMillis = currentTimeMillis
            )
            WatchFaceLayout.WEAR_OS_TILE -> WearOsTileFace(
                settings = settings,
                solarDate = solarDate,
                occasions = occasions,
                currentTimeMillis = currentTimeMillis
            )
        }
    }
}

// ---------------------------------------------------------------------------
// MODULAR COMPLICATION BADGE RENDERER
// ---------------------------------------------------------------------------
@Composable
fun ComplicationBadge(
    type: WatchComplicationType,
    settings: WatchCustomSettings,
    solarDate: SolarDate,
    gregorianDate: GregorianDate,
    lunarDate: LunarHijriDate,
    occasions: List<PersianOccasion>,
    isAmbient: Boolean,
    modifier: Modifier = Modifier
) {
    if (type == WatchComplicationType.NONE) return

    val theme = settings.theme
    val heartAnim = remember { Animatable(1f) }

    LaunchedEffect(type) {
        if (type == WatchComplicationType.HEART_RATE && !isAmbient) {
            heartAnim.animateTo(
                targetValue = 1.25f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 850
                        1.0f at 0
                        1.25f at 180
                        1.0f at 360
                        1.18f at 520
                        1.0f at 850
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isAmbient) Color.Transparent else theme.surfaceColor.copy(alpha = 0.9f))
            .border(
                1.dp,
                if (isAmbient) Color(0xFF444444) else theme.primaryColor.copy(alpha = 0.45f),
                RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 9.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        when (type) {
            WatchComplicationType.HEART_RATE -> {
                val bpm = if (settings.usePersianDigits) {
                    SolarHijriCalendar.toPersianDigits(settings.simulatedHeartRate.toString())
                } else {
                    settings.simulatedHeartRate.toString()
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "ضربان قلب",
                        tint = if (isAmbient) Color.LightGray else Color(0xFFFF3366),
                        modifier = Modifier
                            .size(13.dp)
                            .scale(heartAnim.value)
                    )
                    Text(
                        text = "$bpm",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAmbient) Color.White else theme.textPrimary
                    )
                    Text(
                        text = "BPM",
                        fontSize = 8.5.sp,
                        color = if (isAmbient) Color.Gray else theme.textSecondary
                    )
                }
            }

            WatchComplicationType.STEP_COUNTER -> {
                val stepsFormatted = if (settings.usePersianDigits) {
                    SolarHijriCalendar.toPersianDigits(String.format(Locale.US, "%,d", settings.simulatedSteps))
                } else {
                    String.format(Locale.US, "%,d", settings.simulatedSteps)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.DirectionsWalk,
                        contentDescription = "گام‌شمار",
                        tint = if (isAmbient) Color.LightGray else Color(0xFF10B981),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = stepsFormatted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAmbient) Color.White else theme.textPrimary
                    )
                }
            }

            WatchComplicationType.BATTERY -> {
                val battFormatted = if (settings.usePersianDigits) {
                    SolarHijriCalendar.toPersianDigits("${settings.simulatedBattery}٪")
                } else {
                    "${settings.simulatedBattery}%"
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.BatteryFull,
                        contentDescription = "شارژ باتری ساعت",
                        tint = if (isAmbient) Color.LightGray else theme.primaryColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = battFormatted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAmbient) Color.White else theme.textPrimary
                    )
                }
            }

            WatchComplicationType.CALORIES -> {
                val calFormatted = if (settings.usePersianDigits) {
                    SolarHijriCalendar.toPersianDigits(settings.simulatedCalories.toString())
                } else {
                    settings.simulatedCalories.toString()
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = "کالری",
                        tint = if (isAmbient) Color.LightGray else Color(0xFFFF6D00),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "$calFormatted KCal",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAmbient) Color.White else theme.textPrimary
                    )
                }
            }

            WatchComplicationType.WEATHER -> {
                val tempFormatted = if (settings.usePersianDigits) {
                    SolarHijriCalendar.toPersianDigits("۲۵°")
                } else {
                    "25°"
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.WbSunny,
                        contentDescription = "آب و هوا",
                        tint = if (isAmbient) Color.LightGray else Color(0xFFFBBF24),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = tempFormatted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAmbient) Color.White else theme.textPrimary
                    )
                }
            }

            WatchComplicationType.OCCASIONS -> {
                val firstOccasion = occasions.firstOrNull()?.title ?: "عید نوروز باستانی"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "مناسبت روز",
                        tint = if (isAmbient) Color.LightGray else theme.accentColor,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = firstOccasion,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isAmbient) Color.White else theme.secondaryColor,
                        maxLines = 1
                    )
                }
            }

            WatchComplicationType.SEASON_PROGRESS -> {
                val seasonPct = if (settings.usePersianDigits) {
                    SolarHijriCalendar.toPersianDigits("${solarDate.seasonProgressPercent}٪")
                } else {
                    "${solarDate.seasonProgressPercent}%"
                }
                Text(
                    text = "${solarDate.seasonName} $seasonPct",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isAmbient) Color.LightGray else theme.secondaryColor
                )
            }

            WatchComplicationType.GREGORIAN_DATE -> {
                Text(
                    text = "${gregorianDate.day} ${gregorianDate.monthName}",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isAmbient) Color.LightGray else theme.accentColor
                )
            }

            WatchComplicationType.LUNAR_DATE -> {
                val lDay = if (settings.usePersianDigits) {
                    SolarHijriCalendar.toPersianDigits(lunarDate.day.toString())
                } else {
                    lunarDate.day.toString()
                }
                Text(
                    text = "$lDay ${lunarDate.monthName}",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isAmbient) Color.LightGray else theme.textSecondary
                )
            }

            else -> {}
        }
    }
}

// ---------------------------------------------------------------------------
// 1. BIG CLOCK MODULAR FACE (ساعت فوق‌العاده درشت با تاریخ کوچکتر و ۴ ویجت)
// ---------------------------------------------------------------------------
@Composable
fun BigClockModularFace(
    settings: WatchCustomSettings,
    solarDate: SolarDate,
    gregorianDate: GregorianDate,
    lunarDate: LunarHijriDate,
    occasions: List<PersianOccasion>,
    currentTimeMillis: Long
) {
    val theme = settings.theme
    val isAmbient = settings.isAmbientAodMode

    val (timeMain, timeSec) = formatLiveClock(
        currentTimeMillis,
        settings.usePersianDigits,
        settings.is24Hour,
        settings.showSeconds,
        isAmbient
    )

    val yearFormatted = if (settings.usePersianDigits) {
        SolarHijriCalendar.toPersianDigits(solarDate.year.toString())
    } else {
        solarDate.year.toString()
    }
    val dayFormatted = if (settings.usePersianDigits) {
        SolarHijriCalendar.toPersianDigits(solarDate.day.toString())
    } else {
        solarDate.day.toString()
    }

    // Outer subtle dial gauge
    val daysInMonth = SolarHijriCalendar.getDaysInMonth(solarDate.year, solarDate.month)
    val monthProgress = (solarDate.day.toFloat() / daysInMonth.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Outer decorative activity/calendar arc
        Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            val strokeW = if (isAmbient) 1.5.dp.toPx() else 3.5.dp.toPx()
            val trackColor = if (isAmbient) Color(0xFF1E1E1E) else theme.surfaceColor.copy(alpha = 0.5f)
            val arcColor = if (isAmbient) Color.LightGray else theme.primaryColor

            drawArc(
                color = trackColor,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )
            drawArc(
                color = arcColor,
                startAngle = 135f,
                sweepAngle = 270f * monthProgress,
                useCenter = false,
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )
        }

        // TOP COMPLICATION SLOT
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 18.dp)
        ) {
            ComplicationBadge(
                type = settings.topSlotComplication,
                settings = settings,
                solarDate = solarDate,
                gregorianDate = gregorianDate,
                lunarDate = lunarDate,
                occasions = occasions,
                isAmbient = isAmbient
            )
        }

        // LEFT COMPLICATION SLOT
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 14.dp)
        ) {
            ComplicationBadge(
                type = settings.leftSlotComplication,
                settings = settings,
                solarDate = solarDate,
                gregorianDate = gregorianDate,
                lunarDate = lunarDate,
                occasions = occasions,
                isAmbient = isAmbient
            )
        }

        // RIGHT COMPLICATION SLOT
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 14.dp)
        ) {
            ComplicationBadge(
                type = settings.rightSlotComplication,
                settings = settings,
                solarDate = solarDate,
                gregorianDate = gregorianDate,
                lunarDate = lunarDate,
                occasions = occasions,
                isAmbient = isAmbient
            )
        }

        // CENTER MAIN AREA: PROMINENT BIG CLOCK + SMALLER BALANCED PERSIAN DATE
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 30.dp)
        ) {
            // 1. HUGE DIGITAL CLOCK (درخواست کاربر: ساعت درشت نمایش داده شود)
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = timeMain,
                    fontSize = if (settings.clockSizeLarge) 66.sp else 58.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isAmbient) Color.White else theme.primaryColor,
                    letterSpacing = (-1).sp,
                    lineHeight = 64.sp
                )
                if (timeSec != null) {
                    Text(
                        text = timeSec,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.accentColor,
                        modifier = Modifier.padding(bottom = 8.dp, start = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 2. 50% OCCUPANCY PERSIAN SOLAR DATE (ساعت و تاریخ ۵۰٪ صفحه واچ)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${solarDate.dayOfWeekName}،",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAmbient) Color.LightGray else theme.secondaryColor
                )
                Text(
                    text = "$dayFormatted ${solarDate.monthName} $yearFormatted",
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isAmbient) Color.White else theme.textPrimary
                )
            }

            // 3. Mini Season / Zodiac Tag
            if (!isAmbient && settings.showSeasonProgress) {
                Text(
                    text = "فصل ${solarDate.seasonName} • برج ${solarDate.zodiacName}",
                    fontSize = 9.sp,
                    color = theme.textSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // BOTTOM COMPLICATION SLOT
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp)
        ) {
            ComplicationBadge(
                type = settings.bottomSlotComplication,
                settings = settings,
                solarDate = solarDate,
                gregorianDate = gregorianDate,
                lunarDate = lunarDate,
                occasions = occasions,
                isAmbient = isAmbient
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 2. HEALTH DASHBOARD FACE (داشبورد سلامت و تقویم سامسونگ)
// ---------------------------------------------------------------------------
@Composable
fun HealthDashboardFace(
    settings: WatchCustomSettings,
    solarDate: SolarDate,
    occasions: List<PersianOccasion>,
    currentTimeMillis: Long
) {
    val theme = settings.theme
    val isAmbient = settings.isAmbientAodMode

    val (timeMain, timeSec) = formatLiveClock(
        currentTimeMillis,
        settings.usePersianDigits,
        settings.is24Hour,
        settings.showSeconds,
        isAmbient
    )

    val dayFormatted = if (settings.usePersianDigits) {
        SolarHijriCalendar.toPersianDigits(solarDate.day.toString())
    } else {
        solarDate.day.toString()
    }
    val yearFormatted = if (settings.usePersianDigits) {
        SolarHijriCalendar.toPersianDigits(solarDate.year.toString())
    } else {
        solarDate.year.toString()
    }

    val stepProgress = (settings.simulatedSteps / 10000f).coerceIn(0.1f, 1f)
    val calProgress = (settings.simulatedCalories / 600f).coerceIn(0.1f, 1f)
    val battProgress = (settings.simulatedBattery / 100f).coerceIn(0.1f, 1f)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Samsung Health Activity 3-Concentric Rings
        Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val strokeW = if (isAmbient) 2.dp.toPx() else 4.dp.toPx()
            val spacing = strokeW + 2.5.dp.toPx()

            val r1 = (size.minDimension / 2f) - strokeW // Move / Steps (Pink/Red)
            val r2 = r1 - spacing // Calories (Yellow/Gold)
            val r3 = r2 - spacing // Battery (Mint/Teal)

            fun drawHealthRing(r: Float, prog: Float, color: Color, trackColor: Color) {
                drawCircle(color = trackColor, radius = r, center = center, style = Stroke(width = strokeW))
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * prog,
                    useCenter = false,
                    topLeft = Offset(center.x - r, center.y - r),
                    size = Size(r * 2, r * 2),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
            }

            drawHealthRing(r1, stepProgress, if (isAmbient) Color.LightGray else Color(0xFFFF2A6D), Color(0xFF26101B))
            drawHealthRing(r2, calProgress, if (isAmbient) Color.Gray else Color(0xFFFBBF24), Color(0xFF261E0A))
            drawHealthRing(r3, battProgress, if (isAmbient) Color.DarkGray else Color(0xFF2DD4BF), Color(0xFF092420))
        }

        // Center Content: Big Clock + Smaller Persian Date + Activity stats
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Large Clock
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = timeMain,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isAmbient) Color.White else theme.textPrimary
                )
                if (timeSec != null) {
                    Text(
                        text = timeSec,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.accentColor
                    )
                }
            }

            // Smaller Solar Date
            Text(
                text = "${solarDate.dayOfWeekName} $dayFormatted ${solarDate.monthName}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAmbient) Color.LightGray else theme.primaryColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Pulse & Steps Row
            if (!isAmbient) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val bpm = if (settings.usePersianDigits) {
                        SolarHijriCalendar.toPersianDigits(settings.simulatedHeartRate.toString())
                    } else {
                        settings.simulatedHeartRate.toString()
                    }
                    val stp = if (settings.usePersianDigits) {
                        SolarHijriCalendar.toPersianDigits(settings.simulatedSteps.toString())
                    } else {
                        settings.simulatedSteps.toString()
                    }
                    Text(
                        text = "♥ $bpm bpm",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF2A6D)
                    )
                    Text(text = "•", fontSize = 10.sp, color = Color.Gray)
                    Text(
                        text = "⚡ $stp قدم",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2DD4BF)
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 3. SOLAR MINIMAL FACE (ساعت درشت و تاریخ شمسی)
// ---------------------------------------------------------------------------
@Composable
fun SolarMinimalFace(
    settings: WatchCustomSettings,
    solarDate: SolarDate,
    gregorianDate: GregorianDate,
    lunarDate: LunarHijriDate,
    occasions: List<PersianOccasion>,
    currentTimeMillis: Long
) {
    val theme = settings.theme
    val isAmbient = settings.isAmbientAodMode
    val (timeMain, timeSec) = formatLiveClock(
        currentTimeMillis,
        settings.usePersianDigits,
        settings.is24Hour,
        settings.showSeconds,
        isAmbient
    )

    val dayNum = if (settings.usePersianDigits) {
        SolarHijriCalendar.toPersianDigits(solarDate.day.toString())
    } else {
        solarDate.day.toString()
    }
    val yearNum = if (settings.usePersianDigits) {
        SolarHijriCalendar.toPersianDigits(solarDate.year.toString())
    } else {
        solarDate.year.toString()
    }

    val daysInMonth = SolarHijriCalendar.getDaysInMonth(solarDate.year, solarDate.month)
    val progress = (solarDate.day.toFloat() / daysInMonth.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Outer arc progress of the Persian month
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val strokeW = if (isAmbient) 2.dp.toPx() else 4.dp.toPx()
            val trackColor = if (isAmbient) Color(0xFF222222) else theme.surfaceColor.copy(alpha = 0.5f)
            val arcColor = if (isAmbient) Color.LightGray else theme.primaryColor

            drawArc(
                color = trackColor,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )
            drawArc(
                color = arcColor,
                startAngle = 135f,
                sweepAngle = 270f * progress,
                useCenter = false,
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            // Live Large Clock at top
            if (settings.showClock) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = timeMain,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isAmbient) Color.White else theme.primaryColor,
                        letterSpacing = 0.5.sp
                    )
                    if (timeSec != null) {
                        Text(
                            text = timeSec,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = theme.accentColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
            }

            // Day of week badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isAmbient) Color.Transparent else theme.surfaceColor)
                    .border(
                        1.dp,
                        if (isAmbient) Color(0xFF555555) else theme.primaryColor.copy(alpha = 0.4f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text(
                    text = solarDate.dayOfWeekName,
                    color = if (isAmbient) Color.White else theme.secondaryColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Persian Day Numeral
            Text(
                text = dayNum,
                fontSize = if (settings.font == WatchFontType.BOLD_DISPLAY) 46.sp else 40.sp,
                fontWeight = FontWeight.Black,
                color = if (isAmbient) Color.White else theme.textPrimary,
                lineHeight = 44.sp
            )

            // Month and Year (کوچکتر از ساعت)
            Text(
                text = "${solarDate.monthName} $yearNum",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAmbient) Color.LightGray else theme.primaryColor
            )

            // Complications: Heart rate & Battery & Occasion
            if (!isAmbient) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val bpm = if (settings.usePersianDigits) {
                        SolarHijriCalendar.toPersianDigits(settings.simulatedHeartRate.toString())
                    } else {
                        settings.simulatedHeartRate.toString()
                    }
                    val batt = if (settings.usePersianDigits) {
                        SolarHijriCalendar.toPersianDigits("${settings.simulatedBattery}٪")
                    } else {
                        "${settings.simulatedBattery}%"
                    }
                    Text(text = "♥ $bpm", fontSize = 9.sp, color = Color(0xFFFF3366), fontWeight = FontWeight.Bold)
                    Text(text = "•", fontSize = 8.sp, color = Color.Gray)
                    Text(text = "$batt باتری", fontSize = 9.sp, color = theme.primaryColor, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 4. CHRONO DIAL FACE (کرنوگراف عقربه‌ای با سنسورها)
// ---------------------------------------------------------------------------
@Composable
fun ChronoDialFace(
    settings: WatchCustomSettings,
    solarDate: SolarDate,
    currentTimeMillis: Long
) {
    val theme = settings.theme
    val isAmbient = settings.isAmbientAodMode

    val cal = Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
    val hour = cal.get(Calendar.HOUR)
    val minute = cal.get(Calendar.MINUTE)
    val second = cal.get(Calendar.SECOND)

    val hourAngle = (hour + minute / 60f) * 30f
    val minuteAngle = (minute + second / 60f) * 6f
    val secondAngle = second * 6f

    val dayNum = if (settings.usePersianDigits) {
        SolarHijriCalendar.toPersianDigits(solarDate.day.toString())
    } else {
        solarDate.day.toString()
    }

    val (timeMain, _) = formatLiveClock(
        currentTimeMillis,
        settings.usePersianDigits,
        settings.is24Hour,
        false,
        isAmbient
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 14.dp.toPx()

            // Outer dial hour ticks
            for (i in 0 until 60) {
                val angleRad = Math.toRadians((i * 6).toDouble())
                val isHour = i % 5 == 0
                val tickLen = if (isHour) 11.dp.toPx() else 4.5.dp.toPx()
                val tickWidth = if (isHour) 2.2.dp.toPx() else 1.dp.toPx()
                val tickColor = when {
                    isAmbient -> if (isHour) Color.White else Color(0xFF444444)
                    isHour -> theme.primaryColor
                    else -> theme.surfaceColor.copy(alpha = 0.8f)
                }

                val startX = (center.x + (radius - tickLen) * sin(angleRad)).toFloat()
                val startY = (center.y - (radius - tickLen) * cos(angleRad)).toFloat()
                val endX = (center.x + radius * sin(angleRad)).toFloat()
                val endY = (center.y - radius * cos(angleRad)).toFloat()

                drawLine(
                    color = tickColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = tickWidth,
                    cap = StrokeCap.Round
                )
            }

            // Left Subdial (Heart Rate & Day)
            val leftCenter = Offset(center.x - radius * 0.44f, center.y)
            val subRadius = radius * 0.22f
            drawCircle(
                color = if (isAmbient) Color(0xFF1A1A1A) else theme.surfaceColor.copy(alpha = 0.7f),
                radius = subRadius,
                center = leftCenter
            )
            drawCircle(
                color = if (isAmbient) Color(0xFF444444) else Color(0xFFFF3366).copy(alpha = 0.5f),
                radius = subRadius,
                center = leftCenter,
                style = Stroke(width = 1.dp.toPx())
            )

            // Hour Hand
            val hourRad = Math.toRadians((hourAngle - 90).toDouble())
            val hourHandLen = radius * 0.52f
            drawLine(
                color = if (isAmbient) Color.White else theme.textPrimary,
                start = center,
                end = Offset(
                    (center.x + hourHandLen * cos(hourRad)).toFloat(),
                    (center.y + hourHandLen * sin(hourRad)).toFloat()
                ),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Minute Hand
            val minRad = Math.toRadians((minuteAngle - 90).toDouble())
            val minHandLen = radius * 0.75f
            drawLine(
                color = if (isAmbient) Color.LightGray else theme.primaryColor,
                start = center,
                end = Offset(
                    (center.x + minHandLen * cos(minRad)).toFloat(),
                    (center.y + minHandLen * sin(minRad)).toFloat()
                ),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Second Hand (AOD hidden)
            if (!isAmbient) {
                val secRad = Math.toRadians((secondAngle - 90).toDouble())
                val secHandLen = radius * 0.85f
                drawLine(
                    color = theme.accentColor,
                    start = Offset(
                        (center.x - 12.dp.toPx() * cos(secRad)).toFloat(),
                        (center.y - 12.dp.toPx() * sin(secRad)).toFloat()
                    ),
                    end = Offset(
                        (center.x + secHandLen * cos(secRad)).toFloat(),
                        (center.y + secHandLen * sin(secRad)).toFloat()
                    ),
                    strokeWidth = 1.2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            drawCircle(
                color = if (isAmbient) Color.White else theme.accentColor,
                radius = 3.5.dp.toPx(),
                center = center
            )
        }

        // Persian Date Window at 3 o'clock position
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 36.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isAmbient) Color.Black else theme.surfaceColor)
                .border(
                    1.dp,
                    if (isAmbient) Color.White else theme.primaryColor,
                    RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 7.dp, vertical = 2.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = dayNum,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAmbient) Color.White else theme.textPrimary
                )
                Text(
                    text = solarDate.monthName,
                    fontSize = 8.5.sp,
                    color = if (isAmbient) Color.LightGray else theme.primaryColor
                )
            }
        }

        // Digital Time Window at 6 o'clock position
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isAmbient) Color.Transparent else theme.surfaceColor)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = timeMain,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAmbient) Color.White else theme.primaryColor
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 5. PROGRESS RINGS FACE
// ---------------------------------------------------------------------------
@Composable
fun ProgressRingsFace(
    settings: WatchCustomSettings,
    solarDate: SolarDate,
    occasions: List<PersianOccasion>,
    currentTimeMillis: Long
) {
    val theme = settings.theme
    val isAmbient = settings.isAmbientAodMode

    val (timeMain, timeSec) = formatLiveClock(
        currentTimeMillis,
        settings.usePersianDigits,
        settings.is24Hour,
        settings.showSeconds,
        isAmbient
    )

    val daysInMonth = SolarHijriCalendar.getDaysInMonth(solarDate.year, solarDate.month)
    val monthProgress = (solarDate.day.toFloat() / daysInMonth.toFloat()).coerceIn(0.01f, 1f)
    val seasonProgress = (solarDate.seasonProgressPercent / 100f).coerceIn(0.01f, 1f)
    val weekProgress = ((solarDate.dayOfWeek + 1).toFloat() / 7f).coerceIn(0.01f, 1f)

    val dayNum = if (settings.usePersianDigits) {
        SolarHijriCalendar.toPersianDigits(solarDate.day.toString())
    } else {
        solarDate.day.toString()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val strokeW = if (isAmbient) 2.dp.toPx() else 4.5.dp.toPx()
            val spacing = strokeW + 3.dp.toPx()

            val r1 = (size.minDimension / 2f) - strokeW
            val r2 = r1 - spacing
            val r3 = r2 - spacing

            fun drawRing(radius: Float, progress: Float, trackCol: Color, fillCol: Color) {
                drawCircle(color = trackCol, radius = radius, center = center, style = Stroke(width = strokeW))
                drawArc(
                    color = fillCol,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
            }

            drawRing(r1, seasonProgress, if (isAmbient) Color(0xFF1A1A1A) else theme.surfaceColor, if (isAmbient) Color.LightGray else theme.primaryColor)
            drawRing(r2, monthProgress, if (isAmbient) Color(0xFF151515) else theme.surfaceColor.copy(alpha = 0.6f), if (isAmbient) Color(0xFFAAAAAA) else theme.secondaryColor)
            drawRing(r3, weekProgress, if (isAmbient) Color(0xFF101010) else theme.surfaceColor.copy(alpha = 0.4f), if (isAmbient) Color(0xFF888888) else theme.accentColor)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Big Clock
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = timeMain,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isAmbient) Color.White else theme.primaryColor
                )
                if (timeSec != null) {
                    Text(text = timeSec, fontSize = 11.sp, color = theme.accentColor)
                }
            }

            Text(
                text = "${solarDate.dayOfWeekName} $dayNum",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = if (isAmbient) Color.White else theme.textPrimary
            )
            Text(
                text = solarDate.monthName,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isAmbient) Color.LightGray else theme.secondaryColor
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 6. WEAR OS TILE FACE
// ---------------------------------------------------------------------------
@Composable
fun WearOsTileFace(
    settings: WatchCustomSettings,
    solarDate: SolarDate,
    occasions: List<PersianOccasion>,
    currentTimeMillis: Long
) {
    val theme = settings.theme
    val isAmbient = settings.isAmbientAodMode

    val (timeMain, _) = formatLiveClock(
        currentTimeMillis,
        settings.usePersianDigits,
        settings.is24Hour,
        false,
        isAmbient
    )

    val daysInMonth = SolarHijriCalendar.getDaysInMonth(solarDate.year, solarDate.month)
    val firstDayJdn = SolarHijriCalendar.gregorianToJdn(
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH) + 1,
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    ) - (solarDate.day - 1)
    val firstDaySolar = SolarHijriCalendar.jdnToSolar(firstDayJdn)
    val startDayOfWeek = firstDaySolar.dayOfWeek

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 10.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val yearNum = if (settings.usePersianDigits) {
            SolarHijriCalendar.toPersianDigits(solarDate.year.toString())
        } else {
            solarDate.year.toString()
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${solarDate.monthName} $yearNum",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAmbient) Color.White else theme.primaryColor
            )
            Text(
                text = timeMain,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = theme.accentColor
            )
        }

        // 7 Day Headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SolarHijriCalendar.PERSIAN_WEEK_DAYS_SHORT.forEach { shortDay ->
                Text(
                    text = shortDay,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (shortDay == "ج") theme.accentColor else theme.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(18.dp)
                )
            }
        }

        // Calendar Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            var dayCounter = 1
            for (row in 0 until 5) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        if (cellIndex < startDayOfWeek || dayCounter > daysInMonth) {
                            Spacer(modifier = Modifier.width(18.dp))
                        } else {
                            val currentDay = dayCounter
                            val isToday = currentDay == solarDate.day
                            val formattedDay = if (settings.usePersianDigits) {
                                SolarHijriCalendar.toPersianDigits(currentDay.toString())
                            } else {
                                currentDay.toString()
                            }

                            Box(
                                modifier = Modifier
                                    .size(17.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isToday && isAmbient -> Color.White
                                            isToday -> theme.primaryColor
                                            else -> Color.Transparent
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = formattedDay,
                                    fontSize = 8.sp,
                                    fontWeight = if (isToday) FontWeight.Black else FontWeight.Normal,
                                    color = when {
                                        isToday && isAmbient -> Color.Black
                                        isToday -> Color.Black
                                        col == 6 -> theme.accentColor
                                        else -> if (isAmbient) Color.LightGray else theme.textPrimary
                                    },
                                    textAlign = TextAlign.Center
                                )
                            }
                            dayCounter++
                        }
                    }
                }
            }
        }

        // Bottom stats: Pulse & Battery
        if (!isAmbient) {
            val bpm = if (settings.usePersianDigits) {
                SolarHijriCalendar.toPersianDigits(settings.simulatedHeartRate.toString())
            } else {
                settings.simulatedHeartRate.toString()
            }
            val batt = if (settings.usePersianDigits) {
                SolarHijriCalendar.toPersianDigits("${settings.simulatedBattery}٪")
            } else {
                "${settings.simulatedBattery}%"
            }
            Text(
                text = "♥ $bpm bpm  •  $batt شارژ",
                fontSize = 8.5.sp,
                color = theme.secondaryColor,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}
