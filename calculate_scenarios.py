#!/usr/bin/env python3
"""
Kalkulator B2B vs UOP - Obliczenia dla różnych scenariuszy
"""

from datetime import date, timedelta
from typing import List, Tuple
import calendar

# Święta w 2026 roku (dni robocze)
HOLIDAYS_2026 = [
    date(2026, 1, 1),   # Czwartek - Nowy Rok
    date(2026, 1, 6),   # Wtorek - Trzech Króli
    date(2026, 4, 6),   # Poniedziałek - Poniedziałek Wielkanocny
    date(2026, 5, 1),   # Piątek - Święto Pracy
    date(2026, 6, 4),   # Czwartek - Boże Ciało
    date(2026, 11, 11), # Środa - Niepodległość
    date(2026, 12, 25), # Piątek - Boże Narodzenie
]


def count_working_days_2026() -> Tuple[int, int]:
    """
    Oblicza liczbę dni roboczych w 2026 roku (bez sobót, niedziel i świąt)
    Zwraca: (dni_robocze_ogółem, święta_w_dni_robocze)
    """
    working_days = 0
    workday_holidays = 0

    start_date = date(2026, 1, 1)
    end_date = date(2026, 12, 31)
    current = start_date

    while current <= end_date:
        # Sprawdź czy to dzień roboczy (pon-pt)
        if current.weekday() < 5:  # 0=Monday, 4=Friday
            if current in HOLIDAYS_2026:
                workday_holidays += 1
            else:
                working_days += 1
        current += timedelta(days=1)

    return working_days, workday_holidays


def calculate_b2b(hourly_rate: float, hours_per_day: float,
                  tax_rate: float, working_days: int, vacation_days: int) -> dict:
    """
    Oblicza wynagrodzenie B2B
    """
    worked_days = working_days - vacation_days
    total_hours = worked_days * hours_per_day
    revenue = total_hours * hourly_rate
    tax = revenue * (tax_rate / 100.0)
    net = revenue - tax

    return {
        'revenue': revenue,
        'tax': tax,
        'net': net,
        'worked_days': worked_days,
        'vacation_days': vacation_days,
        'total_hours': total_hours
    }


def calculate_uop_month(gross: float, author_cost_percent: int,
                        accumulated_income: float, accumulated_author_costs: float,
                        bonus: float = 0.0) -> dict:
    """
    Oblicza wynagrodzenie UOP dla jednego miesiąca

    UWAGA: Koszty autorskie liczy się od (Brutto - ZUS), a nie od pełnego brutto!
    Limit roczny kosztów autorskich: 120,000 PLN
    """
    # Całkowite wynagrodzenie (pensja + premia)
    total_gross = gross + bonus

    # Składki ZUS (pracownik)
    zus_rate = 0.1371  # 9.76% + 1.5% + 2.45%
    zus_contributions = total_gross * zus_rate

    # Składka zdrowotna
    health_base = total_gross - zus_contributions
    health_contribution = health_base * 0.09
    health_deduction = health_base * 0.0775

    # Koszty uzyskania przychodu (autorskie)
    # WAŻNE: Koszty autorskie liczone od (brutto - ZUS), nie od pełnego brutto!
    author_base = total_gross - zus_contributions
    costs_before_limit = author_base * (author_cost_percent / 100.0)

    # Sprawdź limit roczny 120,000 PLN
    author_costs_limit = 120000.0
    remaining_limit = max(0, author_costs_limit - accumulated_author_costs)
    costs = min(costs_before_limit, remaining_limit)

    # Podstawa opodatkowania
    taxable_income = total_gross - zus_contributions - costs
    tax_base = int(taxable_income / 10.0) * 10.0  # Zaokrąglenie do 10 zł

    # Obliczenie podatku progresywnego
    new_accumulated = accumulated_income + tax_base
    tax = calculate_progressive_tax(new_accumulated, accumulated_income)

    # Netto
    net = total_gross - zus_contributions - health_contribution - tax

    return {
        'gross': gross,
        'bonus': bonus,
        'total_gross': total_gross,
        'zus': zus_contributions,
        'health': health_contribution,
        'tax_base': tax_base,
        'tax': tax,
        'net': net,
        'author_costs': costs,
        'author_costs_before_limit': costs_before_limit
    }


def calculate_progressive_tax(new_accumulated: float, old_accumulated: float) -> float:
    """
    Oblicza podatek progresywny (12% do 120k, 32% powyżej)
    """
    threshold = 120000.0
    free_amount = 30000.0

    # Podatek od nowego skumulowanego dochodu
    if new_accumulated <= threshold:
        tax_new = new_accumulated * 0.12 - (free_amount * 0.12)
    else:
        part1 = threshold * 0.12
        part2 = (new_accumulated - threshold) * 0.32
        tax_new = part1 + part2 - (free_amount * 0.12)
    tax_new = max(0.0, tax_new)

    # Podatek od starego skumulowanego dochodu
    if old_accumulated <= threshold:
        tax_old = old_accumulated * 0.12 - (free_amount * 0.12)
    else:
        part1 = threshold * 0.12
        part2 = (old_accumulated - threshold) * 0.32
        tax_old = part1 + part2 - (free_amount * 0.12)
    tax_old = max(0.0, tax_old)

    return tax_new - tax_old


def calculate_uop_year(gross_salary: float, author_cost_percent: int,
                       bonus_percent: float) -> dict:
    """
    Oblicza wynagrodzenie UOP dla całego roku
    """
    # Oblicz premię roczną
    yearly_gross = gross_salary * 12
    yearly_bonus = yearly_gross * (bonus_percent / 100.0)

    accumulated_income = 0.0
    accumulated_author_costs = 0.0
    total_net = 0.0
    total_tax = 0.0
    total_zus = 0.0
    total_health = 0.0
    total_author_costs = 0.0

    for month in range(12):
        # Premia wypłacana w grudniu (miesiąc 11)
        bonus = yearly_bonus if month == 11 else 0.0

        result = calculate_uop_month(gross_salary, author_cost_percent,
                                     accumulated_income, accumulated_author_costs,
                                     bonus)
        accumulated_income += result['tax_base']
        accumulated_author_costs += result['author_costs']
        total_net += result['net']
        total_tax += result['tax']
        total_zus += result['zus']
        total_health += result['health']
        total_author_costs += result['author_costs']

    return {
        'gross_salary': gross_salary,
        'yearly_gross': yearly_gross,
        'bonus': yearly_bonus,
        'total_gross': yearly_gross + yearly_bonus,
        'total_zus': total_zus,
        'total_health': total_health,
        'total_tax': total_tax,
        'total_net': total_net,
        'total_author_costs': total_author_costs
    }


def print_scenario(scenario_num: int, uop_gross: float, b2b_rate: float,
                   author_cost: int, bonus_percent: float,
                   working_days: int, vacation_days: int):
    """
    Drukuje wyniki dla danego scenariusza
    """
    print(f"\n{'='*80}")
    print(f"SCENARIUSZ {scenario_num}: UOP {uop_gross:,.0f} PLN brutto + {author_cost}% prawa autorskie + {bonus_percent}% premii")
    print(f"{'='*80}")

    # Obliczenia UOP
    uop = calculate_uop_year(uop_gross, author_cost, bonus_percent)

    # Obliczenia B2B (stawka 190 PLN/h, takie same dni wolne jak UOP)
    b2b = calculate_b2b(
        hourly_rate=b2b_rate,
        hours_per_day=8.0,
        tax_rate=12.0,
        working_days=working_days,
        vacation_days=vacation_days
    )

    # Wyniki UOP
    print(f"\nUOP:")
    print(f"  Wynagrodzenie brutto:     {uop['yearly_gross']:>12,.2f} PLN")
    print(f"  Premia roczna ({bonus_percent}%):     {uop['bonus']:>12,.2f} PLN")
    print(f"  Razem brutto:             {uop['total_gross']:>12,.2f} PLN")
    print(f"  Składki ZUS:             -{uop['total_zus']:>12,.2f} PLN")
    print(f"  Koszty autorskie ({author_cost}%):   -{uop['total_author_costs']:>12,.2f} PLN")
    print(f"  Składka zdrowotna:       -{uop['total_health']:>12,.2f} PLN")
    print(f"  Podatek:                 -{uop['total_tax']:>12,.2f} PLN")
    print(f"  {'─'*45}")
    print(f"  NETTO:                    {uop['total_net']:>12,.2f} PLN")
    print(f"  Dni urlopu:               20 dni (płatne)")
    print(f"  Święta:                   7 dni (płatne)")

    # Wyniki B2B
    print(f"\nB2B (stawka {b2b_rate} PLN/h, 12% ryczałt):")
    print(f"  Przychód:                 {b2b['revenue']:>12,.2f} PLN")
    print(f"  Podatek (12%):           -{b2b['tax']:>12,.2f} PLN")
    print(f"  {'─'*45}")
    print(f"  NETTO:                    {b2b['net']:>12,.2f} PLN")
    print(f"  Przepracowane dni:        {b2b['worked_days']} dni")
    print(f"  Dni wolne:                {b2b['vacation_days']} dni (nieopłacone)")
    print(f"  Przepracowane godziny:    {b2b['total_hours']:.0f} h")

    # Porównanie
    difference = b2b['net'] - uop['total_net']
    difference_percent = (difference / uop['total_net']) * 100

    print(f"\n{'─'*80}")
    print(f"RÓŻNICA (B2B - UOP):")
    print(f"  Kwota:                    {difference:>12,.2f} PLN")
    print(f"  Procent:                  {difference_percent:>12,.2f}%")

    if difference > 0:
        print(f"  ✓ B2B bardziej opłacalne o {difference:,.2f} PLN rocznie")
    elif difference < 0:
        print(f"  ✓ UOP bardziej opłacalne o {abs(difference):,.2f} PLN rocznie")
    else:
        print(f"  ✓ Obie formy są równoważne")

    return {
        'uop_net': uop['total_net'],
        'b2b_net': b2b['net'],
        'difference': difference,
        'difference_percent': difference_percent
    }


def main():
    print("\n" + "="*80)
    print("KALKULATOR B2B vs UOP - ANALIZA SCENARIUSZY")
    print("="*80)

    # Oblicz dni robocze w 2026
    working_days, workday_holidays = count_working_days_2026()

    print(f"\nRok 2026:")
    print(f"  Dni robocze (bez świąt):  {working_days} dni")
    print(f"  Święta w dni robocze:     {workday_holidays} dni")
    print(f"  UOP dni urlopu:           20 dni (płatne)")
    print(f"  B2B dni wolne:            {20 + workday_holidays} dni (nieopłacone, równoważne UOP)")

    # Parametry wspólne
    b2b_rate = 190.0
    author_cost = 20  # 20% prawa autorskie
    bonus_percent = 10.0  # 10% premii
    vacation_days = 20 + workday_holidays  # UOP urlop + święta

    # Scenariusze
    scenarios = [
        (1, 28000.0),
        (2, 30000.0),
        (3, 31000.0)
    ]

    results = []
    for scenario_num, uop_gross in scenarios:
        result = print_scenario(
            scenario_num, uop_gross, b2b_rate, author_cost,
            bonus_percent, working_days, vacation_days
        )
        results.append((scenario_num, uop_gross, result))

    # Podsumowanie
    print(f"\n{'='*80}")
    print("PODSUMOWANIE WSZYSTKICH SCENARIUSZY")
    print(f"{'='*80}\n")

    print(f"{'Scenariusz':<15} {'UOP brutto':<15} {'UOP netto':<15} {'B2B netto':<15} {'Różnica':<15}")
    print(f"{'-'*80}")

    for scenario_num, uop_gross, result in results:
        print(f"Scenariusz {scenario_num:<5} "
              f"{uop_gross:>12,.0f}  "
              f"{result['uop_net']:>12,.2f}  "
              f"{result['b2b_net']:>12,.2f}  "
              f"{result['difference']:>+12,.2f}")

    print(f"\n{'='*80}")
    print("Wszystkie obliczenia uwzględniają:")
    print("  • B2B: stawka 190 PLN/h, 8h/dzień, 12% podatek ryczałtowy")
    print("  • B2B: 27 dni wolnych (20 urlop + 7 świąt, wszystkie nieopłacone)")
    print("  • UOP: 20% koszty autorskie (od brutto - ZUS), limit 120k PLN/rok")
    print("  • UOP: 10% premia roczna (wypłacana w grudniu)")
    print("  • UOP: 20 dni urlopu płatnego + 7 świąt płatnych")
    print("  • UOP: Podatek progresywny 12% do 120k, 32% powyżej")
    print(f"{'='*80}\n")


if __name__ == "__main__":
    main()
