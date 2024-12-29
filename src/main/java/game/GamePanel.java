package game;

import entity.Player;
import network.manager.ClientManager;
import network.manager.PlayerManager;
import network.packet.GameData;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16*16 tile
    final int scale = 3;
    public int tileSize = originalTileSize * scale; // 48*48 tile

    public int maxScreenCol = 16;
    public int maxScreenRow = 12;
    public int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // FPS
    private final int FPS = 60;

    private Thread gameThread;
    private Player player;
    private PlayerManager playerManager;
    private ClientManager clientManager;
    private KeyHandler kh;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        setDoubleBuffered(true);
        this.setFocusable(true);

        kh = new KeyHandler(this);

        this.addKeyListener(kh);

        player = new Player(this, kh);
        playerManager = new PlayerManager(this, kh);

        try {
            clientManager = new ClientManager("localhost", 12345, this::handleGameData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000D / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void handleGameData(GameData data) {
        playerManager.updatePlayer(data);
    }

    public void update() {
        player.update();
        // Отправляем данные о нашем игроке
        if (clientManager != null && clientManager.getPlayerId() != -1) {
            clientManager.sendPlayerData(clientManager.getPlayerId(), player.getX(), player.getY(), player.getDirection(), player.getSpriteNum());
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        player.draw(g2);
        playerManager.drawPlayers(g2);

        g2.dispose();
    }
}