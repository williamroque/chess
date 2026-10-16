package dataaccess;

public class GameNotFoundException extends Exception{
    public GameNotFoundException(String message) {
        super(message);
    }
    public GameNotFoundException(String message, Throwable ex) {
        super(message, ex);
    }
}
