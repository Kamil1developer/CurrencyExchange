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
            long baseCurrencyId = currencyService.findByCode(exchangeRateDto.baseCurrencyCode()).id();
            long targetCurrencyId = currencyService.findByCode(exchangeRateDto.targetCurrencyCode()).id();
            BigDecimal rate = new BigDecimal(exchangeRateDto.rate());
            int id = exchangeRateDao.insert(baseCurrencyId,targetCurrencyId,rate);
            if (id != CurrencyConstants.ALREADY_EXISTS.getValue()) {
                Map<String, Object> linkedHashMap = new LinkedHashMap<>();
                linkedHashMap.put("id", id);
                linkedHashMap.put("baseCurrency", currencyService.findById(baseCurrencyId));
                linkedHashMap.put("targetCurrency", currencyService.findById(targetCurrencyId));
                linkedHashMap.put("rate", new BigDecimal(exchangeRateDto.rate()));
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
                CurrenciesEntity baseCurrency = currencyService.findById(exchangeRateEntity.baseCurrencyId());
                CurrenciesEntity targetCurrency = currencyService.findById(exchangeRateEntity.targetCurrencyId());
                long id = exchangeRateDao.findByCodes(baseCurrency.id(),targetCurrency.id());
                Map<String,Object> map = Map.of("id",id,"baseCurrency",baseCurrency,"targetCurrency",targetCurrency,"rate",exchangeRateEntity.rate());

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
            String code = currencyService.findById(exchangeRateEntity.baseCurrencyId()).code() + currencyService.findById(exchangeRateEntity.targetCurrencyId()).code();
            if (currencyCodes.equals(code)){
                return exchangeRateEntity.id();
            }
        }
        return -1;
    }
    public Map<String,String> findExchangeRateCodes(String currencyCodes){
        List<ExchangeRateEntity> exchangeRateEntityList;
        exchangeRateEntityList = exchangeRateDao.findAll();
        for(ExchangeRateEntity exchangeRateEntity:exchangeRateEntityList) {
            CurrenciesEntity baseCurrency = currencyService.findById(exchangeRateEntity.baseCurrencyId());
            CurrenciesEntity targetCurrency = currencyService.findById(exchangeRateEntity.targetCurrencyId());
            String code = currencyService.findById(baseCurrency.id()).code() + currencyService.findById(targetCurrency.id()).code();
            if (currencyCodes.equals(code)){
                return Map.of("baseCurrencyCode",baseCurrency.code(),"targetCurrencyCode", targetCurrency.code());
            }
        }
        return Map.of();
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
            throw new ServiceException("Поле rate неправильное ",numberFormatException);
        }
        return true;
    }


}
