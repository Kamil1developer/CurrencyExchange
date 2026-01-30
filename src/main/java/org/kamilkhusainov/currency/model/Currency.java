package org.kamilkhusainov.currency.model;
public class Currency {
    private String name;
    private String code;
    private String sign;
    public Currency(String name, String code, String sign){
        this.name = name;
        this.code = code;
        this.sign = sign;
    }
    public String getName(){
        return this.name;
    }
    public String getCode(){
        return this.code;
    }
    public String getSig(){
        return this.sign;
    }
}
