package com.financetracker;



import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private final int id;
    private final BigDecimal amount;
    private final String category;
    private final String description;
    private final String dateTime;
    private final TransactionType transactionType;

    public Transaction(int id, BigDecimal amount, String category, String description, String dateTime, TransactionType
            transactionType){
        this.id = id;
        this.amount = Money.of(amount);
        this.category = category;
        this.description = description;
        this.dateTime = dateTime;
        this.transactionType = transactionType;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public int getId(){
        return id;
    }

    public BigDecimal getAmount(){ return amount; }

    public String getCategory(){ return category; }

    public String getDescription(){ return description; }

    public LocalDateTime getTimeOfTransaction(){ return  LocalDateTime.parse(dateTime); }
}
