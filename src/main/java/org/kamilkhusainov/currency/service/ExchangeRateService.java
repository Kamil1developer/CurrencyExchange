package org.kamilkhusainov.currency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.kamilkhusainov.currency.dao.ExchangeRateDao;
import org.kamilkhusainov.currency.dto.CurrencyResponseDto;
import org.kamilkhusainov.currency.dto.ExchangeRateResponseDto;
import org.kamilkhusainov.currency.dto.ExchangeRequestDto;
import org.kamilkhusainov.currency.dto.ExchangeAmountResponseDto;
import org.kamilkhusainov.currency.entity.CurrencyEntity;
import org.kamilkhusainov.currency.entity.ExchangeRateEntity;
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
            long baseCurrencyId = currencyService.findByCode(exchangeRateDto.baseCurrencyCode()).id();
            long targetCurrencyId = currencyService.findByCode(exchangeRateDto.targetCurrencyCode()).id();
            BigDecimal rate = new BigDecimal(exchangeRateDto.rate());
            int id = exchangeRateDao.insert(baseCurrencyId,targetCurrencyId,rate);
            if (id != 0) {
                return new ExchangeRateResponseDto(id,
                        currencyService.findById(baseCurrencyId),
                        currencyService.findById(targetCurrencyId),
                        new BigDecimal(exchangeRateDto.rate()));
            }
            else {
                throw new AlreadyExistsException(ErrorMessages.DUPLICATE_EXCHANGE_RATE);
            }
    }


    public List<ExchangeRateResponseDto> findAll(){
        List<ExchangeRateResponseDto> exchangeRateResponseDtoList = new LinkedList<>();
        try {
            List<ExchangeRateEntity> exchangeRateEntityList = exchangeRateDao.findAll();
            for(ExchangeRateEntity exchangeRateEntity:exchangeRateEntityList) {
                CurrencyResponseDto baseCurrency = currencyService.findById(exchangeRateEntity.baseCurrencyId());
                CurrencyResponseDto targetCurrency = currencyService.findById(exchangeRateEntity.targetCurrencyId());
                long id = exchangeRateDao.findByCodes(baseCurrency.id(),targetCurrency.id());
                exchangeRateResponseDtoList.add(new ExchangeRateResponseDto(id, baseCurrency, targetCurrency, exchangeRateEntity.rate()));
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return exchangeRateResponseDtoList;
    }
    public long findId(String currencyCodes){
        List<ExchangeRateEntity> exchangeRateEntityList;
        exchangeRateEntityList = exchangeRateDao.findAll();
        for(ExchangeRateEntity exchangeRateEntity:exchangeRateEntityList) {
            String code = currencyService.findById(exchangeRateEntity.baseCurrencyId()).code() + currencyService.findById(exchangeRateEntity.targetCurrencyId()).code();
            if (currencyCodes.equals(code)){
                return exchangeRateEntity.id();
            }
        }
        throw new NotFoundException(ErrorMessages.EXCHANGE_RATE_NOT_FOUND);
    }
    public Map<String,String> findExchangeRateCodes(String currencyCodes){
        List<ExchangeRateEntity> exchangeRateEntityList =  exchangeRateDao.findAll();
        for(ExchangeRateEntity exchangeRateEntity:exchangeRateEntityList) {
            CurrencyResponseDto baseCurrency = currencyService.findById(exchangeRateEntity.baseCurrencyId());
            CurrencyResponseDto targetCurrency = currencyService.findById(exchangeRateEntity.targetCurrencyId());
            String code = currencyService.findById(baseCurrency.id()).code() + currencyService.findById(targetCurrency.id()).code();
            if (currencyCodes.equals(code)){
                return Map.of("baseCurrencyCode",baseCurrency.code(),"targetCurrencyCode", targetCurrency.code());
            }
        }
        return Map.of();
    }
    public BigDecimal findRate(String currencyCodes){
        List<ExchangeRateEntity> exchangeRateEntityList;
        exchangeRateEntityList = exchangeRateDao.findAll();
        for(ExchangeRateEntity exchangeRateEntity:exchangeRateEntityList) {
            CurrencyResponseDto baseCurrency = currencyService.findById(exchangeRateEntity.baseCurrencyId());
            CurrencyResponseDto targetCurrency = currencyService.findById(exchangeRateEntity.targetCurrencyId());
            String code = currencyService.findById(baseCurrency.id()).code() + currencyService.findById(targetCurrency.id()).code();
            if (currencyCodes.equals(code)){
                return exchangeRateEntity.rate();
            }
        }
        throw new NotFoundException(ErrorMessages.RATE_NOT_FOUND);
    }
    public ExchangeRateResponseDto getExchangeRate(String exchangeRateCodes){
        long id = findId(exchangeRateCodes);
        Map<String,String> exchangeRateCodesMap = findExchangeRateCodes(exchangeRateCodes);
        BigDecimal rate = findRate(exchangeRateCodes);
        CurrencyResponseDto baseCurrency = currencyService.findByCode(exchangeRateCodesMap.get("baseCurrencyCode"));
        CurrencyResponseDto targetCurrency = currencyService.findByCode(exchangeRateCodesMap.get("targetCurrencyCode"));
        return new ExchangeRateResponseDto(id, baseCurrency, targetCurrency, rate);

    }
    public Map<String, Object> patch(String exchangeRateCodes,String rate){
        if (isValidRate(rate)) {
            long id = findId(exchangeRateCodes);
            exchangeRateDao.update(id,rate);
            Map<String,String> exchangeRateCodesMap = findExchangeRateCodes(exchangeRateCodes);
            Map<String, Object> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap.put("id", id);
            linkedHashMap.put("baseCurrency", currencyService.findByCode(exchangeRateCodesMap.get("baseCurrencyCode")));
            linkedHashMap.put("targetCurrency", currencyService.findByCode(exchangeRateCodesMap.get("targetCurrencyCode")));
            linkedHashMap.put("rate", new BigDecimal(rate));
            return linkedHashMap;
        }
        throw new NumberFormatException();
    }

    public boolean isValidRate(String rate) throws NumberFormatException {
        try {
            new BigDecimal(rate);
        } catch (NumberFormatException numberFormatException) {
            throw new ValidationException("Поле rate неправильное ",numberFormatException);
        }
        return true;
    }


}
