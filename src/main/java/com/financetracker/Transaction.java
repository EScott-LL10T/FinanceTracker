package com.financetracker;

import java.time.LocalDateTime;

public class Transaction {
    private final int amount;
    private final String category;
    private final String description;
    private final String dateTime;

    public Transaction(int amount, String category, String description, String dateTime){
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.dateTime = dateTime;
    }

    public int getAmount(){
        return amount;
    }

    public String getCategory(){
        return category;
    }

    public String getDescription(){
        return description;
    }

    public LocalDateTime getTimeOfTransaction(){
        return  LocalDateTime.parse(dateTime);
    }
}
