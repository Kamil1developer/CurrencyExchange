package org.kamilkhusainov.currency.dto;

import org.kamilkhusainov.currency.entity.CurrencyEntity;

import java.math.BigDecimal;

public record ExchangeRateUpdateResponseDto(CurrencyResponseDto baseCurrency, CurrencyResponseDto targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
}
