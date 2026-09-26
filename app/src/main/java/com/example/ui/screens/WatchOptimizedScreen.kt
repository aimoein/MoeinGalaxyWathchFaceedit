package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.SolarHijriCalendar
import com.example.calendar.SolarOccasions
import com.example.data.PreferencesManager
import com.example.model.WatchCustomSettings
import com.example.model.WatchFaceLayout
import com.example.model.WatchThemePalette
import com.example.model.WidgetArrangement
import com.example.model.WidgetColorStyle
import com.example.model.WidgetDisplayStyle
import com.example.model.WidgetSize
import com.example.sensor.RealSensorManager
import com.example.sensor.RealWatchTelemetry
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianMint
import com.example.ui.theme.PersianTurquoise
import com.example.util.GalaxyWearableHelper
import com.example.widget.PersianDateWidgetProvider
import kotlinx.coroutines.delay
import java.util.Calendar

/**
 * Compact, tactile, circular-optimized Watch UI for Galaxy Watch 4 (Wear OS)
 * Featuring 50% Time & Date screen space occupancy, safe sensor permissions,
 * and seamless watch face switching.
 */
@Composable
fun WatchOptimizedScreen() {
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }
    var settings by remember { mutableStateOf(prefsManager.loadSettings()) }

    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val sensorManager = remember { RealSensorManager(context) }
    var telemetry by remember { mutableStateOf(sensorManager.getSnapshot()) }

    // Safe permission request launcher for Galaxy Watch 4
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        sensorManager.startListening()
        telemetry = sensorManager.getSnapshot()
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        if (!sensorManager.hasHeartRatePermission()) {
            permissionsToRequest.add(Manifest.permission.BODY_SENSORS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !sensorManager.hasStepPermission()) {
            permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        if (permissionsToRequest.isNotEmpty()) {
            try {
                permissionLauncher.launch(permissionsToRequest.toTypedArray())
            } catch (_: Throwable) {}
        }
    }

    DisposableEffect(Unit) {
        sensorManager.startListening()
        sensorManager.onDataChanged = {
            telemetry = sensorManager.getSnapshot()
        }
        onDispose {
            sensorManager.stopListening()
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            telemetry = sensorManager.getSnapshot()
            delay(1000L)
        }
    }

    val cal = remember(currentTimeMillis) {
        Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
    }

    val solarDate = remember(cal) {
        SolarHijriCalendar.fromGregorian(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    val occasions = remember(solarDate) {
        SolarOccasions.getOccasionsFor(solarDate.month, solarDate.day)
    }

    fun updateSettings(newSettings: WatchCustomSettings) {
        settings = newSettings
        try {
            prefsManager.saveSettings(newSettings)
            PersianDateWidgetProvider.updateAllWidgets(context)
            val intent = Intent("com.example.wear.SETTINGS_CHANGED")
            context.sendBroadcast(intent)
        } catch (_: Throwable) {}
    }

    // 0: Main Dashboard, 1: Choose Layout, 2: Choose Theme, 3: Sensors
    var activeSubPage by remember { mutableIntStateOf(0) }

    // Intercept back gesture on Galaxy Watch
    BackHandler(enabled = activeSubPage != 0) {
        activeSubPage = 0
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        when (activeSubPage) {
            0 -> WatchMainDashboard(
                settings = settings,
                solarDate = solarDate,
                telemetry = telemetry,
                currentTimeMillis = currentTimeMillis,
                occasionsTitle = occasions.firstOrNull()?.title,
                onRequestPermissions = {
                    val perms = mutableListOf<String>()
                    perms.add(Manifest.permission.BODY_SENSORS)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        perms.add(Manifest.permission.ACTIVITY_RECOGNITION)
                    }
                    permissionLauncher.launch(perms.toTypedArray())
                },
                onSetAsWatchFace = {
                    GalaxyWearableHelper.setAsWatchFace(context)
                },
                onToggleDigits = {
                    val updated = settings.copy(usePersianDigits = !settings.usePersianDigits)
                    updateSettings(updated)
                },
                onOpenLayouts = { activeSubPage = 1 },
                onOpenThemes = { activeSubPage = 2 },
                onOpenSensors = { activeSubPage = 3 },
                onOpenWidgetCustomizer = { activeSubPage = 4 }
            )

            1 -> WatchLayoutPicker(
                currentLayout = settings.layout,
                onSelectLayout = { newLayout ->
                    updateSettings(settings.copy(layout = newLayout))
                    Toast.makeText(context, "طرح «${newLayout.titlePersian}» اعمال شد", Toast.LENGTH_SHORT).show()
                    activeSubPage = 0
                },
                onBack = { activeSubPage = 0 }
            )

            2 -> WatchThemePicker(
                currentTheme = settings.theme,
                onSelectTheme = { newTheme ->
                    updateSettings(settings.copy(theme = newTheme))
                    Toast.makeText(context, "رنگ «${newTheme.titlePersian}» اعمال شد", Toast.LENGTH_SHORT).show()
                    activeSubPage = 0
                },
                onBack = { activeSubPage = 0 }
            )

            3 -> WatchLiveSensorsPage(
                telemetry = telemetry,
                onRequestPermissions = {
                    val perms = mutableListOf<String>()
                    perms.add(Manifest.permission.BODY_SENSORS)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        perms.add(Manifest.permission.ACTIVITY_RECOGNITION)
                    }
                    permissionLauncher.launch(perms.toTypedArray())
                },
                onBack = { activeSubPage = 0 }
            )

            4 -> WatchWidgetsCustomizerScreen(
                settings = settings,
                onUpdateSettings = { newSettings ->
                    updateSettings(newSettings)
                    Toast.makeText(context, "تنظیمات ویجت‌ها اعمال شد", Toast.LENGTH_SHORT).show()
                },
                onBack = { activeSubPage = 0 }
            )
        }
    }
}

/**
 * PAGE 0: MAIN DASHBOARD (50% SCREEN OCCUPIED BY CLOCK & SOLAR DATE)
 */
@Composable
private fun WatchMainDashboard(
    settings: WatchCustomSettings,
    solarDate: com.example.calendar.SolarDate,
    telemetry: RealWatchTelemetry,
    currentTimeMillis: Long,
    occasionsTitle: String?,
    onRequestPermissions: () -> Unit,
    onSetAsWatchFace: () -> Unit,
    onToggleDigits: () -> Unit,
    onOpenLayouts: () -> Unit,
    onOpenThemes: () -> Unit,
    onOpenSensors: () -> Unit,
    onOpenWidgetCustomizer: () -> Unit
) {
    val cal = remember(currentTimeMillis) {
        Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
    }
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val min = cal.get(Calendar.MINUTE)
    val sec = cal.get(Calendar.SECOND)

    val timeRaw = String.format("%02d:%02d", hour, min)
    val timeFormatted = if (settings.usePersianDigits) SolarHijriCalendar.toPersianDigits(timeRaw) else timeRaw

    val secRaw = String.format(":%02d", sec)
    val secFormatted = if (settings.usePersianDigits) SolarHijriCalendar.toPersianDigits(secRaw) else secRaw

    val fullSolarDate = "${solarDate.day} ${solarDate.monthName} ${solarDate.year}"
    val solarDateFormatted = if (settings.usePersianDigits) SolarHijriCalendar.toPersianDigits(fullSolarDate) else fullSolarDate

    val batteryText = if (settings.usePersianDigits) {
        SolarHijriCalendar.toPersianDigits("${telemetry.batteryPercent}٪")
    } else "${telemetry.batteryPercent}%"

    val hrText = if (settings.usePersianDigits) {
        SolarHijriCalendar.toPersianDigits("${telemetry.heartRateBpm}")
    } else "${telemetry.heartRateBpm}"

    val stepsText = if (settings.usePersianDigits) {
        SolarHijriCalendar.toPersianDigits("${telemetry.stepCount}")
    } else "${telemetry.stepCount}"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 18.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. TOP LIVE BATTERY COMPLICATION (LARGER)
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF131D2D))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(
                    Icons.Default.BatteryFull,
                    contentDescription = null,
                    tint = PersianMint,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = batteryText,
                    fontSize = 13.5.sp,
                    color = PersianMint,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 2. CENTER ZONE: TIME & SOLAR DATE OCCUPYING 50% OF WATCH SCREEN
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleDigits() }
                    .padding(vertical = 4.dp)
            ) {
                // Giant Clock
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = timeFormatted,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = (-1.5).sp
                    )
                    Text(
                        text = secFormatted,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PersianTurquoise
                    )
                }

                // Divider line
                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .height(2.5.dp)
                        .background(Color(0xFF334155))
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Giant Solar Date (Day Month Year) in Persian Gold
                Text(
                    text = solarDateFormatted,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Black,
                    color = PersianGold
                )

                // Day of Week in Persian Mint
                Text(
                    text = solarDate.dayOfWeekName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersianMint
                )

                if (!occasionsTitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = occasionsTitle,
                        fontSize = 10.5.sp,
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }

        // 3. SENSOR CHIPS (PULSE & STEPS - LARGER & TOUCH-FRIENDLY)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Heart Rate
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF261219))
                        .clickable { onOpenSensors() }
                        .padding(horizontal = 13.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFFF3366),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "$hrText نبض",
                        fontSize = 12.5.sp,
                        color = Color(0xFFFFCCD8),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Steps
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F291E))
                        .clickable { onOpenSensors() }
                        .padding(horizontal = 13.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        Icons.Default.DirectionsWalk,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "$stepsText قدم",
                        fontSize = 12.5.sp,
                        color = Color(0xFFD1FAE5),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Sensor permission prompt if not yet granted
        if (!telemetry.hasSensorPermission) {
            item {
                Button(
                    onClick = onRequestPermissions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF332000)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = PersianGold, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("اجازه دسترسی به سنسور نبض و گام", fontSize = 10.5.sp, color = PersianGold)
                }
            }
        }

        // 4. BIG ACTION: SET AS WATCH FACE
        item {
            Button(
                onClick = onSetAsWatchFace,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PersianTurquoise,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(22.dp)
            ) {
                Icon(Icons.Default.Watch, contentDescription = null, modifier = Modifier.size(17.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("تنظیم به عنوان واچ‌فیس اصلی", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // 5. QUICK MENU BUTTONS
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Change Layout Button
                Button(
                    onClick = onOpenLayouts,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF161F2E)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(Icons.Default.ViewCarousel, contentDescription = null, tint = PersianMint, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تغییر طرح (${settings.layout.titlePersian})", fontSize = 11.sp, color = Color.White)
                }

                // Change Theme Button
                Button(
                    onClick = onOpenThemes,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF161F2E)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = PersianGold, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تغییر رنگ‌بندی و تم", fontSize = 11.sp, color = Color.White)
                }

                // Customize Widgets Button (اندازه، رنگ، جایگاه، استایل)
                Button(
                    onClick = onOpenWidgetCustomizer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2A40)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = PersianMint, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("سفارشی‌سازی ویجت‌ها (اندازه، رنگ، چیدمان)", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                // Live Sensors Detail Button
                Button(
                    onClick = onOpenSensors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF161F2E)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("جزئیات سنسورهای واقعی ساعت", fontSize = 11.sp, color = Color.White)
                }
            }
        }

        // 6. Signature
        item {
            Text(
                text = "طراحی : دکتر خسروی • 1tw.ir",
                fontSize = 9.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * PAGE 1: WATCH LAYOUT PICKER
 */
@Composable
private fun WatchLayoutPicker(
    currentLayout: WatchFaceLayout,
    onSelectLayout: (WatchFaceLayout) -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "بازگشت", tint = Color.White)
                }
                Text("انتخاب طرح واچ‌فیس", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PersianMint)
                Spacer(modifier = Modifier.size(36.dp))
            }
        }

        items(WatchFaceLayout.values().toList()) { layout ->
            val isSelected = layout == currentLayout
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectLayout(layout) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF1B2A40) else Color(0xFF12151D)
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(PersianMint, PersianTurquoise))
                ) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = layout.titlePersian,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) PersianMint else Color.White
                        )
                        Text(
                            text = layout.description,
                            fontSize = 9.5.sp,
                            color = Color(0xFF94A3B8),
                            maxLines = 1
                        )
                    }
                    if (isSelected) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = PersianMint, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

/**
 * PAGE 2: WATCH THEME PICKER
 */
@Composable
private fun WatchThemePicker(
    currentTheme: WatchThemePalette,
    onSelectTheme: (WatchThemePalette) -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "بازگشت", tint = Color.White)
                }
                Text("انتخاب رنگ‌بندی", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PersianGold)
                Spacer(modifier = Modifier.size(36.dp))
            }
        }

        items(WatchThemePalette.values().toList()) { theme ->
            val isSelected = theme == currentTheme
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectTheme(theme) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF222838) else Color(0xFF12151D)
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(theme.primaryColor, theme.secondaryColor))
                ) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(theme.primaryColor)
                        )
                        Text(
                            text = theme.titlePersian,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) theme.primaryColor else Color.White
                        )
                    }
                    if (isSelected) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = theme.primaryColor, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

/**
 * PAGE 3: WATCH REAL SENSORS DIAGNOSTIC
 */
@Composable
private fun WatchLiveSensorsPage(
    telemetry: RealWatchTelemetry,
    onRequestPermissions: () -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "بازگشت", tint = Color.White)
                }
                Text("داده‌های زنده سنسورها", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                Spacer(modifier = Modifier.size(36.dp))
            }
        }

        if (!telemetry.hasSensorPermission) {
            item {
                Button(
                    onClick = onRequestPermissions,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF431407)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("درخواست مجوز سنسورهای واقعی", fontSize = 11.sp, color = PersianGold)
                }
            }
        }

        // Battery
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2D))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.BatteryFull, contentDescription = null, tint = PersianMint)
                        Text("باتری گلکسی واچ", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Text("${telemetry.batteryPercent}٪", fontSize = 14.sp, color = PersianMint, fontWeight = FontWeight.Black)
                }
            }
        }

        // Heart Rate
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF261219))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFFF3366))
                        Text("ضربان قلب واقعی", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Text("${telemetry.heartRateBpm} BPM", fontSize = 14.sp, color = Color(0xFFFF3366), fontWeight = FontWeight.Black)
                }
            }
        }

        // Steps
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F291E))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = Color(0xFF10B981))
                        Text("گام‌شمار واقعی", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Text("${telemetry.stepCount} قدم", fontSize = 14.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Black)
                }
            }
        }

        // Calories
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1C0E))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF9900))
                        Text("کالری فعال", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Text("${telemetry.caloriesKcal} KCal", fontSize = 14.sp, color = Color(0xFFFF9900), fontWeight = FontWeight.Black)
                }
            }
        }

        item {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E283C)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("بازگشت به صفحه اصلی", fontSize = 11.5.sp, color = Color.White)
            }
        }
    }
}

/**
 * PAGE 4: WATCH WIDGETS CUSTOMIZER (سفارشی‌سازی کامل اندازه، رنگ، جایگاه و نحوه نمایش ویجت‌ها)
 */
@Composable
private fun WatchWidgetsCustomizerScreen(
    settings: WatchCustomSettings,
    onUpdateSettings: (WatchCustomSettings) -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "بازگشت", tint = Color.White)
                }
                Text("سفارشی‌سازی ویجت‌ها", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PersianMint)
                Spacer(modifier = Modifier.size(36.dp))
            }
        }

        // =================================================================
        // SECTION 1: ANDAZEH (WIDGET SIZE)
        // =================================================================
        item {
            Text(
                text = "۱. اندازه ویجت‌ها",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = PersianGold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        items(WidgetSize.values().toList()) { sizeItem ->
            val isSelected = settings.widgetSize == sizeItem
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUpdateSettings(settings.copy(widgetSize = sizeItem)) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF1A3344) else Color(0xFF131720)
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(PersianMint, PersianTurquoise))
                ) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = sizeItem.titlePersian,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PersianMint else Color.White
                    )
                    if (isSelected) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = PersianMint, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // =================================================================
        // SECTION 2: RANG-BANDI (WIDGET COLOR STYLE)
        // =================================================================
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "۲. رنگ‌بندی ویجت‌ها",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = PersianGold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        items(WidgetColorStyle.values().toList()) { colorItem ->
            val isSelected = settings.widgetColorStyle == colorItem
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUpdateSettings(settings.copy(widgetColorStyle = colorItem)) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF232D3F) else Color(0xFF131720)
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(PersianGold, PersianMint))
                ) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(
                                    when (colorItem) {
                                        WidgetColorStyle.NEON_MULTICOLOR -> PersianTurquoise
                                        WidgetColorStyle.PURE_GOLD -> PersianGold
                                        WidgetColorStyle.PERSIAN_TURQUOISE -> PersianTurquoise
                                        WidgetColorStyle.MINIMAL_WHITE -> Color.White
                                        WidgetColorStyle.THEME_MATCHED -> settings.theme.primaryColor
                                    }
                                )
                        )
                        Text(
                            text = colorItem.titlePersian,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) PersianGold else Color.White
                        )
                    }
                    if (isSelected) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = PersianGold, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // =================================================================
        // SECTION 3: JAYGAH VA CHIDEMAN (WIDGET PLACEMENT / ARRANGEMENT)
        // =================================================================
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "۳. جایگاه و نحوه چیدمان",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = PersianGold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        items(WidgetArrangement.values().toList()) { arrangeItem ->
            val isSelected = settings.widgetArrangement == arrangeItem
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUpdateSettings(settings.copy(widgetArrangement = arrangeItem)) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF192A3D) else Color(0xFF131720)
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(PersianMint, PersianTurquoise))
                ) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = arrangeItem.titlePersian,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PersianMint else Color.White
                    )
                    if (isSelected) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = PersianMint, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // =================================================================
        // SECTION 4: NAHVEH NAMAYESH (DISPLAY STYLE)
        // =================================================================
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "۴. استایل و فرم نمایش",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = PersianGold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        items(WidgetDisplayStyle.values().toList()) { styleItem ->
            val isSelected = settings.widgetDisplayStyle == styleItem
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUpdateSettings(settings.copy(widgetDisplayStyle = styleItem)) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF1C2433) else Color(0xFF131720)
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(PersianTurquoise, PersianMint))
                ) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = styleItem.titlePersian,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PersianTurquoise else Color.White
                    )
                    if (isSelected) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = PersianTurquoise, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // =================================================================
        // SECTION 5: NAMAYESH / ADAME NAMAYESH (VISIBILITY TOGGLES)
        // =================================================================
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "۵. فعال/غیرفعال‌سازی هر ویجت",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = PersianGold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        // Toggle Battery
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onUpdateSettings(settings.copy(showBatteryWidget = !settings.showBatteryWidget))
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (settings.showBatteryWidget) Color(0xFF142433) else Color(0xFF181818)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.BatteryFull, contentDescription = null, tint = PersianMint, modifier = Modifier.size(16.dp))
                        Text("ویجت شارژ باتری", fontSize = 11.sp, color = Color.White)
                    }
                    Text(
                        text = if (settings.showBatteryWidget) "روشن ✓" else "خاموش",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (settings.showBatteryWidget) PersianMint else Color.Gray
                    )
                }
            }
        }

        // Toggle Heart Rate
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onUpdateSettings(settings.copy(showHeartRateWidget = !settings.showHeartRateWidget))
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (settings.showHeartRateWidget) Color(0xFF2D161F) else Color(0xFF181818)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFFF3366), modifier = Modifier.size(16.dp))
                        Text("ویجت ضربان قلب", fontSize = 11.sp, color = Color.White)
                    }
                    Text(
                        text = if (settings.showHeartRateWidget) "روشن ✓" else "خاموش",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (settings.showHeartRateWidget) Color(0xFFFF3366) else Color.Gray
                    )
                }
            }
        }

        // Toggle Steps
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onUpdateSettings(settings.copy(showStepsWidget = !settings.showStepsWidget))
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (settings.showStepsWidget) Color(0xFF132A1C) else Color(0xFF181818)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                        Text("ویجت گام‌شمار", fontSize = 11.sp, color = Color.White)
                    }
                    Text(
                        text = if (settings.showStepsWidget) "روشن ✓" else "خاموش",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (settings.showStepsWidget) Color(0xFF10B981) else Color.Gray
                    )
                }
            }
        }

        // Bottom Back Button
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PersianTurquoise, contentColor = Color.Black),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("تایید و بازگشت", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
