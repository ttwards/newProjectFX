package com.sokoban.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.util.Duration;


public class DynamicShape extends StaticShape {
	protected Level level;
	protected Pane container;

	protected String upImage;
	protected String downImage;
	protected String leftImage;
	protected String rightImage;

	public DynamicShape(double x, double y, String imagePath) {
		super(x, y, imagePath);
	}

	private void animatedMove(int deltaX, int deltaY) {
        int newPixelX = this.x + deltaX;
		int newPixelY = this.y + deltaY;
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
    }

	public void directMove(double deltaX, double deltaY) {
        int newPixelX = this.x + (int) deltaX * 50;
		int newPixelY = this.y + (int) deltaY * 50;
		this.x = newPixelX;
		this.y = newPixelY;

		level.stepnum++;

		// 创建动画
		Timeline timeline = new Timeline(
				new KeyFrame(Duration.seconds(0.04), // 缩短动画时间使移动更流畅
						new KeyValue(imageView.layoutXProperty(), newPixelX),
						new KeyValue(imageView.layoutYProperty(), newPixelY)
				)
		);
		timeline.play();
    }

	public void moveUp() {
        animatedMove(0, -50);
    }

    public void moveDown() {
        animatedMove(0, 50);
    }

    public void moveLeft() {
		this.imageView.setImage(new Image(getClass().getResourceAsStream(leftImage)));
        animatedMove(-50, 0);
    }

    public void moveRight() {
        animatedMove(50, 0);
    }

    public void moveXY(double x, double y) {
        animatedMove((int) (x * 50), (int) (y * 50));
    }
}
