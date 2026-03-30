package org.kamilkhusainov.currency.service;

import org.kamilkhusainov.currency.CurrencyConstants;
import org.kamilkhusainov.currency.dao.ExchangeAmountDao;
import org.kamilkhusainov.currency.dao.ExchangeRateDao;
import org.kamilkhusainov.currency.entity.CurrenciesEntity;
import org.kamilkhusainov.currency.entity.ExchangeRateEntity;
import org.kamilkhusainov.currency.infrastructure.AppContainer;

import java.math.BigDecimal;
import java.util.*;

public class ExchangeAmountService {
    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateDao exchangeRateDao;
    private final CurrencyService currencyService;
    private record Result(List<Map<CurrenciesEntity,CurrenciesEntity>> pairsFromTo, ExchangeRateEntity exchangeRateEntity,  CurrenciesEntity baseCurrency){}

    public ExchangeAmountService(ExchangeRateDao exchangeRateDao, ExchangeRateService exchangeRateService, CurrencyService currencyService) {
        this.exchangeRateService = exchangeRateService;
        this.exchangeRateDao = exchangeRateDao;

        this.currencyService = currencyService;
    }
    public void existsExchangeRate(String from, String to, int amount){
        Map<String,String> exchangeRateCodes = exchangeRateService.findExchangeRateCodes(from + to);
        if (exchangeRateCodes.isEmpty()){
            if (existsReverseExchangeRate(to,from).isEmpty()){
                Optional<Result> crossExchangeRates = findCrossExchangeRate(from,to);
                if (crossExchangeRates.isPresent()) {
                    int convertedAmount = calculateAmountOfCross(crossExchangeRates.get().pairsFromTo,crossExchangeRates.get().exchangeRateEntity(),amount);
                }
            }
        }
    }
    public Map<String,String> existsReverseExchangeRate(String to, String from){
        return exchangeRateService.findExchangeRateCodes(to + from);
    }
    public Optional<Result> findCrossExchangeRate(String from, String to){
        List<ExchangeRateEntity> exchangeRateEntityList = exchangeRateDao.findAll();
        for (ExchangeRateEntity exchangeRateEntity: exchangeRateEntityList){
            CurrenciesEntity baseCurrency = currencyService.findById(exchangeRateEntity.baseCurrencyId());
            List<Map<CurrenciesEntity,CurrenciesEntity>> listPairsFromTo = findPairForCurrency(baseCurrency,from,to);
            if (listPairsFromTo.size() == CurrencyConstants.CROSS_PAIR_COMPONENTS.getValue()){
                return Optional.of(new Result(listPairsFromTo,exchangeRateEntity,baseCurrency));
            }
        }
        return Optional.empty();
    }
    private List<Map<CurrenciesEntity,CurrenciesEntity>> findPairForCurrency(CurrenciesEntity baseCurrency,String from, String to) {
        List<ExchangeRateEntity> exchangeRateEntityList = exchangeRateDao.findAllPairs(baseCurrency.id());
        List<Map<CurrenciesEntity,CurrenciesEntity>> listPairsFromTo = new ArrayList<>();
        for (ExchangeRateEntity exchangeRateEntity : exchangeRateEntityList) {
            CurrenciesEntity targetCurrency = currencyService.findById(exchangeRateEntity.targetCurrencyId());
            if (targetCurrency.code().equals(from) || targetCurrency.code().equals(to)){
                listPairsFromTo.add(new LinkedHashMap<>(Map.of(baseCurrency,targetCurrency)));
            }
        }
        return listPairsFromTo;
    }
    private int calculateAmountOfCross(List<Map<CurrenciesEntity,CurrenciesEntity>> listCrossExchangeRates, ExchangeRateEntity exchangeRateEntity, int amount){
        Set<CurrenciesEntity> currenciesEntities = listCrossExchangeRates.getFirst().keySet();
        CurrenciesEntity baseCurrency = currenciesEntities.iterator().next();
        CurrenciesEntity rateCrossToCurrency1 = listCrossExchangeRates.get(CurrencyConstants.INDEX_CROSS_FIRST_PAIR.getValue()).get(baseCurrency);
        BigDecimal rateCrossToCurrency2;
        return 1;
    }


}
