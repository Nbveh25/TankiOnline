package entity;

import game.GamePanel;
import game.KeyHandler;
import util.BulletUtil;
import util.Constants;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player extends Entity {
    private GamePanel gp;
    private KeyHandler kh;
    private List<Bullet> bullets;

    private final int screenX;
    private final int screenY;

    private long reloadTime = 500; // Время перезарядки в миллисекундах
    private long lastShotTime = 0;

    public Player(GamePanel gp, KeyHandler kh) {
        this.gp = gp;
        this.kh = kh;
        bullets = new ArrayList<>();

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle();
        solidArea.x = 4;
        solidArea.y = 4;
        solidArea.width = 40;
        solidArea.height = 40;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = 100;
        worldY = 100;
        speed = 1;
        direction = "down";
        setStartMuzzlePosition(direction);
    }

    public void getPlayerImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/smoki_up_1.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/smoki_up_2.png")));
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/smoki_down_1.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/smoki_down_2.png")));
            left1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/smoki_left_1.png")));
            left2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/smoki_left_2.png")));
            right1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/smoki_right_1.png")));
            right2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/smoki_right_2.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        handleMovement();
        BulletUtil.updateBullets(gp, bullets);

        long currentTime = System.currentTimeMillis();
        if (kh.shootPressed && (currentTime - lastShotTime >= reloadTime)) {
            BulletUtil.shoot(gp, bullets, screenX + muzzleX, screenY + muzzleY, direction);
            if (gp.clientManager != null) {
                gp.clientManager.sendBulletData(
                    gp.clientManager.getPlayerId(),
                    worldX + muzzleX,
                    worldY + muzzleY,
                    direction
                );
            }
            lastShotTime = currentTime;
        }

        collisionOn = false;

        gp.collisionChecker.checkTile(this);
    }

    private void handleMovement() {
        if (kh.upPressed || kh.downPressed || kh.leftPressed || kh.rightPressed) {
            if (kh.upPressed) {
                direction = Constants.UP;
                setStartMuzzlePosition(direction);
                worldY -= speed;
            } else if (kh.downPressed) {
                direction = Constants.DOWN;
                setStartMuzzlePosition(direction);
                worldY += speed;
            } else if (kh.leftPressed) {
                direction = Constants.LEFT;
                setStartMuzzlePosition(direction);
                worldX -= speed;
            } else if (kh.rightPressed) {
                direction = Constants.RIGHT;
                setStartMuzzlePosition(direction);
                worldX += speed;
            }

            spriteCounter++;
            if (spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1; // Переключение спрайта
                spriteCounter = 0;
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = getCurrentImage();
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null); // Отрисовка игрока
        BulletUtil.drawBullet(g2, bullets); // Отрисовка пуль
    }

    private BufferedImage getCurrentImage() {
        switch (direction) {
            case Constants.UP:
                return (spriteNum == 1) ? up1 : up2;
            case Constants.DOWN:
                return (spriteNum == 1) ? down1 : down2;
            case Constants.LEFT:
                return (spriteNum == 1) ? left1 : left2;
            case Constants.RIGHT:
                return (spriteNum == 1) ? right1 : right2;
            default:
                return null; // Default case
        }
    }

    private void setStartMuzzlePosition(String direction) {
        switch (direction) {
            case Constants.UP:
                muzzleX = (gp.tileSize / 2) - 8;
                muzzleY = -(gp.tileSize / 3);
                break;
            case Constants.DOWN:
                muzzleX = (gp.tileSize / 2) - 8;
                muzzleY = gp.tileSize;
                break;
            case Constants.LEFT:
                muzzleX = -(gp.tileSize / 3);
                muzzleY = (gp.tileSize / 2) - 8;
                break;
            case Constants.RIGHT:
                muzzleX = gp.tileSize;
                muzzleY = (gp.tileSize / 2) - 8;
                break;
        }
    }

    public GamePanel getGp() {
        return gp;
    }

    public void setGp(GamePanel gp) {
        this.gp = gp;
    }

    public KeyHandler getKh() {
        return kh;
    }

    public void setKh(KeyHandler kh) {
        this.kh = kh;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public void setBullets(List<Bullet> bullets) {
        this.bullets = bullets;
    }

    public long getReloadTime() {
        return reloadTime;
    }

    public void setReloadTime(long reloadTime) {
        this.reloadTime = reloadTime;
    }

    public long getLastShotTime() {
        return lastShotTime;
    }

    public void setLastShotTime(long lastShotTime) {
        this.lastShotTime = lastShotTime;
    }

    public int getScreenX() {
        return screenX;
    }

    public int getScreenY() {
        return screenY;
    }
}