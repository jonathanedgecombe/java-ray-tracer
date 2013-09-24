package com.jonathanedgecombe.raytracer;

public final class Sphere extends Shape {
	private final Vector center;
	private final double radius;

	public Sphere(Vector center, double radius, Vector color, double specularity, double reflectivity) {
		super(color, specularity, reflectivity);

		this.center = center;
		this.radius = radius;
	}

	@Override
	public Vector normalToPoint(Vector point) {
		Vector v = point.sub(center);
		return v.div(radius);
	}

	@Override
	public Intersection intersect(Ray ray) {
		double distance = Double.POSITIVE_INFINITY;

		Vector v = center.sub(ray.getOrigin());

		double dot = v.dot(v);
		double b = v.dot(ray.getDirection());

		double discriminant = b*b - dot + radius*radius;

		IntersectionType type = IntersectionType.NONE;

		if (discriminant > 0) {
			double d = Math.sqrt(discriminant);
			double root1 = b-d;
			double root2 = b+d;

			if (root2 > 0) {
				if (root1 < 0) {
					if (root2 < distance) {
						distance = root2;
						type = IntersectionType.EXTERNAL;
					}
				} else {
					if (root1 < distance) {
						distance = root1;
						type = IntersectionType.INTERNAL;
					}
				}
			}
		}

		return new Intersection(type, distance);
	}

	public Vector getCenter() {
		return center;
	}

	public double getRadius() {
		return radius;
	}

}
