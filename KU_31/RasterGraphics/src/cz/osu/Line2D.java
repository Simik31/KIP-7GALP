package cz.osu;

public class Line2D {

    public Point2D pointA;
    public Point2D pointB;

    public Line2D(Point2D pA, Point2D pB){

        pointA = pA;
        pointB = pB;
    }

    public void applyMatrix(Matrix2D matrix2D){

        pointA.applyMatrix(matrix2D);
        pointB.applyMatrix(matrix2D);
    }

    public Line2D clone(){

        return new Line2D(pointA.clone(), pointB.clone());
    }
}
