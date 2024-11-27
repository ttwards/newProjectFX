package com.sokoban.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class GameBoard {
    static int SIZE = 6;
    GamePiece[][] board = new GamePiece[SIZE][SIZE];

    // 初始化游戏板
    public GameBoard(GamePiece[][] level) {
        loadLevel(level);
    }

    // 加载关卡数据
    private void loadLevel(GamePiece[][] level) {
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(level[i], 0, board[i], 0, SIZE);
        }
    }

    // 在GridPane上绘制游戏板
    public void drawBoard(GridPane gridPane) {
        gridPane.getChildren().clear();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] != null) {
                    ImageView imageView = board[row][col].getImageView();
                    gridPane.add(imageView, col, row);
                }
            }
        }
    }

    // 获取特定位置的棋子
    public GamePiece getPiece(int row, int col) {
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
            return board[row][col];
        }
        return null;
    }

    // 设置特定位置的棋子
    public void setPiece(int row, int col, GamePiece piece) {
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
            board[row][col] = piece;
        }
    }
}