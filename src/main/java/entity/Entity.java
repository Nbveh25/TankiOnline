package entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {
    protected int worldX, worldY;
    protected int muzzleX, muzzleY;
    protected int speed;

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    protected String direction;

    protected int spriteCounter = 0;
    protected int spriteNum = 1;

    public Rectangle solidArea;
    public boolean collisionOn = false;

    public int getX() {
        return worldX;
    }

    public void setX(int x) {
        this.worldX = x;
    }

    public int getY() {
        return worldY;
    }

    public void setY(int y) {
        this.worldY = y;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getSpriteNum() {
        return spriteNum;
    }

    public void setSpriteNum(int spriteNum) {
        this.spriteNum = spriteNum;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}