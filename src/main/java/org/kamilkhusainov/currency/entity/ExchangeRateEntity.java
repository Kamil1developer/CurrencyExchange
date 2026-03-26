package org.kamilkhusainov.currency.entity;

import java.math.BigDecimal;

public record ExchangeRateEntity(long id , int baseCurrencyId, int targetCurrencyId, BigDecimal rate) {
}