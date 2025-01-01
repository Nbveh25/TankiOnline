package network.manager;

import entity.Bullet;
import game.GamePanel;
import network.packet.BulletData;
import network.protocol.MessageType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class BulletManager {
    private final GamePanel gp;
    private ConcurrentHashMap<Integer, List<Bullet>> playerBullets;

    public BulletManager(GamePanel gp) {
        this.gp = gp;
        this.playerBullets = new ConcurrentHashMap<>();
    }

    public void addBullet(int playerId, int x, int y, String direction) {
        System.out.println("Adding bullet for player " + playerId + " at " + x + "," + y + " direction: " + direction);
        playerBullets.computeIfAbsent(playerId, k -> new ArrayList<>())
                    .add(new Bullet(gp, x, y, direction));
    }

    public void update() {
        playerBullets.forEach((playerId, bullets) -> {
            bullets.removeIf(bullet -> {
                bullet.update();
                return isOutOfBounds(bullet);
            });
        });
    }

    private boolean isOutOfBounds(Bullet bullet) {
        return bullet.getX() < 0 || bullet.getX() > gp.screenWidth ||
               bullet.getY() < 0 || bullet.getY() > gp.screenHeight;
    }

    public void handleBulletData(BulletData data) {
        System.out.println("Received bullet data from player " + data.getPlayerId());
        if (data.getType() == MessageType.BULLET_SHOT) {
            addBullet(data.getPlayerId(), data.getX(), data.getY(), data.getDirection());
        }
    }

    public void draw(Graphics2D g2) {
        playerBullets.forEach((playerId, bullets) -> {
            bullets.forEach(bullet -> {
                int screenX = bullet.getX() - gp.getPlayer().getX() + gp.getPlayer().getScreenX();
                int screenY = bullet.getY() - gp.getPlayer().getY() + gp.getPlayer().getScreenY();
                
                g2.drawImage(bullet.image, screenX, screenY, gp.tileSize/3, gp.tileSize/3, null);
            });
        });
    }
}
