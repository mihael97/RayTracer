package hr.fer.zemris.math;

import static org.junit.Assert.*;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class ComplexPolynomialTest {

	@Test(expected = NullPointerException.class)
	public void complexPolynomNull() {
		@SuppressWarnings("unused")
		ComplexPolynomial polynom = new ComplexPolynomial(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void complexPolynomEmpty() {
		@SuppressWarnings("unused")
		ComplexPolynomial polynom = new ComplexPolynomial(new Complex[] {});
	}

	@Test
	public void order() {
		ComplexPolynomial polynom = new ComplexPolynomial(
				new Complex[] { new Complex(5, 7), new Complex(7, 8), new Complex(7, 9) });
		assertEquals(2, polynom.order());
	}

	@Test(expected = IllegalArgumentException.class)
	public void deriveFirst() {
		ComplexPolynomial polynom = new ComplexPolynomial(new Complex[] { new Complex(5, 7) });
		assertEquals("", polynom.derive());
	}

	@Test
	public void deriveSecond() {
		ComplexPolynomial polynom = new ComplexPolynomial(new Complex[] { new Complex(5, 7), new Complex(7, 4) });
		assertEquals("(5.0+7.0i)", polynom.derive().toString());
	}

	@Test
	public void deriveThird() {
		ComplexPolynomial polynom = new ComplexPolynomial(
				new Complex[] { new Complex(5, 7), new Complex(7, 4), new Complex(1, 0) });
		assertEquals("(10.0+14.0i)*z+(7.0+4.0i)", polynom.derive().toString());
	}

	@Test
	public void multiplyFirst() {
		ComplexPolynomial first = new ComplexPolynomial(
				new Complex[] { new Complex(1, 0), Complex.ZERO, new Complex(0, 4) });
		ComplexPolynomial second = new ComplexPolynomial(new Complex[] { new Complex(0, 5), new Complex(6, 0) });

		assertEquals("(5.0i)*z^3+(6.0)*z^2+(-20.0)*z+(24.0i)", first.multiply(second).toString());
	}

	@Test
	public void multiplySecond() {
		ComplexPolynomial first = new ComplexPolynomial(new Complex[] { new Complex(2, 1), new Complex(1, 0) });
		ComplexPolynomial second = new ComplexPolynomial(new Complex[] { new Complex(1, 0), new Complex(-1, 0) });

		assertEquals("(2.0+1.0i)*z^2+(-1.0-1.0i)*z+(-1.0)", first.multiply(second).toString());
	}

}
