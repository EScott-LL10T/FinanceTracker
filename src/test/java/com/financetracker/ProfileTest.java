package com.financetracker;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {
    // String name, String role, double debt, double salary, String timeOfAccountCreation, int id
    String d = LocalDateTime.now().toString();
    Profile p = new Profile("Elliot", "Student", 0.0, 4000.0, d, 1);
    Profile pe1 = new Profile("Elliot", "Employed", 0.0, 40000, d, 1);
    Profile pe2 = new Profile("Elliot", "Employed", 0.0, 100000, d, 1);

    @org.junit.jupiter.api.Test
    void getName(){
        assertEquals("Elliot", p.getName());
    }

    @org.junit.jupiter.api.Test
    void getRole(){
        assertEquals("Student", p.getRole());
    }

    @org.junit.jupiter.api.Test
    void getDebt(){
        assertEquals(0.0, p.getDebt());
    }

    @org.junit.jupiter.api.Test
    void getSalary(){
        assertEquals(4000.0, p.getSalary());
    }


    @org.junit.jupiter.api.Test
    void getCalculateTax(){
        assertEquals(0.0, p.calculateTax());
        assertEquals(0.2, pe1.calculateTax());
        assertEquals(0.4, pe2.calculateTax());
    }

    @org.junit.jupiter.api.Test
    void getTimeOfAccountCreation(){
        assertEquals(LocalDateTime.parse(d), p.getTimeOfAccountCreation());
    }

    @org.junit.jupiter.api.Test
    void getId(){
        assertEquals(1, p.getId());
    }
}