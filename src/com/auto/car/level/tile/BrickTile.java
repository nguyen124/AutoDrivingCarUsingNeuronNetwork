package com.auto.car.level.tile;

import com.auto.graphics.Screen;
import com.auto.graphics.Sprite;

public class BrickTile extends Tile {

	public BrickTile(Sprite sprite, int size) {
		super(sprite, size);
	}

	public void render(int x, int y, Screen screen) {
		screen.renderTile(x, y, this);
	}

	/*
	 * BrickTile is solid so we can detect collision
	 */
	public boolean solid() {
		return true;
	}
}
