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
                Optional<List<ExchangeRateEntity>> crossExchangeRates = findCrossExchangeRate(from,to);
                if (crossExchangeRates.isPresent()) {
                    CurrencyResponseDto baseCurrency = currencyService.findById(crossExchangeRates.get().get(CurrencyConstants.INDEX_CROSS_FIRST_PAIR).id());
                    CurrencyResponseDto targetCurrency = currencyService.findById(crossExchangeRates.get().get(CurrencyConstants.INDEX_CROSS_SECOND_PAIR).id());
                    BigDecimal rate = convertAmountOfCross(crossExchangeRates.get());

                    return Optional.of(new ExchangeAmountResponseDto(baseCurrency,targetCurrency,rate,amount, rate.multiply(amount)));
                }
                else{
                    return Optional.empty();
                }
            }
            CurrencyResponseDto baseCurrency = currencyService.findByCode(reverseExists.get("targetCurrencyCode"));
            CurrencyResponseDto targetCurrency = currencyService.findByCode(reverseExists.get("baseCurrencyCode"));
            BigDecimal rate = BigDecimal.ONE.divide(exchangeRateDao.getRate(targetCurrency.id(), baseCurrency.id()),15,RoundingMode.HALF_UP);
            return Optional.of(new ExchangeAmountResponseDto(baseCurrency,targetCurrency,rate,amount, rate.multiply(amount)));
        }
        CurrencyResponseDto baseCurrency = currencyService.findByCode(exchangeRateCodes.get("baseCurrencyCode"));
        CurrencyResponseDto targetCurrency = currencyService.findByCode(exchangeRateCodes.get("targetCurrencyCode"));
        BigDecimal rate = exchangeRateDao.getRate(baseCurrency.id(), targetCurrency.id());
        return Optional.of(new ExchangeAmountResponseDto(baseCurrency,targetCurrency,rate,amount, rate.multiply(amount)));
    }

    public Map<String,String> existsReverseExchangeRate(String to, String from){
        return exchangeRateService.findExchangeRateCodes(to + from);
    }

    public Optional<List<ExchangeRateEntity>> findCrossExchangeRate(String from, String to){
        List<ExchangeRateRow> exchangeRateRowList = exchangeRateDao.findAll();
        for (ExchangeRateRow exchangeRateRow: exchangeRateRowList){
            CurrencyResponseDto baseCurrency = currencyService.findById(exchangeRateRow.baseCurrencyId());
            List<ExchangeRateEntity> listPairsFromTo = findPairForCurrency(baseCurrency,from,to);
            if (listPairsFromTo.size() == CurrencyConstants.CROSS_PAIR_COMPONENTS){
                return Optional.of(listPairsFromTo);
            }
        }
        return Optional.empty();
    }

    private List<ExchangeRateEntity> findPairForCurrency(CurrencyResponseDto baseCurrency, String from, String to) {
        List<ExchangeRateEntity> exchangeRateEntityList = exchangeRateDao.findAllByBaseCurrencyID(baseCurrency.id());
        List<ExchangeRateEntity> listPairsFromTo = new ArrayList<>();
        for (ExchangeRateEntity exchangeRateEntity : exchangeRateEntityList) {
            CurrencyResponseDto targetCurrency = currencyService.findById(exchangeRateEntity.targetCurrencyId());
            if (targetCurrency.code().equals(from) || targetCurrency.code().equals(to)){
                listPairsFromTo.add(exchangeRateEntity);
            }
        }
        return listPairsFromTo;
    }

    private BigDecimal convertAmountOfCross(List<ExchangeRateEntity> listCrossExchangeRates){
        BigDecimal rateCrossToCurrency1 = listCrossExchangeRates.get(CurrencyConstants.INDEX_CROSS_FIRST_PAIR).rate();
        BigDecimal rateCrossToCurrency2 = listCrossExchangeRates.get(CurrencyConstants.INDEX_CROSS_SECOND_PAIR).rate();

        return rateCrossToCurrency2.divide(rateCrossToCurrency1,2, RoundingMode.HALF_UP);
    }


}
