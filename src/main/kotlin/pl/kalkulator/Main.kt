package pl.kalkulator

fun main(args: Array<String>) {
    // Sprawdź czy użytkownik chce trybu interaktywnego czy domyślnego
    val useInteractive = args.isEmpty() || args.contains("-i") || args.contains("--interactive")

    val config = if (useInteractive) {
        // Tryb interaktywny - pytaj o parametry
        Config.fromUserInput()
    } else {
        // Tryb szybki - użyj domyślnych parametrów
        println()
        println("Używam domyślnych parametrów (uruchom z flagą -i dla trybu interaktywnego)")
        println()
        Config()
    }

    // Wyświetl podsumowanie konfiguracji
    config.printSummary()

    // Tworzenie kalkulatora
    val calculator = Calculator(config)

    // Obliczenia
    val summary = calculator.calculateYear()

    // Wyświetlanie wyników
    TablePrinter.printYearlySummary(summary)
}
