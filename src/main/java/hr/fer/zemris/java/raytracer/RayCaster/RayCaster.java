package hr.fer.zemris.java.raytracer.RayCaster;

import java.util.Arrays;
import java.util.Objects;

import hr.fer.zemris.java.raytracer.model.GraphicalObject;
import hr.fer.zemris.java.raytracer.model.IRayTracerProducer;
import hr.fer.zemris.java.raytracer.model.IRayTracerResultObserver;
import hr.fer.zemris.java.raytracer.model.LightSource;
import hr.fer.zemris.java.raytracer.model.Point3D;
import hr.fer.zemris.java.raytracer.model.Ray;
import hr.fer.zemris.java.raytracer.model.RayIntersection;
import hr.fer.zemris.java.raytracer.model.Scene;
import hr.fer.zemris.java.raytracer.viewer.RayTracerViewer;

/**
 * Razred koji predstavlja crtanje RayCasterom
 * 
 * @author Mihael
 *
 */
public class RayCaster {

	/**
	 * Glavni program
	 * 
	 * @param args
	 *            - ne koristi se
	 */
	public static void main(String[] args) {
		RayTracerViewer.show(getIRayTracerProducer(), new Point3D(10, 0, 0), new Point3D(0, 0, 0),
				new Point3D(0, 0, 10), 20, 20);
	}

	/**
	 * Metoda koja predstavlja inicijalizaciju posla i poziva izračunavanje boje za
	 * svaki dijelić ekrana(piksel)
	 * 
	 * @return {@link IRayTracerProducer} objekt
	 */
	private static IRayTracerProducer getIRayTracerProducer() {
		return new IRayTracerProducer() {
			@Override
			public void produce(Point3D eye, Point3D view, Point3D viewUp, double horizontal, double vertical,
					int width, int height, long requestNo, IRayTracerResultObserver observer) {
				System.out.println("Započinjem izračune...");
				short[] red = new short[width * height];
				short[] green = new short[width * height];
				short[] blue = new short[width * height];

				Point3D OG = view.sub(eye).modifyNormalize();
				@SuppressWarnings("unused")
				Point3D zAxis = null;
				Point3D yAxis = viewUp.normalize().sub(OG.scalarMultiply(viewUp.normalize().scalarProduct(OG)));
				Point3D xAxis = OG.vectorProduct(yAxis).normalize();
				Point3D screenCorner = view.sub(xAxis.scalarMultiply(horizontal / 2.0))
						.add(yAxis.scalarMultiply(vertical / 2.0));
				Scene scene = RayTracerViewer.createPredefinedScene();

				short[] rgb = new short[3];
				int offset = 0;
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						Point3D screenPoint = screenCorner
								.add(xAxis.scalarMultiply(horizontal).scalarMultiply(x / (width - 1.0)))
								.sub(yAxis.scalarMultiply(y / (height - 1.0)).scalarMultiply(vertical));
						Ray ray = Ray.fromPoints(eye, screenPoint);
						tracer(scene, ray, rgb);
						red[offset] = rgb[0] > 255 ? 255 : rgb[0];
						green[offset] = rgb[1] > 255 ? 255 : rgb[1];
						blue[offset] = rgb[2] > 255 ? 255 : rgb[2];
						offset++;
					}
				}
				System.out.println("Izračuni gotovi...");
				observer.acceptResult(red, green, blue, requestNo);
				System.out.println("Dojava gotova...");
			}
		};
	}

	/**
	 * Metoda koja pronalazi najbliži objekt kojeg dodiruje zraka(ako postoji) i
	 * poziva računanje boja
	 * 
	 * @param scene
	 *            - scena
	 * @param ray
	 *            - zraka
	 * @param rgb
	 *            - polje komponenta RGB boje
	 * 
	 * @throws NullPointerException
	 *             - ako je neki od argumenata <code>null</code>
	 */
	protected static void tracer(Scene scene, Ray ray, short[] rgb) {

		Objects.requireNonNull(ray);
		Objects.requireNonNull(scene);

		double[] newRGB = new double[3];
		newRGB[0] = 15;
		newRGB[1] = 15;
		newRGB[2] = 15;

		RayIntersection closest = findClosestIntersection(scene, ray);
		if (closest != null) {
			newRGB = determineColorFor(scene, closest, ray, newRGB);
		}

		rgb[0] = (short) newRGB[0];
		rgb[1] = (short) newRGB[1];
		rgb[2] = (short) newRGB[2];
	}

	/**
	 * Metoda koja ovisno o svakom svjetlosnom izvoru računa boju kojom će se piksel
	 * obojati
	 * 
	 * @param scene
	 *            - scena
	 * @param closest
	 *            - opisnik o najbližem objektu
	 * @param ray
	 *            - zraka
	 * @param newRGB
	 *            - polje komponenta RGB boje
	 * @return novo polje RGB boje,eventualno modificirano
	 * 
	 * @throws NullPointerException
	 *             - ako su najbliži,scena ili zraka null
	 */
	private static double[] determineColorFor(Scene scene, RayIntersection closest, Ray ray, double[] newRGB) {

		Objects.requireNonNull(scene);
		Objects.requireNonNull(closest);
		Objects.requireNonNull(ray);

		double[] forReturn = Arrays.copyOf(newRGB, newRGB.length);

		for (LightSource source : scene.getLights()) {
			Ray newRay = Ray.fromPoints(source.getPoint(), closest.getPoint());
			RayIntersection rayIntersection = findClosestIntersection(scene, newRay);

			if (rayIntersection != null && source.getPoint().sub(rayIntersection.getPoint()).norm()
					+ Math.pow(10, -2) >= source.getPoint().sub(closest.getPoint()).norm()) {
				changeColors(forReturn, rayIntersection, source, ray);
			}
		}

		return forReturn;
	}

	/**
	 * Metoda koja mjenja komponente RGB boje ovisno o poziciji. Komponentama se
	 * dodaju zrcalna i ambijentna komponenta
	 * 
	 * @param forReturn
	 *            - polje komponenta RGB boje
	 * @param rayIntersection
	 *            - opsinik o dodiru
	 * @param source
	 *            - svjetlosni izvor
	 * @param ray
	 *            - zraka
	 * @throws NullPointerException
	 *             - ako je neki od argumenata <code>null</code>
	 */
	private static void changeColors(double[] forReturn, RayIntersection rayIntersection, LightSource source, Ray ray) {

		Objects.requireNonNull(source);
		Objects.requireNonNull(ray);
		Objects.requireNonNull(rayIntersection);

		// to every component(R,G,B) we are adding diffuse and reflective component of
		// color

		// diffuse
		Point3D normal = rayIntersection.getNormal();
		Point3D wayToSource = source.getPoint().sub(rayIntersection.getPoint());

		// reflective
		Point3D v = ray.start.sub(rayIntersection.getPoint());
		Point3D plane = normal.scalarMultiply(wayToSource.scalarProduct(normal));
		Point3D r = plane.add(plane.negate().add(wayToSource).scalarMultiply(-1));
		double reflective = r.normalize().scalarProduct(v.normalize());

		if (reflective >= 0) {
			reflective = Math.pow(reflective, rayIntersection.getKrn());
		} else {
			reflective = 0;
		}

		forReturn[0] += source.getR()
				* (rayIntersection.getKdr() * Math.max(wayToSource.normalize().scalarProduct(normal), 0));
		forReturn[0] += source.getR() * rayIntersection.getKrr() * reflective;

		forReturn[1] += source.getG()
				* (rayIntersection.getKdg() * Math.max(wayToSource.normalize().scalarProduct(normal), 0));
		forReturn[1] += source.getG() * rayIntersection.getKrg() * reflective;

		forReturn[2] += source.getB()
				* (rayIntersection.getKdb() * Math.max(wayToSource.normalize().scalarProduct(normal), 0));
		forReturn[2] += source.getB() * rayIntersection.getKrb() * reflective;

	}

	/**
	 * Metoda koja pronalazi najbliže diralište zrake i nekog objekta. Ako ono ne
	 * postoji vraća <code>null</code>
	 * 
	 * @param scene
	 *            - scena
	 * @param ray
	 *            - zraka
	 * @return opisnik o dodiru {@link RayIntersection}
	 * 
	 * @throws NullPointerException
	 *             - ako je neki od argumenata <code>null</code>
	 */
	private static RayIntersection findClosestIntersection(Scene scene, Ray ray) {

		Objects.requireNonNull(ray);
		Objects.requireNonNull(scene);

		RayIntersection closest = null;

		for (GraphicalObject graphical : scene.getObjects()) {
			RayIntersection returned = graphical.findClosestRayIntersection(ray);

			if (returned != null && (closest == null || closest.getDistance() > returned.getDistance())) {
				closest = returned;
			}

		}

		return closest;
	}

}
