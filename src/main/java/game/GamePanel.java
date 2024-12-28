package game;

import entity.Player;
import network.GameClient;
import network.GameData;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16*16 tile
    final int scale = 3;
    public int tileSize = originalTileSize * scale; // 48*48 tile

    public int maxScreenCol = 16;
    public int maxScreenRow = 12;
    public int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public int screenHeight = tileSize * maxScreenRow; // 576 pixels

    //FPS
    private final int FPS = 60;

    KeyHandler kh = new KeyHandler(this);
    private Thread gameThread;
    Player player = new Player(this, kh);

    //Network
    private Map<Integer, Player> players = new HashMap<>();
    private GameClient gameClient;


    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(kh);

        try {
            gameClient = new GameClient("localhost", 12345, this::handleGameData);
            gameClient.receive();
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
        if (!players.containsKey(data.getPlayerId())) {
            players.put(data.getPlayerId(), new Player(this, null));
        }
        Player player = players.get(data.getPlayerId());
        player.setX(data.getX());
        player.setY(data.getY());
        player.setDirection(data.getDirection());
    }

    public void update() {
        player.update();
        // Отправляем данные о нашем игроке
        if (gameClient != null && gameClient.getPlayerId() != -1) {
            gameClient.send(new GameData(
                    gameClient.getPlayerId(),
                    player.getX(),
                    player.getY(),
                    player.getDirection()
            ));
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        player.draw(g2);
        // Отрисовка других игроков
        for (Player p : players.values()) {
            p.draw(g2);
        }

        g2.dispose();
    }
}