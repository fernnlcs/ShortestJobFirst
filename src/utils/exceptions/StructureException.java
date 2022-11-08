package utils.exceptions;

public class StructureException extends RuntimeException {
    
    private String message = "";

    public StructureException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    
}
