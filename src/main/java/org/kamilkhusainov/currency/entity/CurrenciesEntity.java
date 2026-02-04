package org.kamilkhusainov.currency.entity;

public class CurrenciesEntity {
    private final long id;
    private final String name;
    private final String code;
    private final String sign;
    public CurrenciesEntity(long id, String name, String code, String sign){
        this.id = id;
        this.name = name;
        this.code = code;
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
