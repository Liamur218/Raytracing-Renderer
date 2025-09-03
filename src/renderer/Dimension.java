package renderer;

public class Dimension {

    public int width, height;

    public Dimension() {
        this(0, 0);
    }

    public Dimension(int size) {
        this(size, size);
    }

    public Dimension(Dimension dimension) {
        this(dimension.width, dimension.height);
    }

    public Dimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static Dimension multiply(Dimension dimension, float scalar) {
        return new Dimension(dimension).multiply(scalar);
    }

    public Dimension multiply(float scalar) {
        width = (int) (width * scalar);
        height = (int) (height * scalar);
        return this;
    }
}
