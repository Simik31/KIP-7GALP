package cz.osu;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class KU_4 {
    public static void main(String[] args) {
        int[][][] masks = new int[][][]{
            new int[][] {
                new int[]{ 1, 2, 1 },
                new int[]{ 2, 4, 2 },
                new int[]{ 1, 2, 1 }
            },
            new int[][] {
                new int[]{ 1, 1, 1, 1, 1 },
                new int[]{ 1, 3, 3, 3, 1 },
                new int[]{ 1, 3, 6, 3, 1 },
                new int[]{ 1, 3, 3, 3, 1 },
                new int[]{ 1, 1, 1, 1, 1 }
            },
            new int[][] {
                new int[]{ 0, 1, 1, 2, 1, 1, 0 },
                new int[]{ 1, 1, 2, 4, 2, 1, 1 },
                new int[]{ 1, 2, 4, 6, 4, 2, 1 },
                new int[]{ 2, 4, 6, 8, 6, 4, 2 },
                new int[]{ 1, 2, 4, 6, 4, 2, 1 },
                new int[]{ 1, 1, 2, 4, 2, 1, 1 },
                new int[]{ 0, 1, 1, 2, 1, 1, 0 }
            }
        };

        String[] fNames = new String[] { "Ciri.png", "Me.jpg", "spsei.png" };
        int[] Ts = new int[]{0, 10, 30, 50, 100, 150, 200, 255};

        try {
            for (String fName : fNames) {
                BufferedImage in = ImageIO.read(new File(fName));
                String inName = fName.split("\\.")[0];
                if (!Files.exists(Paths.get(inName))) Files.createDirectory(Paths.get(inName));
                ArrayList<BufferedImage> out = new ArrayList<>();
                for (int ignored : Ts) out.add(new BufferedImage(in.getWidth(), in.getHeight(), in.getType()));
                HashMap<Integer, HashMap<Integer, Integer>> map = new HashMap<>();
                for (int x = 0; x < in.getWidth(); x++) {
                    map.put(x, new HashMap<>());
                    for (int y = 0; y < in.getHeight(); y++) {
                        Color p = new Color(in.getRGB(x, y));
                        map.get(x).put(y, (int) (0.299 * p.getRed() + 0.587 * p.getGreen() + 0.114 * p.getBlue()));
                    }
                }

                System.out.println("\n" + fName);
                long pixels = (long) map.size() * map.get(0).size();
                long totalOps = 0;
                System.out.format(" - pixels: %,d\n", pixels);
                for (int[][] mask : masks) {
                    int ops = mask.length * mask[0].length;
                    totalOps += pixels * ops;
                    System.out.format(" - mask_%dx%d\n   - Operations per pixel: %,d\n   - Mask total operations: %,d\n", mask.length, mask[0].length, ops, pixels * ops);
                }
                System.out.format(" - Image total operations: %,d\n\n", totalOps);

                for (int[][] mask : masks) {
                    String mask_dir = inName + "\\mask_" + mask.length + "x" + mask[0].length;
                    if (!Files.exists(Paths.get(mask_dir))) Files.createDirectory(Paths.get(mask_dir));
                    for (int x = 0; x < map.size(); x++) {
                        for (int y = 0; y < map.get(0).size(); y++) {
                            int f = 0;
                            for (int i = x - (mask[0].length - 1) / 2, mask_x = 0, mask_y = 0; i <= x + (mask[0].length - 1) / 2; i++, mask_y = 0, mask_x++)
                                for (int j = y - (mask.length - 1) / 2; j <= y + (mask.length - 1) / 2; j++, mask_y++) {
                                    int k = Math.min(Math.max(i, 0), map.size() - 1);
                                    int l = Math.min(Math.max(j, 0), map.get(0).size() - 1);
                                    f += map.get(k).get(l) * mask[mask_y][mask_x];
                                }
                            f /= Arrays.stream(mask).mapToInt(arr -> Arrays.stream(arr).sum()).sum();
                            int a = map.get(x).get(y);
                            for (int i = 0; i < Ts.length; i++)
                                out.get(i).setRGB(x, y, ((Math.abs(f - a) < Ts[i]) ? new Color(f, f, f).getRGB() : new Color(a, a, a).getRGB()));
                        }
                    }
                    for (int i = 0; i < Ts.length; i++) {
                        System.out.println(mask_dir + "\\t_" + Ts[i] + ".png");
                        ImageIO.write(out.get(i), "png", new File(mask_dir + "\\t_" + Ts[i] + ".png"));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
