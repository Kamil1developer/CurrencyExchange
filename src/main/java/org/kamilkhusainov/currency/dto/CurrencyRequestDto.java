package org.kamilkhusainov.currency.dto;

public record CurrencyRequestDto(
        String code,
        String name,
        String sign
) {
}
