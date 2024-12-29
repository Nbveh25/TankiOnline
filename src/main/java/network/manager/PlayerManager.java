package network.manager;

import entity.Player;
import game.GamePanel;
import game.KeyHandler;
import network.packet.GameData;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private final Map<Integer, Player> players = new HashMap<>();
    private final GamePanel gamePanel;
    private final KeyHandler keyHandler;

    public PlayerManager(GamePanel gamePanel, KeyHandler keyHandler) {
        this.gamePanel = gamePanel;
        this.keyHandler = keyHandler;
    }

    public Player getPlayer(int playerId) {
        return players.get(playerId);
    }

    public void addPlayer(int playerId, Player player) {
        players.put(playerId, player);
    }

    public void updatePlayer(GameData data) {
        Player player = players.computeIfAbsent(data.getPlayerId(), id -> new Player(gamePanel, keyHandler));
        player.setX(data.getPlayerPosX());
        player.setY(data.getPlayerPosY());
        player.setDirection(data.getDirection());
        player.setSpriteNum(data.getSpriteNum()); // Обновляем номер спрайта
    }

    public void drawPlayers(Graphics2D g2) {
        for (Player player : players.values()) {
            player.draw(g2);
        }
    }
}