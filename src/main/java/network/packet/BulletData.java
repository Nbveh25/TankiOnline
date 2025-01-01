package network.packet;

import network.protocol.MessageType;

import java.io.Serializable;

public class BulletData implements Serializable {
    private MessageType type;
    private int playerId;
    private int x;
    private int y;
    private String direction;

    public BulletData(MessageType type, int playerId, int x, int y, String direction) {
        this.type = type;
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public MessageType getType() {
        return type;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getDirection() {
        return direction;
    }
}
