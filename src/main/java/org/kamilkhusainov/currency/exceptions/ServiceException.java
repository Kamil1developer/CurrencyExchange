package org.kamilkhusainov.currency.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ServiceException extends RuntimeException{
    private Type type;
    public ServiceException(String message,Throwable cause){
        super(message,cause);
    }
    public ServiceException(Type type){
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
        MAX_SIGN_LENGTH("Длина знака больше 3 ",400),
        CURRENCY_CODE_MISSING("Код валюты отсутствует в адресе", 400),
        DATABASE_ERROR("База данных недоступна",501),
        CURRENCY_NOT_FOUND("Валюта не найдена",404),
        EXCHANGE_MISSING("Валюта не найдена",400),
        RATE_NOT_FOUND("Курс отсуствует", 400),
        EXCHANGE_RATE_NOT_FOUND("Обменный курс для пары не найден", 404),
        EXCHANGE_RATES_NOT_FOUND("Коды валют пары отсутствуют в адресе", 400);
        private final Map<String,String> map = new HashMap<>();
        private final int code;
        Type( String message, int code){
            map.put("message", message);
            this.code = code;
        }
        public Map<String,String> getMessage() {
            return map;
        }
        public int getCode() {
            return code;
        }
    }

}
