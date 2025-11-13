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
        var accumulatedAuthorCosts = 0.0

        // Oblicz premię roczną
        val yearlyBonus = if (config.uopYearlyBonus > 0.0) {
            config.uopYearlyBonus
        } else if (config.uopYearlyBonusPercent > 0.0) {
            val yearlyGross = config.uopGrossSalary * 12
            yearlyGross * (config.uopYearlyBonusPercent / 100.0)
        } else {
            0.0
        }

        val uopResults = monthsData.mapIndexed { index, monthData ->
            val authorCostPercent = config.authorCostsMonthly[index]
            // Premia wypłacana w grudniu (miesiąc 11)
            val bonus = if (index == 11) yearlyBonus else 0.0

            val result = UOPMonthResult.calculate(
                monthData = monthData,
                grossSalary = config.uopGrossSalary,
                authorCostPercent = authorCostPercent,
                accumulatedIncome = accumulatedIncome,
                accumulatedAuthorCosts = accumulatedAuthorCosts,
                bonus = bonus
            )
            accumulatedIncome += result.taxBase
            accumulatedAuthorCosts += result.authorCosts
            result
        }

        // Oblicz równoważną pensję UOP i urlop B2B
        val equivalentUopSalary = try {
            calculateEquivalentUopSalary(b2bResults.sumOf { it.net })
        } catch (e: Exception) {
            null
        }

        val equivalentB2BVacationDays = try {
            calculateEquivalentB2BVacationDays()
        } catch (e: Exception) {
            null
        }

        return YearlySummary(
            b2bResults = b2bResults,
            uopResults = uopResults,
            config = config,
            equivalentUopSalary = equivalentUopSalary,
            equivalentB2BVacationDays = equivalentB2BVacationDays
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

    /**
     * Oblicza równoważną pensję UOP potrzebną do wyrównania z B2B
     * Używa metody bisekcji do znalezienia pensji
     */
    fun calculateEquivalentUopSalary(targetB2BNet: Double): Double {
        var low = 1000.0
        var high = 100000.0
        val tolerance = 10.0 // Dokładność 10 zł

        while (high - low > tolerance) {
            val mid = (low + high) / 2.0

            // Oblicz UOP z tą pensją
            val testConfig = config.copy(uopGrossSalary = mid)
            val testCalculator = Calculator(testConfig)
            val testSummary = testCalculator.calculateYear()
            val uopNet = testSummary.uopTotalNet

            when {
                uopNet < targetB2BNet -> low = mid
                uopNet > targetB2BNet -> high = mid
                else -> return mid
            }
        }

        return (low + high) / 2.0
    }

    /**
     * Oblicza równoważną liczbę dni urlopu B2B przy zachowaniu równości z UOP
     */
    fun calculateEquivalentB2BVacationDays(): Int {
        val currentSummary = calculateYear()
        val targetNet = currentSummary.uopTotalNet

        var low = 0
        var high = 250

        while (high - low > 1) {
            val mid = (low + high) / 2

            val testConfig = config.copy(b2bVacationDays = mid)
            val testCalculator = Calculator(testConfig)
            val testSummary = testCalculator.calculateYear()
            val b2bNet = testSummary.b2bTotalNet

            when {
                b2bNet > targetNet -> low = mid
                b2bNet < targetNet -> high = mid
                else -> return mid
            }
        }

        return low
    }
}
