package cz.osu;

import java.util.ArrayList;

public class Bezier {
    private final Point2D p0;
    private final Point2D p1;
    private final Point2D p2;
    private final Point2D p3;

    private final double qx0;
    private final double qx1;
    private final double qx2;
    private final double qx3;

    private final double qy0;
    private final double qy1;
    private final double qy2;
    private final double qy3;

    public Bezier(ArrayList<Point2D> points, int startIndex) {
        p0 = points.get(startIndex);
        p1 = points.get(startIndex + 1);
        p2 = points.get(startIndex + 2);
        p3 = points.get(startIndex + 3);

        qx0 = p0.Values[0];
        qx1 = 3 * (p1.Values[0] - p0.Values[0]);
        qx2 = 3 * (p2.Values[0] - 2 * p1.Values[0] + p0.Values[0]);
        qx3 = p3.Values[0] - 3 * p2.Values[0] + 3 * p1.Values[0] - p0.Values[0];

        qy0 = p0.Values[1];
        qy1 = 3 * (p1.Values[1] - p0.Values[1]);
        qy2 = 3 * (p2.Values[1] - 2 * p1.Values[1] + p0.Values[1]);
        qy3 = p3.Values[1] - 3 * p2.Values[1] + 3 * p1.Values[1] - p0.Values[1];
    }

    public Point2D getPointForT(double t) {
        double tt = t * t;
        double ttt = tt * t;

        double x = qx0 + qx1 * t + qx2 * tt + qx3 * ttt;
        double y = qy0 + qy1 * t + qy2 * tt + qy3 * ttt;

        return new Point2D(x, y);
    }

    public void drawBezier(KU_3 ku3, int steps, int brightness, boolean showPoints) {
        Point2D startPoint = p0;

        if(showPoints) {
            ku3.printPoint(startPoint.getRoundedPoint().x, startPoint.getRoundedPoint().y, brightness, 3);
        }

        for(int i = 1; i <= steps; i++) {
            Point2D endPoint = getPointForT(1.0 * i / steps);
            ku3.print(startPoint, endPoint, brightness, false, false, false);
            if(showPoints)
                ku3.printPoint(endPoint.getRoundedPoint().x, endPoint.getRoundedPoint().y, brightness, 3);
            startPoint = endPoint;
        }

    }
}













