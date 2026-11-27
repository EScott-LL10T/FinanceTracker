package com.financetracker;


import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;

public class Profile {
    // UK income tax and National Insurance thresholds (annual, in pounds)
    private static final BigDecimal PERSONAL_ALLOWANCE = new BigDecimal("12570");
    private static final BigDecimal ALLOWANCE_TAPER_THRESHOLD = new BigDecimal("100000");
    private static final BigDecimal BASIC_RATE_LIMIT = new BigDecimal("37700");
    private static final BigDecimal HIGHER_RATE_LIMIT = new BigDecimal("125140");
    private static final BigDecimal BASIC_RATE = new BigDecimal("0.20");
    private static final BigDecimal HIGHER_RATE = new BigDecimal("0.40");
    private static final BigDecimal ADDITIONAL_RATE = new BigDecimal("0.45");

    private static final BigDecimal NI_UPPER_EARNINGS_LIMIT = new BigDecimal("50270");
    private static final BigDecimal NI_MAIN_RATE = new BigDecimal("0.08");
    private static final BigDecimal NI_UPPER_RATE = new BigDecimal("0.02");

    private static final BigDecimal TWO = BigDecimal.valueOf(2);

    private final String name;
    private final String role;
    private final BigDecimal debt;
    private final BigDecimal salary;
    private final String timeOfAccountCreation;
    private final int id;

    public Profile(String name, String role, BigDecimal debt, BigDecimal salary, String timeOfAccountCreation, int id){
        this.name = name;
        this.role = role;
        this.debt = Money.of(debt);
        this.salary = Money.of(salary);
        this.timeOfAccountCreation = timeOfAccountCreation;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public String getRole(){
        return role;
    }

    public boolean isStudent(){
        return role.equals("Student");
    }

    public BigDecimal getDebt(){
        return debt;
    }

    public BigDecimal getSalary(){
        return salary;
    }

    /** Total income tax for the year, in pounds and pence. */
    public BigDecimal calculateAnnualTax(){
        if(isStudent() || salary.compareTo(PERSONAL_ALLOWANCE) <= 0){
            return Money.ZERO;
        }

        // Personal allowance drops by £1 for every £2 over £100,000, down to zero
        BigDecimal taper = salary.subtract(ALLOWANCE_TAPER_THRESHOLD).max(BigDecimal.ZERO).divide(TWO);
        BigDecimal allowance = PERSONAL_ALLOWANCE.subtract(taper).max(BigDecimal.ZERO);

        BigDecimal taxable = salary.subtract(allowance).max(BigDecimal.ZERO);

        // 20% on the first £37,700 of taxable income
        BigDecimal tax = taxable.min(BASIC_RATE_LIMIT).multiply(BASIC_RATE);

        // 40% on taxable income from £37,700 to £125,140
        if(taxable.compareTo(BASIC_RATE_LIMIT) > 0){
            tax = tax.add(taxable.min(HIGHER_RATE_LIMIT).subtract(BASIC_RATE_LIMIT).multiply(HIGHER_RATE));
        }

        // 45% on taxable income over £125,140
        if(taxable.compareTo(HIGHER_RATE_LIMIT) > 0){
            tax = tax.add(taxable.subtract(HIGHER_RATE_LIMIT).multiply(ADDITIONAL_RATE));
        }

        return Money.of(tax);
    }

    /** Total National Insurance for the year, in pounds and pence. */
    public BigDecimal calculateAnnualNationalInsurance(){
        if(isStudent() || salary.compareTo(PERSONAL_ALLOWANCE) <= 0){
            return Money.ZERO;
        }

        BigDecimal ni = salary.min(NI_UPPER_EARNINGS_LIMIT).subtract(PERSONAL_ALLOWANCE).multiply(NI_MAIN_RATE);

        if(salary.compareTo(NI_UPPER_EARNINGS_LIMIT) > 0){
            ni = ni.add(salary.subtract(NI_UPPER_EARNINGS_LIMIT).multiply(NI_UPPER_RATE));
        }

        return Money.of(ni);
    }

    /** Effective tax rate (annual tax / salary). Use calculateAnnualTax() for money calculations. */
    public double calculateTax(){
        return effectiveRate(calculateAnnualTax());
    }

    /** Effective NI rate (annual NI / salary). Use calculateAnnualNationalInsurance() for money calculations. */
    public double calculateNationalInsurance(){
        return effectiveRate(calculateAnnualNationalInsurance());
    }

    private double effectiveRate(BigDecimal annualAmount){
        if(salary.signum() == 0){
            return 0.0;
        }
        return annualAmount.divide(salary, MathContext.DECIMAL64).doubleValue();
    }

    public LocalDateTime getTimeOfAccountCreation(){
        return LocalDateTime.parse(timeOfAccountCreation);
    }

    public int getId() {
        return id;
    }

}
