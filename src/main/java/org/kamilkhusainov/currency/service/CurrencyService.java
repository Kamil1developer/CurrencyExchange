package org.kamilkhusainov.currency.service;

import org.kamilkhusainov.currency.dao.CurrencyDao;
import org.kamilkhusainov.currency.entity.CurrenciesEntity;
import org.kamilkhusainov.currency.exceptions.DaoException;
import org.kamilkhusainov.currency.exceptions.ServiceException;
import org.kamilkhusainov.currency.model.Currency;

import java.util.List;
import java.util.Optional;

public class CurrencyService {
    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }
    public List<CurrenciesEntity> findAll(){
        return currencyDao.findAll();
    }
    public CurrenciesEntity findByCode(String code){
        return currencyDao.findByCode(code);
    }
    public void create(Currency currency){
        try {
            currencyDao.insert(currency);
        }
        catch (DaoException daoException){
            throw new ServiceException(daoException.getMessage(),daoException.getCause());
        }

    }


}
