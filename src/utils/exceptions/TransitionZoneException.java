package utils.exceptions;

public class TransitionZoneException extends Exception {

    public TransitionZoneException() {
    }

    public TransitionZoneException(String message) {
        super(message);
    }

    public TransitionZoneException(Throwable cause) {
        super(cause);
    }

    public TransitionZoneException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransitionZoneException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
