package com.example.calendar

/**
 * Iranian Solar Calendar Occasions and Holidays.
 * Displays national celebrations, historical Iranian events, and official holidays.
 */
data class PersianOccasion(
    val title: String,
    val isHoliday: Boolean = false,
    val description: String = ""
)

object SolarOccasions {

    private val OCCASIONS_MAP = mapOf(
        // فروردین (Month 1)
        "1-1" to listOf(PersianOccasion("جشن نوروز / آغاز سال نو", true, "عید باستانی نوروز")),
        "1-2" to listOf(PersianOccasion("عید نوروز", true)),
        "1-3" to listOf(PersianOccasion("عید نوروز", true)),
        "1-4" to listOf(PersianOccasion("عید نوروز", true)),
        "1-6" to listOf(PersianOccasion("روز امید / زایش زرتشت")),
        "1-12" to listOf(PersianOccasion("روز جمهوری اسلامی ایران", true)),
        "1-13" to listOf(PersianOccasion("روز طبیعت (سیزده‌بدر)", true, "جشن باستانی سیزده‌بدر")),
        "1-18" to listOf(PersianOccasion("روز سلامت و بهداشت")),
        "1-25" to listOf(PersianOccasion("بزرگداشت عطار نیشابوری")),
        "1-29" to listOf(PersianOccasion("روز ارتش جمهوری اسلامی ایران")),

        // اردیبهشت (Month 2)
        "2-1" to listOf(PersianOccasion("بزرگداشت سعدی شیرازی")),
        "2-2" to listOf(PersianOccasion("جشن اردیبهشتگان / روز زمین پاک")),
        "2-10" to listOf(PersianOccasion("روز ملی خلیج فارس")),
        "2-12" to listOf(PersianOccasion("روز معلم و استاد")),
        "2-15" to listOf(PersianOccasion("روز جهانی ماما / جشن بهاربد")),
        "2-25" to listOf(PersianOccasion("بزرگداشت حکیم فردوسی / روز پاسداشت زبان فارسی")),
        "2-28" to listOf(PersianOccasion("بزرگداشت حکیم عمر خیام نیشابوری / روز جهانی موزه")),

        // خرداد (Month 3)
        "3-1" to listOf(PersianOccasion("بزرگداشت ملاصدرا")),
        "3-3" to listOf(PersianOccasion("فتح خرمشهر / روز مقاومت")),
        "3-6" to listOf(PersianOccasion("جشن خردادگان")),
        "3-14" to listOf(PersianOccasion("رحلت امام خمینی", true)),
        "3-15" to listOf(PersianOccasion("قیام خونین ۱۵ خرداد", true)),
        "3-31" to listOf(PersianOccasion("شهادت دکتر مصطفی چمران")),

        // تیر (Month 4)
        "4-1" to listOf(PersianOccasion("جشن آب‌پاشونک / آغاز تابستان")),
        "4-7" to listOf(PersianOccasion("شهادت آیت‌الله بهشتی / روز قوه قضاییه")),
        "4-10" to listOf(PersianOccasion("روز صنعت و معدن")),
        "4-13" to listOf(PersianOccasion("جشن تیرگان / روز قلم")),
        "4-25" to listOf(PersianOccasion("روز بهزیستی و تامین اجتماعی")),

        // مرداد (Month 5)
        "5-7" to listOf(PersianOccasion("جشن مردادگان")),
        "5-8" to listOf(PersianOccasion("بزرگداشت شیخ شهاب‌الدین سهروردی")),
        "5-14" to listOf(PersianOccasion("صدور فرمان مشروطیت")),
        "5-17" to listOf(PersianOccasion("روز خبرنگار")),
        "5-28" to listOf(PersianOccasion("کودتای ۲۸ مرداد")),

        // شهریور (Month 6)
        "6-1" to listOf(PersianOccasion("روز پزشک / بزرگداشت بوعلی سینا")),
        "6-2" to listOf(PersianOccasion("آغاز هفته دولت")),
        "6-4" to listOf(PersianOccasion("جشن شهریورگان / روز کارمند")),
        "6-5" to listOf(PersianOccasion("روز داروسازی / بزرگداشت زکریای رازی")),
        "6-13" to listOf(PersianOccasion("روز تعاون / بزرگداشت ابوریحان بیرونی")),
        "6-21" to listOf(PersianOccasion("روز ملی سینما")),
        "6-27" to listOf(PersianOccasion("روز شعر و ادب پارسی / بزرگداشت استاد شهریار")),
        "6-31" to listOf(PersianOccasion("آغاز هفته دفاع مقدس")),

        // مهر (Month 7)
        "7-1" to listOf(PersianOccasion("آغاز سال تحصیلی / پاییز")),
        "7-8" to listOf(PersianOccasion("بزرگداشت مولوی (مولانا)")),
        "7-10" to listOf(PersianOccasion("جشن مهرگان")),
        "7-16" to listOf(PersianOccasion("روز جهانی کودک")),
        "7-20" to listOf(PersianOccasion("بزرگداشت حافظ شیرازی")),
        "7-26" to listOf(PersianOccasion("روز تربیت بدنی و ورزش")),

        // آبان (Month 8)
        "8-7" to listOf(PersianOccasion("بزرگداشت کوروش بزرگ")),
        "8-10" to listOf(PersianOccasion("جشن آبانگان")),
        "8-13" to listOf(PersianOccasion("روز دانش‌آموز")),
        "8-24" to listOf(PersianOccasion("روز کتاب و کتابخوانی / بزرگداشت علامه طباطبایی")),

        // آذر (Month 9)
        "9-1" to listOf(PersianOccasion("جشن آذرجشن")),
        "9-9" to listOf(PersianOccasion("جشن آذرگان")),
        "9-16" to listOf(PersianOccasion("روز دانشجو")),
        "9-25" to listOf(PersianOccasion("روز پژوهش")),
        "9-30" to listOf(PersianOccasion("شب یلدا (چله)", false, "طولانی‌ترین شب سال و جشن باستانی یلدا")),

        // دی (Month 10)
        "10-1" to listOf(PersianOccasion("جشن خرم‌روز / آغاز زمستان")),
        "10-5" to listOf(PersianOccasion("روز ایمنی در برابر زلزله / یادبود زلزله بم")),
        "10-14" to listOf(PersianOccasion("جشن سیرسور")),
        "10-20" to listOf(PersianOccasion("سالروز شهادت میرزا تقی خان امیرکبیر")),

        // بهمن (Month 11)
        "11-2" to listOf(PersianOccasion("جشن بهمنگان")),
        "11-10" to listOf(PersianOccasion("جشن سده")),
        "11-12" to listOf(PersianOccasion("آغاز دهه فجر انقلاب اسلامی")),
        "11-22" to listOf(PersianOccasion("پیروزی انقلاب اسلامی", true)),
        "11-29" to listOf(PersianOccasion("جشن سپندارمذگان / روز مهر ایرانی")),

        // اسفند (Month 12)
        "12-5" to listOf(PersianOccasion("روز مهندس / بزرگداشت خواجه نصیرالدین طوسی")),
        "12-15" to listOf(PersianOccasion("روز درختکاری")),
        "12-25" to listOf(PersianOccasion("پایان سرایش شاهنامه فردوسی")),
        "12-29" to listOf(PersianOccasion("روز ملی شدن صنعت نفت ایران", true))
    )

    fun getOccasionsFor(month: Int, day: Int): List<PersianOccasion> {
        val key = "$month-$day"
        return OCCASIONS_MAP[key] ?: emptyList()
    }
}
