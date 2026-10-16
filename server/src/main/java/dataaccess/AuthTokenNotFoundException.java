package dataaccess;

public class AuthTokenNotFoundException extends Exception {
    public AuthTokenNotFoundException(String message) {
        super(message);
    }
    public AuthTokenNotFoundException(String message, Throwable ex) {
        super(message, ex);
    }
}
