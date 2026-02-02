package org.kamilkhusainov.currency.infrastructure.db;

import org.kamilkhusainov.currency.service.CurrencyService;

public class Services {
    private final CurrencyService currencyService;
    public Services(Daos daos){
        currencyService = new CurrencyService(daos.currencyDao());
    }
    public CurrencyService currencyService(){
        return currencyService;
    }
}
