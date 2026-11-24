package client;


import jakarta.websocket.DeploymentException;

import java.io.IOException;
import java.net.URISyntaxException;

public class ClientMain {
    public static void main(String[] args) throws DeploymentException, URISyntaxException, IOException {
        ChessClient client = new ChessClient();

        client.start();
    }
}
