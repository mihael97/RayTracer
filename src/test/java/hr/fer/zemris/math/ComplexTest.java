package hr.fer.zemris.math;

import static org.junit.Assert.*;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class ComplexTest {
	private static double TOLERANCE = 1E-6;

	@Test
	public void moduleTest() {
		Complex complex = new Complex(1, 2);// 1+2i
		assertEquals(Double.valueOf(Math.sqrt(5)), Double.valueOf(complex.module()));
	}
	
	@Test
	public void addTest() {
		Complex add1 = new Complex(1, 2);// 1+2i
		Complex add2 = new Complex(2, 7);// 2+7i

		Complex result = add1.add(add2);

		assertEquals(Double.valueOf(3), Double.valueOf(result.getRealPart()), TOLERANCE);
		assertEquals(Double.valueOf(9), Double.valueOf(result.getImaginaryPart()), TOLERANCE);

	}

	@Test
	public void subTest() {
		Complex sub1 = new Complex(1, 2);// 1+2i
		Complex sub2 = new Complex(2, 7);// 2+7i

		Complex result = sub1.sub(sub2);
		assertEquals(-1, result.getRealPart(), TOLERANCE);
		assertEquals(-5, result.getImaginaryPart(), TOLERANCE);
	}

	@Test
	public void mulTest() {
		Complex mul1 = new Complex(1, 2);// 1+2i
		Complex mul2 = new Complex(2, 7);// 2+7i

		Complex result = mul1.multiply(mul2);
		assertEquals(-12, result.getRealPart(), TOLERANCE);
		assertEquals(11, result.getImaginaryPart(), TOLERANCE);
	}

	@Test
	public void divTest() {
		Complex div1 = new Complex(1, 2);// 1+2i
		Complex div2 = new Complex(2, 7);// 2+7i

		Complex result = div1.divide(div2);
		assertEquals(0.3018867925, result.getRealPart(), TOLERANCE);
		assertEquals(-0.05660377358, result.getImaginaryPart(), TOLERANCE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void divZeroTest() {
		Complex div1 = new Complex(1, 2);// 1+2i
		Complex div2 = new Complex(0, 0);// 0+0i

		div1.divide(div2);
	}
	
	@Test
	public void negateTest() {
		Complex pow1 = new Complex(1, 2).negate();

		assertEquals(-1, pow1.getRealPart(), TOLERANCE);
		assertEquals(-2, pow1.getImaginaryPart(), TOLERANCE);
	}

	@Test
	public void powTest() {
		Complex pow1 = new Complex(1, 2);// 1+2i

		Complex result = pow1.power(2);
		assertEquals(-3, result.getRealPart(), TOLERANCE);
		assertEquals(4, result.getImaginaryPart(), TOLERANCE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void powExcptionTest() {
		@SuppressWarnings("unused")
		Complex result = new Complex(2, 2).power(-1);
	}

	@Test
	public void rootTest() {
		Complex div1 = new Complex(4, 4);// 4+4i

		Complex result = div1.root(2).get(0);
		assertEquals(2.197368227, result.getRealPart(), TOLERANCE);
		assertEquals(0.9101797211, result.getImaginaryPart(), TOLERANCE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void rootExceptionTest() {
		@SuppressWarnings("unused")
		Complex result = new Complex(2, 2).root(-1).get(1);
	}
}
