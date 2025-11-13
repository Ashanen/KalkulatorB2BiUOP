package pl.kalkulator

/**
 * Konfiguracja kalkulatora
 */
data class Config(
    // B2B
    val b2bHourlyRate: Double = 190.0,
    val b2bHoursPerDay: Double = 8.0,
    val b2bTaxRate: Double = 12.0,
    val b2bVacationDays: Int = 0,  // Dni urlopu do odliczenia

    // UOP
    val uopGrossSalary: Double = 28000.0,
    val uopVacationDays: Int = 20,
    val authorCostsMonthly: List<Int> = List(12) { if (it % 2 == 0) 50 else 20 },
    val uopYearlyBonus: Double = 0.0,        // Premia roczna (kwota)
    val uopYearlyBonusPercent: Double = 0.0  // Premia roczna (procent rocznego brutto)
) {
    companion object {
        /**
         * Interaktywne wprowadzanie konfiguracji
         */
        fun fromUserInput(): Config {
            println("╔═══════════════════════════════════════════════════════════════╗")
            println("║     KALKULATOR B2B vs UOP - KONFIGURACJA PARAMETRÓW          ║")
            println("╚═══════════════════════════════════════════════════════════════╝")
            println()

            // B2B
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("PARAMETRY B2B:")
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            val b2bRate = readDoubleWithDefault("Stawka godzinowa (zł/h)", 190.0)
            val b2bHours = readDoubleWithDefault("Liczba godzin dziennie", 8.0)
            val b2bTax = readDoubleWithDefault("Podatek ryczałtowy (%)", 12.0)
            val b2bVacation = readIntWithDefault("Dni urlopu do odliczenia (0 = brak)", 0)

            println()

            // UOP
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("PARAMETRY UOP:")
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            val uopSalary = readDoubleWithDefault("Wynagrodzenie brutto (zł/mc)", 28000.0)
            val uopVacation = readIntWithDefault("Dni urlopu rocznie", 20)

            println()
            println("Premia roczna:")
            println("  1. Brak premii")
            println("  2. Premia w kwocie stałej")
            println("  3. Premia jako % rocznego brutto")
            val bonusChoice = readIntWithDefault("Wybierz opcję (1-3)", 1)

            val (bonusAmount, bonusPercent) = when (bonusChoice) {
                2 -> {
                    val amount = readDoubleWithDefault("  Kwota premii (zł)", 0.0)
                    Pair(amount, 0.0)
                }
                3 -> {
                    val percent = readDoubleWithDefault("  Procent rocznego brutto (%)", 0.0)
                    Pair(0.0, percent)
                }
                else -> Pair(0.0, 0.0)
            }

            println()
            println("Koszty uzyskania przychodu (autorskie):")
            println("  1. Zawsze 20%")
            println("  2. Zawsze 50%")
            println("  3. Mieszane 50%/20% (co drugi miesiąc)")
            println("  4. Własny rozkład dla każdego miesiąca")

            val authorChoice = readIntWithDefault("Wybierz opcję (1-4)", 3)

            val authorCosts = when (authorChoice) {
                1 -> List(12) { 20 }
                2 -> List(12) { 50 }
                3 -> List(12) { if (it % 2 == 0) 50 else 20 }
                4 -> {
                    println()
                    println("Wprowadź % kosztów autorskich dla każdego miesiąca (20 lub 50):")
                    List(12) { monthIndex ->
                        val monthName = Calendar2026.getMonthName(
                            java.time.Month.of(monthIndex + 1)
                        )
                        readIntInRange("  $monthName", 20, 50, listOf(20, 50))
                    }
                }
                else -> List(12) { if (it % 2 == 0) 50 else 20 }
            }

            println()
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

            return Config(
                b2bHourlyRate = b2bRate,
                b2bHoursPerDay = b2bHours,
                b2bTaxRate = b2bTax,
                b2bVacationDays = b2bVacation,
                uopGrossSalary = uopSalary,
                uopVacationDays = uopVacation,
                authorCostsMonthly = authorCosts,
                uopYearlyBonus = bonusAmount,
                uopYearlyBonusPercent = bonusPercent
            )
        }

        /**
         * Wczytanie liczby zmiennoprzecinkowej z wartością domyślną
         */
        private fun readDoubleWithDefault(prompt: String, default: Double): Double {
            print("$prompt [${default}]: ")
            val input = readlnOrNull()?.trim()
            return if (input.isNullOrBlank()) {
                default
            } else {
                input.toDoubleOrNull() ?: default
            }
        }

        /**
         * Wczytanie liczby całkowitej z wartością domyślną
         */
        private fun readIntWithDefault(prompt: String, default: Int): Int {
            print("$prompt [${default}]: ")
            val input = readlnOrNull()?.trim()
            return if (input.isNullOrBlank()) {
                default
            } else {
                input.toIntOrNull() ?: default
            }
        }

        /**
         * Wczytanie liczby całkowitej z zakresu
         */
        private fun readIntInRange(
            prompt: String,
            min: Int,
            max: Int,
            allowedValues: List<Int>? = null
        ): Int {
            while (true) {
                print("$prompt ($min-$max): ")
                val input = readlnOrNull()?.trim()?.toIntOrNull()

                if (input != null) {
                    if (allowedValues != null) {
                        if (input in allowedValues) return input
                        println("  ⚠ Dozwolone wartości: ${allowedValues.joinToString(", ")}")
                    } else if (input in min..max) {
                        return input
                    } else {
                        println("  ⚠ Wartość musi być w zakresie $min-$max")
                    }
                } else {
                    println("  ⚠ Wprowadź poprawną liczbę")
                }
            }
        }
    }

    /**
     * Wyświetla podsumowanie konfiguracji
     */
    fun printSummary() {
        println()
        println("╔═══════════════════════════════════════════════════════════════╗")
        println("║              PODSUMOWANIE PARAMETRÓW                          ║")
        println("╚═══════════════════════════════════════════════════════════════╝")
        println()
        println("B2B (Ryczałt $b2bTaxRate%):")
        println("  • Stawka godzinowa: $b2bHourlyRate zł/h")
        println("  • Godzin dziennie: $b2bHoursPerDay h")
        println("  • Podatek ryczałtowy: $b2bTaxRate%")
        if (b2bVacationDays > 0) {
            println("  • Odliczony urlop: $b2bVacationDays dni (nieopłacone)")
        } else {
            println("  • Urlop: brak (pracujesz cały rok)")
        }
        println()
        println("UOP:")
        println("  • Wynagrodzenie brutto: ${String.format("%,.0f", uopGrossSalary)} zł/mc")
        println("  • Dni urlopu: $uopVacationDays dni (płatne)")

        // Premia roczna
        if (uopYearlyBonus > 0.0) {
            println("  • Premia roczna: ${String.format("%,.0f", uopYearlyBonus)} zł")
        } else if (uopYearlyBonusPercent > 0.0) {
            val yearlyGross = uopGrossSalary * 12
            val bonusAmount = yearlyGross * (uopYearlyBonusPercent / 100.0)
            println("  • Premia roczna: ${String.format("%.1f", uopYearlyBonusPercent)}% (${String.format("%,.0f", bonusAmount)} zł)")
        }

        println("  • Koszty autorskie:")

        val costsPattern = authorCostsMonthly.joinToString(", ") { "${it}%" }
        if (authorCostsMonthly.all { it == 20 }) {
            println("    Zawsze 20%")
        } else if (authorCostsMonthly.all { it == 50 }) {
            println("    Zawsze 50%")
        } else if (authorCostsMonthly.withIndex().all { (idx, v) -> v == if (idx % 2 == 0) 50 else 20 }) {
            println("    Mieszane 50%/20% (co drugi miesiąc)")
        } else {
            println("    Własny rozkład: $costsPattern")
        }
        println()
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        println()
    }
}
