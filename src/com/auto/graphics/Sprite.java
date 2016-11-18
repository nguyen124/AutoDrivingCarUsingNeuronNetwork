package com.auto.graphics;

/*
 * @Description: This class represents the individual Sprite which is got from
 * the SpriteSheet. The SIZE of Sprite is fixed which is 16 in this program
 */
public class Sprite {
	// SIZE in Decimal
	public static final int SIZE = 16;
	// SIZE in Binary
	public static final int SIZE_2BASED = 4;// 2^4
	/*
	 * The coordinate of sprite in the SpriteSheet. Every unit of x and y is
	 * equal to 16 pixels in SpriteSheet
	 */
	private int x, y;
	private int[] pixels;
	private SpriteSheet sheet;

	/*
	 * Preloaded Sprites
	 */
	public static Sprite grass = new Sprite(0, 0, SpriteSheet.tiles16x16);
	public static Sprite brick = new Sprite(2, 0, SpriteSheet.tiles16x16);
	public static Sprite voidSprite = new Sprite(0xE6FFA3);
	public static Sprite carSprite = new Sprite(4, 0, SpriteSheet.tiles16x16);

	/*
	 * Sprite Constructor
	 */
	public Sprite(int x, int y, SpriteSheet sheet) {
		pixels = new int[SIZE * SIZE];
		this.x = x * SIZE;
		this.y = y * SIZE;
		this.sheet = sheet;
		load();
	}

	public Sprite(int colour) {
		pixels = new int[SIZE * SIZE];
		setColour(colour);
	}

	private void setColour(int colour) {
		for (int i = 0; i < SIZE * SIZE; i++) {
			pixels[i] = colour;
		}

	}

	/*
	 * This method get data from the SpriteSheet and load it into the Sprite
	 */
	private void load() {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				pixels[j + i * SIZE] = sheet.pixels[(j + this.x) + (i + this.y)
						* sheet.getSize()];
			}
		}
	}

	public int[] getPixels() {
		// TODO Auto-generated method stub
		return pixels;
	}

}
