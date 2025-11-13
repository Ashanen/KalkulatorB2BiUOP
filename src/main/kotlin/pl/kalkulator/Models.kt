package pl.kalkulator

import java.time.LocalDate
import java.time.Month

/**
 * Dane miesiąca
 */
data class MonthData(
    val month: Month,
    val year: Int,
    val workingDays: Int,
    val holidays: List<LocalDate>
)

/**
 * Wynik obliczeń B2B dla miesiąca
 */
data class B2BMonthResult(
    val monthData: MonthData,
    val hourlyRate: Double,
    val hoursPerDay: Double,
    val taxRate: Double,
    val revenue: Double,
    val tax: Double,
    val net: Double,
    val totalHours: Double
) {
    companion object {
        fun calculate(
            monthData: MonthData,
            hourlyRate: Double,
            hoursPerDay: Double,
            taxRate: Double
        ): B2BMonthResult {
            val totalHours = monthData.workingDays * hoursPerDay
            val revenue = totalHours * hourlyRate
            val tax = revenue * (taxRate / 100.0)
            val net = revenue - tax

            return B2BMonthResult(
                monthData = monthData,
                hourlyRate = hourlyRate,
                hoursPerDay = hoursPerDay,
                taxRate = taxRate,
                revenue = revenue,
                tax = tax,
                net = net,
                totalHours = totalHours
            )
        }
    }
}

/**
 * Wynik obliczeń UOP dla miesiąca
 */
data class UOPMonthResult(
    val monthData: MonthData,
    val grossSalary: Double,
    val authorCostPercent: Int,
    val zusContributions: Double,
    val healthContribution: Double,
    val healthDeduction: Double,
    val taxBase: Double,
    val tax: Double,
    val net: Double
) {
    companion object {
        fun calculate(
            monthData: MonthData,
            grossSalary: Double,
            authorCostPercent: Int,
            accumulatedIncome: Double
        ): UOPMonthResult {
            // Składki ZUS (pracownik)
            val zusEmployeeRate = 0.1371 // 9.76% emerytalna + 1.5% rentowa + 2.45% chorobowa
            val zusContributions = grossSalary * zusEmployeeRate

            // Podstawa wymiaru składki zdrowotnej
            val healthBase = grossSalary - zusContributions
            val healthContribution = healthBase * 0.09
            val healthDeduction = healthBase * 0.0775

            // Koszty uzyskania przychodu (autorskie)
            val costs = grossSalary * (authorCostPercent / 100.0)

            // Podstawa opodatkowania
            val taxableIncome = grossSalary - zusContributions - costs
            val taxBase = (taxableIncome / 10.0).toInt() * 10.0 // Zaokrąglenie do 10 zł

            // Obliczenie podatku
            val newAccumulatedIncome = accumulatedIncome + taxBase
            val tax = calculateTax(newAccumulatedIncome, accumulatedIncome, taxBase)

            // Netto
            val net = grossSalary - zusContributions - healthContribution - tax

            return UOPMonthResult(
                monthData = monthData,
                grossSalary = grossSalary,
                authorCostPercent = authorCostPercent,
                zusContributions = zusContributions,
                healthContribution = healthContribution,
                healthDeduction = healthDeduction,
                taxBase = taxBase,
                tax = tax,
                net = net
            )
        }

        private fun calculateTax(
            newAccumulatedIncome: Double,
            oldAccumulatedIncome: Double,
            currentTaxBase: Double
        ): Double {
            val threshold1 = 120000.0
            val freeAmount = 30000.0

            // Podatek od skumulowanego dochodu (nowy)
            val taxFromNew = when {
                newAccumulatedIncome <= threshold1 -> newAccumulatedIncome * 0.12 - (freeAmount * 0.12)
                else -> {
                    val part1 = threshold1 * 0.12
                    val part2 = (newAccumulatedIncome - threshold1) * 0.32
                    part1 + part2 - (freeAmount * 0.12)
                }
            }.coerceAtLeast(0.0)

            // Podatek od skumulowanego dochodu (stary)
            val taxFromOld = when {
                oldAccumulatedIncome <= threshold1 -> oldAccumulatedIncome * 0.12 - (freeAmount * 0.12)
                else -> {
                    val part1 = threshold1 * 0.12
                    val part2 = (oldAccumulatedIncome - threshold1) * 0.32
                    part1 + part2 - (freeAmount * 0.12)
                }
            }.coerceAtLeast(0.0)

            return taxFromNew - taxFromOld
        }
    }
}

/**
 * Roczne podsumowanie
 */
data class YearlySummary(
    val b2bResults: List<B2BMonthResult>,
    val uopResults: List<UOPMonthResult>
) {
    val b2bTotalRevenue = b2bResults.sumOf { it.revenue }
    val b2bTotalTax = b2bResults.sumOf { it.tax }
    val b2bTotalNet = b2bResults.sumOf { it.net }
    val b2bTotalDays = b2bResults.sumOf { it.monthData.workingDays }
    val b2bTotalHours = b2bResults.sumOf { it.totalHours }

    val uopTotalGross = uopResults.sumOf { it.grossSalary }
    val uopTotalZus = uopResults.sumOf { it.zusContributions }
    val uopTotalHealth = uopResults.sumOf { it.healthContribution }
    val uopTotalTax = uopResults.sumOf { it.tax }
    val uopTotalNet = uopResults.sumOf { it.net }
    val uopTotalHolidays = uopResults.sumOf { it.monthData.holidays.size }

    val difference = b2bTotalNet - uopTotalNet
    val winner = if (difference > 0) "B2B" else if (difference < 0) "UOP" else "Remis"
}
