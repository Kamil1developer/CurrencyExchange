package org.kamilkhusainov.currency.mapper;

import org.kamilkhusainov.currency.dto.ExchangeRateAmountDto;
import org.kamilkhusainov.currency.entity.CurrenciesEntity;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExchangeRateMapper {
    public static ExchangeRateAmountDto toJson(CurrenciesEntity baseCurrency, CurrenciesEntity targetCurrency, BigDecimal rate, BigDecimal amount){
        return new ExchangeRateAmountDto(baseCurrency,targetCurrency, rate, amount, rate.multiply(amount));
    }
}
