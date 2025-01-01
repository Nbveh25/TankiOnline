package util;

import entity.Entity;
import game.GamePanel;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        int entityLeftWorldX = entity.getX() + entity.solidArea.x;
        int entityRightWorldX = entity.getX() + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.getY() + entity.solidArea.y;
        int entityBottomWorldY = entity.getY() + entity.solidArea.y + entity.solidArea.height;

        // Вычисляем следующую позицию в зависимости от направления
        switch (entity.getDirection()) {
            case Constants.UP -> entityTopWorldY -= entity.getSpeed();
            case Constants.DOWN -> entityBottomWorldY += entity.getSpeed();
            case Constants.LEFT -> entityLeftWorldX -= entity.getSpeed();
            case Constants.RIGHT -> entityRightWorldX += entity.getSpeed();
        }

        // Вычисляем колонки и строки для проверки
        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        // Проверяем границы карты
        if (entityLeftCol < 0 || entityRightCol >= gp.maxWorldCol || 
            entityTopRow < 0 || entityBottomRow >= gp.maxWorldRow) {
            entity.collisionOn = true;
            return;
        }

        // Проверяем коллизии с тайлами
        int tileNum1 = -1, tileNum2 = -1;

        switch (entity.getDirection()) {
            case Constants.UP -> {
                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityTopRow];
            }
            case Constants.DOWN -> {
                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityBottomRow];
            }
            case Constants.LEFT -> {
                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
            }
            case Constants.RIGHT -> {
                tileNum1 = gp.tileManager.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityBottomRow];
            }
        }

        if (tileNum1 >= 0 && tileNum2 >= 0) {
            entity.collisionOn = gp.tileManager.tile[tileNum1].collision || 
                                gp.tileManager.tile[tileNum2].collision;
        }
    }
}
