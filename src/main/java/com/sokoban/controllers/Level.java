package com.sokoban.controllers;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.Arrays;

//-1表示空,0表示空地，1表示墙，2表示玩家，3表示箱子，4表示目标
//每个关卡都设置成8*8
public class Level {
    public static final int NULL=-1;
    public static final int EMPTY = 0;
    public static final int WALL = 1;
    public static final int PLAYER = 2;
    public static final int BOX = 3;
    public static final int TARGET = 4;


    public int stepnum=0;//步数
    private int currentLevelIndex = 0; // 当前关卡索引
    private Pane root; // 游戏场景根节点
    private Player player; // 玩家对象
    private ArrayList<Box> boxList = new ArrayList<>();

    private int LEVEL_WIDTH;
    private int LEVEL_HEIGHT;

    // 定义五个关卡
    private static int[][][] LEVELS = {
            {   // 关卡 1
				{1, 1, 1, 1, 1, 1, 1, 1},
				{1, 0, 4, 0, 3, 0, 0, 1},
				{1, 3, 0, 0, 4, 1, 0, 1},
				{1, 0, 0, 1, 0, 0, 0, 1},
				{1, 0, 0, 1, 2, 3, 0, 1},
				{1, 0, 0, 1, 0, 0, 0, 1},
				{1, 0, 4, 0, 0, 0, 0, 1},
				{1, 1, 1, 1, 1, 1, 1, 1},
            },
            {   // 关卡 2
                    {-1,-1,-1,-1,-1,-1,-1,-1},
                    {-1,1,1,1,1,1,1,0},
                    {-1,1,2,0,0,0,1,1},
                    {-1,1,0,3,3,0,0,1},
                    {-1,1,0,1,4,0,4,1},
                    {-1,1,0,0,0,0,0,1},
                    {-1,1,1,1,1,1,1,1},
                    {-1,-1,-1,-1,-1,-1,-1,-1}
            },
            {   // 关卡 3
                    {-1,-1,-1,-1,-1,-1,-1,-1},
                    {-1,-1,-1,1,1,1,1,-1},
                    {-1,1,1,1,0,0,1,-1},
                    {-1,1,2,0,4,3,1,1},
                    {-1,1,0,0,0,3,0,1},
                    {-1,1,0,1,4,0,0,1},
                    {-1,1,0,0,0,0,0,1},
                    {-1,1,1,1,1,1,1,1}
            },
            {   // 关卡 4
                    {-1,-1,-1,-1,-1,-1,-1,-1},
                    {-1,-1,1,1,1,1,1,-1},
                    {-1,1,1,2,0,0,1,1},
                    {-1,1,0,0,1,0,0,1},
                    {-1,1,0,3,5,3,0,1},
                    {-1,1,0,0,4,0,0,1},
                    {-1,1,1,0,4,0,1,1},
                    {-1,-1,1,1,1,1,1,-1}
            },
            {   // 关卡 5
                    {-1,-1,-1,-1,-1,-1,-1,-1},
                    {1,1,1,1,1,1,-1,-1},
                    {1,0,0,0,0,1,1,1},
                    {1,0,0,0,4,4,0,1},
                    {1,0,3,3,3,2,0,1},
                    {1,0,0,1,0,4,0,1},
                    {1,1,1,1,1,1,1,1},
                    {-1,-1,-1,-1,-1,-1,-1,-1}
            }
    };

    private static Box[][] BOXES = new Box[8][8];

    public Level(Pane root) {
        this.root = root;
        this.LEVEL_WIDTH = LEVELS[0][0].length;
        this.LEVEL_HEIGHT = LEVELS[0].length;
    }

    public void loadLevel(int levelIndex) {
        if (levelIndex < 0 || levelIndex >= LEVELS.length) {
            throw new IllegalArgumentException("Invalid level index: " + levelIndex);
        }
        currentLevelIndex = levelIndex;
        createLevel();
    }

    private void createLevel() {
        // 清空当前场景
        root.getChildren().clear();
        for (Box box : boxList) {
            box.destroy();
        }
        boxList.clear();
        for (Box[] boxes : BOXES) {
            Arrays.fill(boxes, null);
        }
        root.setPickOnBounds(false);

		MapGenerator mapGenerator = new MapGenerator();
		mapGenerator.mapMake();

		LEVELS[0] = mapGenerator.getMap();
		
        // 获取当前关卡的数据
        int[][] levelData = LEVELS[currentLevelIndex];

        for (int y = 0; y < levelData.length; y++) {
            for (int x = 0; x < levelData[y].length; x++) {
                int cellType = levelData[y][x];
                switch (cellType) {
                    case EMPTY, NULL:
                        root.getChildren().add(new Empty(x * 50.0, y * 50.0).getImageView());
                        break;
                    case WALL:
                        root.getChildren().add(new Wall(x * 50.0, y * 50.0).getImageView());
                        break;
                    case PLAYER:
                        root.getChildren().add(new Empty(x * 50.0, y * 50.0).getImageView());
                        player = new Player(x, y, this, root);
                        break;
                    case BOX:
						root.getChildren().add(new Empty(x * 50.0, y * 50.0).getImageView());
                        boxList.add(new Box(x, y, this, root));
                        break;
                    case TARGET:
                        root.getChildren().add(new Target(x, y).getImageView());
                        break;
                    default:
                        // 忽略其他字符
                        break;
                }
            }
        }
        System.out.println("Children of root pane:");
        root.getChildren().forEach(node -> {
            if (node instanceof ImageView imageView) {
                System.out.println("Found ImageView with image: " + imageView.getImage());
            } else {
                System.out.println("Found node: " + node);
            }
        });
		root.getChildren().add(player.getImageView());
        for(Box box : boxList) {
            root.getChildren().add(box.getImageView());
            BOXES[(int)(box.getX())][(int)box.getY()] = box;
        }
    }

    public boolean isMoveValid(double x, double y, double deltaX, double deltaY) {
		double newX = x + deltaX;
		double newY = y + deltaY;

        // 检查新位置是否在关卡范围内
        if (newX < 0 || x >= LEVEL_WIDTH || newY < 0 || newY >= LEVEL_HEIGHT) {
            System.out.printf("超出范围: %d, %d\n", (int)x, (int)y);
            return false; // 超出范围
        }

        if (BOXES[(int)(newX)][(int)(newY)] != null) {
            System.out.printf("Met Box at %d %d, move failed\n", (int)newX, (int)newY);
            return false;
        }
		if (getCellType((int)newX, (int)newY) == WALL) {
			System.out.println("Met Wall, move failed");
			return false;
		}

        return true;
    }

    public boolean gameEnd() {
        for (Box box : boxList) {
            if (LEVELS[currentLevelIndex][(int)box.getY()][(int)box.getX()] != TARGET) {
				System.out.println("Box not in target: " + box.getX() + ", " + box.getY());
                return false;
            }
        }
        return true;
    }

    private int getCellType(int x, int y) {
        // 返回指定坐标的单元格类型
        return LEVELS[currentLevelIndex][y][x];
    }

    public Player getPlayer() {
        return player;
    }

    public Box getBox(int x, int y) { return BOXES[x][y]; }

	public boolean moveBox(double x, double y, double deltaX, double deltaY) {
		if(x > LEVELS[currentLevelIndex][0].length || y > LEVELS[currentLevelIndex].length) {
			System.out.println("Invalid box position: " + x + ", " + y);
			return false;
		}
		if(BOXES[(int)x][(int)y] == null) {
			System.out.println("No box at: " + x + ", " + y);
			return true;
		}
		System.out.println("Moving box at: " + x + ", " + y + " by: " + deltaX + ", " + deltaY);
		if(!isMoveValid(x, y, deltaX, deltaY)) {
			return false;
		}
		int newX = (int)(x + deltaX);
		int newY = (int)(y + deltaY);
		System.out.println("New box position: " + newX + ", " + newY);
		BOXES[(int)x][(int)y].moveXY(deltaX, deltaY);
        BOXES[newX][newY] = BOXES[(int)x][(int)y];
        BOXES[(int)x][(int)y] = null;
		System.out.println("Box moved successfully");
		return true;
	}

    public double getPlayerX() { return player.getX(); }
    public double getPlayerY() { return player.getY(); }
}