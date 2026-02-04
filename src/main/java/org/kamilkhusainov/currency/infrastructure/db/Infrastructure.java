package org.kamilkhusainov.currency.infrastructure.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class Infrastructure {
    private final DataSource dataSource;

    public Infrastructure(){
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);
        config.setJdbcUrl(DatabaseConfig.getUrl());
        config.setDriverClassName("org.sqlite.JDBC");
        dataSource = new HikariDataSource(config);
    }
    public DataSource dataSource(){
        return dataSource;
    }
}
