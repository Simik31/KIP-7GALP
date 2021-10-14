package cz.osu;

public class Triangle2D {

    public Point2D pointA;
    public Point2D pointB;
    public Point2D pointC;

    public Triangle2D(Point2D pA, Point2D pB, Point2D pC){

        pointA = pA;
        pointB = pB;
        pointC = pC;
    }

    public void applyMatrix(Matrix2D matrix2D){

        pointA.applyMatrix(matrix2D);
        pointB.applyMatrix(matrix2D);
        pointC.applyMatrix(matrix2D);
    }

    public Triangle2D clone(){

        return new Triangle2D(pointA.clone(), pointB.clone(), pointC.clone());
    }
}
