package com.jonathanedgecombe.raytracer;

public final class TraceResult {
	private final Vector color;
	private final Shape object;

	public TraceResult(Vector color, Shape object) {
		this.color = color;
		this.object = object;
	}

	public Vector getColor() {
		return color;
	}

	public Shape getObject() {
		return object;
	}
}
