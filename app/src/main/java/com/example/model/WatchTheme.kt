package com.example.model

import androidx.compose.ui.graphics.Color

enum class WatchThemePalette(
    val id: String,
    val titlePersian: String,
    val description: String,
    val isOledOptimized: Boolean,
    val backgroundColor: Color,
    val surfaceColor: Color,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val textPrimary: Color,
    val textSecondary: Color
) {
    AMOLED_BLACK(
        id = "amoled_black",
        titlePersian = "مشکی سوپرامولد (کم‌مصرف‌ترین)",
        description = "پیکسل‌های کاملاً خاموش #000000 برای حداکثر شارژدهی باتری ساعت",
        isOledOptimized = true,
        backgroundColor = Color(0xFF000000),
        surfaceColor = Color(0xFF0D0F14),
        primaryColor = Color(0xFF2DD4BF), // Mint/Teal
        secondaryColor = Color(0xFF38BDF8),
        accentColor = Color(0xFFFDE047),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFA1A1AA)
    ),
    PERSIAN_TURQUOISE(
        id = "persian_turquoise",
        titlePersian = "فیروزه نیشابور و طلا",
        description = "ترکیب اصیل فیروزه‌ای ایرانی و درخشش طلایی اسلیمی",
        isOledOptimized = false,
        backgroundColor = Color(0xFF05131D),
        surfaceColor = Color(0xFF0B2433),
        primaryColor = Color(0xFF00B4D8),
        secondaryColor = Color(0xFFF59E0B), // Gold
        accentColor = Color(0xFF38E54D),
        textPrimary = Color(0xFFF0FDF4),
        textSecondary = Color(0xFF94A3B8)
    ),
    ULTRA_ORANGE(
        id = "ultra_orange",
        titlePersian = "نارنجی اولترا سامسونگ",
        description = "الهام گرفته از گلکسی واچ اولترا برای دید فوق‌العاده در فضای باز",
        isOledOptimized = true,
        backgroundColor = Color(0xFF000000),
        surfaceColor = Color(0xFF181512),
        primaryColor = Color(0xFFFF6D00), // Vibrant Orange
        secondaryColor = Color(0xFFFFB74D),
        accentColor = Color(0xFFFF3D00),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFD4D4D8)
    ),
    ROYAL_EMERALD(
        id = "royal_emerald",
        titlePersian = "زمرد سلطنتی پارسی",
        description = "سبز تیره فاخر با نشانگرهای کریستالی روشن",
        isOledOptimized = false,
        backgroundColor = Color(0xFF021711),
        surfaceColor = Color(0xFF063326),
        primaryColor = Color(0xFF10B981),
        secondaryColor = Color(0xFF34D399),
        accentColor = Color(0xFFFBBF24),
        textPrimary = Color(0xFFECFDF5),
        textSecondary = Color(0xFF6EE7B7)
    ),
    CYBER_NEON(
        id = "cyber_neon",
        titlePersian = "آبی سایبری شب",
        description = "طراحی مدرن نئونی با کنتراست تند برای خوانایی در تاریکی",
        isOledOptimized = true,
        backgroundColor = Color(0xFF000000),
        surfaceColor = Color(0xFF0A1128),
        primaryColor = Color(0xFF00F0FF),
        secondaryColor = Color(0xFFA855F7),
        accentColor = Color(0xFFFF0055),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFF93C5FD)
    ),
    SUNSET_RUBY(
        id = "sunset_ruby",
        titlePersian = "یاقوت اناری",
        description = "رنگ‌بندی گرم انار و یاقوت ایرانی با جلوه غروب آفتاب",
        isOledOptimized = false,
        backgroundColor = Color(0xFF18080C),
        surfaceColor = Color(0xFF2C0F17),
        primaryColor = Color(0xFFF43F5E),
        secondaryColor = Color(0xFFFB7185),
        accentColor = Color(0xFFFCD34D),
        textPrimary = Color(0xFFFFF1F2),
        textSecondary = Color(0xFFFDA4AF)
    ),
    TITANIUM_SILVER(
        id = "titanium_silver",
        titlePersian = "تیتانیوم کروم مدرن",
        description = "ظاهری متالیک و های‌تک شبیه به ساعت‌های لوکس ورزشی",
        isOledOptimized = true,
        backgroundColor = Color(0xFF000000),
        surfaceColor = Color(0xFF14171C),
        primaryColor = Color(0xFFE2E8F0),
        secondaryColor = Color(0xFF94A3B8),
        accentColor = Color(0xFF38BDF8),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFF64748B)
    )
}
