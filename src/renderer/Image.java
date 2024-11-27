package renderer;

import mesh.DoubleColor;
import util.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class Image extends BufferedImage {

    private boolean infoOnImage;
    RenderSettings renderSettings;

    private static final int IMAGE_TYPE = TYPE_INT_ARGB;

    private static final Color COLOR_1 = new Color(230, 0, 255);
    private static final Color COLOR_2 = new Color(0, 0, 0);
    private static final int SQUARES_PER_SHORT_SIDE = 10;

    public Image(Dimension size) {
        this(size.width, size.height);
    }

    public Image(int width, int height) {
        super(width, height, IMAGE_TYPE);

        // Draw missing texture checkerboard
        int shortSideSize = Math.min(width, height);
        int squareSize = shortSideSize / SQUARES_PER_SHORT_SIDE;
        int borderSize = squareSize / 10;
        int borderSquareSize = squareSize / 10;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x < borderSize || width - borderSize < x || y < borderSize || height - borderSize < y) {
                    setRGB(x, y, (((x + y) / borderSquareSize) % 2 == 0) ? COLOR_1.getRGB() : COLOR_2.getRGB());
                } else {
                    setRGB(x, y, ((x / squareSize + y / squareSize) % 2 == 0) ? COLOR_1.getRGB() : COLOR_2.getRGB());
                }
            }
        }
    }

    public void writeToFile(RenderSettings settings) {
        writeToFile(settings.toFilenameString() + ".png", settings.logger);
    }

    public void writeToFile(String filename, Logger logger) {
        if (infoOnImage) {
            writeStrOnImage("Size: " + renderSettings.size.width + " x " + renderSettings.size.height,
                    0, 3);
            writeStrOnImage("Recursion Limit: " + renderSettings.recursionCount, 1, 3);
            writeStrOnImage("Frames: " + renderSettings.frameCount, 2, 3);
            writeStrOnImage("Threads: " + renderSettings.threadCount, 3, 3);
        }
        try {
            logger.logMsg(Util.getCurrentTime() + " Writing image to file " + filename + "... ");
            long start = System.nanoTime();
            String filepath = "output/renders/" + filename;
            File file = new File(filepath.endsWith(".png") ? filepath : filepath + ".png");
            ImageIO.write(this, "png", file);
            logger.logMsgLn("Done");
            logger.logElapsedTime("-> Write to file complete in: ", start);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeStrOnImage(String str, int lineNumber, int scale) {
        int posX = scale, posY = lineNumber * scale * TextRenderer.HEIGHT + scale + lineNumber;
        for (char c : str.toUpperCase().toCharArray()) {
            byte[][] letter = TextRenderer.renderChar(c);
            for (int i = 0, y = posY - scale; i < letter.length + scale; i++, y += scale) {
                for (int j = 0, x = posX - scale; j < letter[0].length + scale; j++, x += scale) {
                    for (int k = 0; k < scale; k++) {
                        for (int l = 0; l < scale; l++) {
                            safeSetRGB(x + k, y + l, Color.WHITE.getRGB());
                        }
                    }
                }
            }
            for (int i = 0, y = posY; i < letter.length; i++, y += scale) {
                for (int j = 0, x = posX; j < letter[i].length; j++, x += scale) {
                    for (int k = 0; k < scale; k++) {
                        for (int l = 0; l < scale; l++) {
                            safeSetRGB(x + k, y + l, (letter[i][j] == 0) ?
                                    Color.WHITE.getRGB() : Color.BLACK.getRGB());
                        }
                    }
                }
            }
            posX += (letter[0].length + 1) * scale;
        }
    }

    public Dimension getSize() {
        return new Dimension(getWidth(), getHeight());
    }

    public void safeSetRGB(int x, int y, int rgb) {
        if (-1 < x && x < getWidth() && -1 < y && y < getHeight()) {
            setRGB(x, y, rgb);
        }
    }

    public void setRGB(int x, int y, double[] rgb) {
        setRGB(x, y, new DoubleColor(rgb[0], rgb[1], rgb[2]).getRGB());
    }

    public void setRGB(int x, int y, DoubleColor color) {
        setRGB(x, y, color.getRGB());
    }

    public DoubleColor getDoubleColor(int x, int y) {
        return new DoubleColor();
    }

    public Image copy() {
        Image image = new Image(getSize());
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                image.setRGB(i, j, getRGB(i, j));
            }
        }
        return image;
    }

    public void includeInfo() {
        infoOnImage = true;
    }

    public static Image average(ArrayList<Image> imageList) {
        Image average = new Image(imageList.get(0).getWidth(), imageList.get(0).getHeight());
        DoubleColor[] colors = new DoubleColor[imageList.size()];
        for (int i = 0; i < imageList.get(0).getWidth(); i++) {
            for (int j = 0; j < imageList.get(0).getHeight(); j++) {
                for (int k = 0; k < imageList.size(); k++) {
                    colors[k] = imageList.get(k).getDoubleColor(i, j);
                }
                average.setRGB(i, j, DoubleColor.average(colors).getRGB());
            }
        }
        return average;
    }

    public static Image newTestImage(int width, int height, Color backgroundColor) {
        Image image = new Image(width, height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image.setRGB(i, j, backgroundColor.getRGB());
            }
        }
        return image;
    }
}
