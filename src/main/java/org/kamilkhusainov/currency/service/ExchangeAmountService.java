package org.kamilkhusainov.currency.service;

import org.kamilkhusainov.currency.CurrencyConstants;
import org.kamilkhusainov.currency.dao.ExchangeRateDao;
import org.kamilkhusainov.currency.dto.CurrencyResponseDto;
import org.kamilkhusainov.currency.dto.ExchangeAmountResponseDto;
import org.kamilkhusainov.currency.entity.CurrencyEntity;
import org.kamilkhusainov.currency.entity.ExchangeRateEntity;
import org.kamilkhusainov.currency.entity.ExchangeRateRow;

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

    public Optional<ExchangeAmountResponseDto> existsExchangeRate(String from, String to, BigDecimal amount){
        Map<String,String> exchangeRateCodes = exchangeRateService.findExchangeRateCodes(from + to);
        if (exchangeRateCodes.isEmpty()){
            Map<String, String> reverseExists = existsReverseExchangeRate(to,from);
            if (reverseExists.isEmpty()){
                Optional<List<ExchangeRateRow>> crossExchangeRates = findCrossExchangeRate(from,to);
                if (crossExchangeRates.isPresent()) {
                    CurrencyResponseDto baseCurrency = currencyService.find(crossExchangeRates.get().get(CurrencyConstants.INDEX_CROSS_FIRST_PAIR).baseCurrencyCode());
                    CurrencyResponseDto targetCurrency = currencyService.find(crossExchangeRates.get().get(CurrencyConstants.INDEX_CROSS_SECOND_PAIR).targetCurrencyCode());
                    BigDecimal rate = convertAmountOfCross(crossExchangeRates.get());

                    return Optional.of(new ExchangeAmountResponseDto(baseCurrency,targetCurrency,rate,amount, rate.multiply(amount)));
                }
                else{
                    return Optional.empty();
                }
            }
            CurrencyResponseDto baseCurrency = currencyService.find(reverseExists.get("targetCurrencyCode"));
            CurrencyResponseDto targetCurrency = currencyService.find(reverseExists.get("baseCurrencyCode"));
            BigDecimal rate = BigDecimal.ONE.divide(exchangeRateDao.getRate(targetCurrency.id(), baseCurrency.id()),15,RoundingMode.HALF_UP);
            return Optional.of(new ExchangeAmountResponseDto(baseCurrency,targetCurrency,rate,amount, rate.multiply(amount)));
        }
        CurrencyResponseDto baseCurrency = currencyService.find(exchangeRateCodes.get("baseCurrencyCode"));
        CurrencyResponseDto targetCurrency = currencyService.find(exchangeRateCodes.get("targetCurrencyCode"));
        BigDecimal rate = exchangeRateDao.getRate(baseCurrency.id(), targetCurrency.id());
        return Optional.of(new ExchangeAmountResponseDto(baseCurrency,targetCurrency,rate,amount, rate.multiply(amount)));
    }

    public Map<String,String> existsReverseExchangeRate(String to, String from){
        return exchangeRateService.findExchangeRateCodes(to + from);
    }

    public Optional<List<ExchangeRateRow>> findCrossExchangeRate(String from, String to){
        List<ExchangeRateRow> exchangeRateRowList = exchangeRateDao.findAll();
        for (ExchangeRateRow exchangeRateRow: exchangeRateRowList){
            CurrencyResponseDto baseCurrency = currencyService.find(exchangeRateRow.baseCurrencyCode());
            List<ExchangeRateRow> listPairsFromTo = findPairForCurrency(baseCurrency,from,to);
            if (listPairsFromTo.size() == CurrencyConstants.CROSS_PAIR_COMPONENTS){
                return Optional.of(listPairsFromTo);
            }
        }
        return Optional.empty();
    }

    private List<ExchangeRateRow> findPairForCurrency(CurrencyResponseDto baseCurrency, String from, String to) {
        List<ExchangeRateRow> exchangeRateRowList = exchangeRateDao.findAllByBaseCurrencyID(baseCurrency.id());
        List<ExchangeRateRow> listPairsFromTo = new ArrayList<>();
        for (ExchangeRateRow exchangeRateRow : exchangeRateRowList) {
            String targetCurrency = exchangeRateRow.targetCurrencyCode();
            if (targetCurrency.equals(from) || targetCurrency.equals(to)){
                listPairsFromTo.add(exchangeRateRow);
            }
        }
        return listPairsFromTo;
    }

    private BigDecimal convertAmountOfCross(List<ExchangeRateRow> listCrossExchangeRates){
        BigDecimal rateCrossToCurrency1 = listCrossExchangeRates.get(CurrencyConstants.INDEX_CROSS_FIRST_PAIR).rate();
        BigDecimal rateCrossToCurrency2 = listCrossExchangeRates.get(CurrencyConstants.INDEX_CROSS_SECOND_PAIR).rate();

        return rateCrossToCurrency2.divide(rateCrossToCurrency1,2, RoundingMode.HALF_UP);
    }


}
