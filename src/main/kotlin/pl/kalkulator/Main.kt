package pl.kalkulator

fun main() {
    // Parametry domyślne
    val b2bRate = 190.0        // zł/h
    val b2bHours = 8.0         // godzin dziennie
    val b2bTax = 12.0          // procent podatku ryczałtowego
    val uopSalary = 28000.0    // zł brutto miesięcznie
    val authorCostStrategy = Calculator.AuthorCostStrategy.Mixed50_50  // 50/20 na zmianę

    println()
    println("Parametry obliczeniowe:")
    println("  B2B: $b2bRate zł/h × $b2bHours h/dzień, podatek ryczałtowy $b2bTax%")
    println("  UOP: ${String.format("%,.0f", uopSalary)} zł brutto/mc, koszty autorskie 50%/20% (na zmianę)")
    println("  UOP: 20 dni urlopu + płatne święta")
    println()

    // Tworzenie kalkulatora
    val calculator = Calculator(
        b2bHourlyRate = b2bRate,
        b2bHoursPerDay = b2bHours,
        b2bTaxRate = b2bTax,
        uopGrossSalary = uopSalary,
        uopAuthorCostStrategy = authorCostStrategy
    )

    // Obliczenia
    val summary = calculator.calculateYear()

    // Wyświetlanie wyników
    TablePrinter.printYearlySummary(summary)
}
