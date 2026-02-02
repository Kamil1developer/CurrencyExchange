package org.kamilkhusainov.currency.infrastructure.db;

import org.kamilkhusainov.currency.dao.CurrencyDao;

import javax.sql.DataSource;

public class Daos {
    private final CurrencyDao currencyDao;
    public Daos(DataSource dataSource){
        currencyDao = new CurrencyDao(dataSource);
    }
    public CurrencyDao currencyDao(){
        return currencyDao;
    }
}
