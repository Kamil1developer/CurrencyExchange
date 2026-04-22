package org.kamilkhusainov.currency.dto;

public record CurrencyResponseDto(
        long id,
        String code,
        String name,
        String sign
) {
}
