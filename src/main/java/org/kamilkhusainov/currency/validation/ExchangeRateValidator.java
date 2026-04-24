package org.kamilkhusainov.currency.validation;

import org.kamilkhusainov.currency.exceptions.ValidationException;

import java.math.BigDecimal;

public final class ExchangeRateValidator {
    private ExchangeRateValidator(){}

    public static boolean isValidRate(String rate) throws NumberFormatException {
        try {
            new BigDecimal(rate);
        } catch (NumberFormatException numberFormatException) {
            throw new ValidationException("Поле rate неправильное ",numberFormatException);
        }
        return true;
    }
}
