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
        if (currencyDao.findByCode(code).isPresent()){
            return currencyDao.findByCode(code).get();
        }
        else{
            throw new ServiceException(ServiceException.Type.CURRENCY_NOT_FOUND);
        }

    }
    public CurrenciesEntity findById(long id){
        return currencyDao.findById(id);
    }
    public CurrenciesEntity create(Currency currency){
        try {
            long id = currencyDao.insert(currency);
            return findById(id);
        }
        catch (DaoException daoException){
            throw new ServiceException(daoException.getMessage(),daoException.getCause());
        }
    }


}
