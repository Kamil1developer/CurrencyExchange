package org.kamilkhusainov.currency;

public enum CurrencyConstants {
    ALREADY_EXISTS(-1),
    CROSS_PAIR_COMPONENTS(2);
    private final int value;
    CurrencyConstants(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
