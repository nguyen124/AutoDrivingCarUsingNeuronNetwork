package com.auto.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.auto.car.CarDemo;
import com.auto.car.level.tile.Tile;

/*
 * @Description: This class takes care of all graphic rendering on the screen.   
 */
public class Screen {
	private int width;
	private int height;
	private int[] pixels;
	private int xOffset, yOffset;
	// public static final int TILES = 64;
	// public static final int TILES_MASK = TILES - 1;
	// public int[] tiles = new int[TILES * TILES];
	private Graphics2D graphics;
	private int scale = CarDemo.scale;
	
	public Screen() {

	}

	public Screen(int w, int h) {
		width = w;
		height = h;
		pixels = new int[w * h];
	}

	public int[] getPixels() {
		return pixels;
	}

	/*
	 * @Description: This function renders single Tile on screen
	 * 
	 * @param xPosition x coordinate of Tile on screen.
	 * 
	 * @param yPosition y coordinate of Tile on screen.
	 * 
	 * @param tile the tile to be rendered.
	 */
	public void renderTile(int xPosition, int yPosition, Tile tile) {
		// substract the tile position by the offset of screen when screen move
		xPosition -= xOffset;
		yPosition -= yOffset;
		for (int yTile = 0; yTile < tile.size; yTile++) {
			int yScreen = yTile + yPosition;
			for (int xTile = 0; xTile < tile.size; xTile++) {
				int xScreen = xTile + xPosition;
				// if the xScreen and yScreen are out of boundary then no
				// rendering. -tile.size is to render the boundary of the map
				// without rendering black strike.
				if (xScreen < -tile.size || xScreen >= width || yScreen < 0
						|| yScreen >= height) {
					break;
				}
				if (xScreen < 0) {
					xScreen = 0;
				}
				pixels[xScreen + yScreen * width] = tile.sprite.getPixels()[xTile
						+ yTile * tile.size];
			}
		}
	}

	/*
	 * @Description: This function renders single Sprite on screen
	 * 
	 * @param xPosition x coordinate of Sprite on screen.
	 * 
	 * @param yPosition y coordinate of Sprite on screen.
	 * 
	 * @param angle The angle of the car.
	 * 
	 * @param sprite the sprite to be rendered.
	 */
	public void renderCar(int xPosition, int yPosition, double angle,
			Sprite sprite) {
		// substract the Tile position by the offset of screen when screen moves
		xPosition -= xOffset;
		yPosition -= yOffset;
		BufferedImage img = new BufferedImage(Sprite.SIZE, Sprite.SIZE,
				BufferedImage.TYPE_INT_ARGB);
		for (int ySprite = 0; ySprite < Sprite.SIZE; ySprite++) {
			for (int xSprite = 0; xSprite < Sprite.SIZE; xSprite++) {
				int color = sprite.getPixels()[(xSprite + ySprite * Sprite.SIZE)];
				if (color != 0xffffffff) {
					img.setRGB(xSprite, ySprite, color);
				}
			}
		}
		AffineTransform reset = new AffineTransform();
		reset.rotate(0, 0, 0);
		graphics.rotate(angle, (xPosition + Sprite.SIZE / 2) * scale,
				(yPosition + Sprite.SIZE / 2) * scale);
		graphics.drawImage(img, xPosition * scale, yPosition * scale,
				Sprite.SIZE * scale, Sprite.SIZE * scale, null);
		/*
		 * graphics.drawRect(xPosition * scale, yPosition * scale, Sprite.SIZE
		 * scale, Sprite.SIZE * scale);
		 */
		graphics.setTransform(reset);
	}

	/*
	 * @Description: draw a line between 2 points
	 * 
	 * @param x xCoordinate of first point
	 * 
	 * @param y yCoordinate of first point
	 * 
	 * @param x2 xCoordinate of second point
	 * 
	 * @param y2 yCoordinate of second point
	 * 
	 * @param color the color of line
	 */
	public void renderLine(double x, double y, double x2, double y2, Color color) {
		graphics.setColor(color);
		graphics.drawLine(((int) (x - xOffset + Sprite.SIZE / 2)) * scale,
				((int) (y - yOffset + Sprite.SIZE / 2)) * scale, ((int) (x2
						- xOffset + Sprite.SIZE / 2))
						* scale, ((int) (y2 - yOffset + Sprite.SIZE / 2))
						* scale);

	}

	/*
	 * @Description: render a circle from a certain point
	 * 
	 * @param x xCoordinate of center of circle
	 * 
	 * @param y yCoordinate of center of cicle
	 * 
	 * @param r radius of circle
	 * 
	 * @param color the color of circle
	 */
	public void renderCircle(double x, double y, double r, Color color) {
		graphics.setColor(color);
		graphics.drawOval((int) (x - xOffset - r + Sprite.SIZE / 2) * scale,
				(int) (y - r - yOffset + Sprite.SIZE / 2) * scale,
				(int) (2 * r * scale), (int) (2 * r * scale));
	}

	public void setGraphic(Graphics2D g) {
		this.graphics = g;
	}

	public void renderStatistics(ArrayList<String> info) {
		graphics.setColor(Color.black);
		graphics.setFont(graphics.getFont().deriveFont(20f));
		graphics.drawString(info.get(0), 700, 20);
		graphics.drawString(info.get(1), 700, 40);
		graphics.drawString(info.get(2), 700, 60);
		graphics.drawString(info.get(3), 700, 80);
	}

	public void dispose() {
		graphics.dispose();
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public int getXOffset() {
		return xOffset;
	}

	public int getYOffset() {
		return yOffset;
	}
	/*
	 * @Description: rotate the image
	 * 
	 * @param pixels data of image
	 * 
	 * @param width width of image
	 * 
	 * @param height height of image
	 * 
	 * @param angle angle to rotate to
	 */
	/*
	 * private int[] rotate(int[] pixels, int width, int height, double angle) {
	 * int[] result = new int[width * height]; double x_rotatedX =
	 * rotateX(-angle, 1.0, 0.0); double x_rotatedY = rotateY(-angle, 1.0, 0.0);
	 * double y_rotatedX = rotateX(-angle, 0.0, 1.0); double y_rotatedY =
	 * rotateY(-angle, 0.0, 1.0); double x0 = rotateX(-angle, -width / 2.0,
	 * -height / 2.0) + width / 2.0; double y0 = rotateY(-angle, -width / 2.0,
	 * -height / 2.0) + height / 2.0; for (int y = 0; y < height; y++) { double
	 * x1 = x0; double y1 = y0; for (int x = 0; x < width; x++) { int intX =
	 * (int) x1; int intY = (int) y1; int color = 0; if (intX < 0 || intX >=
	 * width || intY < 0 || intY >= height) { color = 0xffffffff; } else { color
	 * = pixels[intX + intY * width]; } result[x + y * width] = color; x1 +=
	 * x_rotatedX; y1 += x_rotatedY; } x0 += y_rotatedX; y0 += y_rotatedY; }
	 * return result;
	 * 
	 * }
	 */

	/*
	 * @Description: Rotate x coordinate
	 * 
	 * @param angle angle to rotate
	 * 
	 * @param x xCoordinate
	 * 
	 * @param y yCoordinate
	 */
	/*
	 * private double rotateX(double angle, double x, double y) { double cos =
	 * Math.cos(angle); double sin = Math.sin(angle); return x * cos + y *
	 * (-sin); }
	 */

	/*
	 * @Description: Rotate y coordinate
	 * 
	 * @param angle angle to rotate
	 * 
	 * @param x xCoordinate
	 * 
	 * @param y yCoordinate
	 */
	/*
	 * private double rotateY(double angle, double x, double y) { double cos =
	 * Math.cos(angle); double sin = Math.sin(angle); return x * sin + y * cos;
	 * }
	 */

	/*
	 * Reference
	 * http://tech-algorithm.com/articles/drawing-line-using-bresenham-
	 * algorithm/
	 */
	/*
	 * public void renderLine(double x, double y, double x2, double y2, int
	 * color) { int w = (int) (x2 - x); int h = (int) (y2 - y); int dx1 = 0, dy1
	 * = 0, dx2 = 0, dy2 = 0; if (w < 0) dx1 = -1; else if (w > 0) dx1 = 1; if
	 * (h < 0) dy1 = -1; else if (h > 0) dy1 = 1; if (w < 0) dx2 = -1; else if
	 * (w > 0) dx2 = 1; int longest = Math.abs(w); int shortest = Math.abs(h);
	 * if (!(longest > shortest)) { longest = Math.abs(h); shortest =
	 * Math.abs(w); if (h < 0) dy2 = -1; else if (h > 0) dy2 = 1; dx2 = 0; } int
	 * numerator = longest >> 1;
	 * 
	 * int xx = (int) x - xOffset + Sprite.SIZE / 2; int yy = (int) y - yOffset
	 * + Sprite.SIZE / 2; for (int i = 0; i <= longest; i++) {
	 * 
	 * if (xx < 0 || xx >= width || yy < 0 || yy >= height) continue; pixels[xx
	 * + yy * width] = color; numerator += shortest; if (!(numerator < longest))
	 * { numerator -= longest; xx += dx1; yy += dy1; } else { xx += dx2; yy +=
	 * dy2; } } }
	 */

	/*
	 * public void renderCircle(double x, double y, double r, int color) { int
	 * xx = 0; int yy = 0; double agl; for (double i = 0; i < 360; i += 0.1) {
	 * agl = i * Math.PI / 180; xx = (int) (r * Math.sin(agl) + Sprite.SIZE / 2
	 * - xOffset + x); yy = (int) (r * Math.cos(agl) + Sprite.SIZE / 2 - yOffset
	 * + y); if (xx < 0 || xx >= width || yy < 0 || yy >= height) continue;
	 * pixels[(int) (xx + yy * width)] = color; } }
	 */
	/*
	 * public void renderStrip(double x, double y, double r, int color) { int xx
	 * = (int) x - xOffset + Sprite.SIZE / 2; int yy = (int) y - yOffset +
	 * Sprite.SIZE / 2; double agl, xDelta, yDelta; for (double i = 0; i < 360;
	 * i += 0.1) { agl = i * Math.PI / 180; xDelta = r * Math.cos(agl); yDelta =
	 * r * Math.sin(agl); xDelta += xx; yDelta += yy; if (xDelta < 0 || xDelta
	 * >= width || yDelta < 0 || yDelta >= height) continue; pixels[(int)
	 * (xDelta + yDelta * width)] = color; } }
	 */
	/*
	 * public void renderCar(int xPosition, int yPosition, double angle, Sprite
	 * sprite) { // substract the Tile position by the offset of screen when
	 * screen moves xPosition -= xOffset; yPosition -= yOffset; for (int ySprite
	 * = 0; ySprite < Sprite.SIZE; ySprite++) { int yScreen = ySprite +
	 * yPosition; for (int xSprite = 0; xSprite < Sprite.SIZE; xSprite++) { int
	 * xScreen = xSprite + xPosition; // if the xScreen and yScreen are out of
	 * boundary then no // rendering. if (xScreen < -Sprite.SIZE || xScreen >=
	 * width || yScreen < 0 || yScreen >= height) { break; } if (xScreen < 0) {
	 * xScreen = 0; } int[] rotatedPixels = rotate(sprite.pixels, Sprite.SIZE,
	 * Sprite.SIZE, angle); // WHITE is the background of car sprite so we don't
	 * render it int color = rotatedPixels[(xSprite + ySprite * Sprite.SIZE)];
	 * if (color != 0xffffffff) { pixels[xScreen + yScreen * width] = color; } }
	 * } }
	 */
}
