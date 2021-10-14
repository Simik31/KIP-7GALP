package cz.osu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Martin Šimara (R19584) - KIP/7GALP - Koresponednční úkol 1 - Generujte úsečku
 * pomocí Vámi navrhnutého algoritmu.
 */
public class KU1 {
    private V_RAM vram;
    private ImagePanel imagePanel;

    /**
     * @param vram       V_RAM object to draw lines to
     * @param imagePanel ImagePanel object that contains V_RAM - used to redraw
     *                   scene
     * @param sleep      boolean indicating if there should be slight delay while
     *                   drawing grid lines.
     */
    public KU1(V_RAM vram, ImagePanel imagePanel, boolean sleep) {
        this.vram = vram;
        this.imagePanel = imagePanel;

        // PlotGrid(sleep);
        // waitAndClear();
        // PlotAA();
        // waitAndClear();
        PlotClock();
    }

    /**
     * Sleep for 500ms. This adds delay between each line drawing. Only called if
     * _sleep is true or when waitAndClear() is called.
     */
    private void trySleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delay clearScreen() by sleep time
     */
    private void waitAndClear() {
        trySleep();
        vram.fill(255);
    }

    /**
     * Generate array of points for grid example.
     * 
     * @param x_array array of X positions
     * @param y_array array of Y positions
     * @return array of points with X and Y coordinates from x_array and y_array
     */
    private List<Point> GenerateAbsolutePoints(List<Integer> x_list, List<Integer> y_list) {
        assert x_list.size() == y_list.size();

        List<Point> points = new ArrayList<>(x_list.size());

        for (int i = 0; i < x_list.size(); i++)
            points.add(new Point(x_list.get(i), y_list.get(i)));

        return points;
    }

    /**
     * Generate array of points for grid example.
     * 
     * @param x_array array of X positions
     * @param y_array array of Y positions
     * @return array of points with X and Y coordinates from x_array and y_array
     */
    private List<Point> GenerateRelativePoints(List<Integer> x_list, List<Integer> y_list) {
        assert x_list.size() == y_list.size();

        double x_ratio = vram.getWidth() / 100.0;
        double y_ratio = vram.getHeight() / 100.0;

        List<Point> points = new ArrayList<>(x_list.size());

        for (int i = 0; i < x_list.size(); i++)
            points.add(new Point(x_list.get(i) * x_ratio, y_list.get(i) * y_ratio));

        return points;
    }

    /**
     * Generate array of lines for grid example.
     * 
     * @param p_array array of Point2D to generate lines for
     * @param a_array array of indexes as first point of each line
     * @param b_array array of indexes as second point of each line
     * @return array of lines with points from p_array, with indexes from a_array
     *         and b_array
     */
    private List<Line> GenerateLines(List<Point> points, List<Integer> a_list, List<Integer> b_list) {
        assert a_list.size() == b_list.size();
        assert points.size() >= a_list.size();

        List<Line> lines = new ArrayList<>(a_list.size());

        for (int i = 0; i < a_list.size(); i++)
            lines.add(new Line(points.get(a_list.get(i)), points.get(b_list.get(i))));

        return lines;
    }

    /**
     * Draw horizontal line.
     * 
     * @param line       Line to draw
     * @param brightness brightness of line.
     */
    private void DrawLineHorizontal(Line line, int brightness) {
        int x = line.getStart().getXInt();
        int xEnd = line.getEnd().getXInt();
        int y = line.getStart().getYInt();

        for (; x <= xEnd; x++)
            vram.setPixel(x, y, brightness);
    }

    /**
     * Draw vertical line.
     * 
     * @param line       Line to draw
     * @param brightness brightness of line.
     */
    private void DrawLineVertical(Line line, int brightness) {
        if (line.getStart().getY() > line.getEnd().getY())
            line.swapPoints();

        int y = line.getStart().getYInt();
        int yEnd = line.getEnd().getYInt();
        int x = line.getStart().getXInt();

        for (; y <= yEnd; y++)
            vram.setPixel(x, y, brightness);

    }

    /**
     * Draw line with bresenham algorithm (for lines with angle < 45° from x axis)
     * 
     * @param line         line to draw
     * @param brightness   brightness of line
     * @param antialiasing indicates if line should be drawn with antialiasing
     */
    private void DrawLineBresenhamX(Line line, int brightness, boolean antialiasing) {
        int x = line.getStart().getXInt();
        int xEnd = line.getEnd().getXInt();
        int y = line.getStart().getYInt();

        int h1 = 2 * line.getDeltaYInt();
        int h2 = h1 - 2 * line.getDeltaXReversedInt();
        int h = h1 - line.getDeltaXReversedInt();

        int slope = (line.getStart().getY() < line.getEnd().getY()) ? 1 : -1;

        if (antialiasing) {
            int l = Math.abs(h / h1) + 1;
            int lc = 1;
            int step = (255 - brightness) / l;

            for (; x <= xEnd; x++) {
                if (h > 0) {
                    h += h2;
                    y += slope;
                    l = Math.abs(h / h1) + 2;
                    if (l > xEnd - x)
                        l -= xEnd - x - 1;
                    lc = 1;
                    step = (255 - brightness) / l;
                } else
                    h += h1;
                vram.setPixel(x, y + slope, step * (l - lc));
                vram.setPixel(x, y - slope, step * lc);
                vram.setPixel(x, y, brightness);
                lc++;
            }
        } else {
            for (; x <= xEnd; x++) {
                if (h > 0) {
                    h += h2;
                    y += slope;
                } else
                    h += h1;

                vram.setPixel(x, y, brightness);
            }
        }
    }

    /**
     * Draw line with bresenham algorithm (for lines with angle < 45° from y axis)
     * 
     * @param line         line to draw
     * @param brightness   brightness of line
     * @param antialiasing indicates if line should be drawn with antialiasing
     */
    private void DrawLineBresenhamY(Line line, int brightness, boolean antialiasing) {
        int x = line.getStart().getXInt();
        int y = line.getStart().getYInt();
        int yEnd = line.getEnd().getYInt();

        int h1 = 2 * line.getDeltaXInt();
        int h2 = h1 - 2 * line.getDeltaYReversedInt();
        int h = h1 - line.getDeltaYReversedInt();

        int slope = (line.getStart().getX() < line.getEnd().getX()) ? 1 : -1;

        if (antialiasing) {
            int l = Math.abs(h / h1) + 1;
            int lc = 1;
            int step = (255 - brightness) / l;

            for (; y <= yEnd; y++) {
                if (h > 0) {
                    h += h2;
                    x += slope;
                    l = Math.abs(h / h1) + 2;
                    if (l > yEnd - y)
                        l -= yEnd - y - 1;
                    lc = 1;
                    step = (255 - brightness) / l;
                } else
                    h += h1;
                vram.setPixel(x, y, brightness);
                vram.setPixel(x + slope, y, step * (l - lc));
                vram.setPixel(x - slope, y, step * lc);
                lc++;
            }
        } else {
            for (; y <= yEnd; y++) {
                if (h > 0) {
                    h += h2;
                    x += slope;
                } else
                    h += h1;

                vram.setPixel(x, y, brightness);
            }
        }
    }

    /**
     * Draw diagonal line with (lines with angle = 45°)
     * 
     * @param line         line to draw
     * @param brightness   brightness of line
     * @param antialiasing indicates if line should be drawn with antialiasing
     */
    private void DrawLineDiagonal(Line line, int brightness, boolean antialiasing) {
        int slope = (line.getStart().getY() < line.getEnd().getY()) ? 1 : -1;

        if (antialiasing) {
            int aa_brightness = (255 - brightness) / 2;
            int xStart = line.getStart().getXInt();
            int xEnd = line.getEnd().getXInt();
            int yStart = line.getStart().getYInt();
            int yEnd = line.getEnd().getYInt();

            vram.setPixel(xStart, yStart + slope, aa_brightness);
            vram.setPixel(xStart, yStart, brightness);

            for (int x = 1; x < Math.abs(xEnd - xStart); x++) {
                vram.setPixel(xStart + x, yStart + (x * slope), brightness);
                vram.setPixel(xStart + x, yStart + (x * slope) + 1, aa_brightness);
                vram.setPixel(xStart + x, yStart + (x * slope) - 1, aa_brightness);
            }

            vram.setPixel(xEnd, yEnd, brightness);
            vram.setPixel(xEnd, yEnd - slope, aa_brightness);
        } else {
            int xEnd = line.getDeltaXReversedInt();
            int xStart = (int) line.getStart().getX();
            int yStart = (int) line.getStart().getY();

            for (int x = 0; x <= xEnd; x++)
                vram.setPixel(xStart + x, yStart + (x * slope), brightness);
        }
    }

    /**
     * Selects algorithm co draw line (and than draws it)
     * 
     * @param line         line to draw
     * @param brightness   brightness of the line
     * @param redraw       indicates whether to redraw after drawing every line
     * @param antialiasing indicates if line should be drawn with antialiasing
     */
    private void DrawLine(Line line, int brightness, boolean redraw, boolean antialiasing) {
        if (line.getStart().getX() > line.getEnd().getX())
            line.swapPoints();

        if (line.getDeltaY() == 0)
            DrawLineHorizontal(line, brightness);

        else if (line.getDeltaX() == 0)
            DrawLineVertical(line, brightness);

        else if (line.getDeltaX() > line.getDeltaY())
            DrawLineBresenhamX(line, brightness, antialiasing);

        else if (line.getDeltaX() < line.getDeltaY()) {
            if (line.getStart().getY() > line.getEnd().getY())
                line.swapPoints();
            DrawLineBresenhamY(line, brightness, antialiasing);
        }

        else
            DrawLineDiagonal(line, brightness, antialiasing);

        if (redraw)
            imagePanel.setImage(vram.getImage());
    }

    /**
     * Draws the line and if sleep is true, adds 500ms delay
     * 
     * @param line         line to draw
     * @param brightness   brightness of the line
     * @param sleep        indicates if there should be delay after drawing the line
     * @param redraw       indicates whether to redraw after drawing every line
     * @param antialiasing indicates if line should be drawn with antialiasing
     */
    private void PlotLine(Line line, int brightness, boolean sleep, boolean redraw, boolean antialiasing) {
        DrawLine(line, brightness, redraw, antialiasing);

        if (sleep)
            trySleep();
    }

    /**
     * Draw lines with given index and brightness values
     * 
     * @param lines          array of lines to draw
     * @param l_indexes      array of lines to draw
     * @param l_brightnesses array of brightnesses to draw
     * @param sleep          indicates if there should be delay after drawing the
     *                       line
     * @param redraw         indicates whether to redraw after drawing every line
     */
    private void PlotLines(List<Line> lines, List<Integer> indexes, List<Integer> brightnesses, boolean sleep,
            boolean redraw) {
        assert indexes.size() == brightnesses.size();
        assert lines.size() >= indexes.size();

        for (int i = 0; i < indexes.size(); i++)
            PlotLine(lines.get(indexes.get(i)), brightnesses.get(i), sleep, redraw, false);

        imagePanel.setImage(vram.getImage());
    }

    private void DrawCircle(int x, int y, int p, int q) {
        System.out.println("DrawCircle(" + x + ", " + y + ", " + p + ", " + q + ")");

        vram.setPixel(x + p, y + q, 0);
        vram.setPixel(x - p, y + q, 0);
        vram.setPixel(x + p, y - q, 0);
        vram.setPixel(x - p, y - q, 0);
        vram.setPixel(x + q, y + p, 0);
        vram.setPixel(x - q, y + p, 0);
        vram.setPixel(x + q, y + p, 0);
        vram.setPixel(x - q, y + p, 0);

        imagePanel.setImage(vram.getImage());
        trySleep();
    }

    private void GenerateCirclePoints(Point center, int radius) {
        int x = center.getXInt();
        int y = center.getYInt();
        int r = radius;
        int p = 0;
        int q = r;
        int d = 3 - 2 * r;

        while (p <= q) {
            DrawCircle(x, y, p, q);
            p++;
            if (d < 0)
                d += 4 * p + 6;
            else {
                r -= 1;
                d += 4 * (p - q) + 10;
            }
            DrawCircle(x, y, p, q);
        }
    }

    /**
     * Plot Grid test pattern
     * 
     * @param _sleep indicates if there should be delay after drawing the line
     */
    private void PlotGrid(boolean _sleep) {
        List<Integer> x_list = Arrays.asList(50, 10, 10, 10, 10, 10, 30, 50, 70, 90, 90, 90, 90, 90, 70, 50, 30);
        List<Integer> y_list = Arrays.asList(50, 90, 70, 50, 30, 10, 10, 10, 10, 10, 30, 50, 70, 90, 90, 90, 90);

        List<Point> points = GenerateRelativePoints(x_list, y_list);

        List<Integer> a_list = Arrays.asList(5, 9, 13, 1, 2, 3, 4, 6, 7, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0);
        List<Integer> b_list = Arrays.asList(9, 13, 1, 5, 12, 11, 10, 16, 15, 14, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
                13, 14, 15, 16);

        List<Line> lines = GenerateLines(points, a_list, b_list);

        List<Integer> l_bg_indexes = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        List<Integer> l_bg_brightnesses = Arrays.asList(0, 0, 0, 0, 220, 220, 220, 220, 220, 220);

        PlotLines(lines, l_bg_indexes, l_bg_brightnesses, false, false);

        List<Integer> l_indexes = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25);
        List<Integer> l_brightnesses = Arrays.asList(100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
                100, 100, 100);

        PlotLines(lines, l_indexes, l_brightnesses, _sleep, true);
    }

    /**
     * Plot antialiasing test pattern
     */
    private void PlotAA() {
        List<Integer> x_list = Arrays.asList(5, 95, 5, 95, 60, 40, 40, 60, 5, 95, 95, 5);
        List<Integer> y_list = Arrays.asList(60, 40, 40, 60, 5, 95, 5, 95, 5, 95, 5, 95);

        List<Point> points = GenerateRelativePoints(x_list, y_list);

        List<Integer> a_list = Arrays.asList(0, 2, 4, 6, 8, 10);
        List<Integer> b_list = Arrays.asList(1, 3, 5, 7, 9, 11);

        List<Line> lines = GenerateLines(points, a_list, b_list);

        for (int i = 0; i < lines.size(); i++) {
            DrawLine(lines.get(i), 0, true, true);
            waitAndClear();
        }
    }

    private void PlotClock() {
        Point center = new Point(50, 50);
        GenerateCirclePoints(center, 45);
    }
}
