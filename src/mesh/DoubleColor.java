package mesh;

import util.Util;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class DoubleColor implements Serializable {

    public double r, g, b;

    public static final int LENGTH = 3;

    public static final DoubleColor WHITE = new DoubleColor(Color.WHITE);
    public static final DoubleColor LIGHT_GRAY = new DoubleColor(Color.LIGHT_GRAY);
    public static final DoubleColor GRAY = new DoubleColor(Color.GRAY);
    public static final DoubleColor DARK_GRAY = new DoubleColor(Color.DARK_GRAY);
    public static final DoubleColor BLACK = new DoubleColor(Color.BLACK);
    public static final DoubleColor RED = new DoubleColor(Color.RED);
    public static final DoubleColor PINK = new DoubleColor(Color.PINK);
    public static final DoubleColor ORANGE = new DoubleColor(Color.ORANGE);
    public static final DoubleColor YELLOW = new DoubleColor(Color.YELLOW);
    public static final DoubleColor GREEN = new DoubleColor(Color.GREEN);
    public static final DoubleColor MAGENTA = new DoubleColor(Color.MAGENTA);
    public static final DoubleColor CYAN = new DoubleColor(Color.CYAN);
    public static final DoubleColor BLUE = new DoubleColor(Color.BLUE);
    public static final DoubleColor PURPLE = new DoubleColor(new Color(161, 0, 255));
    public static final DoubleColor OFF_WHITE = new DoubleColor(new Color(230, 230, 230));

    public DoubleColor() {
        this(0, 0, 0);
    }

    public DoubleColor(double x) {
        this(x, x, x);
    }

    public DoubleColor(int rgb) {
        this(new Color(rgb));
    }

    public DoubleColor(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public DoubleColor(double[] rgb) {
        this(rgb[0], rgb[1], rgb[2]);
    }

    public DoubleColor(Color color) {
        this((double) color.getRed() / 255, (double) color.getGreen() / 255, (double) color.getBlue() / 255);
    }

    public DoubleColor(DoubleColor color) {
        this(color.r, color.g, color.b);
    }

    public DoubleColor cap() {
        r = Util.clamp(r, 0, 1);
        g = Util.clamp(g, 0, 1);
        b = Util.clamp(b, 0, 1);
        return this;
    }

    public void set(DoubleColor color) {
        set(color.r, color.g, color.b);
    }

    public void set(double r, double g, double b) {
        this.r = r;
        this.b = b;
        this.g = g;
    }

    public void set(int r, int g, int b) {
        set(new Color(r, g, b));
    }

    public void set(Color color) {
        set(new DoubleColor(color));
    }

    public int getRGB() {
        //return ((((int) (r * 255)) & 0xFF) << 16) | ((((int) (g * 255)) & 0xFF) << 8)  | ((((int) (b * 255)) & 0xFF));
        DoubleColor capped = new DoubleColor(this).cap();
        return new Color((int) (capped.r * 255), (int) (capped.g * 255), (int) (capped.b * 255)).getRGB();
    }

    public double[] toDoubleArray() {
        return new double[]{ r, g, b };
    }

    public DoubleColor add(DoubleColor color) {
        r += color.r;
        g += color.g;
        b += color.b;
        return this;
    }

    public static DoubleColor add(DoubleColor color1, DoubleColor color2) {
        return new DoubleColor(color1.r + color2.r, color1.g + color2.g, color1.b + color2.b);
    }

    public DoubleColor subtract(DoubleColor color) {
        r -= color.r;
        g -= color.g;
        b -= color.b;
        return this;
    }

    public static DoubleColor subtract(DoubleColor color1, DoubleColor color2) {
        return new DoubleColor(color1.r - color2.r, color1.g - color2.g, color1.b - color2.b);
    }

    public static DoubleColor multiply(DoubleColor color, double a) {
        return new DoubleColor(color).multiply(a);
    }

    public DoubleColor multiply(double a) {
        r *= a;
        g *= a;
        b *= a;
        return this;
    }

    public DoubleColor multiply(DoubleColor color) {
        r *= color.r;
        g *= color.g;
        b *= color.b;
        return this;
    }

    public static DoubleColor multiply(DoubleColor color1, DoubleColor color2) {
        return new DoubleColor(color1.r * color2.r, color1.g * color2.g, color1.b * color2.b);
    }

    public static DoubleColor average(ArrayList<DoubleColor> colors) {
        double r = 0, g = 0, b = 0;
        for (DoubleColor color : colors) {
            r += color.r;
            g += color.g;
            b += color.b;
        }
        return new DoubleColor(r / colors.size(), g / colors.size(), b / colors.size());
    }

    public static DoubleColor average(DoubleColor[] colors) {
        double r = 0, g = 0, b = 0;
        for (DoubleColor color : colors) {
            r += color.r;
            g += color.g;
            b += color.b;
        }
        return new DoubleColor(r / colors.length, g / colors.length, b / colors.length);
    }

    public static DoubleColor interpolate(DoubleColor low, DoubleColor high, double index) {
        return new DoubleColor(
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
        if (object instanceof DoubleColor color) {
            return color.r == r && color.g == g && color.b == b;
        }
        return false;
    }
}
