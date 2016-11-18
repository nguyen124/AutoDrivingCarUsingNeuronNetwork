package com.auto.car.level.tile;

import com.auto.graphics.Screen;
import com.auto.graphics.Sprite;

public class GrassTile extends Tile {

	public GrassTile(Sprite sprite, int size) {
		super(sprite, size);
	}

	public void render(int x, int y, Screen screen) {
		screen.renderTile(x, y, this);
	}
}
