package org.kamilkhusainov.currency.dto;

public record ExchangeRequestDto(String baseCurrencyCode, String targetCurrencyCode, String rate) {
}
