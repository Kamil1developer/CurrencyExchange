package org.kamilkhusainov.currency.exceptions;

public final class ErrorMessages {

    private ErrorMessages() {
    }

    public static final String DUPLICATE_CURRENCY =
            "Валюта с таким кодом уже существует";

    public static final String DUPLICATE_EXCHANGE_RATE =
            "Валютная пара с таким кодом уже существует";

    public static final String MISSING_FIELD =
            "Отсутствует нужное поле формы";

    public static final String NOT_INTEGER =
            "Ожидается число";

    public static final String MAX_SIGN_LENGTH =
            "Длина знака больше 3";

    public static final String CURRENCY_CODE_MISSING =
            "Код валюты отсутствует в адресе";

    public static final String DATABASE_ERROR =
            "База данных недоступна";

    public static final String CURRENCY_NOT_FOUND =
            "Валюта не найдена";

    public static final String EXCHANGE_MISSING =
            "Валюта не найдена";

    public static final String RATE_NOT_FOUND =
            "Курс отсутствует";

    public static final String EXCHANGE_RATE_NOT_FOUND =
            "Обменный курс для пары не найден";

    public static final String EXCHANGE_RATES_NOT_FOUND =
            "Коды валют пары отсутствуют в адресе";
}