package pl.kalkulator

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

/**
 * Kalendarz roku 2026 ze świętami państwowymi w Polsce
 */
object Calendar2026 {

    /**
     * Lista świąt państwowych w Polsce w 2026 roku
     */
    val holidays = listOf(
        LocalDate.of(2026, 1, 1),   // Czwartek - Nowy Rok
        LocalDate.of(2026, 1, 6),   // Wtorek - Trzech Króli
        LocalDate.of(2026, 4, 6),   // Poniedziałek - Poniedziałek Wielkanocny
        LocalDate.of(2026, 5, 1),   // Piątek - Święto Pracy
        LocalDate.of(2026, 5, 3),   // Niedziela - Święto Konstytucji 3 Maja
        LocalDate.of(2026, 6, 4),   // Czwartek - Boże Ciało
        LocalDate.of(2026, 8, 15),  // Sobota - Wniebowzięcie NMP
        LocalDate.of(2026, 11, 1),  // Niedziela - Wszystkich Świętych
        LocalDate.of(2026, 11, 11), // Środa - Niepodległość
        LocalDate.of(2026, 12, 25), // Piątek - Boże Narodzenie
        LocalDate.of(2026, 12, 26)  // Sobota - Drugi dzień Bożego Narodzenia
    )

    /**
     * Generuje dane dla wszystkich miesięcy 2026 roku
     */
    fun generateYear(): List<MonthData> {
        return Month.values().map { month ->
            val monthHolidays = getHolidaysForMonth(month)
            val workingDays = calculateWorkingDays(2026, month, monthHolidays)

            MonthData(
                month = month,
                year = 2026,
                workingDays = workingDays,
                holidays = monthHolidays
            )
        }
    }

    /**
     * Zwraca święta dla danego miesiąca
     */
    private fun getHolidaysForMonth(month: Month): List<LocalDate> {
        return holidays.filter { it.month == month }
    }

    /**
     * Oblicza liczbę dni roboczych w miesiącu
     * (bez sobót, niedziel i świąt państwowych)
     */
    private fun calculateWorkingDays(year: Int, month: Month, holidays: List<LocalDate>): Int {
        val firstDay = LocalDate.of(year, month, 1)
        val lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth())

        var workingDays = 0
        var currentDay = firstDay

        while (!currentDay.isAfter(lastDay)) {
            val isWeekend = currentDay.dayOfWeek == DayOfWeek.SATURDAY ||
                           currentDay.dayOfWeek == DayOfWeek.SUNDAY
            val isHoliday = holidays.contains(currentDay)

            if (!isWeekend && !isHoliday) {
                workingDays++
            }

            currentDay = currentDay.plusDays(1)
        }

        return workingDays
    }

    /**
     * Oblicza liczbę płatnych dni wolnych dla UOP w danym miesiącu
     * (święta przypadające w dni robocze)
     */
    fun getWorkdayHolidays(monthData: MonthData): Int {
        return monthData.holidays.count { holiday ->
            val isWeekend = holiday.dayOfWeek == DayOfWeek.SATURDAY ||
                           holiday.dayOfWeek == DayOfWeek.SUNDAY
            !isWeekend
        }
    }

    /**
     * Formatuje datę świąt do czytelnej postaci
     */
    fun formatHoliday(date: LocalDate): String {
        val dayName = when (date.dayOfWeek) {
            DayOfWeek.MONDAY -> "Pon"
            DayOfWeek.TUESDAY -> "Wt"
            DayOfWeek.WEDNESDAY -> "Śr"
            DayOfWeek.THURSDAY -> "Czw"
            DayOfWeek.FRIDAY -> "Pt"
            DayOfWeek.SATURDAY -> "Sob"
            DayOfWeek.SUNDAY -> "Nie"
        }
        return "${date.dayOfMonth} ${getMonthName(date.month)} ($dayName)"
    }

    /**
     * Zwraca polską nazwę miesiąca
     */
    fun getMonthName(month: Month): String {
        return when (month) {
            Month.JANUARY -> "Styczeń"
            Month.FEBRUARY -> "Luty"
            Month.MARCH -> "Marzec"
            Month.APRIL -> "Kwiecień"
            Month.MAY -> "Maj"
            Month.JUNE -> "Czerwiec"
            Month.JULY -> "Lipiec"
            Month.AUGUST -> "Sierpień"
            Month.SEPTEMBER -> "Wrzesień"
            Month.OCTOBER -> "Październik"
            Month.NOVEMBER -> "Listopad"
            Month.DECEMBER -> "Grudzień"
        }
    }
}
