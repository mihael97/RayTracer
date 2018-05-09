package hr.fer.zemris.java.raytracer.RayCaster;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

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
 * Razred koji predstavlja crtanje RayCasterom. Za razliku od implementacije u
 * {@link RayCaster} ovdje se posao paralelizira putem {@link ForkJoinPool}
 * načina rekurzivno
 * 
 * @author Mihael
 */
public class RayCasterParallel {
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
	 * svaki dijelić ekrana(piksel). Također pokreće pararelizaciju putem
	 * {@link ForkJoinPool}
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

				ForkJoinPool pool = new ForkJoinPool();
				pool.invoke(new RayCasterJob(0, height, width, height, vertical, horizontal, red, blue, green,
						screenCorner, xAxis, yAxis, eye, scene));
				pool.shutdown();

				System.out.println("Izračuni gotovi...");
				observer.acceptResult(red, green, blue, requestNo);
				System.out.println("Dojava gotova...");
			}
		};
	}

	/**
	 * Razred koji predstavlja posao za iscrtavanje i bojanje sfera. Temelji se na
	 * {@link ForkJoinPool} i rekurziji
	 * 
	 * @author Mihael
	 *
	 */
	public static class RayCasterJob extends RecursiveAction {

		/**
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Minimalni y
		 */
		private int min;
		/**
		 * Makimalni y
		 */
		private int max;
		/**
		 * Širina
		 */
		private int width;
		/**
		 * Visina
		 */
		private int height;
		/**
		 * Vertikalna komponenta
		 */
		private double vertical;
		/**
		 * Horizintalna komponenta
		 */
		private double horizontal;
		/**
		 * Polje za crvenu boju RGB
		 */
		private short[] red;
		/**
		 * Polje za plavu boju RGB
		 */
		private short[] blue;
		/**
		 * Polje za zelenu boju RGB
		 */
		private short[] green;
		/**
		 * Kut ekrana
		 */
		Point3D screenCorner;
		/**
		 * X os
		 */
		private Point3D xAxis;
		/**
		 * Y os
		 */
		private Point3D yAxis;
		/**
		 * 'Oko',mjesto promatrača iz koje se pozicije gleda
		 */
		private Point3D eye;
		/**
		 * Scena
		 */
		Scene scene;
		/**
		 * Donji limit kada se ne ide u dalju rekurziju,djeljenje poslova
		 */
		private static int treshold = 16;

		/**
		 * @param min
		 *            - minimalni y
		 * @param max
		 *            - maksimalni y
		 * @param width
		 *            - širina
		 * @param height
		 *            - visina
		 * @param vertical
		 *            - vertikalno
		 * @param horizontal
		 *            - horizontalno
		 * @param red
		 *            - polje crvene
		 * @param blue
		 *            - polje plave
		 * @param green
		 *            - polje zelene
		 * @param screenCorner
		 *            - kut ekrana
		 * @param xAxis
		 *            - x os
		 * @param yAxis
		 *            - y os
		 * @param eye
		 *            - promatrač
		 * @param scene
		 *            - scena
		 * 
		 * @throws NullPointerException
		 *             - ako je neki od argumenata <code>null</code>
		 */
		public RayCasterJob(int min, int max, int width, int height, double vertical, double horizontal, short[] red,
				short[] blue, short[] green, Point3D screenCorner, Point3D xAxis, Point3D yAxis, Point3D eye,
				Scene scene) {
			super();
			this.min = min;
			this.max = max;
			this.width = width;
			this.height = height;
			this.vertical = vertical;
			this.horizontal = horizontal;
			this.red = red;
			this.blue = blue;
			this.green = green;
			this.screenCorner = Objects.requireNonNull(screenCorner);
			this.xAxis = Objects.requireNonNull(xAxis);
			this.yAxis = Objects.requireNonNull(yAxis);
			this.eye = Objects.requireNonNull(eye);
			this.scene = Objects.requireNonNull(scene);
		}

		/**
		 * Metoda koja rekurzivno dijeli poslove ako je broj poslova veći od donjeg
		 * limita
		 */
		@Override
		protected void compute() {
			if (max - min + 1 <= treshold) {
				computeDirect();
				return;
			}
			invokeAll(
					new RayCasterJob(min, min + (max - min) / 2, width, height, vertical, horizontal, red, blue, green,
							screenCorner, xAxis, yAxis, eye, scene),
					new RayCasterJob(min + (max - min) / 2, max, width, height, vertical, horizontal, red, blue, green,
							screenCorner, xAxis, yAxis, eye, scene));
		}

		/**
		 * Metoda koja poziva metodu za bojanje piksela
		 */
		private void computeDirect() {
			short[] rgb = new short[3];
			int offset = min * width;
			for (int y = min; y < max; y++) {
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
		}
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
		double[] forReturn = Arrays.copyOf(newRGB, newRGB.length);

		Objects.requireNonNull(scene);
		Objects.requireNonNull(closest);
		Objects.requireNonNull(ray);

		for (LightSource source : scene.getLights()) {
			Ray newRay = Ray.fromPoints(source.getPoint(), closest.getPoint());
			RayIntersection rayIntersection = findClosestIntersection(scene, newRay);

			if (rayIntersection != null && source.getPoint().sub(rayIntersection.getPoint()).norm()
					+ Math.pow(10, -3) >= source.getPoint().sub(closest.getPoint()).norm()) {
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
