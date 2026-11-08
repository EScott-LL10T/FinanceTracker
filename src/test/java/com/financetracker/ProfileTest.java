package com.financetracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {
    // String name, String role, double debt, double salary, String timeOfAccountCreation, int id
    String d = LocalDateTime.now().toString();
    Profile p = new Profile("Elliot", "Student", 0.0, 4000.0, d, 1);

    @Test
    void getName(){
        assertEquals("Elliot", p.getName());
    }

    @Test
    void getRole(){
        assertEquals("Student", p.getRole());
    }

    @Test
    void getDebt(){
        assertEquals(0.0, p.getDebt());
    }

    @Test
    void getSalary(){
        assertEquals(4000.0, p.getSalary());
    }

    @ParameterizedTest
    @CsvSource({
            "Elliot, Student, 0.0, 12570.0, 2026-11-08T19:10:00, 1, 0.0",
            "Elliot, Student, 0.0, 15000.0, 2026-11-08T19:10:00, 1, 0.0",
            "Elliot, Employed, 0.0, 12570.0, 2026-11-08T19:10:00, 1, 0.0",
            "Elliot, Employed, 0.0, 50270.0, 2026-11-08T19:10:00, 1, 0.2",
            "Elliot, Employed, 0.0, 125140.0, 2026-11-08T19:10:00, 1, 0.4",
            "Elliot, Employed, 0.0, 125141.0, 2026-11-08T19:10:00, 1, 0.45"
    })
    void calculateTaxParameterized(String n, String r, double d, double s, String dt, int id, double expected){
        Profile pe = new Profile(n, r, d, s, dt, id);
        assertEquals(expected, pe.calculateTax());

    }

    @Test
    void getTimeOfAccountCreation(){
        assertEquals(LocalDateTime.parse(d), p.getTimeOfAccountCreation());
    }

    @Test
    void getId(){
        assertEquals(1, p.getId());
    }

    @Test
    void studentLoanPayment(){
        p.addStudentLoanPayment(4000.0);
        assertEquals(8000.0, p.getSalary());
    }
}