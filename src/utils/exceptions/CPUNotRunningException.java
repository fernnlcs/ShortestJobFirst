package utils.exceptions;

public class CPUNotRunningException extends Exception {

    public CPUNotRunningException() {
    }

    public CPUNotRunningException(String message) {
        super(message);
    }

    public CPUNotRunningException(Throwable cause) {
        super(cause);
    }

    public CPUNotRunningException(String message, Throwable cause) {
        super(message, cause);
    }

    public CPUNotRunningException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
