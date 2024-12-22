package com.sokoban.controllers;

import javafx.scene.layout.Pane;

//-1表示空,0表示空地，1表示墙，2表示玩家，3表示箱子，4表示目标
//每个关卡都设置成8*8
public class Level {
	public int stepnum=0;//步数
    private int currentLevelIndex = 0; // 当前关卡索引
    private Pane root; // 游戏场景根节点

	private Backend map;

    public Level(Pane root) {
        this.root = root;
        // this.LEVEL_WIDTH = LEVELS[0][0].length;
        // this.LEVEL_HEIGHT = LEVELS[0].length;
    }

	public void reShader() {
		clearGraph();
		map.reloadLevel(this, root);
		StaticShape[][] staticShapes = Backend.getLevel();
		for (int y = 0; y < staticShapes.length; y++) {
			for (int x = 0; x < staticShapes[y].length; x++) {
				StaticShape shape = staticShapes[x][y];
				if (shape != null) {
					root.getChildren().add(shape.getImageView());
					System.out.println("Picture at x: " + shape.getX() + ", y: " + shape.getY() + " path: " + shape.imagePath);
				}
			}
		}

		// 保证DynamicShape在静态对象之上
		DynamicShape dynamicShapes[] = map.getDynamicShapes();
        for(DynamicShape shape : dynamicShapes) {
            root.getChildren().add(shape.getImageView());
			System.out.println("Adding dynamic shape at " + shape.getX() + ", " + shape.getY());
        }
	}


    public Backend createLevel(int levelIndex) {
        // 清空当前场景
		clearGraph();

		map = new Backend(10, 10, this, root);

		System.out.println("Loading level " + (currentLevelIndex));

		StaticShape[][] staticShapes = Backend.getLevel();
		for (int y = 0; y < staticShapes.length; y++) {
			for (int x = 0; x < staticShapes[y].length; x++) {
				StaticShape shape = staticShapes[x][y];
				if (shape != null) {
					root.getChildren().add(shape.getImageView());
					System.out.println("Picture at x: " + shape.getX() + ", y: " + shape.getY() + " path: " + shape.imagePath);
				}
			}
		}

		// 保证DynamicShape在静态对象之上
		DynamicShape dynamicShapes[] = map.getDynamicShapes();
        for(DynamicShape shape : dynamicShapes) {
            root.getChildren().add(shape.getImageView());
			System.out.println("Adding dynamic shape at " + shape.getX() + ", " + shape.getY());
        }
		return map;
    }

	public void loadLevel(Backend map) {
		// 清空当前场景
		clearGraph();

		map.reMakeImage(this, root);

		this.map = map;

		System.out.println("Loading level " + (currentLevelIndex));

		StaticShape[][] staticShapes = Backend.getLevel();
		for (int y = 0; y < staticShapes.length; y++) {
			for (int x = 0; x < staticShapes[y].length; x++) {
				StaticShape shape = staticShapes[x][y];
				if (shape != null) {
					root.getChildren().add(shape.getImageView());
					System.out.println("Picture at x: " + shape.getX() + ", y: " + shape.getY() + " path: " + shape.imagePath);
				}
			}
		}

		// 保证DynamicShape在静态对象之上
		DynamicShape dynamicShapes[] = map.getDynamicShapes();
        for(DynamicShape shape : dynamicShapes) {
            root.getChildren().add(shape.getImageView());
			System.out.println("Adding dynamic shape at " + shape.getX() + ", " + shape.getY());
        }
	}

    public boolean gameEnd() {
		DynamicShape dynamicShapes[] = map.getDynamicShapes();
        for (DynamicShape shape : dynamicShapes) {
            if (shape.getClass() == Box.class && 
                    Backend.getAt(shape.getX(), shape.getY()).getClass() != Target.class) {
                return false;
            }
        }
        return true;
    }

	private void clearGraph() {
		if(map != null) {
			root.getChildren().clear();
			root.setPickOnBounds(false);
			DynamicShape dynamicShapes[] = map.getDynamicShapes();
			for (DynamicShape shape : dynamicShapes) {
				if(shape.getClass() == Box.class) {
					Box box = (Box)shape;
					box.destroy();
				}
			}
		}
	}

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
}