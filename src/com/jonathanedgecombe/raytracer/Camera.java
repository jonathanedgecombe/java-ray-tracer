package com.jonathanedgecombe.raytracer;

public final class Camera {
	private final Vector point, direction;
	private final int width, height;
	private final double fov;

	public Camera(Vector point, Vector direction, int width, int height, double fov) {
		this.point = point;
		this.direction = direction;
		this.width = width;
		this.height = height;
		this.fov = fov;
	}

	public Vector getPoint() {
		return point;
	}

	public Vector getDirection() {
		return direction;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double getFov() {
		return fov;
	}

	public double getVFov() {
		return fov * height / width;
	}
}
