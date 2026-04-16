package org.kamilkhusainov.currency.service;

import org.kamilkhusainov.currency.dao.CurrencyDao;
import org.kamilkhusainov.currency.entity.CurrenciesEntity;
import org.kamilkhusainov.currency.exceptions.DataBaseException;
import org.kamilkhusainov.currency.exceptions.ErrorMessages;
import org.kamilkhusainov.currency.exceptions.NotFoundException;
import org.kamilkhusainov.currency.model.Currency;

import java.util.List;

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
            throw new NotFoundException(ErrorMessages.CURRENCY_NOT_FOUND);
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
        catch (DataBaseException dataBaseException){
            throw new DataBaseException(dataBaseException.getMessage());
        }
    }


}
