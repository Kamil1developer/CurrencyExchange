package org.kamilkhusainov.currency.exceptions;
import org.sqlite.SQLiteErrorCode;

public class DaoException extends RuntimeException{
    public DaoException(String message,Throwable cause){
        super(message,cause);
    }
    public enum Type {
        CONSTRAINT_UNIQUE(SQLiteErrorCode.SQLITE_CONSTRAINT.message,SQLiteErrorCode.SQLITE_CONSTRAINT.code);
        private final String message;
        private final int code;
        Type(String message,Integer code){
            this.message = message;
            this.code = code;
        }
        public String getMessage() {
            return message;
        }
        public int getCode() {
            return code;
        }
    }

}
