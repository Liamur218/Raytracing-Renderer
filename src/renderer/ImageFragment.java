package renderer;

import mesh.NormColor;

import java.awt.*;
import java.io.Serializable;

public class ImageFragment implements Serializable {

    public static final Dimension SECTION_SIZE = new Dimension(300, 300);

    NormColor[][] array;
    public Dimension size;
    public int posX, posY;
    public int frameNumber;
    public int frameSpaceID;

    ImageFragment(int width, int height, int posX, int posY, int frameNumber, int frameSpaceID) {
        size = new Dimension(width, height);
        this.posX = posX;
        this.posY = posY;
        this.frameNumber = frameNumber;
        this.frameSpaceID = frameSpaceID;
    }

    public void initialize() {
        array = new NormColor[size.width][size.height];
    }

    void setRGB(int x, int y, NormColor color) {
        array[x][y] = color;
    }
}

