package org.kamilkhusainov.currency.dto;

import java.math.BigDecimal;

public record ExchangeRateUpdateRequestDto(
        BigDecimal rate
) {
}
