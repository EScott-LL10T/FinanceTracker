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
        // uk tax brackets
        // £0–£12,570	    0%
        // £12,571–£50,270	20%
        // £50,271–£125,140	40%
        // Over £125,140	45%

        if(salary <= 12570 || role.equals("Student")){
            return 0;
        }else if(salary <= 50270){
            return 0.2;
        }else if(salary <= 125140){
            return 0.4;
        }else{
            return 0.45;
        }
    }

    public LocalDateTime getTimeOfAccountCreation(){
        return LocalDateTime.parse(timeOfAccountCreation);
    }

    public int getId() {
        return id;
    }
}
