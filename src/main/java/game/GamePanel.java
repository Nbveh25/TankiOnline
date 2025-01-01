package game;

import entity.Player;
import network.manager.BulletManager;
import network.manager.ClientManager;
import network.manager.PlayerManager;
import network.packet.PlayerData;
import network.packet.BulletData;
import tile.TileManager;
import util.CollisionChecker;

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

    // WORLD SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;


    // FPS
    private final int FPS = 60;

    private Thread gameThread;
    private Player player;
    private KeyHandler kh;

    public CollisionChecker collisionChecker = new CollisionChecker(this);

    private PlayerManager playerManager;
    public ClientManager clientManager;
    private BulletManager bulletManager;

    public TileManager tileManager = new TileManager(this);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        setDoubleBuffered(true);
        this.setFocusable(true);

        kh = new KeyHandler(this);

        this.addKeyListener(kh);

        player = new Player(this, kh);

        playerManager = new PlayerManager(this, kh);

        bulletManager = new BulletManager(this);

        try {
            clientManager = new ClientManager("localhost", 12345, 
                this::handlePlayerData,
                this::handleBulletData);
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

    private void handlePlayerData(PlayerData data) {
        playerManager.update(data);
    }

    private void handleBulletData(BulletData data) {
        bulletManager.handleBulletData(data);
    }

    public void update() {
        player.update();
        bulletManager.update();
        
        if (clientManager != null && clientManager.getPlayerId() != -1) {
            clientManager.sendPlayerData(clientManager.getPlayerId(), 
                player.getX(), player.getY(), player.getDirection(), player.getSpriteNum());
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tileManager.draw(g2);
        player.draw(g2);
        playerManager.draw(g2);
        bulletManager.draw(g2);

        g2.dispose();
    }

    public Player getPlayer() {
        return player;
    }
}