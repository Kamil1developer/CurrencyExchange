package org.kamilkhusainov.currency.infrastructure.db;

import com.zaxxer.hikari.HikariConfig;

import javax.sql.DataSource;

public class Infrastructure {
    private final DataSource dataSource;

    public Infrastructure(){
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);
        config.setJdbcUrl(DatabaseConfig.getUrl());
        dataSource = config.getDataSource();
    }
    public DataSource dataSource(){
        return dataSource;
    }
}
