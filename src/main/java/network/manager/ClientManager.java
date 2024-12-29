package network.manager;

import network.client.GameClient;
import network.packet.GameData;
import network.protocol.MessageType;

import java.util.function.Consumer;

public class ClientManager {
    private final GameClient gameClient;

    public ClientManager(String host, int port, Consumer<GameData> onDataReceived) throws Exception {
        gameClient = new GameClient(host, port, onDataReceived);
        gameClient.receive();
    }

    public void sendPlayerData(int playerId, int x, int y, String direction, int spriteNum) {
        gameClient.send(new GameData(MessageType.PLAYER_POSITION, playerId, x, y, direction, spriteNum));
    }

    public int getPlayerId() {
        return gameClient.getPlayerId();
    }
}