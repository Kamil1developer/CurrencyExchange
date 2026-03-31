package org.kamilkhusainov.currency.service;

import org.kamilkhusainov.currency.CurrencyConstants;
import org.kamilkhusainov.currency.dao.ExchangeRateDao;
import org.kamilkhusainov.currency.entity.CurrenciesEntity;
import org.kamilkhusainov.currency.entity.ExchangeRateEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class ExchangeAmountService {
    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateDao exchangeRateDao;
    private final CurrencyService currencyService;

    public ExchangeAmountService(ExchangeRateDao exchangeRateDao, ExchangeRateService exchangeRateService, CurrencyService currencyService) {
        this.exchangeRateService = exchangeRateService;
        this.exchangeRateDao = exchangeRateDao;

        this.currencyService = currencyService;
    }
    public Map<String, Object> existsExchangeRate(String from, String to, BigDecimal amount){
        Map<String,String> exchangeRateCodes = exchangeRateService.findExchangeRateCodes(from + to);
        if (exchangeRateCodes.isEmpty()){
            if (existsReverseExchangeRate(to,from).isEmpty()){
                Optional<List<ExchangeRateEntity>> crossExchangeRates = findCrossExchangeRate(from,to);
                if (crossExchangeRates.isPresent()) {
                    Map<String, Object> linkedHashMap = new LinkedHashMap<>();
                    linkedHashMap.put("baseCurrency",currencyService.findById(crossExchangeRates.get().get(CurrencyConstants.INDEX_CROSS_FIRST_PAIR.getValue()).id()));
                    linkedHashMap.put("targetCurrency",currencyService.findById(crossExchangeRates.get().get(CurrencyConstants.INDEX_CROSS_SECOND_PAIR.getValue()).id()));
                    BigDecimal rate = convertAmountOfCross(crossExchangeRates.get());
                    linkedHashMap.put("rate",rate);
                    linkedHashMap.put("amount", amount);
                    linkedHashMap.put("convertedAmount", rate.multiply(amount));
                    return linkedHashMap;
                }
                return Map.of();
            }
            return Map.of();
        }
        return Map.of();
    }
    public Map<String,String> existsReverseExchangeRate(String to, String from){
        return exchangeRateService.findExchangeRateCodes(to + from);
    }
    public Optional<List<ExchangeRateEntity>> findCrossExchangeRate(String from, String to){
        List<ExchangeRateEntity> exchangeRateEntityList = exchangeRateDao.findAll();
        for (ExchangeRateEntity exchangeRateEntity: exchangeRateEntityList){
            CurrenciesEntity baseCurrency = currencyService.findById(exchangeRateEntity.baseCurrencyId());
            List<ExchangeRateEntity> listPairsFromTo = findPairForCurrency(baseCurrency,from,to);
            if (listPairsFromTo.size() == CurrencyConstants.CROSS_PAIR_COMPONENTS.getValue()){
                return Optional.of(listPairsFromTo);
            }
        }
        return Optional.empty();
    }
    private List<ExchangeRateEntity> findPairForCurrency(CurrenciesEntity baseCurrency,String from, String to) {
        List<ExchangeRateEntity> exchangeRateEntityList = exchangeRateDao.findAllPairs(baseCurrency.id());
        List<ExchangeRateEntity> listPairsFromTo = new ArrayList<>();
        for (ExchangeRateEntity exchangeRateEntity : exchangeRateEntityList) {
            CurrenciesEntity targetCurrency = currencyService.findById(exchangeRateEntity.targetCurrencyId());
            if (targetCurrency.code().equals(from) || targetCurrency.code().equals(to)){
                listPairsFromTo.add(exchangeRateEntity);
            }
        }
        return listPairsFromTo;
    }
    private BigDecimal convertAmountOfCross(List<ExchangeRateEntity> listCrossExchangeRates){
        BigDecimal rateCrossToCurrency1 = listCrossExchangeRates.get(CurrencyConstants.INDEX_CROSS_FIRST_PAIR.getValue()).rate();
        BigDecimal rateCrossToCurrency2 = listCrossExchangeRates.get(CurrencyConstants.INDEX_CROSS_SECOND_PAIR.getValue()).rate();

        return rateCrossToCurrency1.divide(rateCrossToCurrency2,2, RoundingMode.HALF_UP);
    }


}
