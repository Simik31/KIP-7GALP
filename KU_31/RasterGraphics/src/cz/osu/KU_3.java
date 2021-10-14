package cz.osu;

import java.util.ArrayList;

public class KU_3 {
    private final V_RAM vram;
    private final ImagePanel imagePanel;
    private ArrayList<Point2D> points;
    private ArrayList<Bezier> beziers;

    public KU_3(V_RAM _vram, ImagePanel _imagePanel) {
        vram = _vram;
        imagePanel = _imagePanel;
        points = new ArrayList<>();
        beziers = new ArrayList<>();
    }

    public void Bezier(Point2D[] userPoints, int steps, int brightness, boolean showPoints) {
        Point2D[] Ps = new Point2D[userPoints.length + 2];
        Point2D[] Rs = new Point2D[userPoints.length + 2];
        Point2D[] Ls = new Point2D[userPoints.length + 2];

        System.arraycopy(userPoints, 0, Ps, 1, userPoints.length);
        Ps[0] = Ps[1].clone();
        Ps[Ps.length - 1] = Ps[Ps.length - 2].clone();

        for(int i = 1; i < Ps.length - 1; i++) {
            Ls[i] = new Point2D(
                    Ps[i].Values[0] - ((Ps[i + 1].Values[0] - Ps[i - 1].Values[0]) / 6.0),
                    Ps[i].Values[1] - ((Ps[i + 1].Values[1] - Ps[i - 1].Values[1]) / 6.0)
            );
            Rs[i] = new Point2D(
                    Ps[i].Values[0] + ((Ps[i + 1].Values[0] - Ps[i - 1].Values[0]) / 6.0),
                    Ps[i].Values[1] + ((Ps[i + 1].Values[1] - Ps[i - 1].Values[1]) / 6.0)
            );
        }

        for(int i = 1; i < Ps.length - 2; i++) {
            points = new ArrayList<>();
            addPoint(Ps[i]);
            addPoint(Rs[i]);
            addPoint(Ls[i + 1]);
            addPoint(Ps[i + 1]);

            for(int j = 1; j < points.size(); j++)
                print(points.get(j - 1), points.get(j), 200, false, false, false);

            for(Point2D point: points)
                printPoint(point.getRoundedPoint().x, point.getRoundedPoint().y, 200, 3);

            for(Bezier bezier : beziers) {
                Point2D startPoint = points.get(0);

                if(showPoints)
                    printPoint(startPoint.getRoundedPoint().x, startPoint.getRoundedPoint().y, brightness, 3);

                bezier.drawBezier(this, steps, brightness, showPoints);
            }

        }
    }

    private void addPoint(Point2D p) {
        points.add(p);
        beziers = new ArrayList<>();
        for(int i = 0; i < (points.size() - 1) / 3; i++) beziers.add(new Bezier(points, 3 * i));
    }

    public void print(Point2D z, Point2D k, int brightness, boolean sleep, boolean updateOnPrint, boolean antiAliasing) {
        algSelector(z, k, brightness, updateOnPrint, antiAliasing);
        if (sleep) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void printPoint(int x, int y, int brightness, int size) {
        for(int posX = x - ((size - 1) / 2); posX < x + ((size + 1) / 2); posX++)
            for(int posY = y - ((size - 1) / 2); posY < y + ((size + 1) / 2); posY++)
                vram.setPixel(posX, posY, brightness);
    }

    public void algSelector(Point2D z, Point2D k, int brightness, boolean updateOnPrint, boolean antiAliasing) {
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

        if (dy == 0) horizontal(xz, xk, yz, brightness);
        else if (dx == 0) vertical(yz, yk, xz, brightness);
        else if (dx > dy) {
            int h1 = 2 * Math.abs(yz - yk);
            int h2 = h1 - 2 * (xk - xz);
            int h  = h1 - (xk - xz);

            int ay = (yz > yk) ? (-1) : 1;

            if(antiAliasing) bresenhamXAA(xz, yz, xk, h, h1, h2, ay, brightness);
            else bresenhamX(xz, yz, xk, h, h1, h2, ay, brightness);
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

            if(antiAliasing) bresenhamYAA(xz, yz, yk, h, h1, h2, ax, brightness);
            else bresenhamY(xz, yz, yk, h, h1, h2, ax, brightness);
        }
        else {
            int ay = (yz > yk) ? -1 : 1;
            if(antiAliasing) diagonalAA(xz, yz, xk, yk, ay, brightness);
            else diagonal(xz, yz, xk, ay, brightness);
        }
        if(updateOnPrint) imagePanel.setImage(vram.getImage());
    }

    private void diagonal(int xz, int yz, int xk, int ay, int brightness) {
        for (int x = 0; x <= Math.abs(xk - xz); x++) vram.setPixel(xz + x, yz + (x * ay),  brightness);
    }

    private void diagonalAA(int xz, int yz, int xk, int yk, int ay, int brightness) {
        int alB = (255 - brightness) / 2;
        vram.setPixel(xz, yz + ay, alB);
        vram.setPixel(xz, yz, brightness);
        for (int x = 1; x < Math.abs(xk - xz); x++) {
            vram.setPixel(xz + x, yz + (x * ay),  brightness);
            vram.setPixel(xz + x, yz + (x * ay) + 1, alB);
            vram.setPixel(xz + x, yz + (x * ay) - 1, alB);
        }
        vram.setPixel(xk, yk, brightness);
        vram.setPixel(xk, yk - ay, alB);
    }

    private void horizontal(int xz, int xk, int y, int brightness) {
        for (int x = xz; x <= xk; x++) vram.setPixel(x, y, brightness);
    }

    private void vertical(int yz, int yk, int x, int brightness) {
        if (yz > yk) {
            int tmp = yz;
            yz = yk;
            yk = tmp;
        }
        for (int y = yz; y <= yk; y++) vram.setPixel(x, y, brightness);
    }

    private void bresenhamX(int xz, int yz, int xk, int h, int h1, int h2, int ay, int brightness) {
        for (int x = xz, y = yz; x <= xk; x++) {
            if (h > 0) {
                h += h2;
                y += ay;
            } else h += h1;
            vram.setPixel(x, y, brightness);
        }
    }

    private void bresenhamXAA(int xz, int yz, int xk, int h, int h1, int h2, int ay, int brightness) {
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
            vram.setPixel(x, y + ay, step * (l - lc));
            vram.setPixel(x, y - ay, step * lc);
            vram.setPixel(x, y, brightness);
            lc++;
        }
    }

    private void bresenhamY(int xz, int yz, int yk, int h, int h1, int h2, int ax, int brightness) {
        for (int x = xz, y = yz; y <= yk; y++) {
            if (h > 0) {
                h += h2;
                x += ax;
            } else h += h1;
            vram.setPixel(x, y, brightness);
        }
    }

    private void bresenhamYAA(int xz, int yz, int yk, int h, int h1, int h2, int ax, int brightness) {
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
            vram.setPixel(x, y, brightness);
            vram.setPixel(x + ax, y, step * (l - lc));
            vram.setPixel(x - ax, y, step * lc);
            lc++;
        }
    }
}