package Game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import Entities.Player;
import Input.Keyboard;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;
	public static int scale = 1;
	public static int width = Toolkit.getDefaultToolkit().getScreenSize().width
			/ scale;
	public static int height = Toolkit.getDefaultToolkit().getScreenSize().height
			/ scale;

	private static Thread thread;
	public static boolean running = false;
	private Keyboard key;
	private static JFrame frm;
	Player[] p = new Player[3];
	int[] spawn = new int[3];
	int timer = 0;

	Graphics g;
	private static Screen screen;
	int fps;

	private BufferedImage img = new BufferedImage(width, height,
			BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer())
			.getData();

	public Game() {

		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size);
		screen = new Screen(width, height);
		key = new Keyboard();
		p[0] = new Player(0xE60EB0, screen);
		spawn = createRandomSpawn();
		p[0].setSpawn(spawn[0], spawn[1], spawn[2]);
		p[1] = new Player(0x1FDB44, screen);
		spawn = createRandomSpawn();
		p[1].setSpawn(spawn[0], spawn[1], spawn[2]);
		this.setFocusable(true);
		this.requestFocus();
		addKeyListener(key);
	}

	public synchronized void start() {

		running = true;
		thread = new Thread(this, "Display");
		thread.start();

	}

	public synchronized static void stop() {

		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
		}

	}

	int ups;

	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60.0;
		double delta = 0;
		int frames = 0;
		int updates = 0;
		while (running == true) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				update();
				updates++;
				delta--;
			}
			render();
			frames++;

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				fps = frames;
				ups = updates;
				frames = 0;
				updates = 0;
			}
		}
		stop();
	}

	public void update() {
		key.update();
		p[0].update(key.left1, key.right1);
		p[1].update(key.left2, key.right2);
		respawn();
	}

	public int[] createRandomSpawn() {
		int x = 0, y = 0;
		int alpha = (int) (Math.random() * 360);
		while (x == 0 && y == 0) {
			x = (int) (Math.random() * (width - 400) + 200);
			y = (int) (Math.random() * (height - 400) + 200);
			if (!screen.isFreePosition(x, y)) {
				x = 0;
				y = 0;
			}
		}
		return new int[] { x, y, alpha };
	}

	public void render() {

		BufferStrategy bs = getBufferStrategy();

		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		if (key.clear) {
			screen.clear();
		}
		p[0].render();
		p[1].render();
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = screen.pixels[i];
		}

		g = bs.getDrawGraphics();
		g.fillRect(0, 0, width * scale, height * scale);
		g.drawImage(img, 0, 0, width, height, null);
		overlay();
		g.dispose();
		bs.show();
	}

	public void respawn() {
		if (!p[0].moving || !p[1].moving) {
			timer++;
			if (timer > 180) {
				screen.clear();
				spawn = createRandomSpawn();
				p[0].setSpawn(spawn[0], spawn[1], spawn[2]);
				spawn = createRandomSpawn();
				p[1].setSpawn(spawn[0], spawn[1], spawn[2]);
				p[0].moving = true;
				p[1].moving = true;
				timer = 0;
			}
		}
	}

	public void overlay() {
		if (key.fps) {
			g.setColor(Color.white);
			g.setFont(new Font("Arial", 0, 20));
			g.drawString(fps + " fps, " + ups + " ups", 20, 20);
		}
		if (timer > 120) {
			g.setColor(Color.white);
			g.setFont(new Font("Arial", 0, 100));
			g.drawString("1", width / 2, height / 2);
		}
		if (timer > 60 && timer < 120) {
			g.setColor(Color.white);
			g.setFont(new Font("Arial", 0, 100));
			g.drawString("2", width / 2, height / 2);
		}
		if (timer < 60 && timer > 0) {
			g.setColor(Color.white);
			g.setFont(new Font("Arial", 0, 100));
			g.drawString("3", width / 2, height / 2);
		}
	}

	public static void main(String[] args) {

		Game game = new Game();
		frm = new JFrame();
		frm.add(game);
		frm.setUndecorated(true);
		frm.pack();
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setLocationRelativeTo(null);
		frm.setVisible(true);
		game.start();
		screen.clear();
	}
}
