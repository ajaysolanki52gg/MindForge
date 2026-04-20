package com.mindforge.app.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {
    private const val DATE_PATTERN = "yyyy-MM-dd"

    fun todayKey(): String = formatCalendar(Calendar.getInstance())

    fun yesterdayKey(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return formatCalendar(calendar)
    }

    fun formatCalendar(calendar: Calendar): String {
        val formatter = SimpleDateFormat(DATE_PATTERN, Locale.US)
        return formatter.format(calendar.time)
    }
}
