package entity;

import game.GamePanel;
import util.Constants;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Bullet extends Entity {
    public int x, y;
    public int speed;
    public String direction;
    public BufferedImage image;

    private GamePanel gp;

    public Bullet(GamePanel gp, int startX, int startY, String direction) {
        this.gp = gp;
        this.x = startX;
        this.y = startY;
        this.direction = direction;
        this.speed = 5;
        loadImage();
    }

    private void loadImage() {
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/bullet/bullet.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        switch (direction) {
            case Constants.UP:
                y -= speed;
                break;
            case Constants.DOWN:
                y += speed;
                break;
            case Constants.LEFT:
                x -= speed;
                break;
            case Constants.RIGHT:
                x += speed;
                break;
        }
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, (gp.tileSize / 3), (gp.tileSize / 3), null);
    }
}