package org.kamilkhusainov.currency.dto;

import java.math.BigDecimal;

public record ExchangeAmountResponseDto(
        CurrencyResponseDto baseCurrency,
        CurrencyResponseDto targetCurrency,
        BigDecimal rate,
        BigDecimal amount,
        BigDecimal convertedAmount
) {
}