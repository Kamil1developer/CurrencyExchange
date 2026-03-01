package org.kamilkhusainov.currency.dto;

public record ExchangeRateDto(String baseCurrencyId, String targetCurrencyId, String rate) {
}
