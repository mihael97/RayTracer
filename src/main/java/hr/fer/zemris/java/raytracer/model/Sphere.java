package hr.fer.zemris.java.raytracer.model;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.Objects;

/**
 * Razred koji predstavlja model sfere
 * 
 * @author Mihael
 *
 */
public class Sphere extends GraphicalObject {

	/**
	 * Centralna točka sfere
	 */
	Point3D center;

	/**
	 * Radijus
	 */
	private double radius;

	/**
	 * Crvena difuzna komponenta
	 */
	private double kdr;

	/**
	 * Zelena difuzna komponenta
	 */
	private double kdg;

	/**
	 * Plava difuzna komponenta
	 */
	private double kdb;
	/**
	 * Crvena ambijentna komponenta
	 */
	private double krr;
	/**
	 * Zelena ambijentnaa komponenta
	 */
	private double krg;
	/**
	 * Plava ambijentna komponenta
	 */
	private double krb;

	/**
	 * Koeficijent potenciranja
	 */
	private double krn;

	/**
	 * @param center
	 *            - centar
	 * @param radius
	 *            -radijus
	 * @param kdr
	 *            - crvena difuzna
	 * @param kdg
	 *            - zelena difuzna
	 * @param kdb
	 *            - plava difuzna
	 * @param krr
	 *            - crvena ambijentna
	 * @param krg
	 *            - zelena ambijentna
	 * @param krb
	 *            - plava ambijentna
	 * @param krn
	 *            - koeficijent potenciranja
	 * 
	 * @throws NullPointerException
	 *             - ako je točka centra <code>null</code>
	 */
	public Sphere(Point3D center, double radius, double kdr, double kdg, double kdb, double krr, double krg, double krb,
			double krn) {
		this.center = Objects.requireNonNull(center);
		this.radius = radius;
		this.kdr = kdr;
		this.kdg = kdg;
		this.kdb = kdb;
		this.krr = krr;
		this.krg = krg;
		this.krb = krb;
		this.krn = krn;
	}

	/**
	 * Metoda koja provjerava dodiruje li se zraka i sfera u nekoj točci. Ako se ne
	 * diraju,vraća <code>null</code>
	 * 
	 * @return opisnik o dodiru ako postoji,inače <code>null</code>
	 */
	@Override
	public RayIntersection findClosestRayIntersection(Ray ray) {
		double d = -ray.direction.normalize().scalarProduct(ray.start.sub(center));
		double underRoot = pow(d, 2) - pow(ray.start.sub(center).norm(), 2) + pow(radius, 2);

		if (underRoot < 0) { // ray doesn't touch sphere
			return null;
		}

		Point3D firstPoint, secondPoint, point; // firstPoint and secondPoint-intersections

		firstPoint = ray.start.add(ray.direction.scalarMultiply(d + sqrt(underRoot)));
		secondPoint = ray.start.add(ray.direction.scalarMultiply(d - sqrt(underRoot)));

		// we are looking for closer intersection
		if ((ray.start.sub(firstPoint).norm()) > (ray.start.sub(secondPoint).norm())) {
			point = secondPoint;
		} else {
			point = firstPoint;
		}

		return new RayIntersection(point, ray.start.sub(point).norm(), point.sub(center).norm() > radius) {

			@Override
			public Point3D getNormal() {
				return this.getPoint().sub(center).normalize();
			}

			@Override
			public double getKrr() {
				return krr;
			}

			@Override
			public double getKrn() {
				return krn;
			}

			@Override
			public double getKrg() {
				return krg;
			}

			@Override
			public double getKrb() {
				return krb;
			}

			@Override
			public double getKdr() {
				return kdr;
			}

			@Override
			public double getKdg() {
				return kdg;
			}

			@Override
			public double getKdb() {
				return kdb;
			}
		};
	}

}
