package org.kamilkhusainov.currency.infrastructure;

import org.kamilkhusainov.currency.service.CurrencyService;
import org.kamilkhusainov.currency.service.ExchangeRateService;

public class Services {
    private final CurrencyService currencyService;
    private final ExchangeRateService exchangeRateService;
    public Services(Daos daos){
        currencyService = new CurrencyService(daos.currencyDao());
        exchangeRateService = new ExchangeRateService(daos.exchangeRateDao(),currencyService);
    }
    public CurrencyService currencyService(){
        return currencyService;
    }
    public ExchangeRateService exchangeRateService(){return exchangeRateService;}
}
