package org.kamilkhusainov.currency.service;

import org.kamilkhusainov.currency.CurrencyConstants;
import org.kamilkhusainov.currency.dao.ExchangeAmountDao;
import org.kamilkhusainov.currency.dao.ExchangeRateDao;
import org.kamilkhusainov.currency.entity.CurrenciesEntity;
import org.kamilkhusainov.currency.entity.ExchangeRateEntity;
import org.kamilkhusainov.currency.infrastructure.AppContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExchangeAmountService {
    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateDao exchangeRateDao;
    private final CurrencyService currencyService;

    public ExchangeAmountService(ExchangeRateDao exchangeRateDao, ExchangeRateService exchangeRateService, CurrencyService currencyService) {
        this.exchangeRateService = exchangeRateService;
        this.exchangeRateDao = exchangeRateDao;

        this.currencyService = currencyService;
    }
    public void existsExchangeRate(String from, String to, int amount){
        Map<String,String> exchangeRateCodes = exchangeRateService.findExchangeRateCodes(from + to);
        if (exchangeRateCodes.isEmpty()){
            if (existsReverseExchangeRate(to,from).isEmpty()){
                findCrossExchangeRate(from,to);
            }
        }
    }
    public Map<String,String> existsReverseExchangeRate(String to, String from){
        return exchangeRateService.findExchangeRateCodes(to + from);
    }
    public void findCrossExchangeRate(String from, String to){
        List<ExchangeRateEntity> exchangeRateEntityList = exchangeRateDao.findAll();
        for (ExchangeRateEntity exchangeRateEntity: exchangeRateEntityList){
            CurrenciesEntity baseCurrency = currencyService.findById(exchangeRateEntity.baseCurrencyId());
            List<String> pairsFromTo = findPairForCurrency(baseCurrency,from,to);
            if (pairsFromTo.size() == CurrencyConstants.CROSS_PAIR_COMPONENTS.getValue()){
                calculateAmountOfCross();
            }
        }
    }
    private List<String> findPairForCurrency(CurrenciesEntity baseCurrency,String from, String to) {
        List<ExchangeRateEntity> exchangeRateEntityList = exchangeRateDao.findAllPairs(baseCurrency.id());
        List<String> pairsFromTo = new ArrayList<>();
        for (ExchangeRateEntity exchangeRateEntity : exchangeRateEntityList) {
            CurrenciesEntity targetCurrency = currencyService.findById(exchangeRateEntity.targetCurrencyId());
            if (targetCurrency.code().equals(from) || targetCurrency.code().equals(to)){
                pairsFromTo.add(baseCurrency.code() + targetCurrency.code());
            }
        }
        return pairsFromTo;
    }
    private int calculateAmountOfCross(){
        return 1;
    }


}
