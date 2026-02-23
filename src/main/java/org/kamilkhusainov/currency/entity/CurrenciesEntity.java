package org.kamilkhusainov.currency.entity;

public class CurrenciesEntity {
    private final long id;
    private final String code;
    private final String name;

    private final String sign;
    public CurrenciesEntity(long id, String code, String name, String sign){
        this.id = id;
        this.code = code;
        this.name = name;
        this.sign = sign;
    }

    public long getId() {
        return id;
    }
    public String getName(){
        return name;
    }
    public String getCode(){
        return code;
    }
    public String getSign(){
        return sign;
    }
}