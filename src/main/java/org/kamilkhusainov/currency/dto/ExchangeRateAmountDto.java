package org.kamilkhusainov.currency.dto;

import org.kamilkhusainov.currency.entity.CurrenciesEntity;

import java.math.BigDecimal;

public record ExchangeRateAmountDto(CurrenciesEntity baseCurrency, CurrenciesEntity targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
}
