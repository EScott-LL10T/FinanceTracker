package com.financetracker;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {
    String d = LocalDateTime.now().toString();
    Transaction t = new Transaction(10, "Entertainment", "cinema ticket", d);

    @org.junit.jupiter.api.Test
    void getAmount() {
        assertEquals(10, t.getAmount());
    }

    @org.junit.jupiter.api.Test
    void getCategory() {
        assertEquals("Entertainment",t.getCategory());
    }

    @org.junit.jupiter.api.Test
    void getDescription() {
        assertEquals("cinema ticket", t.getDescription());
    }

    @org.junit.jupiter.api.Test
    void getTimeOfTransaction() {
        assertEquals(LocalDateTime.parse(d), t.getTimeOfTransaction());
    }
}