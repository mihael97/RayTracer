package fer.zemris.java.fractals.Newton;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.math.Complex;
import hr.fer.zemris.math.ComplexPolynomial;
import hr.fer.zemris.math.ComplexRootedPolynomial;

/**
 * Razred koji predstavlja model izrade Newtonowih fraktala. Svaki sljedeći član
 * računa se kao razlika prijašnjeg člana i omjera vrijednosti polinoma i
 * njegove prve derivacija u određenoj točci(vektoru)
 * 
 * @author Mihael
 *
 */
public class NewtonFractals {

	/**
	 * Oblik polinama sa korijenima(nul točkama)
	 */
	private static ComplexRootedPolynomial rooted;
	/**
	 * Oblik polinoma s koeficijentima
	 */
	private static ComplexPolynomial polynom;
	/**
	 * Derivirani polinom
	 */
	private static ComplexPolynomial derivation;

	/**
	 * Glavni program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer.");
		System.out.println("Please enter at least two roots, one root per line. Enter 'done' when done.");

		List<Complex> list = new ArrayList<>();
		int index = 1;

		try (Scanner sc = new Scanner(System.in)) {
			while (true) {
				try {
					System.out.println("Root " + index + "> ");
					String input = sc.nextLine();

					if (input.toUpperCase().equals("DONE"))
						break;

					list.add(makeComplex(input));
					index++;
				} catch (IllegalArgumentException e) {
					System.err.println(e.getMessage());
				}
			}

			System.out.println("Roots are: ");
			for (Complex complex : list) {
				System.out.println(complex.toString());
			}

			rooted = new ComplexRootedPolynomial(list);
			polynom = rooted.toComplexPolynom();
			derivation = polynom.derive();

			System.out.println("Image of fractal will appear shortly. Thank you.");
			FractalViewer.show(new Producer());
		}
	}

	/**
	 * Metoda stvara kompleksni broj iz pročitanog sadržaja
	 * 
	 * @param input
	 *            - pročitani sadržaj
	 * @return kompleksni broj
	 */
	private static Complex makeComplex(String input) {
		try {
			if (!input.contains("i")) {
				return new Complex(Double.parseDouble(input), 0);
			}

			double[] arguments = getParts(input);

			return new Complex(arguments[0], arguments[1]);

		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Argument \'" + input + "\' cannot be parsed to Double number!");
		}
	}

	/**
	 * Metoda koja iz pročitanog sadržaja stvara imaginarni i realni dio
	 * 
	 * @param input
	 *            - pročitani sadržaj
	 * @return polje gdje je na prvoj poziciji relani,a drugoj imaginarni dio
	 */
	private static double[] getParts(String input) {
		double[] forReturn = new double[2];
		StringBuilder builder = new StringBuilder();
		char[] array = input.toCharArray();

		for (int i = 0; i < array.length; i++) {
			char c = array[i];

			if (Character.isWhitespace(c))
				continue;

			if (c == 'i') {
				if (builder.length() == 0 || builder.length() == 1) {
					forReturn[0] = 0;
				} else if (builder.length() == 2) {
					System.out.println(builder.toString().charAt(0));
					forReturn[0] = Double.parseDouble(String.valueOf(builder.toString().charAt(0)));
					builder = new StringBuilder(builder.toString().substring(1));
				} else {
					forReturn[0] = Double.parseDouble(builder.toString().substring(0, builder.length() - 1));
					builder = new StringBuilder(builder.toString().substring(builder.length() - 1));
				}

				if (i + 1 < array.length)
					c = array[++i];
				else
					break;
			}

			builder.append(c);
		}

		forReturn[1] = builder.length() == 0 ? 1
				: builder.length() == 1 && builder.toString().equals("-") ? -1 : Double.parseDouble(builder.toString());

		return forReturn;

	}

	/**
	 * Razred koji predstavlja posao kojeg obavlja program(crtanje). Zadatak je da
	 * se koordinate svakog piksela ekrana skaliraju te da se od toga napravi novi
	 * kompleksan broj dok se svaki piksel oboji ovisno o divergenciji novonastalog
	 * broja
	 * 
	 * @author Mihael
	 *
	 */
	public static class CalculatingJob implements Callable<Void> {
		/**
		 * Minimalno realno
		 */
		double reMin;
		/**
		 * Maksimalno realno
		 */
		double reMax;
		/**
		 * Minimalno imaginarno
		 */
		double imMin;
		/**
		 * Maksimalno imaginarno
		 */
		double imMax;
		/**
		 * Širina
		 */
		int width;
		/**
		 * Visina
		 */
		int height;
		/**
		 * Y minimalno
		 */
		int yMin;
		/**
		 * Y maksimalno
		 */
		int yMax;
		/**
		 * Red polinoma
		 */
		int m;
		/**
		 * Podaci koji određuju u koju će se boju obojati koji piksel
		 */
		short[] data;

		/**
		 * Konstruktor koji inicijalizira podatke
		 * 
		 * @param reMin
		 *            - minimalno relano
		 * @param reMax
		 *            - maksimalno realno
		 * @param imMin
		 *            - minimalno imaginarno
		 * @param imMax
		 *            - maksimakno imaginarno
		 * @param width
		 *            - širina
		 * @param height
		 *            - visina
		 * @param yMin
		 *            - y minimalno
		 * @param yMax
		 *            - y maksimalno
		 * @param m
		 *            - red polinoma
		 * @param data
		 *            - podaci o bojanju piksela
		 */
		public CalculatingJob(double reMin, double reMax, double imMin, double imMax, int width, int height, int yMin,
				int yMax, int m, short[] data) {
			super();
			this.reMin = reMin;
			this.reMax = reMax;
			this.imMin = imMin;
			this.imMax = imMax;
			this.width = width;
			this.height = height;
			this.yMin = yMin;
			this.yMax = yMax;
			this.m = m;
			this.data = data;
		}

		/**
		 * Metoda čijim se pozivanjem računa sljedeći član. Stvaranja članova traje sve
		 * dok je razlika priješnjeg i sadašnjeg člana veća od 0.001
		 */
		@Override
		public Void call() {

			int position = yMin * width;
			for (int y = yMin; y <= yMax; y++) {
				for (int x = 0; x < width; x++) {
					double creal = x * (reMax - reMin) / (width - 1) + reMin;
					double cimaginary = (height - 1 - y) * (imMax - imMin) / (height - 1) + imMin;

					Complex zn = new Complex(creal, cimaginary), zn1;
					int iter = 0;
					double module = 0, limit = Math.pow(16, 3), convergenceTreshold, treshold;
					convergenceTreshold = treshold = Double.valueOf(0.001);

					do {
						Complex numerator = polynom.apply(zn);
						Complex denominator = derivation.apply(zn);
						Complex fraction = numerator.divide(denominator);
						zn1 = zn.sub(fraction);
						module = zn1.sub(zn).module();
						zn = zn1;
						iter++;
					} while (module > convergenceTreshold && iter < limit);

					int index = rooted.indexOfClosestRootFor(zn1, treshold);
					data[position++] = (index == -1) ? 0 : (short) (index + 1);

				}
			}

			return null;
		}
	}

	/**
	 * Varijabla koja predstavlja izvođača i dretve za višedretvenost
	 */
	private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
			new ThreadFactory() {

				@Override
				public Thread newThread(Runnable job) {
					Thread thread = Executors.defaultThreadFactory().newThread(job);
					thread.setDaemon(true);
					return thread;
				}
			});

	/**
	 * Razred koji predstavlja tvornicu poslova,tj mjesto od koje se poziva crtanje
	 * i bojanje kao i raspodjela poslova na procesorske jedinice
	 * 
	 * @author Mihael
	 *
	 */
	public static class Producer implements IFractalProducer {

		/**
		 * Metoda iz koje se poziva iscrtavanje fraktala
		 * 
		 * @param reMin
		 *            - minimalno realno
		 * @param reMax
		 *            - realno maksimalno
		 * @param imMax
		 *            - imaginarno maksimalno
		 * @param imMin
		 *            - imaginarno minimalno
		 * @param width
		 *            - širina
		 * @param height
		 *            - visina
		 * @param requestNo
		 *            - broj zahtjeva
		 * @param observer
		 *            - promatrač
		 */
		@Override
		public void produce(double reMin, double reMax, double imMin, double imMax, int width, int height,
				long requestNo, IFractalResultObserver observer) {
			short[] data = new short[width * height];
			final int numberOfTracks = 8 * Runtime.getRuntime().availableProcessors();
			int yByTrack = height / numberOfTracks;

			List<Future<Void>> results = new ArrayList<>();

			for (int i = 0; i < numberOfTracks; i++) {
				int yMin = i * yByTrack;
				int yMax = (i + 1) * yByTrack - 1;
				if (i == numberOfTracks - 1) {
					yMax = height - 1;
				}

				CalculatingJob job = new CalculatingJob(reMin, reMax, imMin, imMax, width, height, yMin, yMax,
						polynom.order() + 1, data);
				results.add(executor.submit(job));
			}

			for (Future<Void> job : results) {
				try {
					job.get();
				} catch (InterruptedException | ExecutionException e) {
				}
			}

			observer.acceptResult(data, (short) (polynom.order() + 1), requestNo);
		}

	}

}
