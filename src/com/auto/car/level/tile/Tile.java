package com.auto.car.level.tile;
import com.auto.graphics.Screen;
import com.auto.graphics.Sprite;

/*
 * @Description: Tile class is the parent class of all other Tiles.
 */
public class Tile {
	public int x, y;
	public Sprite sprite;
	public int size;
	public static Tile grass = new GrassTile(Sprite.grass,16);
	public static Tile brick = new BrickTile(Sprite.brick,16);
	public static Tile voidTile = new VoidTile(Sprite.voidSprite,16);
	public static final int grassColor = 0xff00ff00;
	public static final int brickColor = 0xffFFD800;

	public Tile(Sprite sprite, int size) {
		this.sprite = sprite;
		this.size = size;
	}

	public void render(int x, int y, Screen screen) {

	}

	public boolean solid() {
		return false;
	}
}
