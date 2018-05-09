package hr.fer.zemris.math;

import java.util.Objects;

/**
 * Razred koji implemenira model vektora u 3D sustavu sa koeficijentima x,z,i y.
 * Također sadrži neke osnovne metode za rad sa vektorima
 * 
 * @author Mihael
 *
 */
public class Vector3 {

	/**
	 * X koordinata izraza
	 */
	private final double x;
	/**
	 * Y koordinata izraza
	 */
	private final double z;
	/**
	 * Z koordinata izraza
	 */
	private final double y;

	/**
	 * Konstruktor koji stvara novi vektor
	 * 
	 * @param x
	 *            - x koordinata
	 * @param y
	 *            - y koordinata
	 * @param z
	 *            - z koordinata
	 */
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Metoda vraća modul kompleksng broja
	 * 
	 * @return norma ovog broja
	 */
	public double norm() {
		return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.z, 2) + Math.pow(this.y, 2));
	}

	/**
	 * Metoda vraća trenutni broj u normaliziranom obliku
	 * 
	 * @return normalizirani oblik vektora
	 */
	public Vector3 normalized() {
		return new Vector3(this.x / norm(), this.y / norm(), this.z / norm());
	}

	/**
	 * Metoda koja zbraja trenutni niz i argument
	 * 
	 * @param other
	 *            - drugi vektor
	 * @return novi vektor sa zbrojenim koeficijntima
	 * 
	 * @throws NullPointerException
	 *             - ako je argument null
	 * 
	 */
	public Vector3 add(Vector3 other) {
		Objects.requireNonNull(other);
		return new Vector3(this.x + other.x, this.y + other.y, this.z + other.z);
	}

	/**
	 * Metoda koja oduzima trenutni niz i argument
	 * 
	 * @param other
	 *            - drugi vektor
	 * @return novi vektor sa oduzetima koeficijentima
	 * 
	 * @throws NullPointerException
	 *             - ako je argument null
	 * 
	 */
	public Vector3 sub(Vector3 other) {
		Objects.requireNonNull(other);
		return new Vector3(this.x - other.x, this.y - other.y, this.z - other.z);
	}

	/**
	 * Metpda koja skalrano(sam sa sobom) množi vektor
	 * 
	 * @param other
	 *            - drugi kompleksni broj
	 * @return skalarni oblik proizvodnje
	 * 
	 * @throws NullPointerException
	 *             - ako je argument <code>null</code>
	 */
	public double dot(Vector3 other) {
		Objects.requireNonNull(other);
		return this.x * other.x + this.y * other.y + this.z * other.z;
	}

	/**
	 * Metoda koja izvršava kartezijev produkt dva vekotra(sadašnji i bivši)
	 * 
	 * @param other
	 *            - drugi vektor
	 * @return novi vektor kao kartezijev oblik vektora
	 * 
	 * @throws NullPointerException
	 *             - ako je argument <code>null</code>
	 */
	public Vector3 cross(Vector3 other) {
		Objects.requireNonNull(other);
		return new Vector3(this.y * other.z - this.z * other.y, -this.x * other.z + this.z * other.x,
				this.x * other.y - this.y * other.x);
	}

	/**
	 * Metoda koja vraća trenutni vektor čije su koordinate skalirane za s
	 * 
	 * @param s
	 *            - koeficijent skaliranja
	 * @return skalirani vektor s koeficijentom s
	 */
	public Vector3 scale(double s) {
		return new Vector3(this.x * s, this.y * s, this.z * s);
	}

	/**
	 * Metoda vraća kosinus kuta između trenutnog vektora i argumenta
	 * 
	 * @param other
	 *            - atgument
	 * @return kosinus kuta
	 */
	public double cosAngle(Vector3 other) {
		return dot(other) / (norm() * other.norm());
	}

	/**
	 * Metoda vraća X koordinatu
	 * 
	 * @return X koordinata
	 */
	public double getX() {
		return x;
	}

	/**
	 * Metoda vraća Y koordinatu
	 * 
	 * @return Y koordinata
	 */
	public double getY() {
		return y;
	}

	/**
	 * Metoda vraća Z koordinatu
	 * 
	 * @return Z koordinata
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Metoda stvara novo polje sa koeficijentima
	 * 
	 * @return novo polje koeficijenata
	 */
	public double[] toArray() {
		return new double[] { this.x, this.y, this.z };
	}

	/**
	 * Metoda vraća znakovnu reprezentaciju vektora
	 * 
	 * @return znakovna reprezentacija vektora
	 */
	public String toString() {
		return "(" + String.format("%.5f", this.x).replace(",", ".") + ","
				+ String.format("%.5f", this.y).replace(",", ".") + ","
				+ String.format("%.5f", this.z).replace(",", ".") + ")";
	}
}
