# Kalkulator B2B vs UOP - Rok 2026

Interaktywna aplikacja konsolowa w Kotlinie do porównania zarobków na B2B (ryczałt) vs UOP dla roku 2026.

## Funkcje

- ✅ **Interaktywne wprowadzanie parametrów** - pełna kontrola nad wszystkimi zmiennymi
- ✅ **Regulowana stawka godzinowa B2B** - dowolna kwota
- ✅ **Regulowany procent podatku ryczałtowego** - 12%, 15%, 17% lub inny
- ✅ **Urlop dla B2B** - możliwość odliczenia dni urlopu (nieopłaconych)
- ✅ **Elastyczne koszty autorskie UOP** - wybierz 20%, 50% lub własny rozkład dla każdego miesiąca
- ✅ Dokładne obliczenia dla każdego miesiąca 2026 roku
- ✅ Uwzględnia **rzeczywiste dni robocze** (bez sobót, niedziel i świąt)
- ✅ Wszystkie święta państwowe w Polsce na 2026 rok
- ✅ Czytelna tabelka ASCII w terminalu
- ✅ Automatyczne porównanie który wariant jest lepszy

## Domyślne parametry

**B2B:**
- Stawka godzinowa: 190 zł/h (konfigurowalne)
- Godzin dziennie: 8h (konfigurowalne)
- Podatek ryczałtowy: 12% (konfigurowalne)
- Urlop: 0 dni (konfigurowalne - np. 20 dni)
- Brak składek ZUS

**UOP:**
- Wynagrodzenie brutto: 28 000 zł/mc (konfigurowalne)
- Składki ZUS: 13.71% (emerytalna + rentowa + chorobowa)
- Składka zdrowotna: 9% (odliczana od podatku 7.75%)
- Podatek progresywny: 12% do 120k, potem 32% + kwota wolna 30k
- Koszty autorskie: 50% i 20% na zmianę (konfigurowalne)
- 20 dni urlopu + płatne święta (konfigurowalne)

## Wymagania

- Java 17 lub nowsza
- Gradle (opcjonalnie, można użyć wrapper'a)

## Uruchomienie

### Tryb interaktywny (domyślny)

Wprowadź własne parametry:

```bash
kotlinc src/main/kotlin/pl/kalkulator/*.kt -include-runtime -d kalkulator.jar
java -jar kalkulator.jar
```

Lub bez parametru (tryb interaktywny):

```bash
java -jar kalkulator.jar -i
```

### Tryb szybki (domyślne parametry)

Użyj predefiniowanych wartości:

```bash
java -jar kalkulator.jar --quick
```

### Kompilacja i uruchomienie (bez Gradle)

```bash
# Kompilacja
kotlinc src/main/kotlin/pl/kalkulator/*.kt -include-runtime -d kalkulator.jar

# Uruchomienie
java -jar kalkulator.jar
```

## Struktura projektu

```
KalkulatorB2BiUOP/
├── src/main/kotlin/pl/kalkulator/
│   ├── Main.kt              # Punkt wejścia aplikacji (tryb interaktywny/szybki)
│   ├── Config.kt            # Konfiguracja parametrów (interaktywne wprowadzanie)
│   ├── Models.kt            # Data classes (MonthData, B2BMonthResult, UOPMonthResult, YearlySummary)
│   ├── Calendar2026.kt      # Kalendarz ze świętami i dniami roboczymi dla 2026
│   ├── Calculator.kt        # Logika obliczeń B2B i UOP
│   └── TablePrinter.kt      # Generator tabelek ASCII w terminalu
├── MOCKUP_OUTPUT.txt        # Przykładowy output
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

## Tryby użycia

### 1. Tryb interaktywny

Program poprosi Cię o wprowadzenie wszystkich parametrów:

- **B2B:**
  - Stawka godzinowa (np. 190 zł/h)
  - Liczba godzin dziennie (np. 8h)
  - Podatek ryczałtowy (np. 12%)
  - Dni urlopu do odliczenia (np. 20 - nieopłacone)

- **UOP:**
  - Wynagrodzenie brutto miesięczne (np. 28000 zł)
  - Dni urlopu rocznie (np. 20 - płatne)
  - Strategia kosztów autorskich:
    - Zawsze 20%
    - Zawsze 50%
    - Mieszane 50%/20% (co drugi miesiąc)
    - Własny rozkład dla każdego miesiąca

### 2. Tryb szybki (parametry domyślne)

Użyj predefiniowanych wartości i od razu zobacz wyniki:

```bash
java -jar kalkulator.jar --quick
```

## Przykładowe scenariusze

### Scenariusz 1: Porównanie bez urlopu
- B2B: 190 zł/h, 8h/dzień, 0 dni urlopu
- UOP: 28k brutto, 20 dni urlopu
- **Wynik:** B2B ~98k zł więcej rocznie

### Scenariusz 2: Z urlopem 20 dni na B2B
- B2B: 190 zł/h, 8h/dzień, 20 dni urlopu (nieopłacone)
- UOP: 28k brutto, 20 dni urlopu (płatne)
- **Wynik:** Różnica maleje ze względu na stracony przychód

### Scenariusz 3: Wyższa stawka B2B
- B2B: 250 zł/h, 8h/dzień
- UOP: 28k brutto
- **Wynik:** B2B znacznie bardziej opłacalne

## Licencja

MIT
