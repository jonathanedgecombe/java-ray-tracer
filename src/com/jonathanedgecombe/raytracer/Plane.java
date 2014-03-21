package com.jonathanedgecombe.raytracer;

public final class Plane extends Shape {
	private final double d;
	private final Vector normal;

	public Plane(Vector p1, Vector p2, Vector p3, Vector color, double specularity, double reflectivity, double transparency, double refractiveIndex) {
		super(color, specularity, reflectivity, transparency, refractiveIndex);

		double v1x = p1.getX()-p2.getX();
		double v1y = p1.getY()-p2.getY();
		double v1z = p1.getZ()-p2.getZ();
		double v2x = p1.getX()-p3.getX();
		double v2y = p1.getY()-p3.getY();
		double v2z = p1.getZ()-p3.getZ();

		double nx = (v1y*v2z)-(v1z*v2y);
		double ny = (v1z*v2x)-(v1x*v2z);
		double nz = (v1x*v2y)-(v1y*v2x);

		normal = new Vector(nx, ny, nz);
		d = -(nx*p1.getX() + ny*p1.getY() + nz*p1.getZ());
	}

	@Override
	public Vector normalAtIntersection(Vector point) {
		return normal;
	}

	@Override
	public Intersection intersect(Ray ray) {
		IntersectionType type = IntersectionType.NONE;
		double distance = Double.POSITIVE_INFINITY;

		double ndotrd = normal.dot(ray.getDirection());
		if (ndotrd != 0) {
			double ndotro = normal.dot(ray.getOrigin());
			distance = -(ndotro + d)/ndotrd;

			if (distance > 0) type = IntersectionType.EXTERNAL;
		}

		return new Intersection(type, distance);
	}

}
