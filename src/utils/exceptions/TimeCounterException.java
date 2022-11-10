package utils.exceptions;

public class TimeCounterException extends RuntimeException {

    public TimeCounterException() {
    }

    public TimeCounterException(String message) {
        super(message);
    }

    public TimeCounterException(Throwable cause) {
        super(cause);
    }

    public TimeCounterException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeCounterException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
