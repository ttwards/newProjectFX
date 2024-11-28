package com.sokoban.controllers;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import static com.sokoban.controllers.GamePiece.CELL_SIZE;
import static java.lang.Long.SIZE;

public class GameBoard {
        private static final int EMPTY = 0;
        private static final int WALL = 1;
        private static final int PLAYER = 2;
        private static final int BOX = 3;
        private static final int TARGET = 4;
        private static final int BOX_ON_TARGET = 5;
        private static final int PLAYER_ON_TARGET = 6;

        private static final int CELLSIZE = 10;
        private int width;
        private int height;
        int[][] board;
        int playerX;
        int playerY;
        //构造棋盘
        public GameBoard(int width, int height) {
            this.width = width;
            this.height = height;
            this.board = new int[width][height];
            initializeBoard();
        }

        private void initializeBoard() {
            // 初始化游戏板
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    board[x][y] = EMPTY;
                }
            }

            // 设置墙、箱子、目标等
            // 这里可以加载预定义的关卡布局
            // 例如：设置一些墙、箱子、目标位置
            // board[0][0] = WALL;
            // board[1][1] = BOX;
            // board[2][2] = TARGET;
            // playerX = 3;
            // playerY = 3;
            // board[playerX][playerY] = PLAYER;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public Cell getCell(int x, int y) {
            return new Cell(board[x][y]);
        }

        public int getPlayerX() {
            return playerX;
        }

        public int getPlayerY() {
            return playerY;
        }

        public boolean canMove(int x, int y) {
            return x >= 0 && x < width && y >= 0 && y < height && (board[x][y] == EMPTY || board[x][y] == TARGET);
        }

        public boolean isBox(int x, int y) {
            return board[x][y] == BOX || board[x][y] == BOX_ON_TARGET;
        }

        public boolean canPush(int x, int y) {
            return x >= 0 && x < width && y >= 0 && y < height && (board[x][y] == EMPTY || board[x][y] == TARGET);
        }

        public void movePlayer(int x, int y) {
            board[playerX][playerY] = EMPTY;
            playerX = x;
            playerY = y;
            board[playerX][playerY] = PLAYER;
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
    public void draw(Stage primaryStage) {
        GridPane gridPane = new GridPane();

        for (int row = 0; row < MAP.length; row++) {
            for (int col = 0; col < MAP[row].length; col++) {
                ImageView imageView = createImageView(MAP[row][col]);
                gridPane.add(imageView, col, row);
            }
        }

        Scene scene = new Scene(gridPane, MAP[0].length * GameBoard.CELLSIZE, MAP.length * GameBoard.CELLSIZE);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sokoban Game with Images");
        primaryStage.show();
    }

private ImageView createImageView(String type) {
    String imagePath = "";
    switch (type) {
        case "1":
            imagePath = "images/wall.png";
            break;
        case "3":
            imagePath = "images/box.png";
            break;
        case "4":
            imagePath = "images/target.png";
            break;
        case "2":
            imagePath = "images/player.png";
            break;
        case "5":
            imagePath = "images/box_on_target.png";
        case "6":
            imagePath="images/player_on_target.png";
        default:
            imagePath = "images/empty.png"; // 空格可以使用一个透明的图片或者空白图片
            break;
    }

    Image image = new Image(getClass().getResourceAsStream(imagePath));
    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(GameBoard.CELLSIZE);
    imageView.setFitHeight(GameBoard.CELLSIZE);
    imageView.setPreserveRatio(true); // 保持图像比例
    return imageView;
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