package org.kamilkhusainov.currency.model;
public class Currency {
    private final String code;
    private final String name;
    private final String sign;
    public Currency(String name, String code, String sign){
        this.code = code;
        this.name = name;
        this.sign = sign;
    }

    public String getCode(){
        return this.code;
    }
    public String getName(){
        return this.name;
    }
    public String getSign(){
        return this.sign;
    }
}
