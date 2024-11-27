package com.sokoban.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class GameController {
    @FXML private GridPane gameBoard;
    private int playerX = 0;
    private int playerY = 0;
    private int[][] level;

    @FXML
    public void initialize() {
        gameBoard.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                // 这里可以安全地获取Stage和数据
                Stage stage = (Stage) gameBoard.getScene().getWindow();
                int levelData = (int)stage.getUserData();
                loadLevel(levelData);
                drawLevel();
            }
        });
    }

    @FXML
    public void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
                movePlayer(0, -1);
                break;
            case DOWN:
                movePlayer(0, 1);
                break;
            case LEFT:
                movePlayer(-1, 0);
                break;
            case RIGHT:
                movePlayer(1, 0);
                break;
        }
    }

    private void loadLevel(int num) {
        // 加载关卡数据
        level = new int[][] {
                {1, 1, 1, 1, 1},
                {1, 0, 0, 0, 1},
                {1, 0, 2, 0, 1},
                {1, 0, 0, 0, 1},
                {1, 1, 1, 1, 1}
        };
    }

    private void movePlayer(int dx, int dy) {
        int newX = playerX + dx;
        int newY = playerY + dy;

        if (isValidMove(newX, newY)) {
            playerX = newX;
            playerY = newY;
            updateGameBoard();
        }
    }

    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < level[0].length && y >= 0 && y < level.length && level[y][x] != 1;
    }

    private void updateGameBoard() {
        // 更新游戏界面
        drawLevel();
    }

    private void drawLevel() {
        // 绘制游戏界面
        gameBoard.getChildren().clear();
        // 实现绘制逻辑
    }


}