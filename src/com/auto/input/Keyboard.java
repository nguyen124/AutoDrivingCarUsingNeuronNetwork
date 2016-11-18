package com.auto.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*
 * @Description: This class is used to handle keyboard's action
 */
public class Keyboard implements KeyListener {
	public final int KEYS_SIZE = 120;
	private boolean[] keys = new boolean[KEYS_SIZE];
	public boolean[] getKeys() {
		return keys;
	}

	public boolean up, down, left, right;

	public void update() {
		up = keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W];
		down = keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_S];
		left = keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A];
		right = keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D];
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key < KEYS_SIZE) {
			keys[e.getKeyCode()] = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if (key < KEYS_SIZE) {
			keys[e.getKeyCode()] = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
