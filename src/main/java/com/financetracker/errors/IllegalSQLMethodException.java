package com.financetracker.errors;

public class IllegalSQLMethodException extends Exception {
    public IllegalSQLMethodException(String methodName){
        super("SQL error above occurred in method named " + methodName + ".");
    }

    public void printMessage(){
        System.out.println(getMessage());
    }
}
