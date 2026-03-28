package org.kamilkhusainov.currency.dao;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class ExchangeAmountDao {
    private final DataSource dataSource;

    public ExchangeAmountDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
