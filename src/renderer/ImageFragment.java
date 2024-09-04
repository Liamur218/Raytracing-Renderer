package renderer;

import mesh.DoubleColor;

import java.awt.*;
import java.io.Serializable;

public class ImageFragment implements Serializable {

    public static final Dimension SECTION_SIZE = new Dimension(300, 300);

    DoubleColor[][] array;
    public Dimension size;
    public int posX, posY;
    public int frameNumber;
    public int frameSpaceID;

    ImageFragment(int width, int height, int posX, int posY, int frameNumber, int frameSpaceID) {
        array = new DoubleColor[width][height];
        size = new Dimension(width, height);
        this.posX = posX;
        this.posY = posY;
        this.frameNumber = frameNumber;
        this.frameSpaceID = frameSpaceID;
    }

    void setRGB(int x, int y, DoubleColor color) {
        array[x][y] = color;
    }
}

