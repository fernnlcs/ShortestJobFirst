package utils.exceptions;

public class CPUIsBusyException extends Exception {

    public CPUIsBusyException() {
    }

    public CPUIsBusyException(String message) {
        super(message);
    }

    public CPUIsBusyException(Throwable cause) {
        super(cause);
    }

    public CPUIsBusyException(String message, Throwable cause) {
        super(message, cause);
    }

    public CPUIsBusyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
