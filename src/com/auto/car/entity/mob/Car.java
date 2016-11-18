package com.auto.car.entity.mob;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import com.auto.algorithm.NeuralNetwork;
import com.auto.car.entity.EntityManager;
import com.auto.car.level.Level;
import com.auto.car.level.tile.Tile;
import com.auto.graphics.Screen;
import com.auto.graphics.Sprite;

/*
 * Description: Car has 5 sensors to measure the distance from its center
 * to the wall. It use NeuralNetwork, pass in these 5 sensors's values to 
 * get out the decision of making turn left or right or going straight.
 */
public class Car extends Mob {
	// The identifiers for the feelers of the agnet
	public static int FEELER_COUNT = 5;

	public static enum SensorFeelers {
		FEELER_EAST, FEELER_NORTH_EAST, FEELER_NORTH, FEELER_NORTH_WEST, FEELER_WEST,
	};

	public static float FEELER_LENGTH = 32.0f;
	private double angle;
	private double deltaDistance;
	private Sensor sensor;
	private NeuralNetwork neural;
	private Point2D[] intersections;
	private double[] normalizedIntersectionDepths;

	public int eastIdx = SensorFeelers.FEELER_EAST.ordinal();
	public int northEastIdx = SensorFeelers.FEELER_NORTH_EAST.ordinal();
	public int northIdx = SensorFeelers.FEELER_NORTH.ordinal();
	public int northWestIdx = SensorFeelers.FEELER_NORTH_WEST.ordinal();
	public int westIdx = SensorFeelers.FEELER_WEST.ordinal();

	public static final float MAX_ROTATION_PER_SECOND = 30.0f / 180;

	public class Sensor {
		public Point2D[] feelerTails;
		public Point2D[] feelerHeads;

		public Sensor() {
			feelerTails = new Point2D[FEELER_COUNT];
			feelerHeads = new Point2D[FEELER_COUNT];
		}
	}

	public Car(int x, int y, double angle, Level lv) {
		this.x = x;
		this.y = y;
		this.angle = angle;
		sensor = new Sensor();
		level = lv;
		buildFeelers();
		detectFeelerIntersection();

	}

	public void setLevel(Level lv) {
		level = lv;
	}

	public void update() {
		if (!this.collided) {
			buildFeelers();
			detectFeelerIntersection();
			neural.setInput(normalizedIntersectionDepths);
			neural.update();
			double leftForce = neural
					.getOutput(EntityManager.NeuralNetOuputs.NN_OUTPUT_LEFT_FORCE
							.ordinal());
			double rightForce = neural
					.getOutput(EntityManager.NeuralNetOuputs.NN_OUTPUT_RIGHT_FORCE
							.ordinal());
			System.out.println("left force: " + leftForce + "-right force: "
					+ rightForce);
			// Convert the outputs to a proportion of how much to turn.
			double leftTheta = MAX_ROTATION_PER_SECOND * leftForce;
			double rightTheta = MAX_ROTATION_PER_SECOND * rightForce;
			angle += (leftTheta - rightTheta) * 2;
			double movingX = Math.sin(angle) * 2;
			double movingY = -Math.cos(angle) * 2;
			deltaDistance = Math.sqrt(movingX * movingX + movingY * movingY);
			move((int) movingX, (int) movingY);
		}
	}

	/*
	 * private void turn(int x, int y, int direction) { if (Mouse.getButton() ==
	 * 1) { double dx = Mouse.getX() - CarDemo.getWindowWidth() / 2 - 24; double
	 * dy = Mouse.getY() - CarDemo.getWindowHeight() / 2 - 24; angle =
	 * Math.atan2(dy, dx); }
	 * 
	 * }
	 */

	public Rectangle2D getCarBound() {
		return new Rectangle2D.Double(x - Sprite.SIZE / 2, y - Sprite.SIZE / 2,
				Sprite.SIZE, Sprite.SIZE);
	}

	/*
	 * this function determines the position of sensors when the car turns
	 */
	private void buildFeelers() {

		for (int i = 0; i < FEELER_COUNT; i++) {
			sensor.feelerHeads[i] = new Point2D.Float();
			sensor.feelerTails[i] = new Point2D.Float();
			// All feelers's tails has the same coordinate which is the center
			// of the car.
			sensor.feelerTails[i].setLocation(x, y);
		}
		// East feeler's head
		sensor.feelerHeads[eastIdx].setLocation(
				x + Math.sin(Math.PI - (angle + Math.PI / 2)) * FEELER_LENGTH,
				y + Math.cos(Math.PI - (angle + Math.PI / 2)) * FEELER_LENGTH);
		// North East feeler's head
		sensor.feelerHeads[northEastIdx].setLocation(
				x + Math.sin(Math.PI - (angle + Math.PI / 4)) * FEELER_LENGTH,
				y + Math.cos(Math.PI - (angle + Math.PI / 4)) * FEELER_LENGTH);
		// North feeler's head
		sensor.feelerHeads[northIdx].setLocation(x + Math.sin(Math.PI - angle)
				* FEELER_LENGTH, y + Math.cos(Math.PI - angle) * FEELER_LENGTH);
		// North West feeler's head
		sensor.feelerHeads[northWestIdx].setLocation(
				x + Math.sin(Math.PI - (angle - Math.PI / 4)) * FEELER_LENGTH,
				y + Math.cos(Math.PI - (angle - Math.PI / 4)) * FEELER_LENGTH);
		// West feeler's head
		sensor.feelerHeads[westIdx].setLocation(
				x + Math.sin(Math.PI - (angle - Math.PI / 2)) * FEELER_LENGTH,
				y + Math.cos(Math.PI - (angle - Math.PI / 2)) * FEELER_LENGTH);
	}

	/*
	 * This function measures the distance from center of the car to the wall.
	 */
	public void detectFeelerIntersection() {
		intersections = new Point2D[FEELER_COUNT];
		normalizedIntersectionDepths = new double[FEELER_COUNT];
		for (int k = 0; k < FEELER_COUNT; k++) {
			double xStart = sensor.feelerHeads[k].getX();
			double xEnd = sensor.feelerTails[k].getX();
			double yStart = sensor.feelerHeads[k].getY();
			double yEnd = sensor.feelerTails[k].getY();
			Line2D line = new Line2D.Double();
			line.setLine(sensor.feelerHeads[k], sensor.feelerTails[k]);
			double step = 0.001;
			double slope = (yStart - yEnd) / (xStart - xEnd);
			if (!java.lang.Double.isInfinite(slope)) {
				for (double i = xStart; i < xEnd; i += step) {
					double j = slope * (i - xEnd) + yEnd;
					Tile tile = level.getTile((int) (i + Sprite.SIZE / 2)
							/ Sprite.SIZE, (int) (j + Sprite.SIZE / 2)
							/ Sprite.SIZE);
					if (tile != null) {
						if (tile.solid()) {
							intersections[k] = new Point2D.Float();
							intersections[k].setLocation(i, j);
						}
					}
				}
				for (double i = xStart; i > xEnd; i -= step) {
					double j = slope * (i - xEnd) + yEnd;
					Tile tile = level.getTile((int) (i + Sprite.SIZE / 2)
							/ Sprite.SIZE, (int) (j + Sprite.SIZE / 2)
							/ Sprite.SIZE);
					if (tile != null) {
						if (tile.solid()) {
							intersections[k] = new Point2D.Float();
							intersections[k].setLocation(i, j);
						}
					}
				}
			} else {
				for (double j = yStart; j < yEnd; j += step) {
					Tile tile = level.getTile((int) (xStart + Sprite.SIZE / 2)
							/ Sprite.SIZE, (int) (j + Sprite.SIZE / 2)
							/ Sprite.SIZE);
					if (tile != null) {
						if (tile.solid()) {
							intersections[k] = new Point2D.Float();
							intersections[k].setLocation(xStart, j);
						}
					}
				}
				for (double j = yStart; j > yEnd; j -= step) {
					Tile tile = level.getTile((int) (xStart + Sprite.SIZE / 2)
							/ Sprite.SIZE, (int) (j + Sprite.SIZE / 2)
							/ Sprite.SIZE);
					if (tile != null) {
						if (tile.solid()) {
							intersections[k] = new Point2D.Float();
							intersections[k].setLocation(xStart, j);
						}
					}
				}
			}
			if (intersections[k] != null) {
				normalizedIntersectionDepths[k] = 1 - (Math.sqrt(Math.pow(x
						- intersections[k].getX(), 2)
						+ Math.pow(y - intersections[k].getY(), 2)) / FEELER_LENGTH);
			} else {
				normalizedIntersectionDepths[k] = 0;
			}
		}
	}

	/*public void render(Screen screen) {
		screen.renderCar(x, y, angle, Sprite.carSprite);
	}
*/

	public void attach(NeuralNetwork neuralNet) {
		this.neural = neuralNet;
	}

	public void setPosition(Point2D defaultPosition) {
		x = (int) defaultPosition.getX();
		y = (int) defaultPosition.getY();
	}

	public void clearFailure() {
		collided = false;
	}

	public boolean hasFailed() {
		return collided;
	}

	public double getDistanceDelta() {
		return deltaDistance;
	}

	public void render(Screen screen) {
		// Render the car
		screen.renderCar(x, y, angle, Sprite.carSprite);
		// Render 5 sensors around the car
		screen.renderLine(sensor.feelerHeads[eastIdx].getX(),
				sensor.feelerHeads[eastIdx].getY(),
				sensor.feelerTails[eastIdx].getX(),
				sensor.feelerTails[eastIdx].getY(), Color.YELLOW);

		screen.renderLine(sensor.feelerHeads[northEastIdx].getX(),
				sensor.feelerHeads[northEastIdx].getY(),
				sensor.feelerTails[northEastIdx].getX(),
				sensor.feelerTails[northEastIdx].getY(), Color.YELLOW);

		screen.renderLine(sensor.feelerHeads[northIdx].getX(),
				sensor.feelerHeads[northIdx].getY(),
				sensor.feelerTails[northIdx].getX(),
				sensor.feelerTails[northIdx].getY(), Color.black);

		screen.renderLine(sensor.feelerHeads[northWestIdx].getX(),
				sensor.feelerHeads[northWestIdx].getY(),
				sensor.feelerTails[northWestIdx].getX(),
				sensor.feelerTails[northWestIdx].getY(), Color.YELLOW);

		screen.renderLine(sensor.feelerHeads[westIdx].getX(),
				sensor.feelerHeads[westIdx].getY(),
				sensor.feelerTails[westIdx].getX(),
				sensor.feelerTails[westIdx].getY(), Color.YELLOW);
		screen.renderCircle(x, y, FEELER_LENGTH, Color.YELLOW);
		// draw collisions by a small circle
		for (int k = 0; k < FEELER_COUNT; k++) {
			if (intersections[k] != null) {
				screen.renderCircle(intersections[k].getX(),
						intersections[k].getY(), 3, Color.YELLOW);
			}
		}
	}

	public void setPosition(int x, int y) {
		setX(x);
		setY(y);
	}

}
