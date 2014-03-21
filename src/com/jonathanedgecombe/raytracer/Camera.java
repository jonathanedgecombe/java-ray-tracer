package com.jonathanedgecombe.raytracer;

public final class Camera {
	private final Vector point, direction;
	private final int width, height, supSamples, subSamples, maxDepth;
	private final double fov, focus, aperture;

	public Camera(Vector point, Vector direction, int width, int height, double fov, double focus, double aperture, int maxDepth, int supSamples, int subSamples) {
		this.point = point;
		this.direction = direction;
		this.width = width;
		this.height = height;
		this.fov = fov;
		this.focus = focus;
		this.aperture = aperture;
		this.maxDepth = maxDepth;
		this.supSamples = supSamples;
		this.subSamples = subSamples;
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

	public double getFocus() {
		return focus;
	}

	public double getAperture() {
		return aperture;
	}

	public int getSupSamples() {
		return supSamples;
	}

	public int getSubSamples() {
		return subSamples;
	}

	public int getMaxDepth() {
		return maxDepth;
	}
}
