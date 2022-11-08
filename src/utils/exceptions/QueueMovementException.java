package utils.exceptions;

import utils.MovementType;

public class QueueMovementException extends Exception {

    private MovementType movementType;

    public MovementType getMovementType() {
        return movementType;
    }

    public QueueMovementException() {
    }

    public QueueMovementException(MovementType type) {
        this.movementType = type;
    }

    public QueueMovementException(String message) {
        super(message);
    }

    public QueueMovementException(String message, MovementType type) {
        super(message);
        this.movementType = type;
    }

    public QueueMovementException(Throwable cause) {
        super(cause);
    }

    public QueueMovementException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueueMovementException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
