package com.jonathanedgecombe.raytracer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Scene {
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
				double shadow = 0;

				for (Shape testObject : objects) {
					Intersection test = testObject.intersect(sray);

					if (test.getType() != IntersectionType.NONE && test.getDistance() < lightDistance) {
						shadow += (1 - object.getTransparency()) * (1 - shadow);
					}
				}

				shadow *= 0.66;

				if (shadow < 1) {
					Vector colAdd = new Vector(0, 0, 0);
					/**
					 * Diffuse shading
					 */
					double cosine = normal.dot(direction);
					if (cosine < 0) cosine = 0;
					colAdd = color.add(object.getColor().mul(cosine).mul(light.getColor()));
	
					/**
					 * Specular shading
					 */
					if (object.getSpecularity() > 0.0) {
						Vector vr = direction.sub(normal.mul(cosine * 2));
						double cosSigma = ray.getDirection().dot(vr);
	
						if (cosSigma > 0) {
							colAdd = color.add(light.getColor().mul(object.getSpecularity()).mul(Math.pow(cosSigma, 64)));
						}
					}

					color = color.add(colAdd.mul(1 - shadow));
				}

				/**
				 * Reflections
				 */
				if (object.getReflectivity() > 0 && depth < camera.getMaxDepth()) {
					double dotnr = ray.getDirection().dot(normal);
					Vector rDirection = ray.getDirection().sub(normal.mul(2 * dotnr));
					Ray rRay = new Ray(intersection.add(rDirection.div(10000)), rDirection);

					TraceResult result = traceRay(rRay, depth+1);

					color = color.mul(1-object.getReflectivity());
					color = color.add(result.getColor().mul(object.getReflectivity()));
				}

				if (object.getTransparency() > 0) {
					color = color.mul(1 - object.getTransparency());

					Vector n2 = null;

					if (normal.dot(direction) < 0) {
						Vector s = new Vector(0, 0, 0).sub(normal);
						n2 = ray.getDirection().add(s.mul(object.getRefractiveIndex() - 1));
					} else {
						n2 = ray.getDirection().add(normal.mul(1 - object.getRefractiveIndex()));
					}
					

					Ray tRay = new Ray(intersection.add(n2.div(10000)), n2);
					TraceResult result = traceRay(tRay, depth+1);

					color = color.add(result.getColor().mul(object.getTransparency()));
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
		double f = 1/camera.getAperture();
		Random rng = new Random();

		for (int j = startY; j < endY; j++) {
			for (int i = startX; i < endX; i++) {
				int count = 0;
				Vector c = new Vector(0, 0, 0);

				/**
				 * Anti-aliasing, woo!
				 */
				for (double dx = 0; dx < 1; dx += 1.0/camera.getSupSamples()) {
					for (double dz = 0; dz < 1; dz += 1.0/camera.getSupSamples()) {
						Vector vRotate = camera.getDirection().rotate(new Vector(1, 0, 0), (camera.getVFov()*(j+dz)/camera.getHeight()) - (camera.getVFov()/2));
						Vector direction = vRotate.rotate(new Vector(0, 1, 0), (camera.getFov()*(i+dx)/camera.getWidth()) - (camera.getFov()/2)).normalize();
		
						//TraceResult result = traceRay(new Ray(camera.getPoint(), direction), 0);
						//c = c.add(result.getColor());
						//count++;
						Vector aim = camera.getPoint().add(direction.mul(camera.getFocus()));

						for (int t = 0; t < camera.getSubSamples(); t++) {
							double rx = rng.nextDouble();
							double ry = rng.nextDouble();

							rx = (rx - 0.5) * f;
							ry = (ry - 0.5) * f;

							Vector start = camera.getPoint();
							Vector dir = direction;
							if (camera.getSubSamples() > 1) {
								start = camera.getPoint().add(new Vector(rx, ry, 0));
								dir = aim.sub(start).normalize();
							}

							//System.out.println(direction + "\t" + dir);
							TraceResult result = traceRay(new Ray(start, dir), 0);
							c = c.add(result.getColor());
							count++;
						}
					}
				}

				/*Vector vRotate = camera.getDirection().rotate(new Vector(1, 0, 0), (camera.getVFov()*j/camera.getHeight()) - (camera.getVFov()/2));
				Vector direction = vRotate.rotate(new Vector(0, 1, 0), (camera.getFov()*i/camera.getWidth()) - (camera.getFov()/2)).normalize();
				Vector aim = camera.getPoint().add(direction.mul(camera.getFocus()));

				for (int t = 0; t < SAMPLES; t++) {
					double dx = rng.nextDouble();
					double dy = rng.nextDouble();

					dx = (dx - 0.5) * f;
					dy = (dy - 0.5) * f;

					Vector start = camera.getPoint().add(new Vector(dx, dy, 0));
					Vector dir = aim.sub(start).normalize();

					//System.out.println(direction + "\t" + dir);
					TraceResult result = traceRay(new Ray(start, dir), 0);
					c = c.add(result.getColor());
					count++;
				}*/

				c = c.div(count);

				g.setColor(new Color((float) c.getX(), (float) c.getY(), (float) c.getZ()));
				g.fillRect(i, j, 1, 1);
			}
		}
	}
}
