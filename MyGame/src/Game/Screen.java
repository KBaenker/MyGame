package Game;

import java.util.ArrayList;

import Entities.Player;

public class Screen {

	public int width, height;
	public int[] pixels;
	int aIndex = 0;
	ArrayList<Integer> error = new ArrayList<Integer>();
	Player p;

	public Screen(int width, int height) {

		this.width = width;
		this.height = height;
		pixels = new int[width * height];
	}

	public void clear() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0x000000;
		}
		border();
	}

	public void renderPixel(int x, int y, int color) {
		pixels[x + y * width] = color;
	}

	public boolean isFreePosition(int x, int y) {
		if(x + 8 + (y + 8) * width > pixels.length){
			return false;
		}
		for (int xb = 2; xb <= 5; xb++) {
			for (int yb = 0; yb <= 1; yb++) {
				if (pixels[x + xb + (y + yb) * width] != 0x000000) {
					return false;
				}
			}
		}
		for (int xb = 1; xb <= 6; xb++) {
			for (int yb = 1; yb <= 2; yb++) {
				if (pixels[x + xb + (y + yb) * width] != 0x000000) {
					return false;
				}
			}
		}
		for (int xb = 0; xb <= 7; xb++) {
			for (int yb = 2; yb <= 5; yb++) {
				if (pixels[x + xb + (y + yb) * width] != 0x000000) {
					return false;
				}
			}
		}
		for (int xb = 1; xb <= 6; xb++) {
			for (int yb = 5; yb <= 6; yb++) {
				if (pixels[x + xb + (y + yb) * width] != 0x000000) {
					return false;
				}
			}
		}
		for (int xb = 2; xb <= 5; xb++) {
			for (int yb = 6; yb <= 7; yb++) {
				if (pixels[x + xb + (y + yb) * width] != 0x000000) {
					return false;
				}
			}
		}
		return true;
	}

	public void border() {
		for (int i = 0; i < width * 2; i++) {
			pixels[i] = 0xffffff;
		}
		for (int i = (height - 2) * width; i < (height - 0) * width; i++) {
			pixels[i] = 0xffffff;
		}

		for (int i = 0; i < pixels.length; i += width) {
			pixels[i] = 0xffffff;
		}
		for (int i = 1; i < pixels.length; i += width) {
			pixels[i] = 0xffffff;
		}
		for (int i = width - 1; i < pixels.length; i += width) {
			pixels[i] = 0xffffff;
		}
		for (int i = width - 2; i < pixels.length; i += width) {
			pixels[i] = 0xffffff;
		}
	}

	public void renderPlayer(int color, int x, int y, Player p, int xs, int xe,
			int ys, int ye) {
		this.p = p;
		for (int xb = xs; xb <= xe; xb++) {
			for (int yb = ys; yb <= ye; yb++) {
				if (pixels[x + xb + (y + yb) * width] != 0x000000) {
					if (pixels[x + xb + (y + yb) * width] != color) {
						p.moving = false;
					} else if (!p.a.contains(x + xb + (y + yb) * width)) {
						p.moving = false;
					}
				}
				pixels[x + xb + (y + yb) * width] = color;
				if (!p.a.contains(x + xb + (y + yb) * width)) {
					p.a.set(aIndex, x + xb + (y + yb) * width);
					aIndex++;
				}
				if (aIndex > 1000) {
					aIndex = 0;
				}
			}
		}
	}
}