package client;

public class GenericHttpErrorException extends RuntimeException {
    public int statusCode;

    public GenericHttpErrorException(int statusCode) {
        super("Http exception not handled!");

        this.statusCode = statusCode;
    }
}
