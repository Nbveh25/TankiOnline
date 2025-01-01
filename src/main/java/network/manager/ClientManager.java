package network.manager;

import network.client.GameClient;
import network.packet.PlayerData;
import network.packet.BulletData;
import network.protocol.MessageType;

import java.util.function.Consumer;

public class ClientManager {
    private final GameClient gameClient;

    public ClientManager(String host, int port, Consumer<PlayerData> onPlayerDataReceived, 
                        Consumer<BulletData> onBulletDataReceived) throws Exception {
        gameClient = new GameClient(host, port, data -> {
            if (data instanceof PlayerData) {
                onPlayerDataReceived.accept((PlayerData) data);
            } else if (data instanceof BulletData) {
                onBulletDataReceived.accept((BulletData) data);
            }
        });
        gameClient.receive();
    }

    public void sendPlayerData(int playerId, int x, int y, String direction, int spriteNum) {
        gameClient.send(new PlayerData(MessageType.PLAYER_POSITION, playerId, x, y, direction, spriteNum));
    }

    public void sendBulletData(int playerId, int x, int y, String direction) {
        gameClient.send(new BulletData(MessageType.BULLET_SHOT, playerId, x, y, direction));
    }

    public int getPlayerId() {
        return gameClient.getPlayerId();
    }
}