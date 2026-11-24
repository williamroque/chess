package websocket.messages;

import chess.ChessGame;
import model.GameData;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class LoadGameMessage extends ServerMessage {
    GameData game;

    public LoadGameMessage(GameData game) {
        super(ServerMessageType.LOAD_GAME);

        this.game = game;
    }
}
