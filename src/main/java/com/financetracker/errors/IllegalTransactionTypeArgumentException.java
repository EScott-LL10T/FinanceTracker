package com.financetracker.errors;

public class IllegalTransactionTypeArgumentException extends Exception{
    public IllegalTransactionTypeArgumentException(){
        super("Transaction type argument doesn't match a transaction type id.");
    }

    public void printMessage(){
        System.out.println(getMessage());
    }
}

