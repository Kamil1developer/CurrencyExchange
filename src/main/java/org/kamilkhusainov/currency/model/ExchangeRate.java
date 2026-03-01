package org.kamilkhusainov.currency.model;

public record ExchangeRate(int baseCurrencyId, int targetCurrencyId, double rate) {
}
