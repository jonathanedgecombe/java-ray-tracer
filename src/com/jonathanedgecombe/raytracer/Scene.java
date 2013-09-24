package com.jonathanedgecombe.raytracer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public final class Scene {
	private final static int MAX_DEPTH = 4;

	private final double globalIllumination;

	private final List<Shape> lights = new ArrayList<>();
	private final List<Shape> objects = new ArrayList<>();

	private Camera camera;
	private final Vector skyColor;

	public Scene(Camera camera, double globalIllumination, Vector skyColor) {
		this.camera = camera;
		this.globalIllumination = globalIllumination;
		this.skyColor = skyColor;
	}

	public void addLight(Shape light) {
		lights.add(light);
	}

	public void addObject(Shape object) {
		objects.add(object);
	}

	public TraceResult traceRay(Ray ray, int depth) {
		Shape object = null;
		Vector color = new Vector(0.0, 0.0, 0.0);
		double distance = Double.POSITIVE_INFINITY;

		for (Shape testObject : objects) {
			Intersection intersection = testObject.intersect(ray);
			if (intersection.getType() != IntersectionType.NONE) {
				if (intersection.getDistance() < distance) {
					object = testObject;
					distance = intersection.getDistance();
				}
			}
		}

		if (object != null) {
			color = object.getColor().mul(globalIllumination);

			Vector intersection = ray.getOrigin().add(ray.getDirection().mul(distance));
			Vector normal = object.normalAtIntersection(intersection);

			

			for (Shape l : lights) {
				Light light = (Light) l;
				Vector direction = light.getCenter().sub(intersection).normalize();

				double lightDistance = intersection.sub(light.getCenter()).magnitude();
				Ray sray = new Ray(intersection.add(direction.div(10000)), direction);

				/**
				 * Check for shadows.
				 */
				boolean shadow = false;

				for (Shape testObject : objects) {
					Intersection test = testObject.intersect(sray);

					if (test.getType() != IntersectionType.NONE && test.getDistance() < lightDistance) {
						shadow = true;
						break;
					}
				}

				if (!shadow) {
					/**
					 * Diffuse shading
					 */
					double cosine = normal.dot(direction);
					if (cosine < 0) cosine = 0;
					color = color.add(object.getColor().mul(cosine).mul(light.getColor()));
	
					/**
					 * Specular shading
					 */
					if (object.getSpecularity() > 0.0) {
						Vector vr = direction.sub(normal.mul(cosine * 2));
						double cosSigma = ray.getDirection().dot(vr);
	
						if (cosSigma > 0) {
							color = color.add(light.getColor().mul(object.getSpecularity()).mul(Math.pow(cosSigma, 64)));
						}
					}
				}

				/**
				 * Reflections
				 */
				if (object.getReflectivity() > 0 && depth < MAX_DEPTH) {
					double dotnr = ray.getDirection().dot(normal);
					Vector rDirection = ray.getDirection().sub(normal.mul(2 * dotnr));
					Ray rRay = new Ray(intersection.add(rDirection.div(10000)), rDirection);

					TraceResult result = traceRay(rRay, depth+1);

					color = color.mul(1-object.getReflectivity());
					color = color.add(result.getColor().mul(object.getReflectivity()));
				}
			}

			color = color.max(1.0);
		} else {
			color = skyColor;
		}

		return new TraceResult(color, object);
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public void render(Graphics g, int startX, int startY, int endX, int endY) {
		for (int j = startY; j < endY; j++) {
			for (int i = startX; i < endX; i++) {
				int count = 0;
				Vector c = new Vector(0, 0, 0);

				/**
				 * Anti-aliasing, woo!
				 */
				for (double dx = 0; dx < 1; dx += 0.25) {
					for (double dz = 0; dz < 1; dz += 0.25) {
						Vector vRotate = camera.getDirection().rotate(new Vector(1, 0, 0), (camera.getVFov()*(j+dz)/camera.getHeight()) - (camera.getVFov()/2));
						Vector direction = vRotate.rotate(new Vector(0, 1, 0), (camera.getFov()*(i+dx)/camera.getWidth()) - (camera.getFov()/2)).normalize();
		
						TraceResult result = traceRay(new Ray(camera.getPoint(), direction), 0);
						c = c.add(result.getColor());
						count++;
					}
				}

				c = c.div(count);

				g.setColor(new Color((float) c.getX(), (float) c.getY(), (float) c.getZ()));
				g.fillRect(i, j, 1, 1);
			}
		}
	}
}
