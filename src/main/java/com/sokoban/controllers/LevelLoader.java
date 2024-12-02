package com.sokoban.controllers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {
    private static final int MAP_SIZE = 8;  // 8x8 地图

    // 读取地图文件并返回二维数组
    public static int[][] loadLevel(String levelFile) {
        int[][] map = new int[MAP_SIZE][MAP_SIZE];

        try {
            // 从resources目录读取文件
            InputStream is = LevelLoader.class.getResourceAsStream("/levels/" + levelFile);
            if (is == null) {
                throw new RuntimeException("Cannot find level file: " + levelFile);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            List<String> lines = new ArrayList<>();
            String line;

            // 读取所有行
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }

            // 转换为地图数据
            for (int i = 0; i < MAP_SIZE; i++) {
                String rowData = lines.get(i);
                for (int j = 0; j < MAP_SIZE; j++) {
                    // 将字符转换为对应的数字
                    map[i][j] = Character.getNumericValue(rowData.charAt(j));
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading level: " + e.getMessage());
            e.printStackTrace();
        }

        return map;
    }
}