package org.kamilkhusainov.currency.entity;
import java.math.BigDecimal;

public record ExchangeRateRow(
        long exchangeRateId,
        long baseCurrencyId,
        String baseCurrencyCode,
        String baseCurrencyName,
        String baseCurrencySign,
        long targetCurrencyId,
        String targetCurrencyCode,
        String targetCurrencyName,
        String targetCurrencySign,
        BigDecimal rate
) {
}
