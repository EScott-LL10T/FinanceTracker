package com.financetracker.errors;

public class IllegalAddCategoryException extends Exception{
    public IllegalAddCategoryException(){
        super("failed to add category.");
    }

    public void printMessage(){
        System.out.println(getMessage());
    }
}
