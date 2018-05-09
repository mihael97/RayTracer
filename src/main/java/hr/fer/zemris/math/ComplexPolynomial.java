package hr.fer.zemris.math;

import java.util.Arrays;
import java.util.Objects;

/**
 * Metoda koji reprezentira model polinoma n-tog stupanja zadnog sa
 * koeficijentima
 * 
 * @author Mihael
 *
 */
public class ComplexPolynomial {

	/**
	 * Koeficijenti uz pojedinu potenciju
	 */
	private Complex[] factors;

	/**
	 * Metoda iz koeficijnata stvara novi polinom
	 * 
	 * @param factors
	 *            - koeficijenti
	 * 
	 * @throws NullPointerException
	 *             - ako je argument <code>null</code>
	 * 
	 * @throws IllegalArgumentException
	 *             - ako polje nema elemenata
	 */
	public ComplexPolynomial(Complex[] factors) {
		Objects.requireNonNull(factors);

		if (factors.length == 0) {
			throw new IllegalArgumentException("Array with factors doesn't have any element!");
		}

		this.factors = new Complex[factors.length];
		this.factors = Arrays.copyOf(factors, factors.length);
	}

	/**
	 * Metoda vraća red polinoma(najveća potencija)
	 * 
	 * @return red polinama
	 */
	public short order() {
		return (short) (factors.length - 1);
	}

	/**
	 * Metoda množi dva polinoma
	 * 
	 * @param p
	 *            - drugi sudionik operacije množenja
	 * @return polinom sa novim koeficijentima
	 * 
	 * @throws NullPointerException
	 *             - ako su koeficijenti null
	 */
	public ComplexPolynomial multiply(ComplexPolynomial p) {
		Objects.requireNonNull(p);
		Complex[] newFactors = new Complex[order() + p.order() + 1];

		for (int i = 0; i < newFactors.length; i++) {
			newFactors[i] = Complex.ZERO;
		}

		for (int k = 0, degreeK = factors.length; k < degreeK; k++) {
			for (int j = 0, degreeJ = p.factors.length; j < degreeJ; j++) {
				newFactors[k + j] = newFactors[k + j].add(factors[k].multiply(p.factors[j]));
			}
		}

		return new ComplexPolynomial(newFactors);

	}

	/**
	 * Metoda koja izvršava prvu derivaciju polinoma
	 * 
	 * @return prva derivacija polinoma
	 */
	public ComplexPolynomial derive() {
		Complex[] newFactors = new Complex[factors.length - 1];

		for (int i = 0, length = factors.length - 1; i < length; i++) {
			newFactors[i] = factors[i].multiply(new Complex(length - i, 0));
		}

		return new ComplexPolynomial(newFactors);
	}

	/**
	 * Metoda koja u polnom uvrštava imaginarni broj kao argument
	 * 
	 * @param z
	 *            - vrijednost koju želimo uvrstiti u izraz
	 * @return novi kompleksni broj kao rezultat izraza
	 * 
	 * @throws NullPointerException
	 *             - ako je argument <code>null</code>
	 */
	public Complex apply(Complex z) {
		Objects.requireNonNull(z);

		Complex result = Complex.ZERO;

		for (int i = 0, length = factors.length; i < length; i++) {
			Complex add = factors[i].multiply(z.power(length - 1 - i));
			result = result.add(add);
		}

		return result;
	}

	/**
	 * Metoda vraća znakovnu reprezentaciju polinoma
	 * 
	 * @throws znakovna
	 *             reprezentacija polinoma
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (int i = 0, length = factors.length; i < length; i++) {
			if (!(factors[i].getRealPart() == 0 && factors[i].getImaginaryPart() == 0)) {
				builder.append("(" + factors[i].toString() + ")");

				if (i < (length - 2)) {
					builder.append("*z^").append(Integer.valueOf(length - i - 1));
				} else if (i == (length - 2)) {
					builder.append("*z");
				}

				builder.append("+");
			}
		}

		return builder.toString().substring(0, builder.length() - 1);
	}
}
