package com.jonathanedgecombe.raytracer;

public final class Ray {
	private final Vector origin, direction;

	public Ray(Vector origin, Vector direction) {
		this.origin = origin;
		this.direction = direction;
	}

	public Vector getOrigin() {
		return origin;
	}

	public Vector getDirection() {
		return direction;
	}

	public Ray setOrigin(Vector origin) {
		return new Ray(origin, direction);
	}

	public Ray setDirection(Vector direction) {
		return new Ray(origin, direction);
	}
}
