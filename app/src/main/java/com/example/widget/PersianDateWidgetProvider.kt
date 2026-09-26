package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.calendar.SolarHijriCalendar
import com.example.calendar.SolarOccasions
import com.example.data.PreferencesManager
import java.util.Calendar
import java.util.Locale

class PersianDateWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val prefs = PreferencesManager(context).loadSettings()
        val today = SolarHijriCalendar.now()
        val occasions = SolarOccasions.getOccasionsFor(today.month, today.day)

        val cal = Calendar.getInstance()
        val hour = if (prefs.is24Hour) cal.get(Calendar.HOUR_OF_DAY) else {
            val h = cal.get(Calendar.HOUR)
            if (h == 0) 12 else h
        }
        val min = cal.get(Calendar.MINUTE)
        val timeRaw = String.format(Locale.US, "%02d:%02d", hour, min)
        val timeFormatted = if (prefs.usePersianDigits) SolarHijriCalendar.toPersianDigits(timeRaw) else timeRaw

        val dayNumStr = if (prefs.usePersianDigits) {
            SolarHijriCalendar.toPersianDigits(today.day.toString())
        } else {
            today.day.toString()
        }

        val yearStr = if (prefs.usePersianDigits) {
            SolarHijriCalendar.toPersianDigits(today.year.toString())
        } else {
            today.year.toString()
        }

        val fullPersianDateStr = "${today.dayOfWeekName} $dayNumStr ${today.monthName} $yearStr"
        val occasionStr = occasions.firstOrNull()?.title ?: ""

        val heartRateStr = if (prefs.usePersianDigits) {
            SolarHijriCalendar.toPersianDigits("♥ ${prefs.simulatedHeartRate}")
        } else {
            "♥ ${prefs.simulatedHeartRate}"
        }

        val stepsStr = if (prefs.usePersianDigits) {
            SolarHijriCalendar.toPersianDigits(String.format(Locale.US, "⚡ %,d", prefs.simulatedSteps))
        } else {
            String.format(Locale.US, "⚡ %,d", prefs.simulatedSteps)
        }

        val battStr = if (prefs.usePersianDigits) {
            SolarHijriCalendar.toPersianDigits("${prefs.simulatedBattery}٪")
        } else {
            "${prefs.simulatedBattery}%"
        }

        for (widgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_persian_date)

            if (prefs.showClock) {
                views.setViewVisibility(R.id.widget_time, View.VISIBLE)
                views.setTextViewText(R.id.widget_time, timeFormatted)
            } else {
                views.setViewVisibility(R.id.widget_time, View.GONE)
            }

            views.setTextViewText(R.id.widget_persian_full_date, fullPersianDateStr)
            views.setTextViewText(R.id.widget_heart_rate, heartRateStr)
            views.setTextViewText(R.id.widget_steps, stepsStr)
            views.setTextViewText(R.id.widget_battery, battStr)
            views.setTextViewText(R.id.widget_occasion, occasionStr)

            // Open app on click
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }

    companion object {
        fun updateAllWidgets(context: Context) {
            val intent = Intent(context, PersianDateWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
                ComponentName(context, PersianDateWidgetProvider::class.java)
            )
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }
}
