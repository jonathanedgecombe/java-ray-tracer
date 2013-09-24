package com.jonathanedgecombe.raytracer;

import java.awt.Graphics;
import java.util.concurrent.BlockingQueue;

public final class RenderThread {
	private final Thread thread;

	public RenderThread(BlockingQueue<RenderArea> queue, RayTracer tracer, Graphics g) {
		thread = new Thread(new RenderRunnable(queue, tracer, g));
	}

	public void start() {
		thread.start();
	}
}
