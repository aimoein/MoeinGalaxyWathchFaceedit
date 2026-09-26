package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.SolarHijriCalendar
import com.example.calendar.SolarOccasions
import com.example.data.PreferencesManager
import com.example.model.GalaxyWatchDevice
import com.example.model.WatchComplicationType
import com.example.model.WatchCustomSettings
import com.example.model.WatchFaceLayout
import com.example.model.WatchFontType
import com.example.model.WatchThemePalette
import com.example.ui.components.AboutDoctorKhosraviDialog
import com.example.ui.components.GalaxyWatchFrame
import com.example.ui.components.GalaxyWatchInstallGuideDialog
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianMint
import com.example.ui.theme.PersianTurquoise
import com.example.widget.PersianDateWidgetProvider
import kotlinx.coroutines.delay
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchStudioScreen() {
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }

    var settings by remember { mutableStateOf(prefsManager.loadSettings()) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showInstallGuideDialog by remember { mutableStateOf(false) }

    // Date navigation offset from today
    var dayOffset by remember { mutableIntStateOf(0) }

    // Real-time ticking clock
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val activeCalendar = remember(dayOffset, currentTimeMillis) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = currentTimeMillis
        if (dayOffset != 0) {
            cal.add(Calendar.DAY_OF_YEAR, dayOffset)
        }
        cal
    }

    val solarDate = remember(activeCalendar) {
        SolarHijriCalendar.fromGregorian(
            activeCalendar.get(Calendar.YEAR),
            activeCalendar.get(Calendar.MONTH) + 1,
            activeCalendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val gregorianDate = remember(activeCalendar) {
        SolarHijriCalendar.getGregorianDate(activeCalendar.time)
    }

    val lunarDate = remember(activeCalendar) {
        SolarHijriCalendar.getLunarHijriDate(activeCalendar.time)
    }

    val occasions = remember(solarDate) {
        SolarOccasions.getOccasionsFor(solarDate.month, solarDate.day)
    }

    fun updateSettings(newSettings: WatchCustomSettings) {
        settings = newSettings
        prefsManager.saveSettings(newSettings)
        PersianDateWidgetProvider.updateAllWidgets(context)
        try {
            val intent = Intent("com.example.wear.SETTINGS_CHANGED")
            context.sendBroadcast(intent)
        } catch (_: Exception) {}
    }

    val tabTitles = listOf(
        "سفارشی‌سازی ویجت‌ها",
        "طرح و واچ‌فیس",
        "ساعت و تقویم",
        "مدل ساعت",
        "پوسته و رنگ",
        "راهنمای نصب ساعت",
        "مصرف باتری",
        "درباره ما"
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "تقویم هوشمند گلکسی واچ",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "ساعت درشت، تقویم شمسی و سنسورهای واچ",
                            fontSize = 11.sp,
                            color = PersianMint
                        )
                    }
                },
                actions = {
                    // Quick Install Guide Action Button
                    IconButton(
                        onClick = { showInstallGuideDialog = true },
                        modifier = Modifier.testTag("install_guide_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "چگونه روی ساعت نصب کنم؟",
                            tint = PersianGold
                        )
                    }

                    // Direct Watch Face Launcher
                    IconButton(
                        onClick = {
                            val success = com.example.util.GalaxyWearableHelper.setAsWatchFace(context)
                            if (!success) {
                                com.example.util.GalaxyWearableHelper.openGalaxyWearable(context)
                            }
                        },
                        modifier = Modifier.testTag("set_watch_face_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Watch,
                            contentDescription = "تنظیم واچ‌فیس",
                            tint = PersianTurquoise
                        )
                    }

                    // Quick Ambient AOD Toggle Button
                    IconButton(
                        onClick = {
                            updateSettings(settings.copy(isAmbientAodMode = !settings.isAmbientAodMode))
                        },
                        modifier = Modifier.testTag("aod_toggle_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = "حالت کم‌مصرف AOD",
                            tint = if (settings.isAmbientAodMode) PersianGold else Color(0xFF94A3B8)
                        )
                    }

                    // About Doctor Khosravi Action
                    IconButton(
                        onClick = { showAboutDialog = true },
                        modifier = Modifier.testTag("about_action_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "درباره طراحی دکتر خسروی",
                            tint = PersianMint
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF090D14)
                )
            )
        },
        containerColor = Color(0xFF090D14)
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // -------------------------------------------------------------
            // SECTION 1: GALAXY WATCH INTERACTIVE SIMULATOR FRAME
            // -------------------------------------------------------------
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Watch Frame Component with Live Clock & Date
                    GalaxyWatchFrame(
                        settings = settings,
                        solarDate = solarDate,
                        gregorianDate = gregorianDate,
                        lunarDate = lunarDate,
                        occasions = occasions,
                        currentTimeMillis = currentTimeMillis,
                        onRotateBezelClockwise = {
                            dayOffset++
                        },
                        onRotateBezelCounterClockwise = {
                            dayOffset--
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Date Navigator & Today reset bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF111724))
                            .border(1.dp, Color(0xFF1E283C), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Previous Day Button
                        IconButton(
                            onClick = { dayOffset-- },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("prev_day_button")
                        ) {
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = "روز قبل",
                                tint = PersianMint
                            )
                        }

                        // Current Displayed Date Label
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = solarDate.formatLong(settings.usePersianDigits),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (dayOffset != 0) {
                                Text(
                                    text = if (dayOffset > 0) "+$dayOffset روز بعد" else "$dayOffset روز قبل",
                                    fontSize = 10.sp,
                                    color = PersianGold
                                )
                            }
                        }

                        // Next Day Button
                        IconButton(
                            onClick = { dayOffset++ },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("next_day_button")
                        ) {
                            Icon(
                                Icons.Default.ChevronLeft,
                                contentDescription = "روز بعد",
                                tint = PersianMint
                            )
                        }
                    }

                    // Reset to Today if offset changed
                    if (dayOffset != 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedButton(
                            onClick = { dayOffset = 0 },
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("today_reset_button"),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Today, contentDescription = null, modifier = Modifier.size(14.dp), tint = PersianMint)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("بازگشت به امروز", fontSize = 11.sp, color = PersianMint)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ACTION BUTTONS: ساعت درشت و راهنمای نصب
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showInstallGuideDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("quick_install_guide_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PersianGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("نصب روی گلکسی واچ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                updateSettings(settings.copy(clockSizeLarge = !settings.clockSizeLarge))
                            },
                            modifier = Modifier
                                .weight(0.9f)
                                .height(44.dp)
                                .testTag("toggle_clock_size_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(listOf(PersianMint, PersianTurquoise))
                            )
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = PersianMint,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (settings.clockSizeLarge) "ساعت بسیار درشت" else "ساعت اندازه عادی",
                                fontSize = 11.sp,
                                color = PersianMint,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Direct Watch UI Mode button
                    Button(
                        onClick = {
                            val success = com.example.util.GalaxyWearableHelper.setAsWatchFace(context)
                            if (!success) {
                                com.example.util.GalaxyWearableHelper.openGalaxyWearable(context)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("direct_set_face_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PersianTurquoise,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Watch, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تنظیم فوری واچ‌فیس روی ساعت (Set Face)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 2: DOCTOR KHOSRAVI DESIGN BANNER (شناسنامه اثر)
            // -------------------------------------------------------------
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { showAboutDialog = true }
                        .testTag("doctor_khosravi_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2D)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(PersianMint.copy(alpha = 0.8f), PersianTurquoise.copy(alpha = 0.8f))
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PersianMint.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PersianMint, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(
                                    text = "طراحی : دکتر خسروی",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PersianGold
                                )
                                Text(
                                    text = "09123371764 | https://1tw.ir",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PersianMint
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:09123371764"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "تماس", modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تماس", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 3: TABS FOR DEEP CUSTOMIZATION
            // -------------------------------------------------------------
            item {
                Spacer(modifier = Modifier.height(8.dp))
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF0C101A),
                    contentColor = PersianMint,
                    edgePadding = 16.dp,
                    divider = {}
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) PersianMint else Color(0xFF94A3B8)
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // -------------------------------------------------------------
            // TAB 0: COMPLICATIONS & SENSORS CUSTOMIZER (سفارشی‌سازی ویجت‌ها)
            // -------------------------------------------------------------
            if (selectedTab == 0) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "سفارشی‌سازی ویجت‌ها و سنسورهای واچ‌فیس",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "ساعت درشت در مرکز قرار دارد. می‌توانید هر ویجتی که می‌خواهید (ضربان قلب، گام‌شمار، شارژ باتری و...) را به ۴ اسلات این واچ‌فیس اضافه کنید:",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            lineHeight = 18.sp
                        )

                        // 1. Clock Size Switch (ساعت درشت)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF131D2D))
                                .border(1.dp, PersianMint.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "نمایش ساعت بصورت فوق‌العاده درشت",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PersianMint
                                )
                                Text(
                                    text = "ساعت بزرگ در مرکز قرار گرفته و تاریخ شمسی بصورت ظریف‌تر و شکیل در زیر آن نمایش داده می‌شود",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Switch(
                                checked = settings.clockSizeLarge,
                                onCheckedChange = {
                                    updateSettings(settings.copy(clockSizeLarge = it))
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = PersianMint)
                            )
                        }

                        // 2. Complication Slot Selectors
                        Text(
                            text = "انتخاب ویجت برای هر یک از ۴ جهت ساعت:",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PersianGold
                        )

                        val complicationList = listOf(
                            WatchComplicationType.BATTERY,
                            WatchComplicationType.HEART_RATE,
                            WatchComplicationType.STEP_COUNTER,
                            WatchComplicationType.CALORIES,
                            WatchComplicationType.WEATHER,
                            WatchComplicationType.OCCASIONS,
                            WatchComplicationType.SEASON_PROGRESS,
                            WatchComplicationType.GREGORIAN_DATE,
                            WatchComplicationType.LUNAR_DATE,
                            WatchComplicationType.NONE
                        )

                        // Slot 1: Top Slot
                        SlotSelectorCard(
                            slotTitle = "ویجت بالا (Top Slot)",
                            currentType = settings.topSlotComplication,
                            options = complicationList,
                            onSelect = { updateSettings(settings.copy(topSlotComplication = it)) }
                        )

                        // Slot 2: Left Slot
                        SlotSelectorCard(
                            slotTitle = "ویجت چپ (Left Slot)",
                            currentType = settings.leftSlotComplication,
                            options = complicationList,
                            onSelect = { updateSettings(settings.copy(leftSlotComplication = it)) }
                        )

                        // Slot 3: Right Slot
                        SlotSelectorCard(
                            slotTitle = "ویجت راست (Right Slot)",
                            currentType = settings.rightSlotComplication,
                            options = complicationList,
                            onSelect = { updateSettings(settings.copy(rightSlotComplication = it)) }
                        )

                        // Slot 4: Bottom Slot
                        SlotSelectorCard(
                            slotTitle = "ویجت پایین (Bottom Slot)",
                            currentType = settings.bottomSlotComplication,
                            options = complicationList,
                            onSelect = { updateSettings(settings.copy(bottomSlotComplication = it)) }
                        )

                        HorizontalDivider(color = Color(0xFF1E283C))

                        // 3. Quick Presets
                        Text(
                            text = "پیکربندی‌های سریع با یک کلیک:",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PersianMint
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Fitness Preset
                            Button(
                                onClick = {
                                    updateSettings(
                                        settings.copy(
                                            topSlotComplication = WatchComplicationType.BATTERY,
                                            leftSlotComplication = WatchComplicationType.HEART_RATE,
                                            rightSlotComplication = WatchComplicationType.STEP_COUNTER,
                                            bottomSlotComplication = WatchComplicationType.CALORIES,
                                            layout = WatchFaceLayout.BIG_CLOCK_MODULAR
                                        )
                                    )
                                    Toast.makeText(context, "پیکربندی ورزشی و سنسورها فعال شد", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF162032)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("ورزشی و سلامت", fontSize = 11.sp, color = PersianMint)
                            }

                            // Calendar Preset
                            Button(
                                onClick = {
                                    updateSettings(
                                        settings.copy(
                                            topSlotComplication = WatchComplicationType.WEATHER,
                                            leftSlotComplication = WatchComplicationType.GREGORIAN_DATE,
                                            rightSlotComplication = WatchComplicationType.LUNAR_DATE,
                                            bottomSlotComplication = WatchComplicationType.OCCASIONS,
                                            layout = WatchFaceLayout.BIG_CLOCK_MODULAR
                                        )
                                    )
                                    Toast.makeText(context, "پیکربندی جامع تقویم فعال شد", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF162032)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("تقویم و روزمره", fontSize = 11.sp, color = PersianGold)
                            }
                        }

                        HorizontalDivider(color = Color(0xFF1E283C))

                        // 4. Interactive Sensor Simulation Controls
                        Text(
                            text = "تست و شبیه‌سازی زنده سنسورهای گلکسی واچ:",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )

                        // Heart Rate Slider
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF10141E))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFFF3366), modifier = Modifier.size(16.dp))
                                        Text("سنسور ضربان قلب", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    Text("${settings.simulatedHeartRate} BPM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF3366))
                                }
                                Slider(
                                    value = settings.simulatedHeartRate.toFloat(),
                                    onValueChange = { updateSettings(settings.copy(simulatedHeartRate = it.toInt())) },
                                    valueRange = 55f..160f,
                                    colors = SliderDefaults.colors(thumbColor = Color(0xFFFF3366), activeTrackColor = Color(0xFFFF3366))
                                )
                            }
                        }

                        // Step Counter Slider
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF10141E))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                        Text("گام‌شمار و تحرک روزانه", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    Text("${settings.simulatedSteps} قدم", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                }
                                Slider(
                                    value = settings.simulatedSteps.toFloat(),
                                    onValueChange = { updateSettings(settings.copy(simulatedSteps = it.toInt())) },
                                    valueRange = 500f..15000f,
                                    colors = SliderDefaults.colors(thumbColor = Color(0xFF10B981), activeTrackColor = Color(0xFF10B981))
                                )
                            }
                        }

                        // Battery Level Slider
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF10141E))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.BatteryFull, contentDescription = null, tint = PersianMint, modifier = Modifier.size(16.dp))
                                        Text("میزان شارژ باتری گلکسی واچ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    Text("${settings.simulatedBattery}٪", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PersianMint)
                                }
                                Slider(
                                    value = settings.simulatedBattery.toFloat(),
                                    onValueChange = { updateSettings(settings.copy(simulatedBattery = it.toInt())) },
                                    valueRange = 5f..100f,
                                    colors = SliderDefaults.colors(thumbColor = PersianMint, activeTrackColor = PersianMint)
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // TAB 1: WATCH FACE LAYOUTS (طرح و واچ‌فیس)
            // -------------------------------------------------------------
            if (selectedTab == 1) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "استایل و چیدمان صفحه تقویم و ساعت",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "تمامی استایل‌ها به ساعت زنده، تاریخ شمسی و سنسورهای سلامتی مجهز هستند:",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )

                        WatchFaceLayout.values().forEach { layoutItem ->
                            val isSelected = settings.layout == layoutItem
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        updateSettings(settings.copy(layout = layoutItem))
                                    }
                                    .testTag("layout_item_${layoutItem.id}"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF162032) else Color(0xFF10141E)
                                ),
                                border = if (isSelected) {
                                    CardDefaults.outlinedCardBorder().copy(
                                        brush = Brush.horizontalGradient(
                                            listOf(PersianMint, PersianTurquoise)
                                        )
                                    )
                                } else {
                                    CardDefaults.outlinedCardBorder().copy(
                                        brush = Brush.horizontalGradient(
                                            listOf(Color(0xFF222B3D), Color(0xFF181F2C))
                                        )
                                    )
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = layoutItem.titlePersian,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) PersianMint else Color.White
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = layoutItem.description,
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8),
                                            lineHeight = 16.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "انتخاب شده",
                                            tint = PersianMint,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // TAB 2: TIME & CALENDAR SETTINGS (ساعت و تقویم)
            // -------------------------------------------------------------
            if (selectedTab == 2) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "تنظیمات ساعت زنده و اطلاعات تقویم",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Show Live Clock Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF10141E))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "نمایش ساعت زنده (دیجیتال / عقربه‌ای)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "نمایش دقیق ساعت و دقیقه و ثانیه‌شمار زنده روی واچ‌فیس",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Switch(
                                checked = settings.showClock,
                                onCheckedChange = {
                                    updateSettings(settings.copy(showClock = it))
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = PersianMint)
                            )
                        }

                        // 24-Hour vs 12-Hour format
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF10141E))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "فرمت ۲۴ ساعته (۲۱:۳۰)",
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "در صورت خاموش بودن، ساعت به فرمت ۱۲ ساعته نمایش داده می‌شود",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Switch(
                                checked = settings.is24Hour,
                                onCheckedChange = {
                                    updateSettings(settings.copy(is24Hour = it))
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = PersianMint)
                            )
                        }

                        // Show Seconds Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF10141E))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "نمایش ثانیه‌شمار زنده",
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "حرکت ثانیه‌شمار برای دقت بیشتر (در AOD خودکار متوقف می‌شود)",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Switch(
                                checked = settings.showSeconds,
                                onCheckedChange = {
                                    updateSettings(settings.copy(showSeconds = it))
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = PersianMint)
                            )
                        }

                        // Persian Digits Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF10141E))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ارقام فارسی (۰ ۱ ۲ ۳ ۴ ۵ ۶ ۷ ۸ ۹)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "نمایش تمام ساعت، روزها، ماه‌ها و سال به اعداد فارسی",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Switch(
                                checked = settings.usePersianDigits,
                                onCheckedChange = {
                                    updateSettings(settings.copy(usePersianDigits = it))
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = PersianMint)
                            )
                        }

                        // Font Selection
                        Text(
                            text = "نوع فونت نمایش:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PersianMint
                        )

                        WatchFontType.values().forEach { fontItem ->
                            val isSelected = settings.font == fontItem
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        updateSettings(settings.copy(font = fontItem))
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF162032) else Color(0xFF10141E)
                                ),
                                border = if (isSelected) {
                                    CardDefaults.outlinedCardBorder().copy(
                                        brush = Brush.horizontalGradient(
                                            listOf(PersianMint, PersianTurquoise)
                                        )
                                    )
                                } else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = fontItem.titlePersian,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = fontItem.subtitlePersian,
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = PersianMint)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // TAB 3: GALAXY WATCH MODELS (مدل ساعت)
            // -------------------------------------------------------------
            if (selectedTab == 3) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "انتخاب مدل گلکسی واچ سامسونگ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "پشتیبانی کاملاً اختصاصی از سایزهای ۴۰، ۴۴، ۴۵ و ۴۷ میلی‌متری و انواع بزل فیزیکی و تاچ:",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )

                        GalaxyWatchDevice.values().forEach { deviceItem ->
                            val isSelected = settings.device == deviceItem
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        updateSettings(settings.copy(device = deviceItem))
                                    }
                                    .testTag("device_item_${deviceItem.id}"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF162032) else Color(0xFF10141E)
                                ),
                                border = if (isSelected) {
                                    CardDefaults.outlinedCardBorder().copy(
                                        brush = Brush.horizontalGradient(
                                            listOf(PersianMint, PersianTurquoise)
                                        )
                                    )
                                } else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(deviceItem.outerColor)
                                                .border(2.dp, deviceItem.accentColor, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Watch,
                                                contentDescription = null,
                                                tint = deviceItem.accentColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = deviceItem.titlePersian,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = deviceItem.bezelType,
                                                fontSize = 11.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "انتخاب شده",
                                            tint = PersianMint,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // TAB 4: THEMES & AMOLED COLORS (پوسته و رنگ)
            // -------------------------------------------------------------
            if (selectedTab == 4) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "پوسته‌ها و پالت‌های رنگی اختصاصی",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "رنگ‌بندی‌های بهینه‌سازی‌شده برای صفحه‌نمایش‌های Super AMOLED گلکسی واچ:",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )

                        WatchThemePalette.values().forEach { palette ->
                            val isSelected = settings.theme == palette
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        updateSettings(settings.copy(theme = palette))
                                    }
                                    .testTag("theme_item_${palette.id}"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF162032) else Color(0xFF10141E)
                                ),
                                border = if (isSelected) {
                                    CardDefaults.outlinedCardBorder().copy(
                                        brush = Brush.horizontalGradient(
                                            listOf(palette.primaryColor, palette.secondaryColor)
                                        )
                                    )
                                } else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(palette.backgroundColor)
                                                .border(2.dp, palette.primaryColor, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .background(palette.secondaryColor)
                                            )
                                        }

                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = palette.titlePersian,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                if (palette.isOledOptimized) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(Color(0xFF0F3D3E))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "AMOLED",
                                                            fontSize = 9.sp,
                                                            color = PersianMint,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = palette.description,
                                                fontSize = 11.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "انتخاب شده",
                                            tint = palette.primaryColor,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // TAB 5: WATCH INSTALL GUIDE (راهنمای نصب ساعت)
            // -------------------------------------------------------------
            if (selectedTab == 5) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "راهنمای کامل نصب روی گلکسی واچ سامسونگ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF162032)),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(listOf(PersianMint, PersianGold))
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "مراحل نصب بدون کابل از طریق وای‌فای (Wireless Sideload):",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PersianMint
                                )
                                Text(
                                    text = "۱. در ساعت: به Settings > About watch > Software info بروید و ۷ مرتبه روی Software version ضربه بزنید تا Developer mode روشن شود.\n۲. به منوی جدید Developer options در تنظیمات ساعت بروید و گزینه‌های ADB debugging و Wireless debugging را روشن کنید.\n۳. با یکی از برنامه‌های رایگان گوشی مثل Easy Fire Tools یا GeminiMan Wear OS Manager به آی‌پی ساعت وصل شده و این برنامه را روی ساعت نصب کنید.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFE2E8F0),
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val success = com.example.util.GalaxyWearableHelper.setAsWatchFace(context)
                                if (!success) {
                                    com.example.util.GalaxyWearableHelper.openGalaxyWearable(context)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PersianTurquoise, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Watch, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("فعال‌سازی مستقیم به عنوان واچ‌فیس (Set as Watch Face)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                com.example.util.GalaxyWearableHelper.openGalaxyWearable(context)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PersianMint, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Watch, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("باز کردن مستقیم برنامه Galaxy Wearable", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showInstallGuideDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PersianGold, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("مشاهده راهنمای کامل تصویری نصب", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // TAB 6: BATTERY OPTIMIZATION (مصرف باتری)
            // -------------------------------------------------------------
            if (selectedTab == 6) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "بهینه‌سازی حداکثری مصرف باتری و عملکرد",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2625)),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(listOf(PersianMint, PersianTurquoise))
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = PersianMint)
                                    Text("بازدهی انرژی: ۹۸.۴٪ بهینه", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PersianMint)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "• خاموشی خودکار پیکسل‌های سیاه روی Super AMOLED\n• محاسبات ریاضی O(1) تقویم بدون هیچ سربار پردازشی یا مصرف رم\n• عدم اجرای هرگونه درخواست اینترنتی در پس‌زمینه (۱۰۰٪ آفلاین)\n• توقف کامل ثانیه‌شمار در حالت آماده‌باش (AOD) جهت جلوگیری از بیداری پردازنده ساعت",
                                    fontSize = 12.sp,
                                    color = Color(0xFFCCFBF1),
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        // AOD Toggle Switch Card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF10141E))
                                .border(1.dp, Color(0xFF1E283C), RoundedCornerShape(14.dp))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "پیش‌نمایش حالت همیشه روشن (AOD)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "کاهش نور به حداقل و خطی‌سازی طرح جهت صرفه‌جویی شدید در باتری و محافظت از پیکسل‌ها در برابر سوختگی (Burn-In)",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = settings.isAmbientAodMode,
                                onCheckedChange = {
                                    updateSettings(settings.copy(isAmbientAodMode = it))
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = PersianGold)
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // TAB 7: ABOUT US (درباره ما)
            // -------------------------------------------------------------
            if (selectedTab == 7) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "شناسنامه و درباره ما",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2D)),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(listOf(PersianMint, PersianGold))
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "طراحی : دکتر خسروی",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PersianGold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "09123371764 | https://1tw.ir",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PersianMint,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color(0xFF223147))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "طراحی : دکتر خسروی : 09123371764 | https://1tw.ir",
                                    fontSize = 13.sp,
                                    color = Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:09123371764"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = PersianMint, contentColor = Color.Black),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تماس مستقیم", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://1tw.ir"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = PersianTurquoise, contentColor = Color.Black),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("وب‌سایت", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 4: HOME SCREEN WIDGET SYNC ACTION
            // -------------------------------------------------------------
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111724)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(Color(0xFF222B3D), Color(0xFF181F2C)))
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.Widgets, contentDescription = null, tint = PersianMint)
                            Column {
                                Text("ویجت صفحه اصلی گوشی و واچ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("ساعت درشت، تقویم شمسی و سنسورها", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                        }

                        Button(
                            onClick = {
                                PersianDateWidgetProvider.updateAllWidgets(context)
                                Toast.makeText(context, "ویجت تقویم شمسی و ساعت با موفقیت به‌روزرسانی شد", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E283C), contentColor = PersianMint),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("به‌روزرسانی", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    if (showAboutDialog) {
        AboutDoctorKhosraviDialog(onDismissRequest = { showAboutDialog = false })
    }

    if (showInstallGuideDialog) {
        GalaxyWatchInstallGuideDialog(onDismissRequest = { showInstallGuideDialog = false })
    }
}

// ---------------------------------------------------------------------------
// SLOT SELECTOR CARD COMPONENT
// ---------------------------------------------------------------------------
@Composable
fun SlotSelectorCard(
    slotTitle: String,
    currentType: WatchComplicationType,
    options: List<WatchComplicationType>,
    onSelect: (WatchComplicationType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF10141E)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(Color(0xFF222B3D), Color(0xFF181F2C)))
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = slotTitle,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = currentType.titlePersian,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersianMint
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(options) { opt ->
                    val isSelected = opt == currentType
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) PersianMint else Color(0xFF182030))
                            .border(1.dp, if (isSelected) PersianMint else Color(0xFF2A364F), RoundedCornerShape(10.dp))
                            .clickable { onSelect(opt) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = opt.shortLabelPersian,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else Color(0xFFCBD5E1)
                        )
                    }
                }
            }
        }
    }
}
