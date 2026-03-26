package org.kamilkhusainov.currency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.kamilkhusainov.currency.CurrencyConstants;
import org.kamilkhusainov.currency.dao.ExchangeRateDao;
import org.kamilkhusainov.currency.dto.ExchangeRateDto;
import org.kamilkhusainov.currency.entity.CurrenciesEntity;
import org.kamilkhusainov.currency.entity.ExchangeRateEntity;
import org.kamilkhusainov.currency.exceptions.DaoException;
import org.kamilkhusainov.currency.exceptions.ServiceException;

import java.awt.*;
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

    public Map<String, Object> create(ExchangeRateDto exchangeRateDto) throws JsonProcessingException, DaoException {
            int id = exchangeRateDao.insert(exchangeRateDto);
            if (id != CurrencyConstants.ALREADY_EXISTS.getValue()) {
                Map<String, Object> linkedHashMap = new LinkedHashMap<>();
                linkedHashMap.put("id", id);
                linkedHashMap.put("baseCurrency", currencyService.findByCode(exchangeRateDto.baseCurrencyCode()));
                linkedHashMap.put("targetCurrency", currencyService.findByCode(exchangeRateDto.targetCurrencyCode()));
                linkedHashMap.put("rate", exchangeRateDto.rate());
                return linkedHashMap;
            }
            else {
                throw new ServiceException(ServiceException.Type.DUPLICATE_EXCHANGE_RATE_CODE);
            }
    }


    public List<Map<String, Object>> findAll(){
        List<ExchangeRateEntity> exchangeRateEntityList;
        List<Map<String, Object>> list = new LinkedList<>();
        try {
            exchangeRateEntityList = exchangeRateDao.findAll();
            for(ExchangeRateEntity exchangeRateEntity:exchangeRateEntityList) {
                ExchangeRateDto exchangeRateDto = new ExchangeRateDto(String.valueOf(exchangeRateEntity.baseCurrencyId()),String.valueOf(exchangeRateEntity.targetCurrencyId()),String.valueOf(exchangeRateEntity.rate()));
                CurrenciesEntity baseCurrencyId = currencyService.findById(exchangeRateDto.baseCurrencyCode());
                CurrenciesEntity targetCurrencyId = currencyService.findById(exchangeRateDto.targetCurrencyCode());
                long id = exchangeRateDao.findByCodes(baseCurrencyId.code(),targetCurrencyId.code());
                Map<String,Object> map = Map.of("id",id,"baseCurrency",baseCurrencyId,"targetCurrency",targetCurrencyId,"rate",exchangeRateDto.rate());

                list.add(map);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
    public long findId(String currencyCodes){
        List<ExchangeRateEntity> exchangeRateEntityList;
        exchangeRateEntityList = exchangeRateDao.findAll();
        for(ExchangeRateEntity exchangeRateEntity:exchangeRateEntityList) {
            ExchangeRateDto exchangeRateDto = new ExchangeRateDto(String.valueOf(exchangeRateEntity.baseCurrencyId()),String.valueOf(exchangeRateEntity.targetCurrencyId()),String.valueOf(exchangeRateEntity.rate()));
            String code = currencyService.findByCode(exchangeRateDto.baseCurrencyCode()).code() + currencyService.findByCode(exchangeRateDto.targetCurrencyCode()).code();
            if (currencyCodes.equals(code)){
                return exchangeRateEntity.id();
            }
        }
        return -1;
    }
    public Map<String,String> findCurrenciesCode(String currencyCodes){
        List<ExchangeRateEntity> exchangeRateEntityList;
        exchangeRateEntityList = exchangeRateDao.findAll();
        for(ExchangeRateEntity exchangeRateEntity:exchangeRateEntityList) {
            ExchangeRateDto exchangeRateDto = new ExchangeRateDto(String.valueOf(exchangeRateEntity.baseCurrencyId()),String.valueOf(exchangeRateEntity.targetCurrencyId()),String.valueOf(exchangeRateEntity.rate()));
            CurrenciesEntity baseCurrencyId = currencyService.findById(exchangeRateDto.baseCurrencyCode());
            CurrenciesEntity targetCurrencyId = currencyService.findById(exchangeRateDto.targetCurrencyCode());
            String code = baseCurrencyId.code() + targetCurrencyId.code();
            if (currencyCodes.equals(code)){
                return Map.of("baseCurrencyCode",baseCurrencyId.code(),"targetCurrencyCode", targetCurrencyId.code());
            }
        }
        return Map.of();
    }
    public Map<String, Object> patch(String currencyCodes,String rate){
        if (isValidRate(rate)) {
            long id = findId(currencyCodes);
            exchangeRateDao.update(id,rate);
            Map<String,String> currenciesCode = findCurrenciesCode(currencyCodes);
            Map<String, Object> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap.put("id", id);
            linkedHashMap.put("baseCurrency", currencyService.findByCode(currenciesCode.get("baseCurrencyCode")));
            linkedHashMap.put("targetCurrency", currencyService.findByCode(currenciesCode.get("targetCurrencyCode")));
            linkedHashMap.put("rate", new BigDecimal(rate));
            return linkedHashMap;
        }
        throw new NumberFormatException();
    }


    public boolean isValidRate(String rate) throws NumberFormatException {
        new BigDecimal(rate);
        return true;
    }


}
