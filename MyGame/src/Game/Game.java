package Game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import Entities.Player;
import Entities.Player1;
import Entities.Player2;
import Input.Keyboard;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;
	public static int scale = 1;
	public static int height = Toolkit.getDefaultToolkit().getScreenSize().height
			/ scale;
	public static int width = height / 9 * 16;

	private static Thread thread;
	public static boolean running = false;
	private Keyboard key;
	private JFrame frm;
	Player player1,player2;

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
		frm = new JFrame();
		key = new Keyboard();
		player1 = new Player1(0xffffff, key, screen, width / 2 - 4,
				height / 2 - 4);
		player2 = new Player2(0x000000, key, screen, width / 2 - 4,
				height / 2 - 4);
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

	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60.0;
		double delta = 0;
		int frames = 0;
		@SuppressWarnings("unused")
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
				updates = 0;
				frames = 0;
			}
		}
		stop();
	}

	int x = 0;
	int y = 0;

	public void update() {
		key.update();
		player1.update();
		player2.update();
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
		player1.render();
		player2.render();
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = screen.pixels[i];
		}

		g = bs.getDrawGraphics();
		g.fillRect(0, 0, width * scale, height * scale);
		g.drawImage(img, 0, 0, width * scale, height * scale, null);

		if (key.fps) {
			g.setColor(Color.white);
			g.setFont(new Font("Arial", 0, 20));
			g.drawString(fps + " fps", 20, 20);
		}

		g.dispose();
		bs.show();
	}

	public static void main(String[] args) {

		Game game = new Game();
		game.frm = new JFrame();
		GraphicsDevice ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getScreenDevices()[0];
		ge.setFullScreenWindow(game.frm);
		game.frm.add(game);
		game.frm.pack();
		game.frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frm.setLocationRelativeTo(null);
		game.frm.setVisible(true);

		game.start();
		screen.clear();
	}
}