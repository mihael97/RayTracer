package hr.fer.zemris.math;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

/**
 * Razred koji predstavlja model kompleksnog polinoma zapisana u obliku umnoška
 * nul-točaka izraza
 * 
 * @author Mihael
 *
 */
public class ComplexRootedPolynomial {

	/**
	 * Korijeni kompleksnog broja
	 */
	public List<Complex> roots;

	/**
	 * Konstruktor koji stavra novi polinom od korijena kompleksnog broja
	 * 
	 * @param roots
	 *            - korijeni polinoma(nul-točke)
	 * 
	 * @throws NullPointerException
	 *             - ako je argument null
	 * @throws IllegalArgumentException
	 *             - ako je broj elemenata u listi nula
	 */
	public ComplexRootedPolynomial(List<Complex> roots) {
		Objects.requireNonNull(roots);

		if (roots.size() == 0) {
			throw new IllegalArgumentException("List size is 0!");
		}

		this.roots = new ArrayList<>(roots);
	}

	/**
	 * Metoda koja u trenutni polinama uvrštava argument
	 * 
	 * @param z
	 *            - vrijendost varijable u polinomu
	 * @return novi kompleksni broj kao vrjednost izraza
	 * 
	 * @throws NullPointerException
	 *             - ako je argument null
	 */
	public Complex apply(Complex z) {
		Objects.requireNonNull(z);

		if (roots.size() == 0)
			throw new IllegalArgumentException("Roots length can't be zero!");

		Complex result = Complex.ONE;

		for (Complex root : roots) {
			result.multiply(z.divide(root));
		}

		return result;
	}

	/**
	 * Metoda trenutni polinom pretvara u općenitiji zapis funkcije. Budući polinom
	 * neće se više prezentirati kao umnožak nul-točaka,nego sa koeficijentima i
	 * potencijma reda
	 * 
	 * @return novi polinom
	 */
	public ComplexPolynomial toComplexPolynom() {
		ComplexPolynomial polynom = new ComplexPolynomial(new Complex[] { Complex.ONE, roots.get(0) });

		for (int i = 1, length = roots.size(); i < length; i++) {
			polynom = polynom.multiply(new ComplexPolynomial(new Complex[] { Complex.ONE, roots.get(i) }));
		}

		return polynom;
	}

	/**
	 * Metoda vraća znakovnu reprezentaciju polinoma u obliku umnoška nul-točaka
	 * 
	 * @return znakovni izraz polinoma
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (Complex root : roots) {
			builder.append("(");
			builder.append("z").append("-(").append(root.negate().toString());
			builder.append("))");
		}

		return builder.toString();
	}

	/**
	 * Metoda vraća poziciju korijena koji ima najmanju udaljenost od broja zadanog
	 * preko argumenta,a da je ta udaljenost manja od argumenta treshold. Ako takav
	 * broj ne postoji,vraća se -1
	 * 
	 * @param z
	 *            - broj za usporedbu
	 * @param treshold
	 *            - granična udaljenost
	 * @return pozicija najbližeg korijena
	 * 
	 * @throws NullPointerException
	 *             - ako je argument broja null
	 */
	public int indexOfClosestRootFor(Complex z, double treshold) {

		Objects.requireNonNull(z);

		int index = -1;
		double difference = -1;

		for (int i = 0, length = roots.size(); i < length; i++) {

			double abs = sqrt(pow(z.getImaginaryPart() - roots.get(i).getImaginaryPart(), 2)
					+ pow(z.getRealPart() - roots.get(i).getRealPart(), 2));

			if (abs < treshold) {
				if (difference == -1 || abs < difference) {
					difference = abs;
					index = i;
				}
			}
		}

		return index;
	}
}
