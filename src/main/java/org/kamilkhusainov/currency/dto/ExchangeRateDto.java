package org.kamilkhusainov.currency.dto;

public record ExchangeRateDto(String baseCurrencyCode, String targetCurrencyCode, String rate) {
}
