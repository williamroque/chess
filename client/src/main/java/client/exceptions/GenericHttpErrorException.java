package client.exceptions;

public class GenericHttpErrorException extends FacadeException {
    public int statusCode;

    public GenericHttpErrorException(int statusCode) {
        super("There was an issue communicating with the server.", statusCode);

        this.statusCode = statusCode;
    }
}
