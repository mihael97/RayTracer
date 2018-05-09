package hr.fer.zemris.math;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

/**
 * Razred koji implementira model kompleksnog broja sa realnim i imaginarim
 * dijelom i osnovnom funkcionalnošću
 * 
 * @author Mihael
 *
 */
public class Complex {
	/**
	 * Oblik broja koji je nula
	 */
	public static final Complex ZERO = new Complex(0, 0);
	/**
	 * Oblik broja koji je jedinične duljine u realnom smjeru
	 */
	public static final Complex ONE = new Complex(1, 0);
	/**
	 * Oblik broja koji je jedinična u negativnom smjeru realne osi
	 */
	public static final Complex ONE_NEG = new Complex(-1, 0);
	/**
	 * Oblik broja koji je jedinične duljine u imaginarnom smjeru
	 */
	public static final Complex IM = new Complex(0, 1);
	/**
	 * Oblik broja koji je jedinična u negativnom smjeru imaginarne osi
	 */
	public static final Complex IM_NEG = new Complex(0, -1);

	/**
	 * Nepromjenjivi realni dio broja
	 */
	private final double real;
	/**
	 * Nepromjenjivi imaginarni dio broja
	 */
	private final double imaginary;

	/**
	 * Konstuktor koji stvara novi broj kojem su realni i imaginarni dio nula
	 */
	public Complex() {
		this(0, 0);
	}

	/**
	 * Konstruktor koji stvara novi kompleksni broj sa realnim i imaginarnim brojem
	 * 
	 * @param re
	 *            - realni dio
	 * @param im
	 *            - imaginarni dio
	 */
	public Complex(double re, double im) {
		this.real = re;
		this.imaginary = im;
	}

	/**
	 * Metoda računa modul kompleksnog broja
	 * 
	 * @return
	 */
	public double module() {
		return Math.sqrt(pow(real, 2) + pow(imaginary, 2));
	}

	/**
	 * Metoda koj množi trenutni broj sa argumentom. Rezultat je novi vektor
	 * 
	 * @param c
	 *            - drugi vektor
	 * @return vektor umnoška dva vektora
	 * 
	 * @throws NullPointerException
	 *             - ako je vektor argumenta <code>null</code>
	 */
	public Complex multiply(Complex c) {
		Objects.requireNonNull(c);

		return new Complex(this.real * c.real - this.imaginary * c.imaginary,
				this.imaginary * c.real + this.real * c.imaginary);
	}

	/**
	 * Metoda trenutni vektor dijeli sa vektorom danim preko argumenta
	 * 
	 * @param c
	 *            - nazivnik
	 * @return podijeleni vektor
	 * 
	 * @throws NullPointerException
	 *             - ako je argument <code>null</code>
	 * @throws IllegalArgumentException
	 *             - ako je nazivnik nula
	 */
	public Complex divide(Complex c) {
		Objects.requireNonNull(c);

		double denominator = c.real * c.real + c.imaginary * c.imaginary;

		if (denominator == 0) {
			throw new IllegalArgumentException("Denominator is zero!");
		}

		return new Complex((this.real * c.real + this.imaginary * c.imaginary) / denominator,
				(this.imaginary * c.real - this.real * c.imaginary) / denominator);
	}

	/**
	 * Metoda koja trenutnom vektou zbraja vektor dan preko argumenta
	 * 
	 * @param c
	 *            - vektor kojeg želimo zbrojiti
	 * @return novi vektor,zbrojen
	 * 
	 * @throws NullPointerException
	 *             - ako je argument <code>null</code>
	 */
	public Complex add(Complex c) {
		return new Complex(this.real + c.real, this.imaginary + c.imaginary);
	}

	/**
	 * Metoda koja od trenutnog vektora oduzima vektor dan preko argumenta
	 * 
	 * @param c
	 *            - vektor kojeg želimo oduzeti
	 * @return novi vektor,oduzet
	 * 
	 * @throws NullPointerException
	 *             - ako je argument <code>null</code>
	 */
	public Complex sub(Complex c) {
		Objects.requireNonNull(c);
		return new Complex(this.real - c.real, this.imaginary - c.imaginary);
	}

	/**
	 * Metoda vraća negirani vektor tako da promjeni predznak svakoj koordinati
	 * 
	 * @return negirani vektor
	 */
	public Complex negate() {
		return new Complex(-this.real, -this.imaginary);
	}

	/**
	 * Metoda koja vraća potenciran trenutni vektor
	 * 
	 * @param n
	 *            - potencija
	 * @return potenciran vektor
	 * 
	 * @throws NullPointerException
	 *             - ako je argument manji od nule
	 */
	public Complex power(int n) {
		if (n < 0) {
			throw new IllegalArgumentException("Argument mora biti nenagitvan. Zadan je " + n);
		}
		double angle = Math.atan2(this.imaginary, this.real);
		return new Complex(pow(module(), n) * cos(n * angle), pow(module(), n) * sin(n * angle));

	}

	/**
	 * Metoda koja vraća sve korijene. Argument mora biti veći od nule
	 * 
	 * @param n
	 *            - broj korijena koje želimo
	 * @return lista korijena
	 * 
	 * @throws IllegalArgumentException
	 *             - ako je argument manji od nule
	 */
	public List<Complex> root(int n) {
		if (n <= 0) {
			throw new IllegalArgumentException("Argument mora biti pozitivan. Zadan je " + n);
		}
		List<Complex> list = new ArrayList<>();
		double angle = Math.atan2(this.imaginary, this.real);

		for (int k = 0; k < n; k++) {
			list.add(new Complex(pow(module(), (double) 1 / 2) * cos((angle + 2 * k * Math.PI) / n),
					pow(module(), (double) 1 / 2) * sin((angle + 2 * k * Math.PI) / n)));
		}

		return list;

	}

	/**
	 * Metoda vraća znakovnu reprezentaciju kompleksnog broja
	 * 
	 * @return reprezentacija broja u obliju x+yi
	 */
	@Override
	public String toString() {

		if (real == 0)
			return imaginary + "i";
		if (imaginary == 0)
			return String.valueOf(real);

		String imag = (imaginary < 0) ? Double.valueOf(imaginary).toString() : ("+" + imaginary);
		return real + imag + "i";
	}

	/**
	 * Metoda vraća realni dio kompleksnog broja
	 * 
	 * @return realni dio broja
	 */
	public double getRealPart() {
		return real;
	}

	/**
	 * Metoda vraća imaginarni dio broja kompleksnog broja
	 * 
	 * @return imaginarni dio komplesknog broja
	 */
	public double getImaginaryPart() {
		return imaginary;
	}

}
