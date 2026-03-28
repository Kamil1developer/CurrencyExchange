package org.kamilkhusainov.currency.service;

import org.kamilkhusainov.currency.dao.ExchangeAmountDao;
import org.kamilkhusainov.currency.infrastructure.AppContainer;

import java.util.Map;

public class ExchangeAmountService {
    private final ExchangeRateService exchangeRateService;
    private final ExchangeAmountDao exchangeAmountDao;

    public ExchangeAmountService(ExchangeAmountDao exchangeAmountDao,ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
        this.exchangeAmountDao = exchangeAmountDao;
    }
    public void existsExchangeRate(String from, String to, int amount){
        Map<String,String> exchangeRateCodes = exchangeRateService.findExchangeRateCodes(from + to);
        if (exchangeRateCodes.isEmpty()){
            if (existsReverseExchangeRate(to,from).isEmpty()){
                findCrossExchangeRate();
            }
        }
    }
    public Map<String,String> existsReverseExchangeRate(String to, String from){
        return exchangeRateService.findExchangeRateCodes(to + from);
    }
    public void findCrossExchangeRate(){

    }


}
