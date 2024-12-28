package util;

import entity.Bullet;
import game.GamePanel;

import java.awt.*;
import java.util.List;

public class BulletManager {

    public static void drawBullet(Graphics2D g2, List<Bullet> bullets) {
        for (Bullet bullet : bullets) {
            bullet.draw(g2);
        }
    }

    public static void updateBullets(GamePanel gp, List<Bullet> bullets) {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.update();

            // Удаление снарядов, которые вышли за пределы экрана
            if (bullet.x < 0 || bullet.x > gp.screenWidth || bullet.y < 0 || bullet.y > gp.screenHeight) {
                bullets.remove(i);
                i--;
            }
        }
    }

    public static void shoot(GamePanel gp, List<Bullet> bullets, int startX, int startY, String direction) {
        Bullet bullet = new Bullet(gp, startX, startY, direction);
        bullets.add(bullet);
    }
}
