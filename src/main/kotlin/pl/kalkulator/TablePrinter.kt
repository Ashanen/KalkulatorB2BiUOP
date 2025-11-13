package pl.kalkulator

/**
 * Generator tabelek w terminalu
 */
object TablePrinter {

    /**
     * Wyświetla pełne podsumowanie roczne
     */
    fun printYearlySummary(summary: YearlySummary) {
        printHeader()
        printMonthlyTable(summary)
        printYearlyTotals(summary)
        printComparison(summary)
        printNotes()
    }

    private fun printHeader() {
        println("╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗")
        println("║                           KALKULATOR B2B vs UOP - ROK 2026                                           ║")
        println("╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝")
        println()
    }

    private fun printMonthlyTable(summary: YearlySummary) {
        val hasB2BVacation = summary.config.b2bVacationDays > 0

        if (hasB2BVacation) {
            // Wersja z urlopem B2B
            println("┌─────────────┬──────┬────────┬────────┬─────────────────────────────────────────────┬──────────────────────────────────────────────────┐")
            println("│   Miesiąc   │ Dni  │ Święta │ Urlop  │         B2B (Ryczałt ${summary.config.b2bTaxRate.toInt()}%)             │                UOP                               │")
            println("│             │robocze│ (UOP)  │ (B2B)  ├──────────┬──────────┬──────────┬──────┼──────────┬──────────┬──────────┬──────────┬──────┤")
            println("│             │      │        │        │ Przychód │  Podatek │   Netto  │Godz. │  Brutto  │    ZUS   │ Zdrowot. │  Podatek │Netto │")
            println("├─────────────┼──────┼────────┼────────┼──────────┼──────────┼──────────┼──────┼──────────┼──────────┼──────────┼──────────┼──────┤")

            summary.b2bResults.zip(summary.uopResults).forEach { (b2b, uop) ->
                val monthName = Calendar2026.getMonthName(b2b.monthData.month).padEnd(11)
                val workDays = b2b.monthData.workingDays.toString().padStart(4)
                val holidays = Calendar2026.getWorkdayHolidays(uop.monthData).toString().padStart(6)
                val vacationDays = b2b.vacationDays.toString().padStart(6)

                val b2bRevenue = formatMoney(b2b.revenue)
                val b2bTax = formatMoney(b2b.tax)
                val b2bNet = formatMoney(b2b.net)
                val b2bHours = b2b.totalHours.toInt().toString().padStart(4)

                val uopGross = formatMoney(uop.grossSalary)
                val uopZus = formatMoney(uop.zusContributions)
                val uopHealth = formatMoney(uop.healthContribution)
                val uopTax = formatMoney(uop.tax)
                val uopNet = formatMoney(uop.net)

                println("│ $monthName │ $workDays │ $holidays  │ $vacationDays  │ $b2bRevenue │ $b2bTax │ $b2bNet │$b2bHours  │ $uopGross │ $uopZus │ $uopHealth │ $uopTax │$uopNet│")
            }

            println("└─────────────┴──────┴────────┴────────┴──────────┴──────────┴──────────┴──────┴──────────┴──────────┴──────────┴──────────┴──────┘")
        } else {
            // Wersja bez urlopu B2B
            println("┌─────────────┬──────┬────────┬─────────────────────────────────────────┬──────────────────────────────────────────────────┐")
            println("│   Miesiąc   │ Dni  │ Święta │         B2B (Ryczałt ${summary.config.b2bTaxRate.toInt()}%)             │                UOP                               │")
            println("│             │robocze│ (UOP)  ├──────────┬──────────┬──────────┬──────┼──────────┬──────────┬──────────┬──────────┬──────┤")
            println("│             │      │        │ Przychód │  Podatek │   Netto  │Godz. │  Brutto  │    ZUS   │ Zdrowot. │  Podatek │Netto │")
            println("├─────────────┼──────┼────────┼──────────┼──────────┼──────────┼──────┼──────────┼──────────┼──────────┼──────────┼──────┤")

            summary.b2bResults.zip(summary.uopResults).forEach { (b2b, uop) ->
                val monthName = Calendar2026.getMonthName(b2b.monthData.month).padEnd(11)
                val workDays = b2b.monthData.workingDays.toString().padStart(4)
                val holidays = Calendar2026.getWorkdayHolidays(uop.monthData).toString().padStart(6)

                val b2bRevenue = formatMoney(b2b.revenue)
                val b2bTax = formatMoney(b2b.tax)
                val b2bNet = formatMoney(b2b.net)
                val b2bHours = b2b.totalHours.toInt().toString().padStart(4)

                val uopGross = formatMoney(uop.grossSalary)
                val uopZus = formatMoney(uop.zusContributions)
                val uopHealth = formatMoney(uop.healthContribution)
                val uopTax = formatMoney(uop.tax)
                val uopNet = formatMoney(uop.net)

                println("│ $monthName │ $workDays │ $holidays  │ $b2bRevenue │ $b2bTax │ $b2bNet │$b2bHours  │ $uopGross │ $uopZus │ $uopHealth │ $uopTax │$uopNet│")
            }

            println("└─────────────┴──────┴────────┴──────────┴──────────┴──────────┴──────┴──────────┴──────────┴──────────┴──────────┴──────┘")
        }
        println()
    }

    private fun printYearlyTotals(summary: YearlySummary) {
        println("╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗")
        println("║                                    PODSUMOWANIE ROCZNE 2026                                          ║")
        println("╠═══════════════════════════════════════════════════════════════════════════════════════════════════════╣")
        println("║                                                                                                       ║")
        println("║  ┌─────────────────────────────────────────────┐   ┌──────────────────────────────────────────────┐  ║")
        println("║  │           B2B (Ryczałt ${summary.config.b2bTaxRate.toInt()}%)                 │   │              UOP                             │  ║")
        println("║  ├─────────────────────────────────────────────┤   ├──────────────────────────────────────────────┤  ║")
        println("║  │ Przychód brutto:     ${formatMoneyPadded(summary.b2bTotalRevenue)} │   │ Wynagrodzenie brutto: ${formatMoneyPadded(summary.uopTotalGross)} │  ║")
        println("║  │ Podatek (${summary.config.b2bTaxRate.toInt()}%):       ${formatMoneyPadded(summary.b2bTotalTax)} │   │ Składki ZUS:          ${formatMoneyPadded(summary.uopTotalZus)} │  ║")
        println("║  │ DO KIESZENI:         ${formatMoneyPadded(summary.b2bTotalNet)} │   │ Składka zdrowotna:    ${formatMoneyPadded(summary.uopTotalHealth)} │  ║")
        println("║  │                                             │   │ Podatek dochodowy:    ${formatMoneyPadded(summary.uopTotalTax)} │  ║")

        if (summary.config.b2bVacationDays > 0) {
            println("║  │ Dostępne dni robocze: ${summary.b2bTotalDays.toString().padStart(13)} │   │ DO KIESZENI:          ${formatMoneyPadded(summary.uopTotalNet)} │  ║")
            println("║  │ Urlop (nieopłacony):  ${summary.b2bTotalVacationDays.toString().padStart(13)} │   │                                              │  ║")
            println("║  │ Przepracowane dni:    ${summary.b2bTotalWorkedDays.toString().padStart(13)} │   │ Płatne dni wolne:     ${(summary.uopTotalHolidays + summary.config.uopVacationDays).toString().padStart(15)} │  ║")
            println("║  │ Przepracowane godz:   ${summary.b2bTotalHours.toInt().toString().padStart(13)} │   │   • Święta:           ${summary.uopTotalHolidays.toString().padStart(15)} │  ║")
            println("║  └─────────────────────────────────────────────┘   │   • Urlop:            ${summary.config.uopVacationDays.toString().padStart(15)} │  ║")
            println("║                                                     └──────────────────────────────────────────────┘  ║")
        } else {
            println("║  │ Przepracowane dni:   ${summary.b2bTotalDays.toString().padStart(15)} │   │ DO KIESZENI:          ${formatMoneyPadded(summary.uopTotalNet)} │  ║")
            println("║  │ Przepracowane godz:  ${summary.b2bTotalHours.toInt().toString().padStart(15)} │   │                                              │  ║")
            println("║  └─────────────────────────────────────────────┘   │ Płatne dni wolne:     ${(summary.uopTotalHolidays + summary.config.uopVacationDays).toString().padStart(15)} │  ║")
            println("║                                                     │   • Święta:           ${summary.uopTotalHolidays.toString().padStart(15)} │  ║")
            println("║                                                     │   • Urlop:            ${summary.config.uopVacationDays.toString().padStart(15)} │  ║")
            println("║                                                     └──────────────────────────────────────────────┘  ║")
        }
        println("╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝")
        println()
    }

    private fun printComparison(summary: YearlySummary) {
        println("╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗")
        println("║                                         PORÓWNANIE                                                    ║")
        println("╠═══════════════════════════════════════════════════════════════════════════════════════════════════════╣")

        val diff = summary.difference
        val diffAbs = kotlin.math.abs(diff)
        val diffFormatted = formatMoneyPadded(diffAbs)
        val winner = if (diff > 0) "B2B WYGRYWA" else if (diff < 0) "UOP WYGRYWA" else "REMIS"
        val symbol = if (diff > 0) "+" else if (diff < 0) "-" else " "

        println("║                                                                                                       ║")
        println("║                 Różnica (B2B - UOP):  $symbol $diffFormatted                                          ║")
        println("║                                                                                                       ║")
        println("║                          >>> $winner <<<                                                              ║")
        println("║                                                                                                       ║")

        if (diff > 0) {
            val percent = (diff / summary.uopTotalNet) * 100
            println("║                 B2B daje ${String.format("%.1f%%", percent)} więcej do kieszeni                                             ║")
        } else if (diff < 0) {
            val percent = (kotlin.math.abs(diff) / summary.b2bTotalNet) * 100
            println("║                 UOP daje ${String.format("%.1f%%", percent)} więcej do kieszeni                                             ║")
        }

        println("║                                                                                                       ║")
        println("╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝")
        println()
    }

    private fun printNotes() {
        println("═══════════════════════════════════════════════════════════════════════════════════════════════════════")
        println("UWAGI I ZAŁOŻENIA:")
        println("═══════════════════════════════════════════════════════════════════════════════════════════════════════")
        println()
        println("B2B (Ryczałt 12%):")
        println("  • Nie płaci składek ZUS (oszczędność ~13.71%)")
        println("  • Nie płaci składki zdrowotnej")
        println("  • Podatek ryczałtowy 12% od przychodu")
        println("  • Nie ma urlopu - nie pracujesz = nie zarabiasz")
        println("  • Nie ma płatnych świąt")
        println()
        println("UOP:")
        println("  • Składki ZUS pracownika: 13.71% (emerytalna 9.76% + rentowa 1.5% + chorobowa 2.45%)")
        println("  • Składka zdrowotna: 9% od podstawy (brutto - ZUS)")
        println("  • Podatek progresywny: 12% do 120k, potem 32% + kwota wolna 30k rocznie")
        println("  • Koszty autorskie: co drugi miesiąc 50%, reszta 20%")
        println("  • 20 dni urlopu rocznie (płatne)")
        println("  • Święta państwowe przypadające w dni robocze (płatne)")
        println()
        println("Święta w 2026 roku (dni robocze):")
        Calendar2026.holidays.filter {
            it.dayOfWeek != java.time.DayOfWeek.SATURDAY &&
            it.dayOfWeek != java.time.DayOfWeek.SUNDAY
        }.forEach { holiday ->
            println("  • ${Calendar2026.formatHoliday(holiday)}")
        }
        println()
        println("═══════════════════════════════════════════════════════════════════════════════════════════════════════")
    }

    private fun formatMoney(amount: Double): String {
        return String.format("%,8.0f", amount).replace(',', ' ')
    }

    private fun formatMoneyPadded(amount: Double): String {
        return String.format("%,12.2f zł", amount).replace(',', ' ')
    }
}
