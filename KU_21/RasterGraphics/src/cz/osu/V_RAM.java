package cz.osu;

import java.awt.*;
import java.awt.image.BufferedImage;

public class V_RAM {

    private int width;
    private int height;
    private int[][] rawData;

    public V_RAM(int width, int height){

        this.width = width;
        this.height = height;
        rawData = new int[height][width];
    }

    public int getWidth(){

        return width;
    }

    public int getHeight(){

        return height;
    }

    public int[][] getRawData(){

        return rawData;
    }

    public void setPixel(int x, int y, int brightness){

        brightness = Math.min(255, Math.max(0, brightness));

        rawData[y][x] = brightness;
    }

    public int getPixel(int x, int y){

        return rawData[y][x];
    }

    public BufferedImage getImage(){

        int[] rgbArray = new int[width * height];
        int counter = 0;
        int value;

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){

                value = rawData[y][x];

                rgbArray[counter++] = 255 << 24 | value << 16 | value << 8 | value;
            }
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, rgbArray, 0, width);

        return image;
    }
}
