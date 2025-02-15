package renderer;

import mesh.*;

public class PostProcessor {

    boolean doAntiAliasing;

    boolean doEnhance;
    float enhancementFactor;

    public PostProcessor enableAntiAliasing() {
        doAntiAliasing = true;
        return this;
    }

    public PostProcessor enableLighten(float enhancementFactor) {
        doEnhance = true;
        this.enhancementFactor = enhancementFactor;
        return this;
    }

    void postProcess(Image image) {
        if (doAntiAliasing) {
            Image original = image.copy();
            for (int pixelX = 0; pixelX < image.getWidth(); pixelX++) {
                for (int pixelY = 0; pixelY < image.getHeight(); pixelY++) {
                    int pixelsInSample = 0;
                    NormColor color = new NormColor();
                    for (int x = Math.max(pixelX - 1, 0); x < Math.min(pixelX + 1, image.getWidth()); x++) {
                        for (int y = Math.max(pixelY - 1, 0); y < Math.min(pixelY + 1, image.getHeight()); y++) {
                            color.add(original.getRGB(x, y));
                            pixelsInSample++;
                        }
                    }
                    color.add(new NormColor(original.getRGB(pixelX, pixelY)).multiply(9));
                    image.setRGB(pixelX, pixelY, color.divide(pixelsInSample + 9).getRGB());
                }
            }
        }
        if (doEnhance) {
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    NormColor color = image.getDoubleColor(i, j);
                    color.multiply(enhancementFactor);
                    image.setRGB(i, j, color);
                }
            }
        }
    }
}
