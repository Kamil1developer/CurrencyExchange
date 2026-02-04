package org.kamilkhusainov.currency.service;

import org.kamilkhusainov.currency.dao.CurrencyDao;
import org.kamilkhusainov.currency.entity.CurrenciesEntity;
import org.kamilkhusainov.currency.infrastructure.db.Infrastructure;

import java.util.List;

public class CurrencyService {
    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }
    public List<CurrenciesEntity> getAll(){
        List<CurrenciesEntity> entityList = currencyDao.findAll();
        return entityList;
    }

}
