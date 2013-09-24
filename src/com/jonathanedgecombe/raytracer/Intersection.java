package com.jonathanedgecombe.raytracer;

public final class Intersection {
	private final IntersectionType type;
	private final double distance;

	public Intersection(IntersectionType type, double distance) {
		this.type = type;
		this.distance = distance;
	}

	public IntersectionType getType() {
		return type;
	}

	public double getDistance() {
		return distance;
	}
}
