package cz.osu;

public class Point {

    private double dx, dy;
    private int ix, iy;

    public Point() {
        this.dx = 0;
        this.ix = 0;
        this.dy = 0;
        this.iy = 0;
    }

    public Point(double x, double y) {
        this.dx = x;
        this.ix = (int) Math.round(x);
        this.dy = y;
        this.iy = (int) Math.round(y);
    }

    public double getX() {
        return dx;
    }

    public int getXInt() {
        return ix;
    }

    public double getY() {
        return dy;
    }

    public int getYInt() {
        return iy;
    }

    public Point clone() {
        return new Point(this.dx, this.dy);
    }
}
