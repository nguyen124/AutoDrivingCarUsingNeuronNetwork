package com.auto.car.level;

import com.auto.car.level.tile.Tile;
import com.auto.graphics.Screen;
import com.auto.graphics.Sprite;

/*
 * @Description: Level class represents the map of our program
 */
public class Level {
	protected int width, height;
	protected int[] pixels;

	/*public Level(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
		generateLevel();
	}*/

	public Level(String path) {
		loadLevel(path);
	}

	protected void loadLevel(String path) {

	}

	public void update() {

	}

	/*
	 * @Description: render map on screen
	 * 
	 * @param xScroll: the xOffset of screen
	 * 
	 * @param yScroll: the yOffset of screen
	 */
	public void render(int xScroll, int yScroll, Screen screen) {
		screen.setOffset(xScroll, yScroll);
		// because every single pixel in Level is equal to SIZE of a Sprite
		// so we have to convert the coordinate of screen into coordinate in
		// pixels of Level
		int xMostLeft = xScroll >> Sprite.SIZE_2BASED;
		int xMostRight = (xScroll + screen.getWidth() + Sprite.SIZE) >> Sprite.SIZE_2BASED;
		int yMostTop = yScroll >> Sprite.SIZE_2BASED;
		int yMostBottom = (yScroll + screen.getHeight() + Sprite.SIZE) >> Sprite.SIZE_2BASED;
		for (int y = yMostTop; y < yMostBottom; y++) {
			for (int x = xMostLeft; x < xMostRight; x++) {
				if (x < 0 || y < 0 || x >= width || y >= height) {
					// We have to convert the Level coordinate back to screen
					// coordinate before rendering it on screen
					Tile.voidTile.render(x << Sprite.SIZE_2BASED,
							y << Sprite.SIZE_2BASED, screen);
					continue;
				}
				getTile(x, y).render(x << Sprite.SIZE_2BASED,
						y << Sprite.SIZE_2BASED, screen);
			}
		}
	}

	/*
	 * @Description: each pixels in Level object represents a Tile.
	 * 
	 * @param x: xCoordinate
	 * 
	 * @param y: yCoordinate
	 */
	public Tile getTile(int x, int y) {
		int index = x + y * width;
		if (index >= 0 && index < pixels.length) {
			switch (pixels[index]) {
			case Tile.grassColor:
				return Tile.grass;
			case Tile.brickColor:
				return Tile.brick;
			}
		}
		return Tile.voidTile;
	}

}
