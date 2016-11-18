package com.auto.car;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import com.auto.algorithm.HelperFunction;
import com.auto.car.entity.EntityManager;
import com.auto.graphics.Screen;
import com.auto.input.Keyboard;
import com.auto.input.Mouse;

/*
 * @Description: This class contains main method - entry point to the program
 * 
 */
public class CarDemo extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	private static int width = 600;
	private static int height = width * 9 / 16;
	public static int scale = 2;
	private Thread thread;
	private JFrame frame;
	private boolean running = false;
	private BufferedImage image = new BufferedImage(width, height,
			BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer())
			.getData();
	private Screen screen;
	private Keyboard keyboard;
	private EntityManager entityManager;

	public CarDemo() {
		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size);
		screen = new Screen(width, height);
		frame = new JFrame();
		Mouse mouse = new Mouse();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		keyboard = new Keyboard();
		addKeyListener(keyboard);
		entityManager = new EntityManager();
	}

	public synchronized void start() {
		thread = new Thread(this, "Auto Car");
		running = true;
		thread.start();
	}

	public synchronized void stop() {
		running = false;
		try {
			System.out.println("Goodbye");
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static int fps = 60;

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();

		double deltaTime = 0;
		int frameCount = 0;
		int updateCount = 0;
		requestFocus();
		while (running) {
			double nanoSecondsPerCount = 1000000000.0 / fps;
			long now = System.nanoTime();
			deltaTime += (now - lastTime) / nanoSecondsPerCount;
			lastTime = now;
			while (deltaTime >= 1) {
				update();
				updateCount++;
				//System.out.println(updateCount + " - " + deltaTime);
				deltaTime--;
			}
			render();
			frameCount++;
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				frame.setTitle(updateCount + "ups, " + frameCount + " frames");
				updateCount = 0;
				frameCount = 0;
			}
		}
		stop();
	}

	private void update() {
		keyboard.update();
		if (keyboard.getKeys()[KeyEvent.VK_ESCAPE]) {
			stop();
		} else if (keyboard.getKeys()[KeyEvent.VK_R]) {
			restartSimulation();
		} else if (keyboard.getKeys()[KeyEvent.VK_DOWN]) {
			fps -= 1;
			fps = (int) HelperFunction
					.getValueInRange(fps, 30, 300);
		} else if (keyboard.getKeys()[KeyEvent.VK_UP]) {
			fps += 1;
			fps = (int) HelperFunction
					.getValueInRange(fps, 30, 300);
		} else if (keyboard.getKeys()[KeyEvent.VK_N]) {
			entityManager.nextMapIndex();
		} else {
			if (keyboard.getKeys()[KeyEvent.VK_SPACE]) {
				entityManager.forceToNextAgent();
			}
		}
		entityManager.update();
	}

	private void restartSimulation() {
		entityManager = new EntityManager();
	}

	private void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		screen.setGraphic(g);
		entityManager.renderByPixels(screen);
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = screen.getPixels()[i];
		}
		g.setStroke(new BasicStroke(2));
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		entityManager.renderByGraphics(screen);
		screen.dispose();
		bs.show();
	}

	public static int getWindowWidth() {
		return width * scale;
	}

	public static int getWindowHeight() {
		return height * scale;
	}

	public static void main(String[] args) {
		CarDemo car = new CarDemo();
		car.frame.setResizable(false);
		car.frame.setTitle("Auto Car");
		car.frame.add(car);
		car.frame.pack();
		car.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		car.frame.setLocationRelativeTo(null);
		car.frame.setVisible(true);
		car.start();
	}
}
