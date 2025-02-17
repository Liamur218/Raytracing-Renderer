package mesh;

import java.awt.*;

import static mesh.NormColor.*;

public class Material {

    public NormColor color;

    // Emission & Reflection
    public float emissivity;
    public float reflectivity;
    public float specularity;

    // Refraction
    public float opacity;
    public float refractiveIndex;

    // id
    public int ID;
    private static int ID_COUNTER = 0;

    // Defaults
    public static final float DEFAULT_EMISSIVITY = 0;
    public static final float DEFAULT_REFLECTIVITY = 1;
    public static final float DEFAULT_SPECULARITY = 0;
    public static final float DEFAULT_OPACITY = 1;
    public static final float DEFAULT_REFRACTIVE_INDEX = 1;

    public static final Material AIR = new Material(WHITE);

    // Private templates
    private static final Material LIGHT = new Material(WHITE).setReflectivity(0).setEmissivity(1);
    private static final Material GLASS = new Material(WHITE).setSpecularity(1).setOpacity(0).setRefractiveIndex(1.5f);
    private static final Material MIRROR = new Material(WHITE).setSpecularity(1);

    // Basic materials
    public static final Material WHITE_MAT = new Material(WHITE);
    public static final Material LIGHT_GRAY_MAT = new Material(LIGHT_GRAY);
    public static final Material GRAY_MAT = new Material(GRAY);
    public static final Material DARK_GRAY_MAT = new Material(DARK_GRAY);
    public static final Material BLACK_MAT = new Material(BLACK);
    public static final Material RED_MAT = new Material(RED);
    public static final Material PINK_MAT = new Material(PINK);
    public static final Material ORANGE_MAT = new Material(ORANGE);
    public static final Material YELLOW_MAT = new Material(YELLOW);
    public static final Material GREEN_MAT = new Material(GREEN);
    public static final Material MAGENTA_MAT = new Material(MAGENTA);
    public static final Material CYAN_MAT = new Material(CYAN);
    public static final Material BLUE_MAT = new Material(BLUE);
    public static final Material PURPLE_MAT = new Material(PURPLE);

    // Emissive materials
    public static final Material WHITE_EMISSIVE_MAT = new Material(LIGHT).setColor(WHITE);
    public static final Material LIGHT_GRAY_EMISSIVE_MAT = new Material(LIGHT).setColor(LIGHT_GRAY);
    public static final Material GRAY_EMISSIVE_MAT = new Material(LIGHT).setColor(GRAY);
    public static final Material DARK_GRAY_EMISSIVE_MAT = new Material(LIGHT).setColor(DARK_GRAY);
    public static final Material BLACK_EMISSIVE_MAT = new Material(LIGHT).setColor(BLACK);
    public static final Material RED_EMISSIVE_MAT = new Material(LIGHT).setColor(RED);
    public static final Material PINK_EMISSIVE_MAT = new Material(LIGHT).setColor(PINK);
    public static final Material ORANGE_EMISSIVE_MAT = new Material(LIGHT).setColor(ORANGE);
    public static final Material YELLOW_EMISSIVE_MAT = new Material(LIGHT).setColor(YELLOW);
    public static final Material GREEN_EMISSIVE_MAT = new Material(LIGHT).setColor(GREEN);
    public static final Material MAGENTA_EMISSIVE_MAT = new Material(LIGHT).setColor(MAGENTA);
    public static final Material CYAN_EMISSIVE_MAT = new Material(LIGHT).setColor(CYAN);
    public static final Material BLUE_EMISSIVE_MAT = new Material(LIGHT).setColor(BLUE);
    public static final Material PURPLE_EMISSIVE_MAT = new Material(LIGHT).setColor(PURPLE);

    // Mirrors
    public static final Material WHITE_MIRROR = new Material(MIRROR).setColor(WHITE);
    public static final Material OFF_WHITE_MIRROR = new Material(MIRROR).setColor(OFF_WHITE);
    public static final Material LIGHT_GRAY_MIRROR = new Material(MIRROR).setColor(LIGHT_GRAY);
    public static final Material GRAY_MIRROR = new Material(MIRROR).setColor(GRAY);
    public static final Material DARK_GRAY_MIRROR = new Material(MIRROR).setColor(DARK_GRAY);
    public static final Material BLACK_MIRROR = new Material(MIRROR).setColor(BLACK);
    public static final Material RED_MIRROR = new Material(MIRROR).setColor(RED);
    public static final Material PINK_MIRROR = new Material(MIRROR).setColor(PINK);
    public static final Material ORANGE_MIRROR = new Material(MIRROR).setColor(ORANGE);
    public static final Material YELLOW_MIRROR = new Material(MIRROR).setColor(YELLOW);
    public static final Material GREEN_MIRROR = new Material(MIRROR).setColor(GREEN);
    public static final Material MAGENTA_MIRROR = new Material(MIRROR).setColor(MAGENTA);
    public static final Material CYAN_MIRROR = new Material(MIRROR).setColor(CYAN);
    public static final Material BLUE_MIRROR = new Material(MIRROR).setColor(BLUE);
    public static final Material PURPLE_MIRROR = new Material(MIRROR).setColor(PURPLE);

    // Glasses
    public static final Material WHITE_GLASS = new Material(GLASS).setColor(WHITE);
    public static final Material OFF_WHITE_GLASS = new Material(GLASS).setColor(OFF_WHITE);
    public static final Material LIGHT_GRAY_GLASS = new Material(GLASS).setColor(LIGHT_GRAY);
    public static final Material GRAY_GLASS = new Material(GLASS).setColor(GRAY);
    public static final Material DARK_GRAY_GLASS = new Material(GLASS).setColor(DARK_GRAY);
    public static final Material BLACK_GLASS = new Material(GLASS).setColor(BLACK);
    public static final Material RED_GLASS = new Material(GLASS).setColor(RED);
    public static final Material PINK_GLASS = new Material(GLASS).setColor(PINK);
    public static final Material ORANGE_GLASS = new Material(GLASS).setColor(ORANGE);
    public static final Material YELLOW_GLASS = new Material(GLASS).setColor(YELLOW);
    public static final Material GREEN_GLASS = new Material(GLASS).setColor(GREEN);
    public static final Material MAGENTA_GLASS = new Material(GLASS).setColor(MAGENTA);
    public static final Material CYAN_GLASS = new Material(GLASS).setColor(CYAN);
    public static final Material BLUE_GLASS = new Material(GLASS).setColor(BLUE);
    public static final Material PURPLE_GLASS = new Material(GLASS).setColor(PURPLE);

    public Material(NormColor color) {
        this.color = color;
        setReflectivity(DEFAULT_REFLECTIVITY);
        setEmissivity(DEFAULT_EMISSIVITY);
        setSpecularity(DEFAULT_SPECULARITY);
        setOpacity(DEFAULT_OPACITY);
        setRefractiveIndex(DEFAULT_REFRACTIVE_INDEX);

        ID = ID_COUNTER++;
    }

    public Material(Material material) {
        color = material.color;
        reflectivity = material.reflectivity;
        emissivity = material.emissivity;
        specularity = material.specularity;
        opacity = material.opacity;
        refractiveIndex = material.refractiveIndex;

        ID = ID_COUNTER++;
    }

    public Material copy() {
        return new Material(this);
    }

    public Material setColor(NormColor color) {
        this.color = color;
        return this;
    }

    public Material setColor(Color color) {
        return setColor(new NormColor(color));
    }

    public Material setColor(float r, float g, float b) {
        return setColor(new NormColor(r, g, b));
    }

    public Material setEmissivity(float emissivity) {
        this.emissivity = emissivity;
        return this;
    }

    public Material setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
        return this;
    }

    public Material setSpecularity(float specularity) {
        this.specularity = specularity;
        return this;
    }

    public Material setOpacity(float opacity) {
        this.opacity = opacity;
        return this;
    }

    public Material setRefractiveIndex(float refractiveIndex) {
        this.refractiveIndex = refractiveIndex;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Material material) {
            return ID == material.ID;
        }
        return false;
    }
}
