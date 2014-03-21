package com.jonathanedgecombe.raytracer;

public abstract class Shape {
	public abstract Vector normalAtIntersection(Vector point);
	public abstract Intersection intersect(Ray ray);

	private final double specularity, reflectivity, transparency, refractiveIndex;
	private final Vector color;

	public Shape(Vector color, double specularity, double reflectivity, double transparency, double refractiveIndex) {
		this.color = color;
		this.specularity = specularity;
		this.reflectivity = reflectivity;
		this.transparency = transparency;
		this.refractiveIndex = refractiveIndex;
	}

	public double getSpecularity() {
		return specularity;
	}

	public double getReflectivity() {
		return reflectivity;
	}

	public double getTransparency() {
		return transparency;
	}

	public double getRefractiveIndex() {
		return refractiveIndex;
	}

	public Vector getColor() {
		return color;
	}
}
