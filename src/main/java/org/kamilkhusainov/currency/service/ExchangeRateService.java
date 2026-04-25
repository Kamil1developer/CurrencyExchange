package org.kamilkhusainov.currency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.kamilkhusainov.currency.dao.ExchangeRateDao;
import org.kamilkhusainov.currency.dto.*;
import org.kamilkhusainov.currency.entity.CurrencyEntity;
import org.kamilkhusainov.currency.entity.ExchangeRateRow;
import org.kamilkhusainov.currency.exceptions.AlreadyExistsException;
import org.kamilkhusainov.currency.exceptions.ErrorMessages;
import org.kamilkhusainov.currency.exceptions.NotFoundException;
import org.kamilkhusainov.currency.exceptions.ValidationException;

import java.math.BigDecimal;
import java.util.*;
import java.util.List;

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDao;
    private final CurrencyService currencyService;

    public ExchangeRateService(ExchangeRateDao exchangeRateDao, CurrencyService currencyService) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyService = currencyService;
    }

    public ExchangeRateResponseDto create(ExchangeRequestDto exchangeRateDto) throws JsonProcessingException {
            CurrencyResponseDto baseCurrency = currencyService.find(exchangeRateDto.baseCurrencyCode());
            CurrencyResponseDto targetCurrency = currencyService.find(exchangeRateDto.targetCurrencyCode());
            BigDecimal rate = new BigDecimal(exchangeRateDto.rate());
            int id = exchangeRateDao.insert(baseCurrency.id(),targetCurrency.id(),rate);
            return new ExchangeRateResponseDto(id,
                    baseCurrency,
                    targetCurrency,
                    new BigDecimal(exchangeRateDto.rate()));
    }


    public List<ExchangeRateResponseDto> findAll(){
        List<ExchangeRateResponseDto> exchangeRateResponseDtoList = new LinkedList<>();
        try {
            List<ExchangeRateRow> exchangeRateRowList = exchangeRateDao.findAll();
            for(ExchangeRateRow exchangeRateRow: exchangeRateRowList) {
                CurrencyResponseDto baseCurrency = new CurrencyResponseDto(
                        exchangeRateRow.baseCurrencyId(),
                        exchangeRateRow.baseCurrencyCode(),
                        exchangeRateRow.baseCurrencyName(),
                        exchangeRateRow.baseCurrencySign()

                );

                CurrencyResponseDto targetCurrency = new CurrencyResponseDto(
                        exchangeRateRow.targetCurrencyId(),
                        exchangeRateRow.targetCurrencyCode(),
                        exchangeRateRow.targetCurrencyName(),
                        exchangeRateRow.targetCurrencySign()

                );
                exchangeRateResponseDtoList.add(new ExchangeRateResponseDto(exchangeRateRow.exchangeRateId(), baseCurrency, targetCurrency, exchangeRateRow.rate()));
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return exchangeRateResponseDtoList;
    }
    public ExchangeRateRow find(String baseCurrencyCode, String targetCurrencyCode){
        return exchangeRateDao.findByCodesWithCurrencies(baseCurrencyCode, targetCurrencyCode);
    }
    public Map<String,String> findExchangeRateCodes(String currencyCodes){
        List<ExchangeRateRow> exchangeRateRowList =  exchangeRateDao.findAll();
        for(ExchangeRateRow exchangeRateRow:exchangeRateRowList) {
            String baseCurrency = exchangeRateRow.baseCurrencyCode();
            String targetCurrency = exchangeRateRow.targetCurrencyCode();
            String code = baseCurrency + targetCurrency;
            if (currencyCodes.equals(code)){
                return Map.of("baseCurrencyCode",baseCurrency,"targetCurrencyCode", targetCurrency);
            }
        }
        return Map.of();
    }
    public BigDecimal findRate(String currencyCodes){
        List<ExchangeRateRow> exchangeRateRowList = exchangeRateDao.findAll();
        for(ExchangeRateRow exchangeRateRow: exchangeRateRowList) {
            String baseCurrency = exchangeRateRow.baseCurrencyCode();
            String targetCurrency = exchangeRateRow.targetCurrencyCode();
            String code = baseCurrency + targetCurrency;
            if (currencyCodes.equals(code)){
                return exchangeRateRow.rate();
            }
        }
        throw new NotFoundException(ErrorMessages.RATE_NOT_FOUND);
    }
    public ExchangeRateResponseDto getExchangeRate(String exchangeRateCodes){
        String baseCurrencyCode = exchangeRateCodes.substring(0,3);
        String targetCurrencyCode = exchangeRateCodes.substring(3,6);
        long id = find(baseCurrencyCode, targetCurrencyCode).exchangeRateId();
        Map<String,String> exchangeRateCodesMap = findExchangeRateCodes(exchangeRateCodes);
        BigDecimal rate = findRate(exchangeRateCodes);
        CurrencyResponseDto baseCurrency = currencyService.find(exchangeRateCodesMap.get("baseCurrencyCode"));
        CurrencyResponseDto targetCurrency = currencyService.find(exchangeRateCodesMap.get("targetCurrencyCode"));
        return new ExchangeRateResponseDto(id, baseCurrency, targetCurrency, rate);

    }
    public ExchangeRateUpdateResponseDto patch(String exchangeRateCodes, ExchangeRateUpdateRequestDto requestDto){
        String baseCurrencyCode = exchangeRateCodes.substring(0, 3);
        String targetCurrencyCode = exchangeRateCodes.substring(3, 6);

        ExchangeRateRow exchangeRateRow = find(baseCurrencyCode, targetCurrencyCode);
        long id = exchangeRateRow.exchangeRateId();
        exchangeRateDao.update(id, requestDto.rate());
        exchangeRateRow = find(baseCurrencyCode, targetCurrencyCode);

        CurrencyResponseDto baseCurrency = new CurrencyResponseDto(
                exchangeRateRow.baseCurrencyId(),
                exchangeRateRow.baseCurrencyCode(),
                exchangeRateRow.baseCurrencyName(),
                exchangeRateRow.baseCurrencySign());

        CurrencyResponseDto targetCurrency = new CurrencyResponseDto(
                exchangeRateRow.targetCurrencyId(),
                exchangeRateRow.targetCurrencyCode(),
                exchangeRateRow.targetCurrencyName(),
                exchangeRateRow.targetCurrencySign());
        return new ExchangeRateUpdateResponseDto(id,baseCurrency,targetCurrency,exchangeRateRow.rate());
    }

}
