package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.GalaxyWatchDevice
import com.example.model.WatchComplicationType
import com.example.model.WatchCustomSettings
import com.example.model.WatchFaceLayout
import com.example.model.WatchFontType
import com.example.model.WatchThemePalette
import com.example.model.WidgetArrangement
import com.example.model.WidgetColorStyle
import com.example.model.WidgetDisplayStyle
import com.example.model.WidgetSize

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("galaxy_watch_persian_prefs", Context.MODE_PRIVATE)

    fun loadSettings(): WatchCustomSettings {
        val deviceId = prefs.getString("device_id", GalaxyWatchDevice.WATCH_ULTRA.id)
        val themeId = prefs.getString("theme_id", WatchThemePalette.AMOLED_BLACK.id)
        val layoutId = prefs.getString("layout_id", WatchFaceLayout.BIG_CLOCK_MODULAR.id)
        val fontId = prefs.getString("font_id", WatchFontType.VAZIR_MODERN.id)
        val persianDigits = prefs.getBoolean("persian_digits", true)
        val showClock = prefs.getBoolean("show_clock", true)
        val is24Hour = prefs.getBoolean("is_24_hour", true)
        val showSeconds = prefs.getBoolean("show_seconds", true)
        val clockSizeLarge = prefs.getBoolean("clock_size_large", true)

        val topCompId = prefs.getString("top_comp", WatchComplicationType.BATTERY.id)
        val bottomCompId = prefs.getString("bottom_comp", WatchComplicationType.OCCASIONS.id)
        val leftCompId = prefs.getString("left_comp", WatchComplicationType.HEART_RATE.id)
        val rightCompId = prefs.getString("right_comp", WatchComplicationType.STEP_COUNTER.id)

        val heartRate = prefs.getInt("sim_heart_rate", 78)
        val steps = prefs.getInt("sim_steps", 7450)
        val battery = prefs.getInt("sim_battery", 88)
        val calories = prefs.getInt("sim_calories", 480)

        val occasions = prefs.getBoolean("show_occasions", true)
        val gregorian = prefs.getBoolean("show_gregorian", true)
        val lunar = prefs.getBoolean("show_lunar", true)
        val season = prefs.getBoolean("show_season", true)
        val batterySaver = prefs.getBoolean("battery_saver", true)

        val widgetSizeId = prefs.getString("widget_size", WidgetSize.LARGE.id)
        val widgetColorId = prefs.getString("widget_color", WidgetColorStyle.NEON_MULTICOLOR.id)
        val widgetArrangeId = prefs.getString("widget_arrange", WidgetArrangement.TOP_BATTERY_BOTTOM_SENSORS.id)
        val widgetDisplayId = prefs.getString("widget_display", WidgetDisplayStyle.CAPSULE_CONTAINER.id)
        val showBattery = prefs.getBoolean("show_bat_widget", true)
        val showHR = prefs.getBoolean("show_hr_widget", true)
        val showSteps = prefs.getBoolean("show_steps_widget", true)

        val widgetSize = WidgetSize.values().find { it.id == widgetSizeId } ?: WidgetSize.LARGE
        val widgetColor = WidgetColorStyle.values().find { it.id == widgetColorId } ?: WidgetColorStyle.NEON_MULTICOLOR
        val widgetArrange = WidgetArrangement.values().find { it.id == widgetArrangeId } ?: WidgetArrangement.TOP_BATTERY_BOTTOM_SENSORS
        val widgetDisplay = WidgetDisplayStyle.values().find { it.id == widgetDisplayId } ?: WidgetDisplayStyle.CAPSULE_CONTAINER

        val device = GalaxyWatchDevice.values().find { it.id == deviceId } ?: GalaxyWatchDevice.WATCH_ULTRA
        val theme = com.example.model.WatchThemePalette.values().find { it.id == themeId } ?: com.example.model.WatchThemePalette.AMOLED_BLACK
        val layout = WatchFaceLayout.values().find { it.id == layoutId } ?: WatchFaceLayout.BIG_CLOCK_MODULAR
        val font = WatchFontType.values().find { it.id == fontId } ?: WatchFontType.VAZIR_MODERN

        val topComp = WatchComplicationType.values().find { it.id == topCompId } ?: WatchComplicationType.BATTERY
        val bottomComp = WatchComplicationType.values().find { it.id == bottomCompId } ?: WatchComplicationType.OCCASIONS
        val leftComp = WatchComplicationType.values().find { it.id == leftCompId } ?: WatchComplicationType.HEART_RATE
        val rightComp = WatchComplicationType.values().find { it.id == rightCompId } ?: WatchComplicationType.STEP_COUNTER

        return WatchCustomSettings(
            device = device,
            theme = theme,
            layout = layout,
            font = font,
            usePersianDigits = persianDigits,
            showClock = showClock,
            is24Hour = is24Hour,
            showSeconds = showSeconds,
            clockSizeLarge = clockSizeLarge,
            widgetSize = widgetSize,
            widgetColorStyle = widgetColor,
            widgetArrangement = widgetArrange,
            widgetDisplayStyle = widgetDisplay,
            showBatteryWidget = showBattery,
            showHeartRateWidget = showHR,
            showStepsWidget = showSteps,
            topSlotComplication = topComp,
            bottomSlotComplication = bottomComp,
            leftSlotComplication = leftComp,
            rightSlotComplication = rightComp,
            simulatedHeartRate = heartRate,
            simulatedSteps = steps,
            simulatedBattery = battery,
            simulatedCalories = calories,
            showOccasions = occasions,
            showGregorian = gregorian,
            showLunar = lunar,
            showSeasonProgress = season,
            isAmbientAodMode = false,
            isBatterySaverEnabled = batterySaver
        )
    }

    fun saveSettings(settings: WatchCustomSettings) {
        prefs.edit()
            .putString("device_id", settings.device.id)
            .putString("theme_id", settings.theme.id)
            .putString("layout_id", settings.layout.id)
            .putString("font_id", settings.font.id)
            .putBoolean("persian_digits", settings.usePersianDigits)
            .putBoolean("show_clock", settings.showClock)
            .putBoolean("is_24_hour", settings.is24Hour)
            .putBoolean("show_seconds", settings.showSeconds)
            .putBoolean("clock_size_large", settings.clockSizeLarge)
            .putString("widget_size", settings.widgetSize.id)
            .putString("widget_color", settings.widgetColorStyle.id)
            .putString("widget_arrange", settings.widgetArrangement.id)
            .putString("widget_display", settings.widgetDisplayStyle.id)
            .putBoolean("show_bat_widget", settings.showBatteryWidget)
            .putBoolean("show_hr_widget", settings.showHeartRateWidget)
            .putBoolean("show_steps_widget", settings.showStepsWidget)
            .putString("top_comp", settings.topSlotComplication.id)
            .putString("bottom_comp", settings.bottomSlotComplication.id)
            .putString("left_comp", settings.leftSlotComplication.id)
            .putString("right_comp", settings.rightSlotComplication.id)
            .putInt("sim_heart_rate", settings.simulatedHeartRate)
            .putInt("sim_steps", settings.simulatedSteps)
            .putInt("sim_battery", settings.simulatedBattery)
            .putInt("sim_calories", settings.simulatedCalories)
            .putBoolean("show_occasions", settings.showOccasions)
            .putBoolean("show_gregorian", settings.showGregorian)
            .putBoolean("show_lunar", settings.showLunar)
            .putBoolean("show_season", settings.showSeasonProgress)
            .putBoolean("battery_saver", settings.isBatterySaverEnabled)
            .apply()
    }
}
