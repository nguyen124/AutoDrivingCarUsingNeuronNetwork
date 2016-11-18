package com.auto.car.entity;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import com.auto.graphics.Screen;

/*
 * Checkpoint class is used to measure how well the car could go. 
 * If the car pass by 1 check point,it will gain a certain amount 
 * of fitness bonus which is 15 in this program
 */
public class Checkpoint {
	private Point2D start;
	private Point2D end;
	private boolean isActive;

	public Checkpoint() {
		setStart(null);
		setEnd(null);
		setActive(false);
	}

	public Checkpoint(Point2D start, Point2D end, boolean isActive) {
		this.setStart(start);
		this.setEnd(end);
		this.setActive(isActive);
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Point2D getStart() {
		return start;
	}

	public void setStart(Point2D start) {
		this.start = start;
	}

	public Point2D getEnd() {
		return end;
	}

	public void setEnd(Point2D end) {
		this.end = end;
	}

	/*
	 * draw the line from start to end of a check point
	 */
	public void render(Screen sc) {
		sc.renderCircle((int) start.getX(), (int) start.getY(), 2, Color.YELLOW);
		sc.renderCircle((int) end.getX(), (int) end.getY(), 2, Color.YELLOW);
		Color color = Color.CYAN;
		sc.renderLine(start.getX(), start.getY(), end.getX(), end.getY(), color);
	}

	/*
	 * check the intersection between check point and car
	 */
	public boolean checkIntersect(Rectangle2D rec) {
		double x1 = start.getX();
		double y1 = start.getY();
		double x2 = end.getX();
		double y2 = end.getY();
		Line2D line = new Line2D.Double(x1, y1, x2, y2);
		return line.intersects(rec);
	}
}
