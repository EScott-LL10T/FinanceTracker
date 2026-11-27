package com.financetracker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Helpers for handling money as {@link BigDecimal}.
 * Every amount in the app is kept at 2 decimal places (pounds and pence).
 */
public final class Money {

    public static final int SCALE = 2;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(SCALE, ROUNDING);

    private Money() {
    }

    /** Rounds any amount to pounds and pence. */
    public static BigDecimal of(BigDecimal value) {
        return Objects.requireNonNull(value, "money value must not be null").setScale(SCALE, ROUNDING);
    }

    /** Parses an exact amount, e.g. "12.50". */
    public static BigDecimal of(String value) {
        return of(new BigDecimal(value));
    }

    /**
     * Converts a double (from a Swing spinner or the database) into money.
     * BigDecimal.valueOf uses the shortest decimal form of the double, so 0.1 becomes 0.10
     * rather than 0.1000000000000000055511151231257827.
     */
    public static BigDecimal of(double value) {
        return of(BigDecimal.valueOf(value));
    }

    /** Formats an amount for display, e.g. "£12.50" or "-£3.20". */
    public static String format(BigDecimal amount) {
        BigDecimal rounded = of(amount);
        return (rounded.signum() < 0 ? "-" : "") + "£" + rounded.abs().toPlainString();
    }
}
