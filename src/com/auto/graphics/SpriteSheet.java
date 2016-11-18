package com.auto.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/*
 * @Description: This class is used to load the SpriteSheet which contains
 * Sprites used in program
 */
public class SpriteSheet {
	private String path;
	private final int size;
	public int[] pixels;
	public static SpriteSheet tiles16x16 = new SpriteSheet("/tiles/SpriteSheet16x16.png",
			256);
	public SpriteSheet(String path, int size) {
		this.path = path;
		this.size = size;
		pixels = new int[size * size];
		load();
	}

	/*
	 * @Description: load image to the pixels of SpriteSheet
	 */
	private void load() {
		try {
			BufferedImage image = ImageIO.read(SpriteSheet.class
					.getResource(path));
			int w = image.getWidth();
			int h = image.getHeight();
			image.getRGB(0, 0, w, h, pixels, 0, w);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getSize() {
		return size;
	}
}
