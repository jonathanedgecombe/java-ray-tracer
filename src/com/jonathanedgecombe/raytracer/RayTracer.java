package com.jonathanedgecombe.raytracer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public final class RayTracer extends JPanel {
	private static final long serialVersionUID = -8583024807878825419L;

	private final static int BLOCK_SIZE = 32;
	private final static Color BORDER_COLOR = new Color(0xFF8800);

	private final Scene scene;
	private final BufferedImage image;

	private final List<RenderArea> drawing = new ArrayList<>();

	private final Lock lock = new ReentrantLock();
	private final Condition done = lock.newCondition();
	private int lockCount = 0;


	public RayTracer(Scene scene) {
		image = new BufferedImage(scene.getCamera().getWidth(), scene.getCamera().getHeight(), BufferedImage.TYPE_INT_ARGB);

		JFrame frame = new JFrame("Ray Tracer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.scene = scene;

		Dimension dimension = new Dimension(scene.getCamera().getWidth(), scene.getCamera().getHeight());
		setMinimumSize(dimension);
		setMaximumSize(dimension);
		setPreferredSize(dimension);
		setSize(dimension);

		frame.add(this);
		frame.pack();
		frame.setVisible(true);
	}

	public void inc() {
		lock.lock();
		lockCount++;
		lock.unlock();
	}

	public void dec() {
		lock.lock();
		lockCount--;
		if (lockCount == 0) done.signal();
		lock.unlock();
	}

	public void render(Graphics g, int startX, int startY, int endX, int endY) {
		scene.render(g, startX, startY, endX, endY);
	}

	public void render(int threads) {
		lock.lock();
		BlockingQueue<RenderArea> queue = new ArrayBlockingQueue<>(65536*1024);
		for (int thread = 0; thread < threads; thread++) {
			new RenderThread(queue, this, image.getGraphics()).start();
		}
		for (int x = 0; x < scene.getCamera().getWidth(); x += BLOCK_SIZE) {
			for (int y = 0; y < scene.getCamera().getHeight(); y += BLOCK_SIZE) {
				try {
					inc();
					queue.put(new RenderArea(x, y, min(x + BLOCK_SIZE, scene.getCamera().getWidth()), min(y + BLOCK_SIZE, scene.getCamera().getHeight())));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			done.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lock.unlock();
	}

	public Scene getScene() {
		return scene;
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, scene.getCamera().getWidth(), scene.getCamera().getHeight());

		g.drawImage(image, 0, 0, this);

		synchronized(drawing) {
			for (RenderArea area : drawing) {
				g.setColor(BORDER_COLOR);
				g.drawRect(area.getStartX(), area.getStartY(), area.getEndX()-area.getStartX()-1, area.getEndY()-area.getStartY()-1);
			}
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		Scene scene = new Scene(new Camera(new Vector(0, 2, -10), new Vector(0, -0.2, 1).normalize(), 800, 600, 80.0*(2*Math.PI/360)), 0.5, new Vector(0.9, 0.9, 0.9));

		final RayTracer tracer = new RayTracer(scene);

		tracer.getScene().addObject(new Plane(new Vector(1, -1, 0), new Vector(0, -1, 0), new Vector(0, -1, 1), new Vector(0.3, 0.3, 0.3), 0.5, 0.05));
		tracer.getScene().addObject(new Sphere(new Vector(0.5, 0, 0), 1.0, new Vector(0.66, 0.33, 0), 0.5, 0.03));
		tracer.getScene().addObject(new Sphere(new Vector(-1.5, 0, -0.5), 1.0, new Vector(1, 1, 1), 0.5, 0.8));

		tracer.getScene().addLight(new Light(new Vector(-2, 2, 2), new Vector(1, 1, 1)));
		tracer.getScene().addLight(new Light(new Vector(2, 2, 2), new Vector(1, 1, 1)));
		tracer.getScene().addLight(new Light(new Vector(-2, 2, -2), new Vector(1, 1, 1)));
		tracer.getScene().addLight(new Light(new Vector(2, 2, -2), new Vector(1, 1, 1)));

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					tracer.repaint();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		tracer.render(Runtime.getRuntime().availableProcessors());

		ImageIO.write(tracer.image, "PNG", new File("render.png"));

		System.out.println("Done!");
	}

	public int min(int a, int b) {
		return b < a ? b : a;
	}

	public List<RenderArea> getDrawing() {
		return drawing;
	}
}
