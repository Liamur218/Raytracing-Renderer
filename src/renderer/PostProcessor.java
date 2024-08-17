package renderer;

import mesh.DoubleColor;

import javax.swing.plaf.IconUIResource;

public class PostProcessor {

    boolean doAntiAliasing;

    public PostProcessor(boolean doAntiAliasing) {
        this.doAntiAliasing = doAntiAliasing;
    }

    void postProcess(Image image) {
        if (doAntiAliasing) {
            Image original = image.copy();
            for (int pixelX = 0; pixelX < image.getWidth(); pixelX++) {
                for (int pixelY = 0; pixelY < image.getHeight(); pixelY++) {
                    int pixelsInSample = 0;
                    DoubleColor color = new DoubleColor();
                    for (int x = Math.max(pixelX - 1, 0); x < Math.min(pixelX + 1, image.getWidth()); x++) {
                        for (int y = Math.max(pixelY - 1, 0); y < Math.min(pixelY + 1, image.getHeight()); y++) {
                            color.add(new DoubleColor(original.getRGB(x, y)));
                            pixelsInSample++;
                        }
                    }
                    //color.add(new DoubleColor(original.getRGB(pixelX, pixelY)));
                    image.setRGB(pixelX, pixelY, color.multiply(1.0 / pixelsInSample).getRGB());
                }
            }
        }
    }
}
