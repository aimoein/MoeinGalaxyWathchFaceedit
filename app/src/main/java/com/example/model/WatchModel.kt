package com.example.model

import androidx.compose.ui.graphics.Color

enum class GalaxyWatchDevice(
    val id: String,
    val titlePersian: String,
    val screenSizeMm: String,
    val caseShape: String,
    val bezelType: String,
    val outerColor: Color,
    val accentColor: Color,
    val bezelRingWidthRatio: Float
) {
    WATCH_ULTRA(
        id = "watch_ultra",
        titlePersian = "گلکسی واچ اولترا (۴۷ میلی‌متر)",
        screenSizeMm = "47mm",
        caseShape = "Cushion Titanium",
        bezelType = "حاشیه محافظتی تیتانیوم با خطوط اورنج",
        outerColor = Color(0xFF1E2229),
        accentColor = Color(0xFFFF6D00),
        bezelRingWidthRatio = 0.12f
    ),
    WATCH_7(
        id = "watch_7",
        titlePersian = "گلکسی واچ ۷ (۴۴ میلی‌متر)",
        screenSizeMm = "44mm",
        caseShape = "Circular Slim",
        bezelType = "فریم آلومینیومی باریک و شیشه ضدخش یاقوت",
        outerColor = Color(0xFF242B35),
        accentColor = Color(0xFF38BDF8),
        bezelRingWidthRatio = 0.08f
    ),
    WATCH_6_CLASSIC(
        id = "watch_6_classic",
        titlePersian = "گلکسی واچ ۶ کلاسیک (۴۷ میلی‌متر)",
        screenSizeMm = "47mm",
        caseShape = "Chrono Stainless",
        bezelType = "بزل گردان فیزیکی با شیارهای ۶۰ دقیقه‌ای",
        outerColor = Color(0xFF181A20),
        accentColor = Color(0xFFE2E8F0),
        bezelRingWidthRatio = 0.14f
    ),
    WATCH_5_PRO(
        id = "watch_5_pro",
        titlePersian = "گلکسی واچ ۵ پرو (۴۵ میلی‌متر)",
        screenSizeMm = "45mm",
        caseShape = "Titanium Concave",
        bezelType = "حاشیه فرورفته تیتانیوم ضد ضربه",
        outerColor = Color(0xFF23272E),
        accentColor = Color(0xFF10B981),
        bezelRingWidthRatio = 0.11f
    ),
    WATCH_4_ACTIVE(
        id = "watch_4_active",
        titlePersian = "گلکسی واچ ۴ / اکتیو (۴۰ میلی‌متر)",
        screenSizeMm = "40mm",
        caseShape = "Minimalist",
        bezelType = "لبه صاف مینیمال تاچ هوشمند",
        outerColor = Color(0xFF1B1D24),
        accentColor = Color(0xFFA855F7),
        bezelRingWidthRatio = 0.07f
    )
}
