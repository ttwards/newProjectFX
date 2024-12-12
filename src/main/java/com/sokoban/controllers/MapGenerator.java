package com.sokoban.controllers;

import java.util.*;

public class MapGenerator {
    private static final int H = 8;
    private static final int L = 8;
    private static final int EMPTY = 0;
    private static final int WALL = 1;
    private static final int PLAYER = 2;
    private static final int BOX = 3;
    private static final int TARGET = 4;
    
    private int[][] map = new int[H][L];
    private Random rand = new Random();
    
    public void mapMake() {
        // 1. 初始化地图，设置边界墙
        for (int i = 0; i < H; i++) {
            for (int j = 0; j < L; j++) {
                if (i == 0 || i == H-1 || j == 0 || j == L-1) {
                    map[i][j] = WALL;
                } else {
                    map[i][j] = EMPTY;
                }
            }
        }
        
        // 2. 随机生成一些内部墙
        for (int i = 2; i < H-2; i++) {
            for (int j = 2; j < L-2; j++) {
                if (rand.nextInt(100) < 20) { // 20%的概率生成墙
                    map[i][j] = WALL;
                }
            }
        }
        
        // 3. 放置玩家（在中心区域）
        int playerX = H/2;
        int playerY = L/2;
        while (map[playerX][playerY] == WALL) {
            playerX = rand.nextInt(H/2) + H/4;
            playerY = rand.nextInt(L/2) + L/4;
        }
        map[playerX][playerY] = PLAYER;
        
        // 4. 放置箱子和目标点
        int boxCount = 3; // 固定3个箱子和目标点
        List<int[]> emptySpaces = getEmptySpaces();
        
        // 放置箱子
        for (int i = 0; i < boxCount; i++) {
            if (emptySpaces.isEmpty()) break;
            int index = rand.nextInt(emptySpaces.size());
            int[] pos = emptySpaces.get(index);
            if (isValidBoxPosition(pos[0], pos[1])) {
                map[pos[0]][pos[1]] = BOX;
                emptySpaces.remove(index);
            } else {
                i--; // 重试
                continue;
            }
        }
        
        // 放置目标点
        emptySpaces = getEmptySpaces(); // 重新获取空位置
        for (int i = 0; i < boxCount; i++) {
            if (emptySpaces.isEmpty()) break;
            int index = rand.nextInt(emptySpaces.size());
            int[] pos = emptySpaces.get(index);
            map[pos[0]][pos[1]] = TARGET;
            emptySpaces.remove(index);
        }
    }
    
    private List<int[]> getEmptySpaces() {
        List<int[]> spaces = new ArrayList<>();
        for (int i = 1; i < H-1; i++) {
            for (int j = 1; j < L-1; j++) {
                if (map[i][j] == EMPTY) {
                    spaces.add(new int[]{i, j});
                }
            }
        }
        return spaces;
    }
    
    private boolean isValidBoxPosition(int x, int y) {
        // 检查箱子位置的合法性：不能在角落或者只有一个推动方向
        int wallCount = 0;
        if (map[x-1][y] == WALL) wallCount++;
        if (map[x+1][y] == WALL) wallCount++;
        if (map[x][y-1] == WALL) wallCount++;
        if (map[x][y+1] == WALL) wallCount++;
        
        return wallCount < 1; // 最多只能有一面墙相邻
    }
    
    public void printMap() {
        for (int i = 0; i < H; i++) {
            System.out.print("{");
            for (int j = 0; j < L; j++) {
                System.out.print(map[i][j]);
                if (j < L-1) {
                    System.out.print(", ");
                }
            }
            System.out.println("},");
        }
    }

	public int[][] getMap() {
		return map;
	}
}