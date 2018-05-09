package hr.fer.zemris.math;

import static org.junit.Assert.*;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class Vector3Test {
	@Test
	public void newVector() {
		Vector3 vector = new Vector3(1, 5, 7);

		assertTrue(vector.getX() == 1);
		assertTrue(vector.getY() == 5);
		assertTrue(vector.getZ() == 7);

	}

	@Test
	public void norm() {
		Vector3 vector = new Vector3(1, 1, 1);
		assertEquals(Double.valueOf(Math.sqrt(3)), Double.valueOf(vector.norm()));

		vector = new Vector3(5, 7, 9);
		assertEquals(Double.valueOf(Math.sqrt(155)), Double.valueOf(vector.norm()));
	}

	@Test
	public void normalized() {
		Vector3 vector = new Vector3(1, 1, 1);
		Vector3 normalized = vector.normalized();

		assertEquals(Double.valueOf(1 / Math.sqrt(3)), Double.valueOf(normalized.getX()));
	}

	@Test(expected = NullPointerException.class)
	public void addNull() {
		Vector3 vector = new Vector3(1, 1, 1);
		vector.add(null);
	}

	@Test
	public void add() {
		Vector3 vector = new Vector3(1, 1, 1).add(new Vector3(5, 7, 9));

		assertTrue(vector.getX() == 6);
		assertTrue(vector.getY() == 8);
		assertTrue(vector.getZ() == 10);
	}

	@Test(expected = NullPointerException.class)
	public void subNull() {
		Vector3 vector = new Vector3(1, 1, 1);
		vector.sub(null);
	}

	@Test
	public void sub() {
		Vector3 vector = new Vector3(1, 1, 1).sub(new Vector3(5, 7, 9));

		assertTrue(vector.getX() == -4);
		assertTrue(vector.getY() == -6);
		assertTrue(vector.getZ() == -8);
	}

	@Test(expected = NullPointerException.class)
	public void dotNull() {
		Vector3 vector = new Vector3(1, 1, 1);
		vector.dot(null);
	}

	@Test
	public void dot() {
		double vector = new Vector3(1, 2, 2.5).dot(new Vector3(5, 7, 2));

		assertEquals(Double.valueOf(24), Double.valueOf(vector));
	}

	@Test(expected = NullPointerException.class)
	public void crossNull() {
		Vector3 vector = new Vector3(1, 1, 1);
		vector.sub(null);
	}

	@Test
	public void cross() {
		Vector3 vector = new Vector3(1, 5, 6).cross(new Vector3(4, 8, 7));

		assertTrue(vector.getX() == -13);
		assertTrue(vector.getY() == 17);
		assertTrue(vector.getZ() == -12);
	}

	@Test
	public void scale() {
		Vector3 vector = new Vector3(5, 7, 9).scale(3);

		assertTrue(vector.getX() == 15);
		assertTrue(vector.getY() == 21);
		assertTrue(vector.getZ() == 27);
	}

	@Test
	public void cosAngle() {
		double vector = new Vector3(5, 7, 9).cosAngle(new Vector3(5, 1, 2));

		assertEquals(Double.valueOf(5 * Math.sqrt(186) / 93), Double.valueOf(vector), Math.pow(10, -6));
	}

	@Test
	public void toArray() {
		double[] vector = new Vector3(5.2, 7.4, 9.5).toArray();

		assertEquals(3, vector.length);

		assertTrue(vector[0] == 5.2);
		assertTrue(vector[1] == 7.4);
		assertTrue(vector[2] == 9.5);
	}
}
