package org.kamilkhusainov.currency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kamilkhusainov.currency.dao.CurrencyDao;
import org.kamilkhusainov.currency.dao.ExchangeRateDao;
import org.kamilkhusainov.currency.dto.ExchangeRateDto;
import org.kamilkhusainov.currency.entity.CurrenciesEntity;
import org.kamilkhusainov.currency.exceptions.DaoException;
import org.kamilkhusainov.currency.exceptions.ServiceException;
import org.kamilkhusainov.currency.infrastructure.AppContainer;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDao;
    private final CurrencyService currencyService;

    public ExchangeRateService(ExchangeRateDao exchangeRateDao, CurrencyService currencyService) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyService = currencyService;
    }

    public String create(ExchangeRateDto exchangeRateDto) throws JsonProcessingException {
        try {
            exchangeRateDao.insert(exchangeRateDto);
        }
        catch (DaoException daoException){
            throw new ServiceException(daoException.getMessage(),daoException.getCause());
        }
        CurrenciesEntity baseCurrencyId = currencyService.findByCode(exchangeRateDto.baseCurrencyId());
        CurrenciesEntity targetCurrencyId = currencyService.findByCode(exchangeRateDto.targetCurrencyId());
        long id = exchangeRateDao.findByCodes(baseCurrencyId.code(),targetCurrencyId.code());
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("baseCurrencyId",mapper.writeValueAsString(baseCurrencyId));
        map.put("targetCurrencyId",mapper.writeValueAsString(targetCurrencyId));
        map.put("rate",Integer.parseInt(exchangeRateDto.rate()));
    }

}
