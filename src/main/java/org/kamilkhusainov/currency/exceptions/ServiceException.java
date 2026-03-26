package org.kamilkhusainov.currency.exceptions;

public class ServiceException extends RuntimeException{
    private Type type;
    public ServiceException(String message,Throwable cause){
        super(message,cause);
    }
    public ServiceException(Type type){
        super(type.getMessage());
        this.type = type;
    }
    public Type getType(){
        return type;
    }
    public enum Type {
        DUPLICATE_CURRENCY_CODE("Валюта с таким кодом уже существует",409),
        DUPLICATE_EXCHANGE_RATE_CODE("Валютная пара с таким кодом уже существует",409),
        MISSING_FIELD_CODE("Отсутствует нужное поле формы",400),
        NOT_INTEGER_CODE("Ожидается число",400),
        DATABASE_ERROR("База данных недоступна",500);
        private final String text;
        private final int code;
        Type(String text,Integer code){
            this.text = text;
            this.code = code;
        }
        public String getMessage() {
            return text;
        }
        public int getCode() {
            return code;
        }
    }

}
