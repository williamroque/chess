package dataaccess.exceptions;

public class InvalidGameNameException extends RuntimeException {
    public InvalidGameNameException(String message) {
        super(message);
    }
    public InvalidGameNameException(String message, Throwable ex) {
        super(message, ex);
    }
}
