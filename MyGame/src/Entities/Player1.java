package Entities;

import Game.Screen;
import Input.Keyboard;

public class Player1 extends Player {

	public Player1(int color, Keyboard key, Screen screen, int xspawn,
			int yspawn, int width) {
		super(color, key, screen, xspawn, yspawn, width);
	}

	public void update() {
		if (moving) {
			if (key.left1) {
				alpha -= 2;
			}
			if (key.right1) {
				alpha += 2;
			}
			beta = Math.toRadians(alpha);
			yOffset += 1 * Math.sin(beta);
			xOffset += 1 * Math.cos(beta);
		}
	}
}
