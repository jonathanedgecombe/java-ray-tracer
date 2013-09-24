package com.jonathanedgecombe.raytracer;

public final class Polygon extends Shape {
	private final double d;
	private final Vector normal;
	private final Vector p1, p2, p3;

	public Polygon(Vector p1, Vector p2, Vector p3, Vector color, double specularity, double reflectivity) {
		super(color, specularity, reflectivity);

		double v1x = p1.getX()-p2.getX();
		double v1y = p1.getY()-p2.getY();
		double v1z = p1.getZ()-p2.getZ();
		double v2x = p1.getX()-p3.getX();
		double v2y = p1.getY()-p3.getY();
		double v2z = p1.getZ()-p3.getZ();

		double nx = (v1y*v2z)-(v1z*v2y);
		double ny = (v1z*v2x)-(v1x*v2z);
		double nz = (v1x*v2y)-(v1y*v2x);

		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;

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

			if (!pointInTriangle(p1, p2, p3, ray.getOrigin().add(ray.getDirection().mul(distance)))) {
				type = IntersectionType.NONE;
			}
		}

		return new Intersection(type, distance);
	}

	private boolean pointInTriangle(Vector a, Vector b, Vector c, Vector p) {
		if (sameSide(p, a, b, c) && sameSide(p, b, a, c) && sameSide(p, c, a, b)) {
			Vector vc1 = a.sub(b).crs(a.sub(c));
			if (Math.abs(a.sub(p).dot(vc1)) <= 0.01) return true;
		}

		return false;
	}

	private boolean sameSide(Vector p1, Vector p2, Vector a, Vector b) {
		Vector cp1 = b.sub(a).crs(p1.sub(a));
		Vector cp2 = b.sub(a).crs(p2.sub(a));
		if (cp1.dot(cp2) >= 0) return true;
		return false;
	}
}
