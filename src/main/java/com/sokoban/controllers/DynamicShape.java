package com.sokoban.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class DynamicShape extends StaticShape {
	protected Level level;
	protected Pane container;

	protected String upImage;
	protected String downImage;
	protected String leftImage;
	protected String rightImage;

	protected int x;
	protected int y;

	public DynamicShape(int x, int y, String imagePath) {
		super(x, y, imagePath);
	}

	private void animatedMove(int deltaX, int deltaY) {
        int newPixelX = this.x + deltaX;
		int newPixelY = this.y + deltaY;
        if (level.isMoveValid(newPixelX, newPixelY)) {
            this.x = newPixelX;
            this.y = newPixelY;

            level.stepnum++;

			// 创建动画
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0.2), // 缩短动画时间使移动更流畅
                            new KeyValue(imageView.layoutXProperty(), newPixelX),
                            new KeyValue(imageView.layoutYProperty(), newPixelY)
                    )
            );
            timeline.play();

        } else {
            System.out.println("Invalid move: (" + newPixelX + ", " + newPixelY + ")");
        }
    }

	public void moveUp() {
        animatedMove(0, -50);
    }

    public void moveDown() {
        animatedMove(0, 50);
    }

    public void moveLeft() {
        animatedMove(-50, 0);
    }

    public void moveRight() {
        animatedMove(50, 0);
    }

    public void moveXY(double x, double y) {
        animatedMove((int)(this.x + x * 50), (int)(this.y + y * 50));
    }
}