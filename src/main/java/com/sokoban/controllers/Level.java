package com.sokoban.controllers;



import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.io.InputStream;
import java.util.ArrayList;

//-1表示空,0表示空地，1表示墙，2表示玩家，3表示箱子，4表示目标
//每个关卡都设置成8*8
public class Level {
    public static final int NULL=-1;
    public static final int EMPTY = 0;
    public static final int WALL = 1;
    public static final int PLAYER = 2;
    public static final int BOX = 3;
    public static final int TARGET = 4;


    private int currentLevelIndex = 0; // 当前关卡索引
    private Pane root; // 游戏场景根节点
    private Player player; // 玩家对象
    private ArrayList<Box> boxList = new ArrayList<>();

    private int LEVEL_WIDTH;
    private int LEVEL_HEIGHT;

    // 定义五个关卡
    private static final int[][][] LEVELS = {
            {   // 关卡 1
                    {-1,-1,-1,-1,-1,-1,-1,-1},
                    {-1,1,1,1,1,1,1,-1},
                    {-1,1,2,0,0,0,1,-1},
                    {-1,1,0,0,3,4,1,-1},
                    {-1,1,0,4,3,0,1,-1},
                    {-1,1,1,1,1,1,1,-1},
                    {-1,-1,-1,-1,-1,-1,-1,-1},
                    {-1,-1,-1,-1,-1,-1,-1,-1}
            },
            {   // 关卡 2
                    {1, 1, 1, 1, 1},
                    {1, 0, 0, 0, 1},
                    {1, 0, 3, 0, 1},
                    {1, 0, 4, 0, 1},
                    {1, 2, 0, 0, 1},
                    {1, 1, 1, 1, 1}
            },
            {   // 关卡 3
                    {1, 1, 1, 1, 1, 1},
                    {1, 0, 0, 0, 0, 1},
                    {1, 0, 3, 0, 0, 1},
                    {1, 0, 4, 0, 2, 1},
                    {1, 0, 0, 4, 0, 1},
                    {1, 1, 1, 1, 1, 1}
            },
            {   // 关卡 4
                    {1, 1, 1, 1, 1, 1, 1},
                    {1, 0, 0, 0, 0, 0, 1},
                    {1, 3, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 2, 1},
                    {1, 0, 0, 0, 0, 4, 1},
                    {1, 1, 1, 1, 1, 1, 1}
            },
            {   // 关卡 5
                    {1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 3, 4, 0, 0, 0, 1},
                    {1, 0, 0, 0, 3, 4, 0, 1},
                    {1, 0, 0, 0, 0, 0, 2, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1}
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
        root.setPickOnBounds(false);
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
                        player = new Player(x * 50.0, y * 50.0,this, root);
                        break;
                    case BOX:
                        boxList.add(new Box(x * 50.0, y * 50.0));
                        root.getChildren().add(new Empty(x * 50.0, y * 50.0).getImageView());
                        break;
                    case TARGET:
                        root.getChildren().add(new Target(x * 50.0, y * 50.0).getImageView());
                        break;
                    default:
                        // 忽略其他字符
                        break;
                }
            }
        }
        root.getChildren().add(player.getImageView());
        for(Box box : boxList) {
            root.getChildren().add(box.getImageView());
            BOXES[(int) (box.getX() / 50)][(int) box.getY() / 50] = box;
        }
    }

    public boolean isMoveValid(double newX, double newY) {
        // 将新坐标转换为网格坐标
        int x = (int) (newX / 50);
        int y = (int) (newY / 50);

        // 检查新位置是否在关卡范围内
        if (x < 0 || x >= LEVEL_WIDTH || y < 0 || y >= LEVEL_HEIGHT) {
            System.out.println("超出范围");
            return false; // 超出范围
        }

        // 获取新位置的单元格类型
        int cellType = getCellType(x, y);

        // 判断新位置是否是墙壁或其他不可移动的物体
        return cellType != WALL; // 如果不是墙壁，则移动有效
    }

    private int getCellType(int x, int y) {
        // 返回指定坐标的单元格类型
        return LEVELS[currentLevelIndex][y][x];
    }

    public Player getPlayer() {
        return player;
    }

    public Box getBox(int x, int y) { return BOXES[x][y]; }

    public int getPlayerX() { return (int) (player.getX() / 50); }
    public int getPlayerY() { return (int) (player.getY() / 50); }
}