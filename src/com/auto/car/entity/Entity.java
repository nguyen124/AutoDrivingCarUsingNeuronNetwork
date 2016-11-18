package com.auto.car.entity;

import com.auto.car.level.Level;
import com.auto.graphics.Screen;

public abstract class Entity {
	protected int x, y;
	/*protected boolean removed = false;*/
	protected Level level;

	public void update() {

	}

	public void render(Screen screen) {

	}

	/*public void remove() {
		this.removed = true;
	}

	public boolean isRemoved() {
		return removed;
	}*/

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
}
