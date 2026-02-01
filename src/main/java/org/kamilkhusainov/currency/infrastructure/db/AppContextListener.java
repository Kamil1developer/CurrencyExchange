package org.kamilkhusainov.currency.infrastructure.db;

import com.zaxxer.hikari.HikariConfig;
import org.kamilkhusainov.currency.dao.CurrencyDao;
import org.kamilkhusainov.currency.service.CurrencyService;

import com.zaxxer.hikari.HikariDataSource;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.util.Properties;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DatabaseConfig.getUrl());
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);

        ServletContext context = event.getServletContext();
        DataSource dataSource = new HikariDataSource(config);
        CurrencyDatabaseInitializer initializer = new CurrencyDatabaseInitializer(dataSource);
        initializer.init();
        context.setAttribute("dataSource",dataSource);
    }
}
