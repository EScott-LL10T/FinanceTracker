package com.financetracker.errors;

public class IllegalRoleArgumentException extends Exception {
    // argument
    public IllegalRoleArgumentException(){
        super("role argument doesn't match a role id.");
    }

    public void printMessage(){
        System.out.println(getMessage());
    }
}
