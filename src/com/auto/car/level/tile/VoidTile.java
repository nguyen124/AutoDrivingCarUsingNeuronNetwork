package com.auto.car.level.tile;

import com.auto.graphics.Screen;
import com.auto.graphics.Sprite;

/*
 * @Description: VoidTile is render at where there's nothing on screen 
 */
public class VoidTile extends Tile {

	public VoidTile(Sprite sprite, int size) {
		super(sprite, size);
	}

	public void render(int x, int y, Screen screen) {
		screen.renderTile(x, y, this);
	}
}
