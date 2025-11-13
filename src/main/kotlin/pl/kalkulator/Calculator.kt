package pl.kalkulator

/**
 * Kalkulator porównujący B2B i UOP
 */
class Calculator(
    val config: Config
) {

    /**
     * Oblicza wyniki dla całego roku 2026
     */
    fun calculateYear(): YearlySummary {
        val monthsData = Calendar2026.generateYear()

        // Rozkład urlopu B2B na miesiące (proporcjonalnie do dni roboczych)
        val vacationDistribution = distributeVacation(monthsData, config.b2bVacationDays)

        // Obliczenia B2B
        val b2bResults = monthsData.mapIndexed { index, monthData ->
            val vacationDaysThisMonth = vacationDistribution[index]
            B2BMonthResult.calculate(
                monthData = monthData,
                hourlyRate = config.b2bHourlyRate,
                hoursPerDay = config.b2bHoursPerDay,
                taxRate = config.b2bTaxRate,
                vacationDays = vacationDaysThisMonth
            )
        }

        // Obliczenia UOP
        var accumulatedIncome = 0.0
        val uopResults = monthsData.mapIndexed { index, monthData ->
            val authorCostPercent = config.authorCostsMonthly[index]
            val result = UOPMonthResult.calculate(
                monthData = monthData,
                grossSalary = config.uopGrossSalary,
                authorCostPercent = authorCostPercent,
                accumulatedIncome = accumulatedIncome
            )
            accumulatedIncome += result.taxBase
            result
        }

        return YearlySummary(
            b2bResults = b2bResults,
            uopResults = uopResults,
            config = config
        )
    }

    /**
     * Rozkłada dni urlopu proporcjonalnie do liczby dni roboczych w miesiącach
     */
    private fun distributeVacation(monthsData: List<MonthData>, totalVacationDays: Int): List<Int> {
        if (totalVacationDays == 0) {
            return List(12) { 0 }
        }

        val totalWorkingDays = monthsData.sumOf { it.workingDays }
        val distribution = mutableListOf<Int>()
        var remainingVacation = totalVacationDays

        monthsData.forEachIndexed { index, monthData ->
            val proportionalVacation = if (index == 11) {
                // Ostatni miesiąc - przypisz resztę
                remainingVacation
            } else {
                val proportion = monthData.workingDays.toDouble() / totalWorkingDays
                val vacationForMonth = (totalVacationDays * proportion).toInt()
                remainingVacation -= vacationForMonth
                vacationForMonth
            }
            distribution.add(proportionalVacation)
        }

        return distribution
    }
}
