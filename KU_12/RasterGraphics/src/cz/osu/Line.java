package cz.osu;

public class Line {

    private Point start, end;

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public void swapPoints() {
        Point t = start.clone();
        start = end.clone();
        end = t;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    public double getDeltaX() {
        return Math.abs(this.getStart().getX() - this.getEnd().getX());
    }

    public int getDeltaXInt() {
        return Math.abs(this.getStart().getXInt() - this.getEnd().getXInt());
    }

    public double getDeltaXReversed() {
        return Math.abs(this.getEnd().getX() - this.getStart().getX());
    }

    public int getDeltaXReversedInt() {
        return Math.abs(this.getEnd().getXInt() - this.getStart().getXInt());
    }

    public double getDeltaY() {
        return Math.abs(this.getStart().getY() - this.getEnd().getY());
    }

    public int getDeltaYInt() {
        return Math.abs(this.getStart().getYInt() - this.getEnd().getYInt());
    }

    public double getDeltaYReversed() {
        return Math.abs(this.getEnd().getY() - this.getStart().getY());
    }

    public int getDeltaYReversedInt() {
        return Math.abs(this.getEnd().getYInt() - this.getStart().getYInt());
    }

    public Line clone() {
        return new Line(this.start.clone(), this.end.clone());
    }
}
