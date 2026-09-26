package com.example.model

enum class WatchFaceLayout(
    val id: String,
    val titlePersian: String,
    val description: String,
    val iconName: String
) {
    BIG_CLOCK_MODULAR(
        id = "big_clock_modular",
        titlePersian = "ساعت درشت با ویجت‌های سفارشی",
        description = "ساعت دیجیتال بسیار درشت در مرکز، تاریخ شمسی کوچکتر و ۴ ویجت سنسور قابل انتخاب (ضربان قلب، گام‌شمار، باتری و...)",
        iconName = "access_time"
    ),
    HEALTH_DASHBOARD(
        id = "health_dashboard",
        titlePersian = "داشبورد سلامت و تقویم سامسونگ",
        description = "ساعت درشت، تاریخ شمسی، همراه با حلقه سه‌گانه فعالیت سامسونگ، ضربان قلب زنده و گام‌شمار",
        iconName = "favorite"
    ),
    SOLAR_MINIMAL(
        id = "solar_minimal",
        titlePersian = "مینیمال خورشیدی",
        description = "ساعت درشت بالا، عدد روز شمسی، هلال ماه و خطوط مدرن",
        iconName = "wb_sunny"
    ),
    CHRONO_DIAL(
        id = "chrono_dial",
        titlePersian = "کرنوگراف هیبریدی با سنسورها",
        description = "عقربه‌های آنالوگ لوکس همراه با ساب‌دایال‌های ضربان قلب، باتری و تقویم شمسی",
        iconName = "watch"
    ),
    PROGRESS_RINGS(
        id = "progress_rings",
        titlePersian = "حلقه‌های سه‌گانه پیشرفت",
        description = "ساعت درشت در مرکز با حلقه‌های گرافیکی گام‌شمار، باتری و تقویم",
        iconName = "donut_large"
    ),
    WEAR_OS_TILE(
        id = "wear_os_tile",
        titlePersian = "تایل تقویم و سلامت",
        description = "کارت اختصاصی سامسونگ با شبکه تقویم ماهانه و خلاصه وضعیت سنسورها",
        iconName = "grid_view"
    )
}

enum class WatchFontType(
    val id: String,
    val titlePersian: String,
    val subtitlePersian: String
) {
    VAZIR_MODERN(
        id = "vazir_modern",
        titlePersian = "وزیر متن (مدرن)",
        subtitlePersian = "خواناترین فونت روی نمایشگرهای کوچک ساعت"
    ),
    TRADITIONAL_NASKH(
        id = "traditional_naskh",
        titlePersian = "نسخ ایرانی (کلاسیک)",
        subtitlePersian = "حس و حال سنتی، اصیل و تقویم‌های نفیس"
    ),
    DIGITAL_MONO(
        id = "digital_mono",
        titlePersian = "دیجیتال مدرن (اسپرت)",
        subtitlePersian = "اعداد پررنگ هندسی مخصوص ساعت‌های ورزشی"
    ),
    BOLD_DISPLAY(
        id = "bold_display",
        titlePersian = "تیتر چشمگیر (Bold)",
        subtitlePersian = "خوانش فوق‌العاده سریع با یک نگاه حتی در نور آفتاب"
    )
}

enum class WatchComplicationType(
    val id: String,
    val titlePersian: String,
    val shortLabelPersian: String,
    val unitPersian: String,
    val defaultVal: String
) {
    HEART_RATE("heart_rate", "سنسور ضربان قلب", "نبض", "BPM", "۷۸"),
    STEP_COUNTER("steps", "گام‌شمار و تحرک روزانه", "قدم", "قدم", "۷,۴۵۰"),
    BATTERY("battery", "شارژ باتری گلکسی واچ", "باتری", "٪", "۸۸٪"),
    CALORIES("calories", "کالری سوزانده شده", "کالری", "KCal", "۴۸۰"),
    WEATHER("weather", "آب و هوا و دما", "دما", "°C", "۲۵°"),
    OCCASIONS("occasions", "مناسبت و تعطیلی روز", "مناسبت", "", "عید نوروز"),
    SEASON_PROGRESS("season_progress", "پیشرفت فصل شمسی", "فصل", "٪", "۳۲٪"),
    GREGORIAN_DATE("gregorian", "تاریخ میلادی", "میلادی", "", "25 Sep"),
    LUNAR_DATE("lunar", "تاریخ هجری قمری", "قمری", "", "۱۳ ربیع"),
    NONE("none", "خالی / غیرفعال", "خالی", "", "")
}

enum class WidgetSize(val id: String, val titlePersian: String, val scaleFactor: Float) {
    COMPACT("compact", "کوچک (۷۵٪)", 0.8f),
    STANDARD("standard", "استاندارد (۱۰۰٪)", 1.0f),
    LARGE("large", "بزرگ (۱۲۵٪)", 1.25f),
    EXTRA_LARGE("extra_large", "خیلی بزرگ (۱۵۰٪)", 1.45f)
}

enum class WidgetColorStyle(val id: String, val titlePersian: String) {
    THEME_MATCHED("theme", "هماهنگ با تم واچ‌فیس"),
    NEON_MULTICOLOR("multicolor", "چندرنگ نئونی و ورزشی"),
    PURE_GOLD("gold", "طلای درخشان خورشیدی"),
    PERSIAN_TURQUOISE("turquoise", "فیروزه‌ای درباری"),
    MINIMAL_WHITE("white", "سفید مینیمال خالص")
}

enum class WidgetArrangement(val id: String, val titlePersian: String) {
    TOP_BATTERY_BOTTOM_SENSORS("top_bat_bot_sensors", "باتری بالا • نبض و قدم در پایین"),
    TOP_SENSORS_BOTTOM_BATTERY("top_sensors_bot_bat", "نبض و قدم در بالا • باتری در پایین"),
    HORIZONTAL_ROW_BOTTOM("row_bottom", "ردیف سه‌تایی در پایین"),
    SPLIT_LEFT_RIGHT("split_sides", "دو طرف صفحه (چپ و راست ساعت)"),
    BATTERY_ONLY("battery_only", "فقط نمایش شارژ باتری"),
    HEALTH_ONLY("health_only", "فقط نمایش نبض و گام‌شمار")
}

enum class WidgetDisplayStyle(val id: String, val titlePersian: String) {
    CAPSULE_CONTAINER("capsule", "کپسولی نئونی مدرن"),
    MINIMAL_BORDERLESS("borderless", "متنی مینیمال بدون کادر"),
    CIRCULAR_GAUGE("gauge", "گیج و نوار وضعیت دورانی")
}

data class WatchCustomSettings(
    val device: GalaxyWatchDevice = GalaxyWatchDevice.WATCH_ULTRA,
    val theme: WatchThemePalette = WatchThemePalette.AMOLED_BLACK,
    val layout: WatchFaceLayout = WatchFaceLayout.BIG_CLOCK_MODULAR,
    val font: WatchFontType = WatchFontType.VAZIR_MODERN,
    val usePersianDigits: Boolean = true,
    val showClock: Boolean = true,
    val is24Hour: Boolean = true,
    val showSeconds: Boolean = true,
    val clockSizeLarge: Boolean = true, // ساعت فوق‌العاده درشت
    val widgetSize: WidgetSize = WidgetSize.LARGE,
    val widgetColorStyle: WidgetColorStyle = WidgetColorStyle.NEON_MULTICOLOR,
    val widgetArrangement: WidgetArrangement = WidgetArrangement.TOP_BATTERY_BOTTOM_SENSORS,
    val widgetDisplayStyle: WidgetDisplayStyle = WidgetDisplayStyle.CAPSULE_CONTAINER,
    val showBatteryWidget: Boolean = true,
    val showHeartRateWidget: Boolean = true,
    val showStepsWidget: Boolean = true,
    val topSlotComplication: WatchComplicationType = WatchComplicationType.BATTERY,
    val bottomSlotComplication: WatchComplicationType = WatchComplicationType.OCCASIONS,
    val leftSlotComplication: WatchComplicationType = WatchComplicationType.HEART_RATE,
    val rightSlotComplication: WatchComplicationType = WatchComplicationType.STEP_COUNTER,
    val simulatedHeartRate: Int = 78,
    val simulatedSteps: Int = 7450,
    val simulatedBattery: Int = 88,
    val simulatedCalories: Int = 480,
    val showOccasions: Boolean = true,
    val showGregorian: Boolean = true,
    val showLunar: Boolean = true,
    val showSeasonProgress: Boolean = true,
    val isAmbientAodMode: Boolean = false,
    val isBatterySaverEnabled: Boolean = true,
    val burnInShiftOffset: Int = 0 // Anti-burn-in pixel shifting
)
