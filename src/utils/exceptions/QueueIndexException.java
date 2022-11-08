package utils.exceptions;

public class QueueIndexException extends Exception {

    public QueueIndexException() {
    }

    public QueueIndexException(String message) {
        super(message);
    }

    public QueueIndexException(Throwable cause) {
        super(cause);
    }

    public QueueIndexException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueueIndexException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
