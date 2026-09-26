package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.calendar.SolarHijriCalendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("تقویم واچ", appName)
    }

    @Test
    fun `test solar hijri conversion accuracy`() {
        // March 21, 2026 is 1 Farvardin 1405 (Nowruz)
        val nowruz1405 = SolarHijriCalendar.fromGregorian(2026, 3, 21)
        assertEquals(1405, nowruz1405.year)
        assertEquals(1, nowruz1405.month)
        assertEquals(1, nowruz1405.day)
        assertEquals("فروردین", nowruz1405.monthName)

        // Persian digits conversion
        val persianDigits = SolarHijriCalendar.toPersianDigits("1405/01/01")
        assertEquals("۱۴۰۵/۰۱/۰۱", persianDigits)
    }
}
