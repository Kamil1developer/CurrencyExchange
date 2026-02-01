package org.kamilkhusainov.currency.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Currency;

public class CurrencyDao {
    private final DataSource dataSource;

    public CurrencyDao(DataSource dataSource){
        this.dataSource = dataSource;
    }
}
