package com.jonathanedgecombe.raytracer;

import java.awt.Graphics;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public final class RenderRunnable implements Runnable {
	private final BlockingQueue<RenderArea> queue;
	private final RayTracer tracer;
	private final Graphics g;

	public RenderRunnable(BlockingQueue<RenderArea> queue, RayTracer tracer, Graphics g) {
		this.queue = queue;
		this.tracer = tracer;
		this.g = g;
	}

	@Override
	public void run() {
		List<RenderArea> drawing = tracer.getDrawing();
		while (true) {
			try {
				RenderArea area = queue.take();
				synchronized(drawing) { drawing.add(area); }
				tracer.getScene().render(g, area.getStartX(), area.getStartY(), area.getEndX(), area.getEndY());
				synchronized(drawing) { drawing.remove(area); }
				tracer.dec();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
