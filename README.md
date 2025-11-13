# Kalkulator B2B vs UOP - Rok 2026

Interaktywna aplikacja konsolowa w Kotlinie do porównania zarobków na B2B (ryczałt) vs UOP dla roku 2026.

## Funkcje

- Interaktywne wprowadzanie parametrów
- Regulowana stawka godzinowa B2B
- Regulowany procent podatku ryczałtowego (12%, 15%, 17%)
- Urlop dla B2B (nieopłacone dni)
- Elastyczne koszty autorskie UOP (20%, 50% lub własny rozkład)
- Uwzględnia rzeczywiste dni robocze i święta państwowe
- Czytelna tabelka ASCII w terminalu
- Automatyczne porównanie wariantów

## Wymagania

- Java 17 lub nowsza
- Gradle (opcjonalnie, można użyć wrapper'a)

## Uruchomienie

### Tryb interaktywny (domyślny)

```bash
kotlinc src/main/kotlin/pl/kalkulator/*.kt -include-runtime -d kalkulator.jar
java -jar kalkulator.jar
```

### Tryb szybki (domyślne parametry)

```bash
java -jar kalkulator.jar --quick
```

## Przykładowy output

```
┌─────────┬─────┬──────┬──────────┬──────────┬──────────┬──────┬──────────┬──────────┬──────────┬──────────┬──────┐
│ Miesiąc │ Dni │Święta│          │    B2B   │          │      │          │   UOP    │          │          │      │
│         │rob. │(UOP) │ Przychód │  Podatek │   Netto  │ Godz.│  Brutto  │   ZUS    │ Zdrowot. │  Podatek │ Netto│
├─────────┼─────┼──────┼──────────┼──────────┼──────────┼──────┼──────────┼──────────┼──────────┼──────────┼──────┤
│ Styczeń │  20 │   2  │  30 400  │   3 648  │  26 752  │ 160  │  28 000  │   3 839  │   2 175  │   1 260  │20 726│
│ Luty    │  20 │   0  │  30 400  │   3 648  │  26 752  │ 160  │  28 000  │   3 839  │   2 175  │   2 214  │19 772│
│ Marzec  │  21 │   0  │  31 920  │   3 830  │  28 090  │ 168  │  28 000  │   3 839  │   2 175  │   2 214  │19 772│
│ Kwiecień│  21 │   1  │  31 920  │   3 830  │  28 090  │ 168  │  28 000  │   3 839  │   2 175  │   1 260  │20 726│
│ Maj     │  19 │   1  │  28 880  │   3 466  │  25 414  │ 152  │  28 000  │   3 839  │   2 175  │   2 214  │19 772│
│ Czerwiec│  21 │   1  │  31 920  │   3 830  │  28 090  │ 168  │  28 000  │   3 839  │   2 175  │   1 260  │20 726│
│ Lipiec  │  23 │   0  │  34 960  │   4 195  │  30 765  │ 184  │  28 000  │   3 839  │   2 175  │   2 214  │19 772│
│ Sierpień│  21 │   0  │  31 920  │   3 830  │  28 090  │ 168  │  28 000  │   3 839  │   2 175  │   2 214  │19 772│
│Wrzesień │  22 │   0  │  33 440  │   4 013  │  29 427  │ 176  │  28 000  │   3 839  │   2 175  │   2 214  │19 772│
│Paździer.│  22 │   0  │  33 440  │   4 013  │  29 427  │ 176  │  28 000  │   3 839  │   2 175  │   2 214  │19 772│
│Listopad │  19 │   1  │  28 880  │   3 466  │  25 414  │ 152  │  28 000  │   3 839  │   2 175  │   1 260  │20 726│
│ Grudzień│  22 │   1  │  33 440  │   4 013  │  29 427  │ 176  │  28 000  │   3 839  │   2 175  │   2 214  │19 772│
├─────────┼─────┼──────┼──────────┼──────────┼──────────┼──────┼──────────┼──────────┼──────────┼──────────┼──────┤
│  RAZEM  │ 251 │   6  │ 381 520  │  45 782  │ 335 738  │ 2008 │ 336 000  │  46 068  │  26 100  │  22 764  │245068│
└─────────┴─────┴──────┴──────────┴──────────┴──────────┴──────┴──────────┴──────────┴──────────┴──────────┴──────┘

B2B (Ryczałt 12%) wypada lepiej o:  90 670 zł rocznie
```

## Struktura projektu

```
KalkulatorB2BiUOP/
├── src/main/kotlin/pl/kalkulator/
│   ├── Main.kt
│   ├── Config.kt
│   ├── Models.kt
│   ├── Calendar2026.kt
│   ├── Calculator.kt
│   └── TablePrinter.kt
└── README.md
```

## Licencja

MIT
