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
        ServletContext context = event.getServletContext();
        Infrastructure infrastructure = new Infrastructure();

        DataSource dataSource = infrastructure.dataSource();
        CurrencyDatabaseInitializer databaseInitializer = new CurrencyDatabaseInitializer(dataSource);
        databaseInitializer.init();

        AppContainer appContainer = new AppContainer(dataSource);
        context.setAttribute("appContainer",appContainer);
    }
}
