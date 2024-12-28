package entity;

import java.awt.image.BufferedImage;

public class Entity {
    protected int x, y;
    protected int muzzleX, muzzleY;
    protected int speed;

    protected BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    protected String direction;

    protected int spriteCounter = 0;
    protected int spriteNum = 1;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}