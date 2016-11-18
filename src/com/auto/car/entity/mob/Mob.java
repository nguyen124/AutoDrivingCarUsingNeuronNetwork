package com.auto.car.entity.mob;

import com.auto.car.entity.Entity;
import com.auto.graphics.Sprite;

public abstract class Mob extends Entity {
	protected Sprite sprite;
	//protected int direction = 0;
	//protected boolean moving = false;
	protected boolean collided = false;

	public void move(int xPosition, int yPosition) {
		if (xPosition != 0 && yPosition != 0) {
			move(xPosition, 0);
			move(0, yPosition);
			return;
		}
		/*
		 * if (xPosition > 0) direction = 1; if (xPosition < 0) direction = 3;
		 * if (yPosition > 0) direction = 2; if (yPosition < 0) direction = 0;
		 */

		if (!isCollided(xPosition, yPosition)) {
			x += xPosition;
			y += yPosition;
		}
	}

	public void update() {

	}

	private boolean isCollided(int xPosition, int yPosition) {
		for (int corner = 0; corner < 4; corner++) {
			int xt = ((x + xPosition) + (corner % 2) * 7 + 5) >> 4;
			int yt = ((y + yPosition) + (corner / 2) * 12 + 3) >> 4;
			if (level.getTile(xt, yt).solid()) {
				collided = true;
			}
		}
		return collided;
	}

	public void render() {

	}
}
