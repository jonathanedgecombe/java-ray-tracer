package com.jonathanedgecombe.raytracer;

public abstract class Shape {
	public abstract Vector normalToPoint(Vector point);
	public abstract Intersection intersect(Ray ray);

	private final double specularity, reflectivity;
	private final Vector color;

	public Shape(Vector color, double specularity, double reflectivity) {
		this.color = color;
		this.specularity = specularity;
		this.reflectivity = reflectivity;
	}
	public double getSpecularity() {
		return specularity;
	}
	public double getReflectivity() {
		return reflectivity;
	}
	public Vector getColor() {
		return color;
	}
}
