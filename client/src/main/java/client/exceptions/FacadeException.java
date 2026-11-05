package client.exceptions;

public class FacadeException extends Exception {
    public int statusCode;

    public FacadeException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    public FacadeException(String message, Throwable ex) {
        super(message, ex);
    }
}
