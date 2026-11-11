package com.financetracker;



import java.time.LocalDateTime;

public class Transaction {
    private final int id;
    private final double amount;
    private final String category;
    private final String description;
    private final String dateTime;

    public Transaction(int id, double amount, String category, String description, String dateTime){
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.dateTime = dateTime;
    }

    public int getId(){
        return id;
    }

    public double getAmount(){ return amount; }

    public String getCategory(){ return category; }

    public String getDescription(){ return description; }

    public LocalDateTime getTimeOfTransaction(){ return  LocalDateTime.parse(dateTime); }
}
