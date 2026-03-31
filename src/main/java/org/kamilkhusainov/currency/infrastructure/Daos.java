package org.kamilkhusainov.currency.infrastructure;

import org.kamilkhusainov.currency.dao.CurrencyDao;
import org.kamilkhusainov.currency.dao.ExchangeRateDao;

import javax.sql.DataSource;

public class Daos {
    private final CurrencyDao currencyDao;
    private final ExchangeRateDao exchangeRateDao;
    public Daos(DataSource dataSource){
        currencyDao = new CurrencyDao(dataSource);
        exchangeRateDao = new ExchangeRateDao(dataSource);
    }
    public CurrencyDao currencyDao(){
        return currencyDao;
    }
    public ExchangeRateDao exchangeRateDao(){return exchangeRateDao;}
}
