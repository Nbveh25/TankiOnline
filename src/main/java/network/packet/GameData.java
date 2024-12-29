package network.packet;

import network.protocol.MessageType;

import java.io.Serializable;

public class GameData implements Serializable {
    private MessageType messageType;
    private int playerId;
    private int playerPosX;
    private int playerPosY;
    private String direction;
    private int spriteNum; // Номер спрайта для анимации

    public GameData(MessageType messageType, int playerId, int playerPosX, int playerPosY, String direction, int spriteNum) {
        this.messageType = messageType;
        this.playerId = playerId;
        this.playerPosX = playerPosX;
        this.playerPosY = playerPosY;
        this.direction = direction;
        this.spriteNum = spriteNum; // Инициализация номера спрайта
    }

    // Геттеры и сеттеры
    public MessageType getMessageType() {
        return messageType;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getPlayerPosX() {
        return playerPosX;
    }

    public int getPlayerPosY() {
        return playerPosY;
    }

    public String getDirection() {
        return direction;
    }

    public int getSpriteNum() {
        return spriteNum; // Геттер для номера спрайта
    }
}