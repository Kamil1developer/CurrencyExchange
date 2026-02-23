package org.kamilkhusainov.currency.infrastructure;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

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
        context.setAttribute("dataSource",dataSource);
    }
}
