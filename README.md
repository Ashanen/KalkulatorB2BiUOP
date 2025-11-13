# Kalkulator B2B vs UOP - Rok 2026

Aplikacja konsolowa w Kotlinie do porównania zarobków na B2B (ryczałt 12%) vs UOP dla roku 2026.

## Funkcje

- ✅ Dokładne obliczenia dla każdego miesiąca 2026 roku
- ✅ Uwzględnia rzeczywiste dni robocze (bez sobót, niedziel i świąt)
- ✅ Wszystkie święta państwowe w Polsce na 2026 rok
- ✅ B2B: ryczałt 12%, brak ZUS
- ✅ UOP: składki ZUS, zdrowotna, podatek progresywny, koszty autorskie 20-50%
- ✅ Czytelna tabelka w terminalu z podsumowaniem miesięcznym i rocznym
- ✅ Automatyczne porównanie który wariant jest lepszy

## Parametry

**B2B:**
- Stawka godzinowa: 190 zł/h
- Godzin dziennie: 8h
- Podatek ryczałtowy: 12%
- Brak składek ZUS
- Brak urlopu i płatnych świąt

**UOP:**
- Wynagrodzenie brutto: 28 000 zł/mc
- Składki ZUS: 13.71% (emerytalna + rentowa + chorobowa)
- Składka zdrowotna: 9% (odliczana od podatku 7.75%)
- Podatek progresywny: 12% do 120k, potem 32%
- Koszty autorskie: 50% i 20% na zmianę co miesiąc
- 20 dni urlopu + płatne święta

## Wymagania

- Java 17 lub nowsza
- Gradle (opcjonalnie, można użyć wrapper'a)

## Uruchomienie

### Opcja 1: Gradle Wrapper (zalecane)

```bash
./gradlew run
```

### Opcja 2: Kompilacja i uruchomienie JAR

```bash
./gradlew jar
java -jar build/libs/KalkulatorB2BiUOP-1.0.0.jar
```

### Opcja 3: Bezpośrednio przez Gradle

```bash
gradle run
```

## Struktura projektu

```
KalkulatorB2BiUOP/
├── src/main/kotlin/pl/kalkulator/
│   ├── Main.kt              # Punkt wejścia aplikacji
│   ├── Models.kt            # Data classes (MonthData, B2BMonthResult, UOPMonthResult, YearlySummary)
│   ├── Calendar2026.kt      # Kalendarz ze świętami i dniami roboczymi dla 2026
│   ├── Calculator.kt        # Logika obliczeń B2B i UOP
│   └── TablePrinter.kt      # Generator tabelek w terminalu
├── build.gradle.kts         # Konfiguracja Gradle
└── README.md
```

## Przykładowy output

```
┌─────────────┬──────┬────────┬─────────────────────────────────────────┬──────────────────────────────────────────────────┐
│   Miesiąc   │ Dni  │ Święta │              B2B (Ryczałt 12%)          │                UOP                               │
│             │robocze│ (UOP)  ├──────────┬──────────┬──────────┬──────┼──────────┬──────────┬──────────┬──────────┬──────┤
│             │      │        │ Przychód │  Podatek │   Netto  │Godz. │  Brutto  │    ZUS   │ Zdrowot. │  Podatek │Netto │
├─────────────┼──────┼────────┼──────────┼──────────┼──────────┼──────┼──────────┼──────────┼──────────┼──────────┼──────┤
│ Styczeń     │   20 │      2 │  30 400  │   3 648  │  26 752  │ 160  │  28 000  │   3 839  │   2 175  │   1 260  │20 726│
│ Luty        │   20 │      0 │  30 400  │   3 648  │  26 752  │ 160  │  28 000  │   3 839  │   2 175  │   2 214  │19 772│
...
```

## Święta w 2026 roku

- 1 stycznia (Czwartek) - Nowy Rok
- 6 stycznia (Wtorek) - Trzech Króli
- 6 kwietnia (Poniedziałek) - Poniedziałek Wielkanocny
- 1 maja (Piątek) - Święto Pracy
- 3 maja (Niedziela) - Święto Konstytucji 3 Maja
- 4 czerwca (Czwartek) - Boże Ciało
- 15 sierpnia (Sobota) - Wniebowzięcie NMP
- 1 listopada (Niedziela) - Wszystkich Świętych
- 11 listopada (Środa) - Niepodległość
- 25 grudnia (Piątek) - Boże Narodzenie
- 26 grudnia (Sobota) - Drugi dzień Bożego Narodzenia

**Płatne dni wolne w UOP (święta w dni robocze): 6 dni**

## Modyfikacja parametrów

Aby zmienić parametry obliczeń, edytuj wartości w pliku `src/main/kotlin/pl/kalkulator/Main.kt`:

```kotlin
val b2bRate = 190.0        // Zmień stawkę godzinową B2B
val uopSalary = 28000.0    // Zmień wynagrodzenie brutto UOP
val b2bTax = 12.0          // Zmień % podatku ryczałtowego
// itd.
```

## Licencja

MIT
