package cz.osu;

public class Vector3 {

    public final double x, y, z;

    public Vector3() {
        this(0, 0, 0);
    }

    public Vector3(double x) {
        this(x, 0, 0);
    }

    public Vector3(double x, double y) {
        this(x, y, 0);
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 vectorProduct(Vector3 a) {
        return vectorProduct(this, a);
    }

    public static Vector3 vectorProduct(Vector3 a, Vector3 b) {
        return new Vector3(a.y * b.z - b.y * a.z, a.z * b.x - b.z * a.x, a.x * b.y - b.x * a.y);
    }

    public double scalarProduct(Vector3 a) {
        return scalarProduct(this, a);
    }

    public static double scalarProduct(Vector3 a, Vector3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public double size() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    public Vector3 clone() {
        return new Vector3(x, y, z);
    }
}
