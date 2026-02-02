package org.kamilkhusainov.currency.infrastructure.db;

import javax.sql.DataSource;

public class AppContainer {
    private final Daos daos;
    private final Services services;

    public AppContainer(DataSource dataSource) {
        daos = new Daos(dataSource);
        services = new Services(daos);
    }
    public Services services(){
        return services;
    }
}
