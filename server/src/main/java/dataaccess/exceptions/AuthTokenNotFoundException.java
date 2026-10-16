package dataaccess.exceptions;

public class AuthTokenNotFoundException extends DataAccessException {
    public AuthTokenNotFoundException(String message) {
        super(message);
    }

    public AuthTokenNotFoundException(String message, Throwable ex) {
        super(message, ex);
    }
}
