package com.financetracker;


import java.time.LocalDateTime;

public class Profile {
    private final String name;
    private final String role;
    private final double debt;
    private final double salary;
    private final String timeOfAccountCreation;
    private final int id;

    public Profile(String name, String role, double debt, double salary, String timeOfAccountCreation, int id){
        this.name = name;
        this.role = role;
        this.debt = debt;
        this.salary = salary;
        this.timeOfAccountCreation = timeOfAccountCreation;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public String getRole(){
        return role;
    }

    public double getDebt(){
        return debt;
    }

    public double getSalary(){
        return salary;
    }

    public double calculateTax(){
        if(role.equals("Student") || salary <= 12570.0){
            return 0.0;
        }

        // Personal allowance drops by £1 for every £2 over £100,000, down to zero
        double allowance = 12570.0 - Math.max(0.0, (salary - 100000.0) / 2.0);
        allowance = Math.max(0.0, allowance);

        double taxable = Math.max(0.0, salary - allowance);

        double tax = 0.0;

        // 20% on the first £37,700 of taxable income
        tax += Math.min(taxable, 37700.0) * 0.20;

        // 40% on taxable income from £37,700 to £125,140
        if(taxable > 37700.0){
            tax += (Math.min(taxable, 125140.0) - 37700.0) * 0.40;
        }

        // 45% on taxable income over £125,140
        if(taxable > 125140.0){
            tax += (taxable - 125140.0) * 0.45;
        }

        return tax / salary;   // effective rate, so salary * calculateTax() is the total tax
    }

    public double calculateNationalInsurance(){
        if(role.equals("Student") || salary <= 12570.0){
            return 0.0;
        }

        double ni = (Math.min(salary, 50270.0) - 12570.0) * 0.08;

        if(salary > 50270.0){
            ni += (salary - 50270.0) * 0.02;
        }

        return ni / salary;   // effective rate, same convention as calculateTax()
    }

    public LocalDateTime getTimeOfAccountCreation(){
        return LocalDateTime.parse(timeOfAccountCreation);
    }

    public int getId() {
        return id;
    }

}
