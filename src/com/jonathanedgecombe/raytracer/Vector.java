package com.jonathanedgecombe.raytracer;

public final class Vector {
	private final double x, y, z;

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public double magnitude() {
		return Math.sqrt((x*x) + (y*y) + (z*z));
	}

	public Vector normalize() {
		double m = magnitude();
		if (m == 0) m = 1;
		return new Vector(x/m, y/m, z/m);
	}

	public Vector sub(Vector v) {
		return new Vector(x-v.x, y-v.y, z-v.z);
	}

	public Vector add(Vector v) {
		return new Vector(x+v.x, y+v.y, z+v.z);
	}

	public Vector mul(Vector v) {
		return new Vector(x*v.x, y*v.y, z*v.z);
	}

	public Vector div(Vector v) {
		return new Vector(x/v.x, y/v.y, z/v.z);
	}

	public Vector sub(double v) {
		return new Vector(x-v, y-v, z-v);
	}

	public Vector add(double v) {
		return new Vector(x+v, y+v, z+v);
	}

	public Vector mul(double v) {
		return new Vector(x*v, y*v, z*v);
	}

	public Vector div(double v) {
		return new Vector(x/v, y/v, z/v);
	}

	public double dot(Vector v) {
		return (x*v.x) + (y*v.y) + (z*v.z);
	}

	public Vector crs(Vector v) {
		return new Vector(y*v.z - z*v.y, z*v.x - x*v.z, x*v.y -y*v.x);
	}

	public Vector max(double d) {
		double x = this.x;
		double y = this.y;
		double z = this.z;
		if (x > d) x = d;
		if (y > d) y = d;
		if (z > d) z = d;
		return new Vector(x, y, z);
	}

	public Vector rotate(Vector d, double angle) {
		double s = Math.sin(angle);
		double c = Math.cos(angle);

		double[][] matrix = new double[3][3];

		matrix[0][0] = c+((d.getX()*d.getX())*(1-c));
		matrix[0][1] = (d.getX()*d.getY()*(1-c))-(d.getZ()*s);
		matrix[0][2] = (d.getX()*d.getZ()*(1-c))+(d.getY()*s);
		matrix[1][0] = (d.getY()*d.getX()*(1-c))+(d.getZ()*s);
		matrix[1][1] =  c+((d.getY()*d.getY())*(1-c));
		matrix[1][2] = (d.getY()*d.getZ()*(1-c))-(d.getX()*s);
		matrix[2][0] = (d.getZ()*d.getX()*(1-c))-(d.getY()*s);
		matrix[2][1] = (d.getZ()*d.getY()*(1-c))+(d.getX()*s);
		matrix[2][2] = c+((d.getZ()*d.getZ())*(1-c));

		return new Vector((matrix[0][0]*x)+(matrix[0][1]*y)+(matrix[0][2]*z),(matrix[1][0]*x)+(matrix[1][1]*y)+(matrix[1][2]*z),(matrix[2][0]*x)+(matrix[2][1]*y)+(matrix[2][2]*z));
	}

	@Override
	public String toString() {
		return "Vector(" + x + ", " + y + ", " + z + ")";
	}
}
