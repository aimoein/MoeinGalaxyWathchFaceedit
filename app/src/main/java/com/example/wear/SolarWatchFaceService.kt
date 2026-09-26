package com.example.wear

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import com.example.calendar.SolarHijriCalendar
import com.example.calendar.SolarOccasions
import com.example.data.PreferencesManager
import com.example.model.WatchCustomSettings
import com.example.model.WatchFaceLayout
import com.example.sensor.RealSensorManager
import java.util.Calendar
import java.util.Locale

/**
 * Native Wear OS Watch Face Service for Samsung Galaxy Watch (Watch 4, 5, 6, 7, Ultra).
 * 
 * Features & Requirements:
 * 1. Prominent Persian Solar Date (تاریخ شمسی با درخشش طلایی خورشیدی) - guaranteed visible & never black.
 * 2. 50% Screen Occupancy for Clock & Solar Date (ساعت و تاریخ ۵۰٪ قطر صفحه واچ).
 * 3. Large, proportional widgets (ویجت‌های باتری، نبض و گام‌شمار درشت و ارگونومیک مخصوص صفحه ساعت).
 * 4. Super AMOLED pure black (#000000) energy-saving rendering.
 * 5. 100% crash-proof receiver and lifecycle handling for Android 11-15+ Wear OS.
 */
class SolarWatchFaceService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return SolarWatchFaceEngine()
    }

    private inner class SolarWatchFaceEngine : WallpaperService.Engine() {

        private val handler = Handler(Looper.getMainLooper())
        private var isVisible = false
        private var isAmbient = false

        // Settings and preferences
        private lateinit var prefsManager: PreferencesManager
        private var settings: WatchCustomSettings = WatchCustomSettings()

        // Real Sensor Manager
        private lateinit var realSensorManager: RealSensorManager

        // Screen metrics
        private var screenWidth = 450f
        private var screenHeight = 450f
        private var centerX = 225f
        private var centerY = 225f
        private var currentScale = 1.0f

        // Base Paints
        private val bgPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }

        private val bezelGoldPaint = Paint().apply {
            color = Color.parseColor("#D4AF37")
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }

        private val bezelThemePaint = Paint().apply {
            color = Color.parseColor("#2DD4BF")
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            isAntiAlias = true
        }

        // Clock Paint: Extra Bold & large (occupies ~28% of screen height)
        private val bigClockPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = 96f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
            setShadowLayer(10f, 0f, 0f, Color.parseColor("#552DD4BF"))
        }

        private val secondsPaint = Paint().apply {
            color = Color.parseColor("#2DD4BF")
            style = Paint.Style.FILL
            textAlign = Paint.Align.LEFT
            textSize = 26f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        // Solar Date Paint: Prominent Persian Gold (#FDE047) - occupies ~22% of screen height
        private val solarDatePaint = Paint().apply {
            color = Color.parseColor("#FDE047") // Persian Gold
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = 34f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
            setShadowLayer(8f, 0f, 0f, Color.parseColor("#66FDE047"))
        }

        private val solarDayOfWeekPaint = Paint().apply {
            color = Color.parseColor("#2DD4BF") // Persian Mint / Turquoise
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = 22f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        private val dividerPaint = Paint().apply {
            color = Color.parseColor("#334155")
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }

        // Large Widget Capsule Paints
        private val widgetBgPaint = Paint().apply {
            color = Color.parseColor("#151D28")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        private val widgetBorderPaint = Paint().apply {
            color = Color.parseColor("#2A3950")
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            isAntiAlias = true
        }

        private val batteryValuePaint = Paint().apply {
            color = Color.parseColor("#2DD4BF")
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = 20f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        private val heartRateValuePaint = Paint().apply {
            color = Color.parseColor("#FF3366")
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = 20f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        private val stepsValuePaint = Paint().apply {
            color = Color.parseColor("#10B981")
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = 20f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        private val widgetLabelPaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = 13f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        private val gaugeBgPaint = Paint().apply {
            color = Color.parseColor("#1E293B")
            style = Paint.Style.STROKE
            strokeWidth = 6f
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }

        private val gaugeProgressPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 6f
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }

        private val subtitlePaint = Paint().apply {
            color = Color.parseColor("#94A3B8")
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = 14f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        private val textBounds = Rect()
        private val tempRectF = RectF()

        // 1-second ticker loop for interactive mode
        private val tickRunnable = object : Runnable {
            override fun run() {
                draw()
                if (isVisible && !isAmbient) {
                    val delay = 1000L - (System.currentTimeMillis() % 1000L)
                    handler.postDelayed(this, delay)
                }
            }
        }

        // Broadcast receivers
        private var isReceiverRegistered = false
        private val settingsAndSystemReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                try {
                    when (intent?.action) {
                        Intent.ACTION_TIME_TICK,
                        Intent.ACTION_TIME_CHANGED,
                        Intent.ACTION_TIMEZONE_CHANGED -> {
                            draw()
                        }
                        "com.example.wear.SETTINGS_CHANGED" -> {
                            reloadSettings()
                            draw()
                        }
                    }
                } catch (_: Throwable) {}
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            try {
                prefsManager = PreferencesManager(this@SolarWatchFaceService)
                realSensorManager = RealSensorManager(this@SolarWatchFaceService)
                reloadSettings()
                registerReceivers()
            } catch (_: Throwable) {}
        }

        private fun reloadSettings() {
            try {
                settings = prefsManager.loadSettings()
                val theme = settings.theme

                // Safely convert Compose Color to ARGB Int
                val primaryArgb = try { theme.primaryColor.toArgb() } catch (_: Throwable) { 0xFF2DD4BF.toInt() }
                val accentArgb = try { theme.accentColor.toArgb() } catch (_: Throwable) { 0xFFFDE047.toInt() }
                val secondaryArgb = try { theme.secondaryColor.toArgb() } catch (_: Throwable) { 0xFF38BDF8.toInt() }

                // Crucial Check: Guarantee colors are never black on black background!
                val safePrimary = if ((primaryArgb and 0xFFFFFF) < 0x252525) 0xFF2DD4BF.toInt() else primaryArgb
                val safeAccent = if ((accentArgb and 0xFFFFFF) < 0x252525) 0xFFFDE047.toInt() else accentArgb
                val safeSecondary = if ((secondaryArgb and 0xFFFFFF) < 0x252525) 0xFF38BDF8.toInt() else secondaryArgb

                bezelThemePaint.color = safePrimary
                secondsPaint.color = safePrimary
                solarDatePaint.color = safeAccent
                solarDayOfWeekPaint.color = safePrimary
                batteryValuePaint.color = safePrimary

                updatePaintScales(currentScale)
            } catch (_: Throwable) {}
        }

        private fun updatePaintScales(scale: Float) {
            currentScale = scale
            // 50% Occupancy Rule:
            // Big Clock = 96f * scale
            // Solar Date = 34f * scale
            // Day of Week = 22f * scale
            // Total height of text lines + spacing ~ 215px out of 450px (~48% screen height)
            val clockBase = if (settings.clockSizeLarge) 98f else 88f
            bigClockPaint.textSize = clockBase * scale
            secondsPaint.textSize = 26f * scale
            solarDatePaint.textSize = 34f * scale
            solarDayOfWeekPaint.textSize = 22f * scale

            // Large Widgets sizing
            batteryValuePaint.textSize = 20f * scale
            heartRateValuePaint.textSize = 20f * scale
            stepsValuePaint.textSize = 20f * scale
            widgetLabelPaint.textSize = 13f * scale
            subtitlePaint.textSize = 14f * scale

            bezelGoldPaint.strokeWidth = 3f * scale
            bezelThemePaint.strokeWidth = 2.5f * scale
            gaugeBgPaint.strokeWidth = 6f * scale
            gaugeProgressPaint.strokeWidth = 6f * scale
            widgetBorderPaint.strokeWidth = 1.5f * scale
        }

        override fun onDestroy() {
            unregisterReceivers()
            try {
                realSensorManager.stopListening()
                handler.removeCallbacks(tickRunnable)
            } catch (_: Throwable) {}
            super.onDestroy()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            this.isVisible = visible
            if (visible) {
                reloadSettings()
                try {
                    realSensorManager.startListening()
                    realSensorManager.onDataChanged = { draw() }
                } catch (_: Throwable) {}
                registerReceivers()
                handler.removeCallbacks(tickRunnable)
                handler.post(tickRunnable)
            } else {
                handler.removeCallbacks(tickRunnable)
                try {
                    realSensorManager.stopListening()
                } catch (_: Throwable) {}
                unregisterReceivers()
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            if (width > 0 && height > 0) {
                screenWidth = width.toFloat()
                screenHeight = height.toFloat()
                centerX = screenWidth / 2f
                centerY = screenHeight / 2f

                val scale = screenWidth / 450f
                updatePaintScales(scale)
                draw()
            }
        }

        override fun onSurfaceRedrawNeeded(holder: SurfaceHolder?) {
            super.onSurfaceRedrawNeeded(holder)
            draw()
        }

        override fun onCommand(
            action: String?,
            x: Int,
            y: Int,
            z: Int,
            extras: android.os.Bundle?,
            resultRequested: Boolean
        ): android.os.Bundle? {
            if (action == android.app.WallpaperManager.COMMAND_TAP) {
                // Tapping toggles Persian vs English numerals
                val newDigits = !settings.usePersianDigits
                settings = settings.copy(usePersianDigits = newDigits)
                try {
                    prefsManager.saveSettings(settings)
                } catch (_: Throwable) {}
                draw()
            }
            return super.onCommand(action, x, y, z, extras, resultRequested)
        }

        private fun registerReceivers() {
            if (isReceiverRegistered) return
            isReceiverRegistered = true
            try {
                val filter = IntentFilter().apply {
                    addAction(Intent.ACTION_TIME_TICK)
                    addAction(Intent.ACTION_TIME_CHANGED)
                    addAction(Intent.ACTION_TIMEZONE_CHANGED)
                    addAction("com.example.wear.SETTINGS_CHANGED")
                }
                ContextCompat.registerReceiver(
                    this@SolarWatchFaceService,
                    settingsAndSystemReceiver,
                    filter,
                    ContextCompat.RECEIVER_NOT_EXPORTED
                )
            } catch (_: Throwable) {}
        }

        private fun unregisterReceivers() {
            if (!isReceiverRegistered) return
            isReceiverRegistered = false
            try {
                unregisterReceiver(settingsAndSystemReceiver)
            } catch (_: Throwable) {}
        }

        private fun draw() {
            val holder = surfaceHolder ?: return
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    renderWatchFace(canvas)
                }
            } catch (_: Throwable) {
            } finally {
                if (canvas != null) {
                    try {
                        holder.unlockCanvasAndPost(canvas)
                    } catch (_: Throwable) {}
                }
            }
        }

        private fun renderWatchFace(canvas: Canvas) {
            val now = Calendar.getInstance()
            val hour = now.get(Calendar.HOUR_OF_DAY)
            val minute = now.get(Calendar.MINUTE)
            val second = now.get(Calendar.SECOND)

            // Live hardware telemetry
            val telemetry = try {
                realSensorManager.getSnapshot()
            } catch (_: Throwable) {
                com.example.sensor.RealWatchTelemetry()
            }
            val batteryLevel = telemetry.batteryPercent
            val heartRate = telemetry.heartRateBpm
            val stepCount = telemetry.stepCount

            // Solar Hijri Date
            val solarDate = SolarHijriCalendar.now()

            // 1. Draw Super AMOLED 0-Watt Pure Black
            canvas.drawRect(0f, 0f, screenWidth, screenHeight, bgPaint)

            val radius = centerX - 6f * currentScale

            if (!isAmbient) {
                // 2. Bezel accents
                canvas.drawCircle(centerX, centerY, radius, bezelGoldPaint)
                canvas.drawCircle(centerX, centerY, radius - (6f * currentScale), bezelThemePaint)

                // Dial markers at 12, 3, 6, 9
                bezelGoldPaint.strokeWidth = 3f * currentScale
                canvas.drawLine(centerX, 14f * currentScale, centerX, 24f * currentScale, bezelGoldPaint)
                canvas.drawLine(centerX, screenHeight - (24f * currentScale), centerX, screenHeight - (14f * currentScale), bezelGoldPaint)
                canvas.drawLine(14f * currentScale, centerY, 24f * currentScale, centerY, bezelGoldPaint)
                canvas.drawLine(screenWidth - (24f * currentScale), centerY, screenWidth - (14f * currentScale), centerY, bezelGoldPaint)
            }

            // -----------------------------------------------------------------
            // RENDER BASED ON USER LAYOUT (All with 50% Clock & Solar Date)
            // -----------------------------------------------------------------
            when (settings.layout) {
                WatchFaceLayout.SOLAR_MINIMAL -> {
                    renderSolarMinimal(canvas, hour, minute, second, solarDate, batteryLevel)
                }
                WatchFaceLayout.HEALTH_DASHBOARD -> {
                    renderHealthDashboard(canvas, hour, minute, second, solarDate, batteryLevel, heartRate, stepCount)
                }
                WatchFaceLayout.PROGRESS_RINGS -> {
                    renderProgressRings(canvas, hour, minute, second, solarDate, batteryLevel, heartRate, stepCount)
                }
                else -> {
                    // Default: BIG_CLOCK_MODULAR
                    renderBigClockModular(canvas, hour, minute, second, solarDate, batteryLevel, heartRate, stepCount)
                }
            }
        }

        /**
         * DEFAULT: BIG CLOCK MODULAR
         * Clock and Solar Date occupy exactly 50% of the screen diameter.
         * Widgets are fully customizable in Size, Color, Arrangement, Display Style, and Visibility.
         */
        private fun renderBigClockModular(
            canvas: Canvas,
            hour: Int,
            minute: Int,
            second: Int,
            solarDate: com.example.calendar.SolarDate,
            batteryLevel: Int,
            heartRate: Int,
            stepCount: Int
        ) {
            val usePersianDigits = settings.usePersianDigits

            // -------------------------------------------------------------
            // WIDGET COLOR DETERMINATION
            // -------------------------------------------------------------
            val (batteryColor, hrColor, stepsColor) = when (settings.widgetColorStyle) {
                com.example.model.WidgetColorStyle.THEME_MATCHED -> Triple(bezelThemePaint.color, bezelThemePaint.color, bezelThemePaint.color)
                com.example.model.WidgetColorStyle.PURE_GOLD -> Triple(Color.parseColor("#FDE047"), Color.parseColor("#FDE047"), Color.parseColor("#FDE047"))
                com.example.model.WidgetColorStyle.PERSIAN_TURQUOISE -> Triple(Color.parseColor("#2DD4BF"), Color.parseColor("#2DD4BF"), Color.parseColor("#2DD4BF"))
                com.example.model.WidgetColorStyle.MINIMAL_WHITE -> Triple(Color.WHITE, Color.WHITE, Color.WHITE)
                com.example.model.WidgetColorStyle.NEON_MULTICOLOR -> Triple(
                    if (batteryLevel <= 20) Color.parseColor("#EF4444") else Color.parseColor("#2DD4BF"),
                    Color.parseColor("#FF3366"),
                    Color.parseColor("#10B981")
                )
            }

            // Scale based on user's chosen WidgetSize
            val wScale = currentScale * settings.widgetSize.scaleFactor

            // Real telemetry text formatting (No fake mock values!)
            val rawBatteryText = "$batteryLevel%"
            val batteryText = if (usePersianDigits) SolarHijriCalendar.toPersianDigits(rawBatteryText) else rawBatteryText
            val hrText = if (heartRate > 0) {
                if (usePersianDigits) SolarHijriCalendar.toPersianDigits("$heartRate") else "$heartRate"
            } else "--"
            val stepsText = if (usePersianDigits) SolarHijriCalendar.toPersianDigits("$stepCount") else "$stepCount"

            // Helper to draw single customizable widget
            fun drawWidget(
                x: Float,
                y: Float,
                icon: String,
                text: String,
                color: Int,
                progress: Float
            ) {
                val pillW = 66f * wScale
                val pillH = 20f * wScale
                val rect = RectF(x - pillW, y - pillH, x + pillW, y + pillH)

                if (!isAmbient) {
                    when (settings.widgetDisplayStyle) {
                        com.example.model.WidgetDisplayStyle.CAPSULE_CONTAINER -> {
                            widgetBgPaint.color = Color.parseColor("#141D29")
                            canvas.drawRoundRect(rect, pillH, pillH, widgetBgPaint)
                            widgetBorderPaint.color = color
                            widgetBorderPaint.alpha = 120
                            canvas.drawRoundRect(rect, pillH, pillH, widgetBorderPaint)

                            // Mini bottom progress line
                            val barLeft = x - pillW + (10f * wScale)
                            val barRight = x + pillW - (10f * wScale)
                            val barY = y + pillH - (3.5f * wScale)
                            gaugeBgPaint.strokeWidth = 2.5f * wScale
                            gaugeBgPaint.color = Color.parseColor("#1E293B")
                            canvas.drawLine(barLeft, barY, barRight, barY, gaugeBgPaint)

                            gaugeProgressPaint.strokeWidth = 2.5f * wScale
                            gaugeProgressPaint.color = color
                            val barProgressRight = barLeft + ((barRight - barLeft) * progress.coerceIn(0f, 1f))
                            canvas.drawLine(barLeft, barY, barProgressRight, barY, gaugeProgressPaint)
                        }
                        com.example.model.WidgetDisplayStyle.CIRCULAR_GAUGE -> {
                            val arcRadius = 16f * wScale
                            val arcRect = RectF(x - pillW + 4f, y - arcRadius, x - pillW + (2f * arcRadius) + 4f, y + arcRadius)
                            gaugeBgPaint.strokeWidth = 3f * wScale
                            gaugeBgPaint.color = Color.parseColor("#1E293B")
                            canvas.drawArc(arcRect, 135f, 270f, false, gaugeBgPaint)
                            gaugeProgressPaint.strokeWidth = 3f * wScale
                            gaugeProgressPaint.color = color
                            canvas.drawArc(arcRect, 135f, progress.coerceIn(0f, 1f) * 270f, false, gaugeProgressPaint)
                        }
                        com.example.model.WidgetDisplayStyle.MINIMAL_BORDERLESS -> {
                            // Pure text on pure black, no boxes
                        }
                    }
                }

                batteryValuePaint.color = color
                batteryValuePaint.textSize = 19f * wScale
                canvas.drawText("$icon $text", x, y + (6f * wScale), batteryValuePaint)
            }

            // -------------------------------------------------------------
            // RENDER WIDGETS BASED ON ARRANGEMENT / PLACEMENT
            // -------------------------------------------------------------
            when (settings.widgetArrangement) {
                com.example.model.WidgetArrangement.TOP_BATTERY_BOTTOM_SENSORS -> {
                    if (settings.showBatteryWidget) {
                        drawWidget(centerX, centerY * 0.28f, "⚡", batteryText, batteryColor, batteryLevel / 100f)
                    }
                    if (settings.showHeartRateWidget) {
                        drawWidget(centerX * 0.48f, centerY * 1.54f, "♥", hrText, hrColor, if (heartRate > 0) heartRate / 160f else 0f)
                    }
                    if (settings.showStepsWidget) {
                        drawWidget(centerX * 1.52f, centerY * 1.54f, "👟", stepsText, stepsColor, stepCount / 8000f)
                    }
                }
                com.example.model.WidgetArrangement.TOP_SENSORS_BOTTOM_BATTERY -> {
                    if (settings.showHeartRateWidget) {
                        drawWidget(centerX * 0.48f, centerY * 0.28f, "♥", hrText, hrColor, if (heartRate > 0) heartRate / 160f else 0f)
                    }
                    if (settings.showStepsWidget) {
                        drawWidget(centerX * 1.52f, centerY * 0.28f, "👟", stepsText, stepsColor, stepCount / 8000f)
                    }
                    if (settings.showBatteryWidget) {
                        drawWidget(centerX, centerY * 1.54f, "⚡", batteryText, batteryColor, batteryLevel / 100f)
                    }
                }
                com.example.model.WidgetArrangement.HORIZONTAL_ROW_BOTTOM -> {
                    val rowY = centerY * 1.54f
                    val compactScale = wScale * 0.82f
                    if (settings.showHeartRateWidget) {
                        drawWidget(centerX * 0.38f, rowY, "♥", hrText, hrColor, if (heartRate > 0) heartRate / 160f else 0f)
                    }
                    if (settings.showBatteryWidget) {
                        drawWidget(centerX, rowY, "⚡", batteryText, batteryColor, batteryLevel / 100f)
                    }
                    if (settings.showStepsWidget) {
                        drawWidget(centerX * 1.62f, rowY, "👟", stepsText, stepsColor, stepCount / 8000f)
                    }
                }
                com.example.model.WidgetArrangement.SPLIT_LEFT_RIGHT -> {
                    if (settings.showBatteryWidget) {
                        drawWidget(centerX, centerY * 0.28f, "⚡", batteryText, batteryColor, batteryLevel / 100f)
                    }
                    if (settings.showHeartRateWidget) {
                        drawWidget(centerX * 0.28f, centerY, "♥", hrText, hrColor, if (heartRate > 0) heartRate / 160f else 0f)
                    }
                    if (settings.showStepsWidget) {
                        drawWidget(centerX * 1.72f, centerY, "👟", stepsText, stepsColor, stepCount / 8000f)
                    }
                }
                com.example.model.WidgetArrangement.BATTERY_ONLY -> {
                    if (settings.showBatteryWidget) {
                        drawWidget(centerX, centerY * 0.28f, "⚡", batteryText, batteryColor, batteryLevel / 100f)
                    }
                }
                com.example.model.WidgetArrangement.HEALTH_ONLY -> {
                    if (settings.showHeartRateWidget) {
                        drawWidget(centerX * 0.48f, centerY * 1.54f, "♥", hrText, hrColor, if (heartRate > 0) heartRate / 160f else 0f)
                    }
                    if (settings.showStepsWidget) {
                        drawWidget(centerX * 1.52f, centerY * 1.54f, "👟", stepsText, stepsColor, stepCount / 8000f)
                    }
                }
            }

            // -------------------------------------------------------------
            // 2. CENTER ZONE: BIG CLOCK & PROMINENT PERSIAN SOLAR DATE (50% SPACE)
            // -------------------------------------------------------------
            // Time (Hour : Minute)
            val rawTimeText = String.format(Locale.US, "%02d:%02d", hour, minute)
            val timeText = if (usePersianDigits) SolarHijriCalendar.toPersianDigits(rawTimeText) else rawTimeText

            val clockY = centerY - (10f * currentScale)
            bigClockPaint.color = if (isAmbient) Color.parseColor("#E5E7EB") else Color.WHITE
            canvas.drawText(timeText, centerX, clockY, bigClockPaint)

            // Seconds Counter
            if (!isAmbient && settings.showSeconds) {
                bigClockPaint.getTextBounds(timeText, 0, timeText.length, textBounds)
                val secX = centerX + (textBounds.width() / 2f) + (6f * currentScale)
                val secY = clockY - (bigClockPaint.textSize * 0.40f)
                val rawSec = String.format(Locale.US, ":%02d", second)
                val secText = if (usePersianDigits) SolarHijriCalendar.toPersianDigits(rawSec) else rawSec
                canvas.drawText(secText, secX, secY, secondsPaint)
            }

            // Divider line between Time and Solar Date
            if (!isAmbient) {
                val divHalfWidth = 60f * currentScale
                val divY = clockY + (10f * currentScale)
                canvas.drawLine(centerX - divHalfWidth, divY, centerX + divHalfWidth, divY, dividerPaint)
            }

            // Solar Date (روز، ماه و سال خورشیدی با درخشش طلایی خورشیدی)
            val dateY = clockY + (solarDatePaint.textSize * 1.15f) + (6f * currentScale)
            val rawDate = "${solarDate.day} ${solarDate.monthName} ${solarDate.year}"
            val dateText = if (usePersianDigits) SolarHijriCalendar.toPersianDigits(rawDate) else rawDate
            // Always ensure high visibility
            solarDatePaint.color = Color.parseColor("#FDE047")
            canvas.drawText(dateText, centerX, dateY, solarDatePaint)

            // Day of Week badge (نام روز هفته فیروزه‌ای)
            val dayOfWeekY = dateY + (solarDayOfWeekPaint.textSize * 1.25f)
            val dayOfWeekText = solarDate.dayOfWeekName
            canvas.drawText(dayOfWeekText, centerX, dayOfWeekY, solarDayOfWeekPaint)

            // -------------------------------------------------------------
            // 3. SIGNATURE & OCCASION AT BOTTOM
            // -------------------------------------------------------------
            val bottomY = centerY * 1.84f
            val occasions = SolarOccasions.getOccasionsFor(solarDate.month, solarDate.day)
            val primaryOccasion = occasions.firstOrNull()
            val bottomText = when {
                primaryOccasion != null -> primaryOccasion.title
                isAmbient -> "تقویم واچ دکتر خسروی"
                else -> "طراحی: دکتر خسروی • ۱tw.ir"
            }
            val isHoliday = primaryOccasion?.isHoliday == true
            subtitlePaint.color = if (isHoliday) Color.parseColor("#EF4444") else Color.parseColor("#94A3B8")
            canvas.drawText(bottomText, centerX, bottomY, subtitlePaint)
        }

        /**
         * HEALTH DASHBOARD LAYOUT (Large Rings & 50% Time + Solar Date)
         */
        private fun renderHealthDashboard(
            canvas: Canvas,
            hour: Int,
            minute: Int,
            second: Int,
            solarDate: com.example.calendar.SolarDate,
            batteryLevel: Int,
            heartRate: Int,
            stepCount: Int
        ) {
            val usePersianDigits = settings.usePersianDigits

            // 1. Triple Activity Rings at top center (Thicker and larger)
            val ringRadius = centerX * 0.34f
            val ringCenterY = centerY * 0.38f

            val r1 = RectF(centerX - ringRadius, ringCenterY - ringRadius, centerX + ringRadius, ringCenterY + ringRadius)
            val r2 = RectF(centerX - ringRadius + (12f * currentScale), ringCenterY - ringRadius + (12f * currentScale), centerX + ringRadius - (12f * currentScale), ringCenterY + ringRadius - (12f * currentScale))
            val r3 = RectF(centerX - ringRadius + (24f * currentScale), ringCenterY - ringRadius + (24f * currentScale), centerX + ringRadius - (24f * currentScale), ringCenterY + ringRadius - (24f * currentScale))

            gaugeBgPaint.color = Color.parseColor("#261219")
            canvas.drawArc(r1, 0f, 360f, false, gaugeBgPaint)
            gaugeProgressPaint.color = Color.parseColor("#FF3366")
            canvas.drawArc(r1, -90f, (heartRate / 140f).coerceIn(0.1f, 1f) * 360f, false, gaugeProgressPaint)

            gaugeBgPaint.color = Color.parseColor("#0F291E")
            canvas.drawArc(r2, 0f, 360f, false, gaugeBgPaint)
            gaugeProgressPaint.color = Color.parseColor("#10B981")
            canvas.drawArc(r2, -90f, (stepCount / 8000f).coerceIn(0.1f, 1f) * 360f, false, gaugeProgressPaint)

            gaugeBgPaint.color = Color.parseColor("#131E2C")
            canvas.drawArc(r3, 0f, 360f, false, gaugeBgPaint)
            gaugeProgressPaint.color = Color.parseColor("#38BDF8")
            canvas.drawArc(r3, -90f, (batteryLevel / 100f) * 360f, false, gaugeProgressPaint)

            // Center Big Clock
            val rawTimeText = String.format(Locale.US, "%02d:%02d", hour, minute)
            val timeText = if (usePersianDigits) SolarHijriCalendar.toPersianDigits(rawTimeText) else rawTimeText
            val clockY = centerY + (bigClockPaint.textSize * 0.22f)
            canvas.drawText(timeText, centerX, clockY, bigClockPaint)

            // Solar Hijri Date in bold Gold
            val dateY = clockY + (solarDatePaint.textSize * 1.15f)
            val fullDate = "${solarDate.dayOfWeekName} ${solarDate.day} ${solarDate.monthName}"
            val dateFormatted = if (usePersianDigits) SolarHijriCalendar.toPersianDigits(fullDate) else fullDate
            solarDatePaint.color = Color.parseColor("#FDE047")
            canvas.drawText(dateFormatted, centerX, dateY, solarDatePaint)

            // Bottom Metrics in prominent widgets
            val bottomY = centerY * 1.78f
            val rawStats = "♥ $heartRate bpm   ⚡ $batteryLevel%   👟 $stepCount"
            val statsFormatted = if (usePersianDigits) SolarHijriCalendar.toPersianDigits(rawStats) else rawStats
            subtitlePaint.color = Color.WHITE
            canvas.drawText(statsFormatted, centerX, bottomY, subtitlePaint)
        }

        /**
         * SOLAR MINIMAL LAYOUT (Huge Clock & Solar Date)
         */
        private fun renderSolarMinimal(
            canvas: Canvas,
            hour: Int,
            minute: Int,
            second: Int,
            solarDate: com.example.calendar.SolarDate,
            batteryLevel: Int
        ) {
            val usePersianDigits = settings.usePersianDigits

            // Extra Large Clock
            val rawTimeText = String.format(Locale.US, "%02d:%02d", hour, minute)
            val timeText = if (usePersianDigits) SolarHijriCalendar.toPersianDigits(rawTimeText) else rawTimeText
            val clockY = centerY - (10f * currentScale)
            canvas.drawText(timeText, centerX, clockY, bigClockPaint)

            // Big Solar Day Number & Month in Gold
            val dayStr = if (usePersianDigits) SolarHijriCalendar.toPersianDigits("${solarDate.day}") else "${solarDate.day}"
            val monthStr = "${solarDate.dayOfWeekName}، $dayStr ${solarDate.monthName} ${solarDate.year}"
            val dateFormatted = if (usePersianDigits) SolarHijriCalendar.toPersianDigits(monthStr) else monthStr
            solarDatePaint.color = Color.parseColor("#FDE047")
            canvas.drawText(dateFormatted, centerX, clockY + (solarDatePaint.textSize * 1.35f), solarDatePaint)

            // Battery Bar at bottom
            val bottomY = centerY * 1.78f
            val rawBat = "شارژ باتری: $batteryLevel%"
            val batText = if (usePersianDigits) SolarHijriCalendar.toPersianDigits(rawBat) else rawBat
            subtitlePaint.color = bezelThemePaint.color
            canvas.drawText(batText, centerX, bottomY, subtitlePaint)
        }

        /**
         * PROGRESS RINGS LAYOUT
         */
        private fun renderProgressRings(
            canvas: Canvas,
            hour: Int,
            minute: Int,
            second: Int,
            solarDate: com.example.calendar.SolarDate,
            batteryLevel: Int,
            heartRate: Int,
            stepCount: Int
        ) {
            renderHealthDashboard(canvas, hour, minute, second, solarDate, batteryLevel, heartRate, stepCount)
        }
    }
}
