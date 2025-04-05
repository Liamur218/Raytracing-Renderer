package mesh;

import util.Util;

import java.awt.*;
import java.io.Serializable;

public class NormColor implements Serializable {

    public float r, g, b;

    public static final int LENGTH = 3;
    public static final int RGB_MAX = 255;

    public static final NormColor WHITE = new NormColor(Color.WHITE);
    public static final NormColor LIGHT_GRAY = new NormColor(Color.LIGHT_GRAY);
    public static final NormColor GRAY = new NormColor(Color.GRAY);
    public static final NormColor DARK_GRAY = new NormColor(Color.DARK_GRAY);
    public static final NormColor BLACK = new NormColor(Color.BLACK);
    public static final NormColor RED = new NormColor(Color.RED);
    public static final NormColor PINK = new NormColor(Color.PINK);
    public static final NormColor ORANGE = new NormColor(Color.ORANGE);
    public static final NormColor YELLOW = new NormColor(Color.YELLOW);
    public static final NormColor GREEN = new NormColor(Color.GREEN);
    public static final NormColor MAGENTA = new NormColor(Color.MAGENTA);
    public static final NormColor CYAN = new NormColor(Color.CYAN);
    public static final NormColor BLUE = new NormColor(Color.BLUE);
    public static final NormColor LIGHT_BLUE = new NormColor(new Color(0, 128, 255));
    public static final NormColor PURPLE = new NormColor(new Color(161, 0, 255));
    public static final NormColor OFF_WHITE = new NormColor(new Color(230, 230, 230));

    public NormColor() {
        this(0, 0, 0);
    }

    public NormColor(float f) {
        this(f, f, f);
    }

    public NormColor(int rgb) {
        this(((rgb & 0x00FF0000) >> 16) / 255f, ((rgb & 0x0000FF00) >> 8) / 255f, (rgb & 0x000000FF) / 255f);
    }

    public NormColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public NormColor(float[] rgb) {
        this(rgb[0], rgb[1], rgb[2]);
    }

    public NormColor(Color color) {
        this(color.getRGB());
    }

    public NormColor(NormColor color) {
        this(color.r, color.g, color.b);
    }

    public void set(NormColor color) {
        set(color.r, color.g, color.b);
    }

    public void set(float r, float g, float b) {
        this.r = r;
        this.b = b;
        this.g = g;
    }

    public void set(int r, int g, int b) {
        set(r / 255f, g / 255f, b / 255f);
    }

    public void set(int rgb) {
        set((rgb & 0x00FF0000) >> 16, (rgb & 0x0000FF00) >> 8, (rgb & 0x000000FF));
    }

    public void set(Color color) {
        set(color.getRGB());
    }

    public int getRGB() {
        return 0xFF000000 +
                ((int) (Util.clamp(r, 0, 1) * 255) << 16) +
                ((int) (Util.clamp(g, 0, 1) * 255) << 8) +
                (int) (Util.clamp(b, 0, 1) * 255);
    }

    public double[] toDoubleArray() {
        return new double[]{r, g, b};
    }

    public NormColor add(NormColor color) {
        r += color.r;
        g += color.g;
        b += color.b;
        return this;
    }

    public NormColor add(int rgb) {
        r += ((rgb & 0x00FF0000 >> 16) / 255f);
        g += ((rgb & 0x0000FF00 >> 8) / 255f);
        b += ((rgb & 0x000000FF) / 255f);
        return this;
    }

    public static NormColor add(NormColor color1, NormColor color2) {
        return new NormColor(color1.r + color2.r, color1.g + color2.g, color1.b + color2.b);
    }

    public NormColor subtract(NormColor color) {
        r -= color.r;
        g -= color.g;
        b -= color.b;
        return this;
    }

    public static NormColor subtract(NormColor color1, NormColor color2) {
        return new NormColor(color1.r - color2.r, color1.g - color2.g, color1.b - color2.b);
    }

    public static NormColor multiply(NormColor color, float a) {
        return new NormColor(color).multiply(a);
    }

    public NormColor multiply(float a) {
        r *= a;
        g *= a;
        b *= a;
        return this;
    }

    public NormColor divide(float a) {
        r /= a;
        g /= a;
        b /= a;
        return this;
    }

    public NormColor multiply(NormColor color) {
        r *= color.r;
        g *= color.g;
        b *= color.b;
        return this;
    }

    public static NormColor multiply(NormColor color1, NormColor color2) {
        return new NormColor(color1.r * color2.r, color1.g * color2.g, color1.b * color2.b);
    }

    public static NormColor average(NormColor... colors) {
        float r = 0, g = 0, b = 0;
        for (NormColor color : colors) {
            r += color.r;
            g += color.g;
            b += color.b;
        }
        return new NormColor(r / colors.length, g / colors.length, b / colors.length);
    }

    public static NormColor interpolate(NormColor low, NormColor high, float index) {
        return new NormColor(
                Util.linearInterpolation(0, 1, index, low.r, high.r),
                Util.linearInterpolation(0, 1, index, low.g, high.g),
                Util.linearInterpolation(0, 1, index, low.b, high.b));
    }

    @Override
    public String toString() {
        return "{" + r + ", " + g + ", " + b + "}";
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof NormColor color) {
            return color.r == r && color.g == g && color.b == b;
        }
        return false;
    }
}
