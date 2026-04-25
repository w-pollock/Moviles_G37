package com.example.moviles_g37.data

import java.util.Calendar



//dayOfWeek uses java.util.Calendar constants:
// MONDAY=2, TUESDAY=3, WEDNESDAY=4, THURSDAY=5, FRIDAY=6,
//SATURDAY=7, SUNDAY=1

data class ScheduleEntry(
    val id: String = "",
    val subject: String = "",
    val dayOfWeek: Int = Calendar.MONDAY,
    val startHour: Int = 8,
    val startMinute: Int = 0,
    val endHour: Int = 9,
    val endMinute: Int = 20,
    val room: String = "",
    val professor: String = ""
) {

    val startTotalMinutes: Int get() = startHour * 60 + startMinute
    val endTotalMinutes: Int get() = endHour * 60 + endMinute

    val timeString: String
        get() = "%02d:%02d - %02d:%02d".format(startHour, startMinute, endHour, endMinute)

    val dayName: String
        get() = when (dayOfWeek) {
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            Calendar.SUNDAY -> "Domingo"
            else -> "?"
        }

    val dayNameShort: String
        get() = when (dayOfWeek) {
            Calendar.MONDAY -> "LUN"
            Calendar.TUESDAY -> "MAR"
            Calendar.WEDNESDAY -> "MIE"
            Calendar.THURSDAY -> "JUE"
            Calendar.FRIDAY -> "VIE"
            Calendar.SATURDAY -> "SAB"
            Calendar.SUNDAY -> "DOM"
            else -> "?"
        }

    companion object {
        val ALL_WEEKDAYS = listOf(
            Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
            Calendar.THURSDAY, Calendar.FRIDAY
        )
    }
}
