package org.kamilkhusainov.currency.exceptions;

public class ServiceException extends RuntimeException{
    public ServiceException(String message,Throwable cause){
        super(message,cause);
    }
    public enum Type {
        DUPLICATE_CURRENCY_CODE("Валюта с таким кодом уже существует",409),
        MISSING_FIELD_CODE("Отсутствует нужное поле формы",400);
        private final String text;
        private final int code;
        Type(String text,Integer code){
            this.text = text;
            this.code = code;
        }
        public String getText() {
            return text;
        }
        public int getCode() {
            return code;
        }
    }

}
