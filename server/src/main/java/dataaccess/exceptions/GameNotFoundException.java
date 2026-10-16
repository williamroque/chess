package dataaccess.exceptions;

public class GameNotFoundException extends DataAccessException {
    public GameNotFoundException(String message) {
        super(message);
    }
    public GameNotFoundException(String message, Throwable ex) {
        super(message, ex);
    }
}
