package com.jonathanedgecombe.raytracer;

public final class Light extends Shape {
	private final Vector center;

	public Light(Vector center, Vector color) {
		super(color, 0.0, 0.0);

		this.center = center;
	}

	@Override
	public Vector normalToPoint(Vector point) {
		return point.sub(center).normalize();
	}

	@Override
	public Intersection intersect(Ray ray) {
		return new Intersection(IntersectionType.NONE, 0.0);
	}

	public Vector getCenter() {
		return center;
	}

}
