package com.sokoban.controllers;

import java.util.*;

public class SokobanSolver {
    // 四个方向：上、右、下、左
    private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    
    static class State {
        int playerX, playerY;  // 玩家位置
        List<int[]> boxes;     // 箱子位置
        String path;           // 移动路径
        
        State(int px, int py, List<int[]> boxes, String path) {
            this.playerX = px;
            this.playerY = py;
            this.boxes = boxes;
            this.path = path;
        }
        
        // 生成状态的唯一标识
        String getKey() {
            StringBuilder sb = new StringBuilder();
            sb.append(playerX).append(",").append(playerY).append("#");
            boxes.sort((a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);
            for (int[] box : boxes) {
                sb.append(box[0]).append(",").append(box[1]).append("|");
            }
            return sb.toString();
        }
    }
    
    public static String solve(int[][] map) {
        int rows = map.length;
        int cols = map[0].length;
        
        // 找出初始状态
        int playerX = -1, playerY = -1;
        List<int[]> boxes = new ArrayList<>();
        List<int[]> targets = new ArrayList<>();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (map[i][j] == 2) {
                    playerX = i;
                    playerY = j;
                } else if (map[i][j] == 3) {
                    boxes.add(new int[]{i, j});
                } else if (map[i][j] == 4) {
                    targets.add(new int[]{i, j});
                }
            }
        }
        
        if (boxes.size() != targets.size()) {
            return "无解：箱子数量与目标点数量不匹配";
        }
        
        // BFS
        Queue<State> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        State initialState = new State(playerX, playerY, boxes, "");
        queue.offer(initialState);
        visited.add(initialState.getKey());
        
        while (!queue.isEmpty()) {
            State current = queue.poll();
            
            // 检查是否达到目标
            if (isComplete(current.boxes, targets)) {
                return current.path;
            }
            
            // 尝试四个方向
            for (int d = 0; d < 4; d++) {
                int newPlayerX = current.playerX + DIRECTIONS[d][0];
                int newPlayerY = current.playerY + DIRECTIONS[d][1];
                
                // 检查新位置是否有效
                if (!isValid(newPlayerX, newPlayerY, map)) {
                    continue;
                }
                
                // 检查是否遇到箱子
                int[] boxPos = getBoxAt(newPlayerX, newPlayerY, current.boxes);
                List<int[]> newBoxes = new ArrayList<>(current.boxes);
                
                if (boxPos != null) {
                    // 计算箱子新位置
                    int newBoxX = boxPos[0] + DIRECTIONS[d][0];
                    int newBoxY = boxPos[1] + DIRECTIONS[d][1];
                    
                    // 检查箱子新位置是否有效
                    if (!isValid(newBoxX, newBoxY, map) || getBoxAt(newBoxX, newBoxY, newBoxes) != null) {
                        continue;
                    }
                    
                    // 更新箱子位置
                    newBoxes.remove(boxPos);
                    newBoxes.add(new int[]{newBoxX, newBoxY});
                }
                
                // 创建新状态
                State newState = new State(newPlayerX, newPlayerY, newBoxes, 
                    current.path + getDirectionChar(d));
                String newKey = newState.getKey();
                
                if (!visited.contains(newKey)) {
                    visited.add(newKey);
                    queue.offer(newState);
                }
            }
        }
        
        return "无解";
    }
    
    private static boolean isValid(int x, int y, int[][] map) {
        return x >= 0 && x < map.length && y >= 0 && y < map[0].length && 
               map[x][y] != 1 && map[x][y] != -1;
    }
    
    private static int[] getBoxAt(int x, int y, List<int[]> boxes) {
        for (int[] box : boxes) {
            if (box[0] == x && box[1] == y) {
                return box;
            }
        }
        return null;
    }
    
    private static boolean isComplete(List<int[]> boxes, List<int[]> targets) {
        for (int[] target : targets) {
            boolean found = false;
            for (int[] box : boxes) {
                if (box[0] == target[0] && box[1] == target[1]) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }
    
    private static char getDirectionChar(int direction) {
        return "URDL".charAt(direction);
    }
}