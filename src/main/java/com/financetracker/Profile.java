package com.financetracker;

import java.time.LocalDateTime;

public class Profile {
    private final String name;
    private final String role;
    private double debt;
    private double salary;
    private final String timeOfAccountCreation;

    public Profile(String name, String role, double debt, double salary, String timeOfAccountCreation){
        this.name = name;
        this.role = role;
        this.debt = debt;
        this.salary = salary;
        this.timeOfAccountCreation = timeOfAccountCreation;
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

    public LocalDateTime getTimeOfAccountCreation(){
        return LocalDateTime.parse(timeOfAccountCreation);
    }

    public void updateDebt(int amount){
        debt -= amount;
    }

    public void updateSalary(int newSalary){
        salary = newSalary;
    }
}
