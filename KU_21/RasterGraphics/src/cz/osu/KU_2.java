package cz.osu;

import java.util.ArrayList;

public class KU_2 {
    private V_RAM vram;
    private ImagePanel imagePanel;
    private int xyOffset;
    private int angledLength;
    private int size;
    private ArrayList<ArrayList<Point2D>> edges;

    private final Point3D a, b, c, d, e, f, g, h;

    public KU_2(V_RAM _vram, ImagePanel _imagePanel, int _size) {
        vram = _vram;
        imagePanel = _imagePanel;
        angledLength = _size / 2;
        xyOffset = 50 - (_size + angledLength) / 2;
        size = _size;
        edges = new ArrayList<>();

        a = new Point3D(xyOffset,                       xyOffset + angledLength + size, angledLength);
        b = new Point3D(xyOffset                + size, xyOffset + angledLength + size, angledLength);
        c = new Point3D(xyOffset                + size, xyOffset + angledLength,        angledLength);
        d = new Point3D(xyOffset,                       xyOffset + angledLength,        angledLength);
        e = new Point3D(xyOffset + angledLength,        xyOffset                + size, 0);
        f = new Point3D(xyOffset + angledLength + size, xyOffset                + size, 0);
        g = new Point3D(xyOffset + angledLength + size, xyOffset,                       0);
        h = new Point3D(xyOffset + angledLength,        xyOffset,                       0);

        Cube();

        waitAndClear();

        Tetrahedron();
    }

    private void Cube() {
        printFace(new Point3D[]{ f, e, h, g }, 0, true, true, false);
        printFace(new Point3D[]{ e, a, d, h }, 0, true, true, false);
        printFace(new Point3D[]{ e, f, b, a }, 0, true, true, false);
        printFace(new Point3D[]{ b, f, g, c }, 0, true, true, false);
        printFace(new Point3D[]{ d, c, g, h }, 0, true, true, false);
        printFace(new Point3D[]{ a, b, c, d }, 0, true, true, false);
    }

    private void Tetrahedron() {
        printFace(new Point3D[]{ e, h, g }, 0, true, true, false);
        printFace(new Point3D[]{ e, d, h }, 0, true, true, false);
        printFace(new Point3D[]{ d, g, h }, 0, true, true, false);
        printFace(new Point3D[]{ e, g, d }, 0, true, true, false);
    }

    private void waitAndClear() {
        imagePanel.setImage(vram.getImage());
        try {
            Thread.sleep(1250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GraphicsOperations.fillBrightness(vram, 255);
    }

    private void printFace(Point3D[] points,int brightness, boolean sleep, boolean updateOnPrint, boolean antiAliasing) {

        boolean fillFace = testFill(points, new Vector3( 0, 0, -1 ));

        Point2D[] points2d = new Point2D[points.length];
        for(int i = 0; i < points.length; i++) points2d[i] = new Point2D(points[i].x, points[i].y);

        for(int i = 0; i < points.length; i++)
            print(points2d[i], points2d[(i + 1) % points2d.length], brightness, sleep, updateOnPrint, antiAliasing);

        double minX = vram.getWidth(), maxX = 0, minY = vram.getHeight(), maxY = 0;
        for(ArrayList<Point2D> e : edges) {
            for(Point2D p :  new Point2D[] { e.get(0),  e.get(e.size() - 1) }) {
                if (p.Values[0] < minX) minX = p.Values[0];
                if (p.Values[0] > maxX) maxX = p.Values[0];
                if (p.Values[1] < minY) minY = p.Values[1];
                if (p.Values[1] > maxY) maxY = p.Values[1];
            }
        }

        for(int y = (int)minY + 1; y < (int)maxY; y++) {
            boolean inside = false;
            for(int x = (int)minX; x < (int)maxX; x++) {
                for (ArrayList<Point2D> edge : edges) {
                    if (edgeContainsPointForFill(edge, new Point2D(x, y)) && !edgeContainsPoint(edge, new Point2D(x + 1, y))) {
                        inside = !inside;
                        break;
                    }
                }
                if(fillFace && inside) {
                    if(vram.getPixel(x, y) == 255) vram.setPixel(x, y, 70);
                    else if(vram.getPixel(x, y) == 0) {
                        boolean isEdge = false;
                        for(ArrayList<Point2D> edge : edges) if(edgeContainsPoint(edge, new Point2D(x, y))) {
                            isEdge = true;
                            break;
                        }
                        if(!isEdge) vram.setPixel(x, y, 150);
                    }
                }
            }
        }
        edges.clear();
    }

    private boolean testFill(Point3D[] points, Vector3 observer) {
        Vector3 u = new Vector3(points[1].x - points[0].x, points[1].y - points[0].y, points[1].z - points[0].z);
        Vector3 v = new Vector3(points[2].x - points[1].x, points[2].y - points[1].y, points[2].z - points[1].z);
        return Math.abs(Math.acos(u.vectorProduct(v).scalarProduct(observer) / u.size() / v.size())) < Math.PI / 2;
    }

    private void print(Point2D z, Point2D k, int brightness, boolean sleep, boolean updateOnPrint, boolean antiAliasing) {
        ArrayList<Point2D> edge = new ArrayList<>();
        algSelector(z, k, brightness, updateOnPrint, antiAliasing, edge);
        if (sleep) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        edges.add(edge);
    }

    public void algSelector(Point2D z, Point2D k, int brightness, boolean updateOnPrint, boolean antiAliasing, ArrayList<Point2D> edge) {
        int xz = (int)z.Values[0];
        int yz = (int)z.Values[1];
        int xk = (int)k.Values[0];
        int yk = (int)k.Values[1];
        int dx = Math.abs(xz - xk);
        int dy = Math.abs(yz - yk);

        if(xz > xk) {
            int tmp = xz;
            xz = xk;
            xk = tmp;
            tmp = yz;
            yz = yk;
            yk = tmp;
        }

        if (dy == 0) horizontal(xz, xk, yz, brightness, edge);
        else if (dx == 0) vertical(yz, yk, xz, brightness, edge);
        else if (dx > dy) {
            int h1 = 2 * Math.abs(yz - yk);
            int h2 = h1 - 2 * (xk - xz);
            int h  = h1 - (xk - xz);

            int ay = (yz > yk) ? (-1) : 1;

            if(antiAliasing) bresenhamXAA(xz, yz, xk, h, h1, h2, ay, brightness, edge);
            else bresenhamX(xz, yz, xk, h, h1, h2, ay, brightness, edge);
        }
        else if (dx < dy) {
            if (yz > yk) {
                int tmp = yz;
                yz = yk;
                yk = tmp;
                tmp = xz;
                xz = xk;
                xk = tmp;
            }

            int h1 = 2 * Math.abs(xz - xk);
            int h2 = h1 - 2 * (yk - yz);
            int h = h1 - (yk - yz);

            int ax = (xz > xk) ? (-1) : 1;

            if(antiAliasing) bresenhamYAA(xz, yz, yk, h, h1, h2, ax, brightness, edge);
            else bresenhamY(xz, yz, yk, h, h1, h2, ax, brightness, edge);
        }
        else {
            int ay = (yz > yk) ? -1 : 1;
            if(antiAliasing) diagonalAA(xz, yz, xk, yk, ay, brightness, edge);
            else diagonal(xz, yz, xk, ay, brightness, edge);
        }
        if(updateOnPrint) imagePanel.setImage(vram.getImage());
    }

    private void diagonal(int xz, int yz, int xk, int ay, int brightness, ArrayList<Point2D> edge) {
        for (int x = 0; x <= Math.abs(xk - xz); x++) addPixel(xz + x, yz + (x * ay),  brightness, edge);
    }

    private void diagonalAA(int xz, int yz, int xk, int yk, int ay, int brightness, ArrayList<Point2D> edge) {
        int alB = (255 - brightness) / 2;
        addPixel(xz, yz + ay, alB, edge);
        addPixel(xz, yz, brightness, edge);
        for (int x = 1; x < Math.abs(xk - xz); x++) {
            addPixel(xz + x, yz + (x * ay),  brightness, edge);
            addPixel(xz + x, yz + (x * ay) + 1, alB, edge);
            addPixel(xz + x, yz + (x * ay) - 1, alB, edge);
        }
        addPixel(xk, yk, brightness, edge);
        addPixel(xk, yk - ay, alB, edge);
    }

    private void horizontal(int xz, int xk, int y, int brightness, ArrayList<Point2D> edge) {
        for (int x = xz; x <= xk; x++) addPixel(x, y, brightness, edge);
    }

    private void vertical(int yz, int yk, int x, int brightness, ArrayList<Point2D> edge) {
        if (yz > yk) {
            int tmp = yz;
            yz = yk;
            yk = tmp;
        }
        for (int y = yz; y <= yk; y++) addPixel(x, y, brightness, edge);
    }

    private void bresenhamX(int xz, int yz, int xk, int h, int h1, int h2, int ay, int brightness, ArrayList<Point2D> edge) {
        for (int x = xz, y = yz; x <= xk; x++) {
            if (h > 0) {
                h += h2;
                y += ay;
            } else h += h1;
            addPixel(x, y, brightness, edge);
        }
    }

    private void bresenhamXAA(int xz, int yz, int xk, int h, int h1, int h2, int ay, int brightness, ArrayList<Point2D> edge) {
        int l = Math.abs(h / h1) + 1;
        int lc = 1;
        int step = (255 - brightness) / l;
        for (int x = xz, y = yz; x <= xk; x++) {
            if (h > 0) {
                h += h2;
                y += ay;
                l = Math.abs(h / h1) + 2;
                if(l > xk - x) l -= (xk - x - 1);
                lc = 1;
                step = (255 - brightness) / l;
            } else h += h1;
            addPixel(x, y + ay, step * (l - lc), edge);
            addPixel(x, y - ay, step * lc, edge);
            addPixel(x, y, brightness, edge);
            lc++;
        }
    }

    private void bresenhamY(int xz, int yz, int yk, int h, int h1, int h2, int ax, int brightness, ArrayList<Point2D> edge) {
        for (int x = xz, y = yz; y <= yk; y++) {
            if (h > 0) {
                h += h2;
                x += ax;
            } else h += h1;
            addPixel(x, y, brightness, edge);
        }
    }

    private void bresenhamYAA(int xz, int yz, int yk, int h, int h1, int h2, int ax, int brightness, ArrayList<Point2D> edge) {
        int l = Math.abs(h / h1) + 1;
        int lc = 1;
        int step = (255 - brightness) / l;
        for (int x = xz, y = yz; y <= yk; y++) {
            if (h > 0) {
                h += h2;
                x += ax;
                l = Math.abs(h / h1) + 2;
                if (l > yk - y) l -= (yk - y - 1);
                lc = 1;
                step = (255 - brightness) / l;
            } else h += h1;
            addPixel(x, y, brightness, edge);
            addPixel(x + ax, y, step * (l - lc), edge);
            addPixel(x - ax, y, step * lc, edge);
            lc++;
        }
    }

    private void addPixel(int x, int y, int brightness, ArrayList<Point2D> edge) {
        vram.setPixel(x, y, brightness);
        edge.add(new Point2D(x, y));
    }

    private boolean edgeContainsPoint(ArrayList<Point2D> edge, Point2D point) {
        for (Point2D edgePoint : edge) if (point.Values[0] == edgePoint.Values[0] && point.Values[1] == edgePoint.Values[1]) return true;
        return false;
    }

    public boolean edgeContainsPointForFill(ArrayList<Point2D> points, Point2D point) {
        if(points.get(0).Values[1] != points.get(points.size() - 1).Values[1]) {
            ArrayList<Point2D> pointsToCompare = new ArrayList<>(points);

            int maxId = 0;
            double max = 0;
            for (int i = 0; i < points.size(); i++) {
                Point2D p = pointsToCompare.get(i);
                if (i == 0) max = p.Values[1];
                else if (p.Values[1] > max) {
                    max = p.Values[1];
                    maxId = i;
                }
            }
            pointsToCompare.remove(maxId);
            return edgeContainsPoint(pointsToCompare, point);
        }
        return false;
    }
}