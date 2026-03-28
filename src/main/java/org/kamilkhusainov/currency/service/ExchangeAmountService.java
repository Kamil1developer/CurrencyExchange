package org.kamilkhusainov.currency.service;

import org.kamilkhusainov.currency.dao.ExchangeAmountDao;
import org.kamilkhusainov.currency.infrastructure.AppContainer;

public class ExchangeAmountService {
    private final ExchangeRateService exchangeRateService;
    private final ExchangeAmountDao exchangeAmountDao;

    public ExchangeAmountService(ExchangeAmountDao exchangeAmountDao,ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
        this.exchangeAmountDao = exchangeAmountDao;
    }
    public void existsExchangeRate(){

    }
    public void existsReverseExchangeRate(){

    }
    public void findCrossExchangeRate(){

    }


}
