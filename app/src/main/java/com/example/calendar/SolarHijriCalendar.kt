package com.example.calendar

import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * High-performance Solar Hijri (Shamsi / جلالی) Calendar Engine.
 * Implements exact O(1) mathematical conversion without heavy dependencies,
 * optimized for wearable devices (Galaxy Watch) and ultra-low battery consumption.
 */
data class SolarDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val dayOfWeek: Int, // 0 = شنبه, 1 = یکشنبه, ..., 6 = جمعه
    val dayOfYear: Int,
    val isLeapYear: Boolean
) {
    val monthName: String
        get() = SolarHijriCalendar.PERSIAN_MONTH_NAMES[month - 1]

    val dayOfWeekName: String
        get() = SolarHijriCalendar.PERSIAN_WEEK_DAYS[dayOfWeek]

    val zodiacName: String
        get() = SolarHijriCalendar.ZODIAC_NAMES[month - 1]

    val seasonName: String
        get() = when (month) {
            in 1..3 -> "بهار"
            in 4..6 -> "تابستان"
            in 7..9 -> "پاییز"
            else -> "زمستان"
        }

    val seasonProgressPercent: Int
        get() {
            val daysInSeason = when (month) {
                in 1..3 -> 93 // 31 + 31 + 31
                in 4..6 -> 93 // 31 + 31 + 31
                in 7..9 -> 90 // 30 + 30 + 30
                else -> if (isLeapYear) 90 else 89 // 30 + 30 + 29/30
            }
            val dayInSeason = when (month) {
                1 -> day
                2 -> 31 + day
                3 -> 62 + day
                4 -> day
                5 -> 31 + day
                6 -> 62 + day
                7 -> day
                8 -> 30 + day
                9 -> 60 + day
                10 -> day
                11 -> 30 + day
                else -> 60 + day
            }
            return ((dayInSeason.toFloat() / daysInSeason) * 100).toInt().coerceIn(1, 100)
        }

    val animalYearName: String
        get() {
            // Persian 12-animal cycle: (year - 3) % 12 or (year + 1) % 12
            // 1403 was Whale/Dragon (نهنگ), 1404 Snake (مار), 1405 Horse (اسب)
            val index = (year + 9) % 12
            return SolarHijriCalendar.ANIMAL_YEAR_NAMES[index]
        }

    fun format(usePersianDigits: Boolean = true): String {
        val y = if (usePersianDigits) SolarHijriCalendar.toPersianDigits(year.toString()) else year.toString()
        val m = if (usePersianDigits) SolarHijriCalendar.toPersianDigits(String.format(Locale.US, "%02d", month)) else String.format(Locale.US, "%02d", month)
        val d = if (usePersianDigits) SolarHijriCalendar.toPersianDigits(String.format(Locale.US, "%02d", day)) else String.format(Locale.US, "%02d", day)
        return "$y/$m/$d"
    }

    fun formatLong(usePersianDigits: Boolean = true): String {
        val d = if (usePersianDigits) SolarHijriCalendar.toPersianDigits(day.toString()) else day.toString()
        val y = if (usePersianDigits) SolarHijriCalendar.toPersianDigits(year.toString()) else year.toString()
        return "$dayOfWeekName $d $monthName $y"
    }
}

data class GregorianDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val monthName: String
)

data class LunarHijriDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val monthName: String
)

object SolarHijriCalendar {

    val PERSIAN_MONTH_NAMES = arrayOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    val PERSIAN_WEEK_DAYS = arrayOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه"
    )

    val PERSIAN_WEEK_DAYS_SHORT = arrayOf(
        "ش", "ی", "د", "س", "چ", "پ", "ج"
    )

    val ZODIAC_NAMES = arrayOf(
        "حمل (بره)", "ثور (گاو)", "جوزا (دوپیکر)",
        "سرطان (خرچنگ)", "اسد (شیر)", "سنبله (دوشیزه)",
        "میزان (ترازو)", "عقرب (کژدم)", "قوس (کمانگیر)",
        "جدی (بزغاله)", "دلو (آب‌ریز)", "حوت (ماهی)"
    )

    val ANIMAL_YEAR_NAMES = arrayOf(
        "موش", "گاو", "پلنگ", "خرگوش", "نهنگ", "مار",
        "اسب", "گوسفند", "میمون", "مرغ", "سگ", "خوک"
    )

    val GREGORIAN_MONTH_NAMES = arrayOf(
        "ژانویه", "فوریه", "مارس", "آوریل", "مه", "ژوئن",
        "ژوئیه", "اوت", "سپتامبر", "اکتبر", "نوامبر", "دسامبر"
    )

    val LUNAR_MONTH_NAMES = arrayOf(
        "محرم", "صفر", "ربیع‌الاول", "ربیع‌الثانی", "جمادی‌الاول", "جمادی‌الثانی",
        "رجب", "شعبان", "رمضان", "شوال", "ذی‌القعده", "ذی‌الحجه"
    )

    /**
     * Converts a Gregorian Date to Solar Hijri (Shamsi) Date using
     * standard astronomical conversion algorithm (Borkowski/Pournader).
     */
    fun fromGregorian(gYear: Int, gMonth: Int, gDay: Int): SolarDate {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy = gYear - 1600
        val gm = gMonth - 1
        val gd = gDay - 1

        var gDayNo = 365L * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400
        for (i in 0 until gm) {
            gDayNo += gDaysInMonth[i]
        }
        if (gm > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) {
            gDayNo++
        }
        gDayNo += gd

        var jDayNo = gDayNo - 79

        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        while (jm < 11 && jDayNo >= jDaysInMonth[jm]) {
            jDayNo -= jDaysInMonth[jm]
            jm++
        }

        val jMonth = jm + 1
        val jDay = (jDayNo + 1).toInt()

        val jdn = gregorianToJdn(gYear, gMonth, gDay)
        val dayOfWeek = ((jdn + 2) % 7).toInt()
        val isLeap = isSolarLeapYear(jy.toInt())

        val dayOfYear = when {
            jMonth <= 6 -> (jMonth - 1) * 31 + jDay
            else -> 186 + (jMonth - 7) * 30 + jDay
        }

        return SolarDate(
            year = jy.toInt(),
            month = jMonth,
            day = jDay,
            dayOfWeek = dayOfWeek,
            dayOfYear = dayOfYear,
            isLeapYear = isLeap
        )
    }

    fun fromDate(date: Date = Date()): SolarDate {
        val cal = Calendar.getInstance()
        cal.time = date
        return fromGregorian(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun now(): SolarDate = fromDate()

    fun getGregorianDate(date: Date = Date()): GregorianDate {
        val cal = Calendar.getInstance()
        cal.time = date
        val monthIdx = cal.get(Calendar.MONTH)
        return GregorianDate(
            year = cal.get(Calendar.YEAR),
            month = monthIdx + 1,
            day = cal.get(Calendar.DAY_OF_MONTH),
            monthName = GREGORIAN_MONTH_NAMES[monthIdx]
        )
    }

    /**
     * Approximate Islamic Lunar Hijri conversion.
     */
    fun getLunarHijriDate(date: Date = Date()): LunarHijriDate {
        val cal = Calendar.getInstance()
        cal.time = date
        val jdn = gregorianToJdn(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
        // Lunar epoch JDN is ~1948440
        val lJdn = jdn - 1948440 + 10632
        val n = ((lJdn - 1) / 10631).toInt()
        val l = lJdn - 10631 * n + 354
        val j = (((10985 - l) / 5316).toInt()) * ((50 * l) / 17719).toInt() +
                ((l / 5670).toInt()) * ((43 * l) / 15238).toInt()
        val l2 = l - (((30 - j) / 15).toInt()) * ((17719 * j) / 50).toInt() -
                ((j / 16).toInt()) * ((15238 * j) / 43).toInt() + 29
        val m = ((24 * l2) / 709).toInt()
        val d = l2 - ((709 * m) / 24).toInt()
        val y = 30 * n + j - 30

        val validMonth = (m).coerceIn(1, 12)
        val validDay = d.toInt().coerceIn(1, 30)
        return LunarHijriDate(
            year = y,
            month = validMonth,
            day = validDay,
            monthName = LUNAR_MONTH_NAMES[validMonth - 1]
        )
    }

    /**
     * Julian Day Number from Gregorian Date.
     */
    fun gregorianToJdn(year: Int, month: Int, day: Int): Long {
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        return day + (153L * m + 2) / 5 + 365L * y + y / 4 - y / 100 + y / 400 - 32045
    }

    /**
     * Converts Julian Day Number to Solar Hijri Date.
     */
    fun jdnToSolar(jdn: Long): SolarDate {
        val dep = jdn - 2121446L // Days since epoch
        val cycle = dep / 12053L // 33-year cycle (approx 12053 days)
        val cDay = dep % 12053L

        var yCycle = (cDay * 33) / 12053
        var startDayOfYCycle = (yCycle * 12053 + 16) / 33
        if (cDay < startDayOfYCycle) {
            yCycle--
            startDayOfYCycle = (yCycle * 12053 + 16) / 33
        }

        val year = (cycle * 33 + yCycle + 475).toInt()
        val dayOfYear = (cDay - startDayOfYCycle + 1).toInt()

        val isLeap = isSolarLeapYear(year)

        var month: Int
        var day: Int

        if (dayOfYear <= 186) {
            month = (dayOfYear - 1) / 31 + 1
            day = (dayOfYear - 1) % 31 + 1
        } else {
            val remain = dayOfYear - 186
            month = 6 + (remain - 1) / 30 + 1
            day = (remain - 1) % 30 + 1
        }

        // Calculate Persian Day of Week:
        // JDN modulo 7 gives day index. Saturday is 0 in Persian calendar.
        // JDN 0 was a Monday. (jdn + 1) % 7 -> 0: Sun, 1: Mon, ...
        // Saturday: (jdn + 2) % 7
        val dayOfWeek = ((jdn + 2) % 7).toInt()

        return SolarDate(
            year = year,
            month = month,
            day = day,
            dayOfWeek = dayOfWeek,
            dayOfYear = dayOfYear,
            isLeapYear = isLeap
        )
    }

    fun isSolarLeapYear(year: Int): Boolean {
        // Standard Persian calendar 33-year cycle remainder rule
        val a = ((year + 38) * 31) % 128
        return a < 31
    }

    fun getDaysInMonth(year: Int, month: Int): Int {
        return when {
            month in 1..6 -> 31
            month in 7..11 -> 30
            isSolarLeapYear(year) -> 30
            else -> 29
        }
    }

    fun toPersianDigits(input: String): String {
        val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val sb = StringBuilder(input.length)
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(persianDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }
}
