package org.kamilkhusainov.currency.infrastructure;

import org.kamilkhusainov.currency.dao.CurrencyDao;
import org.kamilkhusainov.currency.dao.ExchangeAmountDao;
import org.kamilkhusainov.currency.dao.ExchangeRateDao;
import org.kamilkhusainov.currency.service.ExchangeRateService;

import javax.sql.DataSource;

public class Daos {
    private final CurrencyDao currencyDao;
    private final ExchangeRateDao exchangeRateDao;
    private final ExchangeAmountDao exchangeAmountDao;
    public Daos(DataSource dataSource){
        currencyDao = new CurrencyDao(dataSource);
        exchangeRateDao = new ExchangeRateDao(dataSource);
        exchangeAmountDao = new ExchangeAmountDao(dataSource);
    }
    public CurrencyDao currencyDao(){
        return currencyDao;
    }
    public ExchangeRateDao exchangeRateDao(){return exchangeRateDao;}
    public ExchangeAmountDao exchangeAmountDao(){return  exchangeAmountDao;}
}
