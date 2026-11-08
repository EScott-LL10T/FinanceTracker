package com.financetracker.errors;

public class IllegalCategoryArgumentException extends Exception {

    public IllegalCategoryArgumentException(){
        super("category argument doesn't match a category id.");
    }

    public void printMessage(){
        System.out.println(getMessage());
    }
}
