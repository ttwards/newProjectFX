package com.sokoban.controllers;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class SokobanSolver {
    private static final int[][] DIRECTIONS = {
            {-1, 0}, // W - 上
            {0, -1}, // A - 左
            {1, 0},  // S - 下
            {0, 1}   // D - 右
    };
    private static final char[] DIR_NAMES = {'A', 'W', 'D', 'S'};
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
    
    private final ExecutorService executor;
    private final ConcurrentHashMap<State, Boolean> globalClosed;
    private final AtomicBoolean solutionFound;
    private final AtomicInteger totalNodes;
    private volatile String bestSolution;
    private volatile int bestSolutionLength;

    public SokobanSolver() {
        this.executor = Executors.newFixedThreadPool(THREAD_COUNT);
        this.globalClosed = new ConcurrentHashMap<>();
        this.solutionFound = new AtomicBoolean(false);
        this.totalNodes = new AtomicInteger(0);
        this.bestSolution = "";
        this.bestSolutionLength = Integer.MAX_VALUE;
    }

    static class State {
        int playerX, playerY;
        Set<String> boxes;
        String path;

        State(int playerX, int playerY, Set<String> boxes, String path) {
            this.playerX = playerX;
            this.playerY = playerY;
            this.boxes = new HashSet<>(boxes);
            this.path = path;
        }

        @Override
        public int hashCode() {
            return Objects.hash(playerX, playerY, boxes.hashCode());
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof State)) return false;
            State other = (State) obj;
            return this.playerX == other.playerX &&
                   this.playerY == other.playerY &&
                   this.boxes.equals(other.boxes);
        }
    }

    public String solve(int[][] grid) {
        long startTime = System.currentTimeMillis();
        
        // 解析初始状态
        Set<String> boxes = new HashSet<>();
        Set<String> goals = new HashSet<>();
        int playerX = -1, playerY = -1;

		// 打印地图
		for (int[] row : grid) {
			for (int cell : row) {
				System.out.print(cell + " ");
			}
			System.out.println();
		}

        // 解析地图
        int rows = grid.length;
        int cols = grid[0].length;
        
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                switch (grid[y][x]) {
                    case 2: playerX = y; playerY = x; break;
                    case 3: boxes.add(y + "," + x); break;
                    case 4: goals.add(y + "," + x); break;
                    case 5: playerX = y; playerY = x; goals.add(y + "," + x); break;
                    case 6: boxes.add(y + "," + x); goals.add(y + "," + x); break;
                }
            }
        }

        if (playerX == -1 || boxes.isEmpty() || goals.isEmpty()) {
            return "";
        }

        // 创建初始状态
        State initialState = new State(playerX, playerY, boxes, "");
        
        // 创建并启动多个搜索线程
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    searchSolution(grid, initialState, goals, threadId);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long endTime = System.currentTimeMillis();
        if (!bestSolution.isEmpty()) {
            System.out.printf("找到解决方案!\n路径: %s\n步数: %d\n耗时: %d ms\n探索节点数: %d\n",
                    bestSolution, bestSolution.length(), endTime - startTime, totalNodes.get());
        } else {
            System.out.printf("未找到解决方案\n耗时: %d ms\n探索节点数: %d\n",
                    endTime - startTime, totalNodes.get());
        }

        return bestSolution;
    }

    private void searchSolution(int[][] grid, State initialState, Set<String> goals, int threadId) {
        PriorityQueue<State> open = new PriorityQueue<>((a, b) -> {
            int fa = a.path.length() + manhattanDistance(a.boxes, goals);
            int fb = b.path.length() + manhattanDistance(b.boxes, goals);
            return Integer.compare(fa, fb);
        });

        open.add(initialState);

        while (!open.isEmpty() && !solutionFound.get()) {
            State current = open.poll();
            totalNodes.incrementAndGet();

            if (isGoalState(current.boxes, goals)) {
                if (current.path.length() < bestSolutionLength) {
                    synchronized (this) {
                        if (current.path.length() < bestSolutionLength) {
                            bestSolution = current.path;
                            bestSolutionLength = current.path.length();
                            solutionFound.set(true);
                        }
                    }
                }
                return;
            }

            if (globalClosed.putIfAbsent(current, Boolean.TRUE) != null) {
                continue;
            }

            // 基于线程ID选择不同的遍历顺序
            for (int i = 0; i < 4; i++) {
                int dir = (i + threadId) % 4;
                exploreDirection(grid, current, dir, goals, open);
            }
        }
    }

    private void exploreDirection(int[][] grid, State current, int dir, Set<String> goals, PriorityQueue<State> open) {
        int newPlayerX = current.playerX + DIRECTIONS[dir][0];
        int newPlayerY = current.playerY + DIRECTIONS[dir][1];

        if (!isValidPosition(newPlayerX, newPlayerY, grid)) {
            return;
        }

        String newPos = newPlayerX + "," + newPlayerY;
        Set<String> newBoxes = new HashSet<>(current.boxes);

        if (newBoxes.contains(newPos)) {
            int newBoxX = newPlayerX + DIRECTIONS[dir][0];
            int newBoxY = newPlayerY + DIRECTIONS[dir][1];

            if (!isValidBoxMove(newBoxX, newBoxY, grid, newBoxes, goals)) {
                return;
            }

            newBoxes.remove(newPos);
            newBoxes.add(newBoxX + "," + newBoxY);

            if (isDeadlock(newBoxX, newBoxY, grid, goals, newBoxes)) {
                return;
            }
        }

        State newState = new State(newPlayerX, newPlayerY, newBoxes,
                current.path + DIR_NAMES[dir]);

        if (!globalClosed.containsKey(newState)) {
            open.add(newState);
        }
    }

    // 保持原有的辅助方法不变
    private boolean isValidPosition(int x, int y, int[][] grid) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length && grid[x][y] != 1;
    }

    private boolean isValidBoxMove(int x, int y, int[][] grid, Set<String> boxes, Set<String> goals) {
        if (!isValidPosition(x, y, grid)) return false;
        String pos = x + "," + y;
        return !boxes.contains(pos);
    }

    private boolean isGoalState(Set<String> boxes, Set<String> goals) {
        return boxes.equals(goals);
    }

    private int manhattanDistance(Set<String> boxes, Set<String> goals) {
        int totalDistance = 0;
        for (String box : boxes) {
            String[] boxCoord = box.split(",");
            int boxX = Integer.parseInt(boxCoord[0]);
            int boxY = Integer.parseInt(boxCoord[1]);

            int minDist = Integer.MAX_VALUE;
            for (String goal : goals) {
                String[] goalCoord = goal.split(",");
                int goalX = Integer.parseInt(goalCoord[0]);
                int goalY = Integer.parseInt(goalCoord[1]);
                int dist = Math.abs(boxX - goalX) + Math.abs(boxY - goalY);
                minDist = Math.min(minDist, dist);
            }
            totalDistance += minDist;
        }
        return totalDistance;
    }

    private boolean isDeadlock(int x, int y, int[][] grid, Set<String> goals, Set<String> boxes) {
        if (goals.contains(x + "," + y)) return false;
        return checkCornerDeadlock(x, y, grid, boxes) || 
               checkEdgeDeadlock(x, y, grid, boxes, goals);
    }

    private boolean checkCornerDeadlock(int x, int y, int[][] grid, Set<String> boxes) {
        boolean[][] corners = {
            {isBlocked(x-1, y, grid, boxes), isBlocked(x, y-1, grid, boxes)},
            {isBlocked(x-1, y, grid, boxes), isBlocked(x, y+1, grid, boxes)},
            {isBlocked(x+1, y, grid, boxes), isBlocked(x, y-1, grid, boxes)},
            {isBlocked(x+1, y, grid, boxes), isBlocked(x, y+1, grid, boxes)}
        };

        for (boolean[] corner : corners) {
            if (corner[0] && corner[1]) return true;
        }
        return false;
    }

    private boolean checkEdgeDeadlock(int x, int y, int[][] grid, Set<String> boxes, Set<String> goals) {
        boolean horizontalBlocked = isBlocked(x, y-1, grid, boxes) && 
                                  isBlocked(x, y+1, grid, boxes);
        boolean verticalBlocked = isBlocked(x-1, y, grid, boxes) && 
                                 isBlocked(x+1, y, grid, boxes);

        if (horizontalBlocked || verticalBlocked) {
            for (String goal : goals) {
                String[] coords = goal.split(",");
                int goalX = Integer.parseInt(coords[0]);
                int goalY = Integer.parseInt(coords[1]);

                if (horizontalBlocked && x == goalX) return false;
                if (verticalBlocked && y == goalY) return false;
            }
            return true;
        }
        return false;
    }

    private boolean isBlocked(int x, int y, int[][] grid, Set<String> boxes) {
        if (!isValidPosition(x, y, grid)) return true;
        return boxes.contains(x + "," + y) || grid[x][y] == 1;
    }
}