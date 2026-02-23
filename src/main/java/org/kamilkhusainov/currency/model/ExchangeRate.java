package org.kamilkhusainov.currency.model;

public class ExchangeRate {
    private final int BaseCurrencyId;
    private final int TargetCurrencyId;
    private final double Rate;
    public ExchangeRate(int BaseCurrencyId, int TargetCurrencyId, double Rate){
        this.BaseCurrencyId = BaseCurrencyId;
        this.TargetCurrencyId = TargetCurrencyId;
        this.Rate = Rate;
    }

    public int getBaseCurrencyId() {
        return BaseCurrencyId;
    }

    public int getTargetCurrencyId() {
        return TargetCurrencyId;
    }

    public double getRate() {
        return Rate;
    }
}
