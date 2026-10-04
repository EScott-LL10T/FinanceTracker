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

    public LocalDateTime getTimeOfAccountCreation(){
        return LocalDateTime.parse(timeOfAccountCreation);
    }

    public int getId() {
        return id;
    }
}
