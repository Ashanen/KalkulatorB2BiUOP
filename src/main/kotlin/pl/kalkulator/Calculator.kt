package pl.kalkulator

/**
 * Kalkulator porównujący B2B i UOP
 */
class Calculator(
    val b2bHourlyRate: Double = 190.0,
    val b2bHoursPerDay: Double = 8.0,
    val b2bTaxRate: Double = 12.0,
    val uopGrossSalary: Double = 28000.0,
    val uopAuthorCostStrategy: AuthorCostStrategy = AuthorCostStrategy.Mixed50_50
) {

    /**
     * Strategia kosztów autorskich
     */
    enum class AuthorCostStrategy {
        ALWAYS_20,  // Zawsze 20%
        ALWAYS_50,  // Zawsze 50%
        Mixed50_50  // 50% miesięcy z 50%, reszta 20%
    }

    /**
     * Oblicza wyniki dla całego roku 2026
     */
    fun calculateYear(): YearlySummary {
        val monthsData = Calendar2026.generateYear()

        // Obliczenia B2B
        val b2bResults = monthsData.map { monthData ->
            B2BMonthResult.calculate(
                monthData = monthData,
                hourlyRate = b2bHourlyRate,
                hoursPerDay = b2bHoursPerDay,
                taxRate = b2bTaxRate
            )
        }

        // Obliczenia UOP
        var accumulatedIncome = 0.0
        val uopResults = monthsData.mapIndexed { index, monthData ->
            val authorCostPercent = getAuthorCostPercent(index)
            val result = UOPMonthResult.calculate(
                monthData = monthData,
                grossSalary = uopGrossSalary,
                authorCostPercent = authorCostPercent,
                accumulatedIncome = accumulatedIncome
            )
            accumulatedIncome += result.taxBase
            result
        }

        return YearlySummary(
            b2bResults = b2bResults,
            uopResults = uopResults
        )
    }

    /**
     * Zwraca procent kosztów autorskich dla danego miesiąca
     */
    private fun getAuthorCostPercent(monthIndex: Int): Int {
        return when (uopAuthorCostStrategy) {
            AuthorCostStrategy.ALWAYS_20 -> 20
            AuthorCostStrategy.ALWAYS_50 -> 50
            AuthorCostStrategy.Mixed50_50 -> {
                // 6 miesięcy z 50%, 6 miesięcy z 20%
                if (monthIndex % 2 == 0) 50 else 20
            }
        }
    }
}
