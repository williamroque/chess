package websocket.messages;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ErrorMessage extends ServerMessage {
    String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);

        this.errorMessage = errorMessage;
    }
}
