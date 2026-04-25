package org.kamilkhusainov.currency.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kamilkhusainov.currency.exceptions.ValidationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public final class ExchangeRateValidator {
    private  ObjectMapper MAPPER;

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
