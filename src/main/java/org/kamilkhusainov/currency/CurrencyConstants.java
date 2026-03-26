package org.kamilkhusainov.currency;

public enum CurrencyConstants {
    ALREADY_EXISTS(-1);

    private final int value;
    CurrencyConstants(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
