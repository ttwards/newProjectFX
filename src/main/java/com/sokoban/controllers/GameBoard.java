package com.sokoban.controllers;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GameBoard {
    private static final int EMPTY = 0;
    private static final int WALL = 1;
    private static final int PLAYER = 2;
    private static final int BOX = 3;
    private static final int TARGET = 4;
    private static final int BOX_ON_TARGET = 5;
    private static final int PLAYER_ON_TARGET = 6;

    private static final int CELL_SIZE = 50; // 修改为更合适的大小
    private final int width;
    private final int height;
    private int[][] board;
    private int playerX;
    private int playerY;

    // 合并两个构造函数
    public GameBoard(int width, int height) {
        this.width = width;
        this.height = height;
        this.board = new int[width][height];
        initializeBoard();
    }

    // 加载关卡数据的构造函数
    public GameBoard(int[][] level) {
        this.width = level.length;
        this.height = level[0].length;
        this.board = new int[width][height];
        loadLevel(level);
    }

    private void loadLevel(int[][] level) {
        for (int i = 0; i < width; i++) {
            System.arraycopy(level[i], 0, board[i], 0, height);
        }
        // 找到玩家位置
        findPlayer();
    }

    private void findPlayer() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (board[x][y] == PLAYER || board[x][y] == PLAYER_ON_TARGET) {
                    playerX = x;
                    playerY = y;
                    return;
                }
            }
        }
    }

    private void initializeBoard() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                board[x][y] = EMPTY;
            }
        }
    }

    // 在GridPane上绘制游戏板
    public void draw(Stage primaryStage) {
        GridPane gridPane = new GridPane();

        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                ImageView imageView = createImageView(board[row][col]);
                gridPane.add(imageView, col, row);
            }
        }

        Scene scene = new Scene(gridPane, height * CELL_SIZE, width * CELL_SIZE);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sokoban Game");
        primaryStage.show();
    }

    private ImageView createImageView(int type) {
        String imagePath = "";
        switch (type) {
            case WALL:
                imagePath = "/images/wall.png";
                break;
            case BOX:
                imagePath = "/images/box.png";
                break;
            case TARGET:
                imagePath = "/images/target.png";
                break;
            case PLAYER:
                imagePath = "/images/player.png";
                break;
            case BOX_ON_TARGET:
                imagePath = "/images/box_on_target.png";
                break;
            case PLAYER_ON_TARGET:
                imagePath = "/images/player_on_target.png";
                break;
            default:
                imagePath = "/images/empty.png";
                break;
        }

        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(CELL_SIZE);
            imageView.setFitHeight(CELL_SIZE);
            imageView.setPreserveRatio(true);
            return imageView;
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath);
            return new ImageView(); // 返回空的ImageView作为后备
        }
    }

    // Getter 和其他方法
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }

    public boolean canMove(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height &&
                (board[x][y] == EMPTY || board[x][y] == TARGET);
    }

    public boolean isBox(int x, int y) {
        return board[x][y] == BOX || board[x][y] == BOX_ON_TARGET;
    }

    public void movePlayer(int x, int y) {
        int currentTile = board[playerX][playerY];
        board[playerX][playerY] = (currentTile == PLAYER_ON_TARGET) ? TARGET : EMPTY;

        int newTile = board[x][y];
        board[x][y] = (newTile == TARGET) ? PLAYER_ON_TARGET : PLAYER;

        playerX = x;
        playerY = y;
    }

    public void moveBox(int fromX, int fromY, int toX, int toY) {
        if (board[toX][toY] == TARGET) {
            board[fromX][fromY] = EMPTY;
            board[toX][toY] = BOX_ON_TARGET;
        } else {
            board[fromX][fromY] = EMPTY;
            board[toX][toY] = BOX;
        }
    }

    public boolean isComplete() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (board[x][y] == TARGET) {
                    return false;
                }
            }
        }
        return true;
    }

    public void reset() {
        initializeBoard();
    }
}