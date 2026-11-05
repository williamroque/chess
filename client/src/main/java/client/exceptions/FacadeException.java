package client.exceptions;

public class FacadeException extends Exception {
    public FacadeException(String message) {
        super(message);
    }
    public FacadeException(String message, Throwable ex) {
        super(message, ex);
    }
}
