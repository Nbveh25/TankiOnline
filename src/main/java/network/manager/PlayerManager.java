package network.manager;

import entity.Player;
import game.GamePanel;
import game.KeyHandler;
import network.packet.PlayerData;
import util.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
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

    public void update(PlayerData data) {
        Player player = players.computeIfAbsent(data.getPlayerId(), id -> new Player(gamePanel, keyHandler));
        player.setX(data.getPlayerPosX());
        player.setY(data.getPlayerPosY());
        player.setDirection(data.getDirection());
        player.setSpriteNum(data.getSpriteNum()); // Обновляем номер спрайта
    }

    public void draw(Graphics2D g2) {
        for (Player player : players.values()) {
            // Вычисляем экранные координаты относительно позиции основного игрока
            int screenX = player.getX() - gamePanel.getPlayer().getX() + gamePanel.getPlayer().getScreenX();
            int screenY = player.getY() - gamePanel.getPlayer().getY() + gamePanel.getPlayer().getScreenY();
            
            // Получаем текущий спрайт
            BufferedImage image = switch (player.getDirection()) {
                case Constants.UP -> player.getSpriteNum() == 1 ? player.up1 : player.up2;
                case Constants.DOWN -> player.getSpriteNum() == 1 ? player.down1 : player.down2;
                case Constants.LEFT -> player.getSpriteNum() == 1 ? player.left1 : player.left2;
                case Constants.RIGHT -> player.getSpriteNum() == 1 ? player.right1 : player.right2;
                default -> null;
            };

            if (image != null) {
                g2.drawImage(image, screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
            }
        }
    }
}