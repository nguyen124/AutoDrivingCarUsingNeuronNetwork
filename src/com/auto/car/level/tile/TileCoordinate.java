package com.auto.car.level.tile;

import com.auto.graphics.Sprite;

/*
 * @Description: This class convert the TileCoordinate to screen coordinate
 */
public class TileCoordinate {

	private int x, y;
	private final int TILE_SIZE = Sprite.SIZE;

	public TileCoordinate(int x, int y) {
		//Every unit in Tile Coordinate is equal to TILE_SIZE pixels
		this.setX(x * TILE_SIZE);
		this.setY(y * TILE_SIZE);
	}

	public TileCoordinate() {

	}

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

	public int[] xy() {
		int[] coordinates = new int[2];
		coordinates[0] = x;
		coordinates[1] = y;
		return coordinates;
	}
}
