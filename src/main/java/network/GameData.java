package network;

import java.io.Serializable;

public class GameData implements Serializable {
    private int playerId;
    private int x;
    private int y;
    private String direction;

    public GameData(int playerId, int x, int y, String direction) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    // Геттеры и сеттеры
    public int getPlayerId() { return playerId; }
    public int getX() { return x; }
    public int getY() { return y; }
    public String getDirection() { return direction; }
}