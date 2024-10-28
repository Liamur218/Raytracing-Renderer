package mesh;

import static mesh.DoubleColor.*;

public class Material {

    public DoubleColor color;

    // Emission & Reflection
    public double emissivity;
    public double reflectivity;
    public double specularity;

    // Refraction
    public double opacity;
    public double refractiveIndex;

    // ID
    public int ID;
    private static int ID_COUNTER = 0;

    // Defaults
    public static final double DEFAULT_EMISSIVITY = 0;
    public static final double DEFAULT_REFLECTIVITY = 1;
    public static final double DEFAULT_SPECULARITY = 0;
    public static final double DEFAULT_OPACITY = 1;
    public static final double DEFAULT_REFRACTIVE_INDEX = 1;

    public static final Material AIR = new Material(WHITE);

    // Private templates
    private static final Material LIGHT = new Material(WHITE).setReflectivity(0).setEmissivity(1);
    private static final Material GLASS = new Material(WHITE).setSpecularity(1).setOpacity(0.1).setRefractiveIndex(1.5);
    private static final Material MIRROR = new Material(WHITE).setSpecularity(1);

    // Basic materials
    public static final Material WHITE_MAT = new Material(WHITE);
    public static final Material L_GRAY_MAT = new Material(LIGHT_GRAY);
    public static final Material GRAY_MAT = new Material(GRAY);
    public static final Material D_GRAY_MAT = new Material(DARK_GRAY);
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
    public static final Material WHITE_E_MAT = new Material(LIGHT).setColor(WHITE);
    public static final Material LIGHT_GRAY_E_MAT = new Material(LIGHT).setColor(LIGHT_GRAY);
    public static final Material GRAY_E_MAT = new Material(LIGHT).setColor(GRAY);
    public static final Material DARK_GRAY_E_MAT = new Material(LIGHT).setColor(DARK_GRAY);
    public static final Material BLACK_E_MAT = new Material(LIGHT).setColor(BLACK);
    public static final Material RED_E_MAT = new Material(LIGHT).setColor(RED);
    public static final Material PINK_E_MAT = new Material(LIGHT).setColor(PINK);
    public static final Material ORANGE_E_MAT = new Material(LIGHT).setColor(ORANGE);
    public static final Material YELLOW_E_MAT = new Material(LIGHT).setColor(YELLOW);
    public static final Material GREEN_E_MAT = new Material(LIGHT).setColor(GREEN);
    public static final Material MAGENTA_E_MAT = new Material(LIGHT).setColor(MAGENTA);
    public static final Material CYAN_E_MAT = new Material(LIGHT).setColor(CYAN);
    public static final Material BLUE_E_MAT = new Material(LIGHT).setColor(BLUE);
    public static final Material PURPLE_E_MAT = new Material(LIGHT).setColor(PURPLE);

    // Bright emissive materials
    public static final Material WHITE_BE_MAT = new Material(WHITE_E_MAT).setEmissivity(2);
    public static final Material LIGHT_GRAY_BE_MAT = new Material(LIGHT_GRAY_E_MAT).setEmissivity(2);
    public static final Material GRAY_BE_MAT = new Material(GRAY_E_MAT).setEmissivity(2);
    public static final Material DARK_GRAY_BE_MAT = new Material(DARK_GRAY_E_MAT).setEmissivity(2);
    public static final Material BLACK_BE_MAT = new Material(BLACK_E_MAT).setEmissivity(2);
    public static final Material RED_BE_MAT = new Material(RED_E_MAT).setEmissivity(2);
    public static final Material PINK_BE_MAT = new Material(PINK_E_MAT).setEmissivity(2);
    public static final Material ORANGE_BE_MAT = new Material(ORANGE_E_MAT).setEmissivity(2);
    public static final Material YELLOW_BE_MAT = new Material(YELLOW_E_MAT).setEmissivity(2);
    public static final Material GREEN_BE_MAT = new Material(GREEN_E_MAT).setEmissivity(2);
    public static final Material MAGENTA_BE_MAT = new Material(MAGENTA_E_MAT).setEmissivity(2);
    public static final Material CYAN_BE_MAT = new Material(CYAN_E_MAT).setEmissivity(2);
    public static final Material BLUE_BE_MAT = new Material(BLUE_E_MAT).setEmissivity(2);
    public static final Material PURPLE_BE_MAT = new Material(PURPLE_E_MAT).setEmissivity(2);

    // Very bright emissive materials
    public static final Material WHITE_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(WHITE_E_MAT).setEmissivity(3);
    public static final Material LIGHT_VERY_GRAY_BRIGHT_EMISSIVE_MATERIAL = new Material(LIGHT_GRAY_E_MAT).setEmissivity(3);
    public static final Material GRAY_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(GRAY_E_MAT).setEmissivity(3);
    public static final Material DARK_VERY_GRAY_BRIGHT_EMISSIVE_MATERIAL = new Material(DARK_GRAY_E_MAT).setEmissivity(3);
    public static final Material BLACK_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(BLACK_E_MAT).setEmissivity(3);
    public static final Material RED_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(RED_E_MAT).setEmissivity(3);
    public static final Material PINK_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(PINK_E_MAT).setEmissivity(3);
    public static final Material ORANGE_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(ORANGE_E_MAT).setEmissivity(3);
    public static final Material YELLOW_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(YELLOW_E_MAT).setEmissivity(3);
    public static final Material GREEN_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(GREEN_E_MAT).setEmissivity(3);
    public static final Material MAGENTA_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(MAGENTA_E_MAT).setEmissivity(3);
    public static final Material CYAN_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(CYAN_E_MAT).setEmissivity(3);
    public static final Material BLUE_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(BLUE_E_MAT).setEmissivity(3);
    public static final Material PURPLE_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(PURPLE_E_MAT).setEmissivity(3);

    // Mirrors
    public static final Material WHITE_MIR = new Material(MIRROR).setColor(WHITE);
    public static final Material O_WHITE_MIR = new Material(MIRROR).setColor(OFF_WHITE);
    public static final Material L_GRAY_MIR = new Material(MIRROR).setColor(LIGHT_GRAY);
    public static final Material GRAY_MIR = new Material(MIRROR).setColor(GRAY);
    public static final Material D_GRAY_MIR = new Material(MIRROR).setColor(DARK_GRAY);
    public static final Material BLACK_MIR = new Material(MIRROR).setColor(BLACK);
    public static final Material RED_MIR = new Material(MIRROR).setColor(RED);
    public static final Material PINK_MIR = new Material(MIRROR).setColor(PINK);
    public static final Material ORANGE_MIR = new Material(MIRROR).setColor(ORANGE);
    public static final Material YELLOW_MIR = new Material(MIRROR).setColor(YELLOW);
    public static final Material GREEN_MIR = new Material(MIRROR).setColor(GREEN);
    public static final Material MAGENTA_MIR = new Material(MIRROR).setColor(MAGENTA);
    public static final Material CYAN_MIR = new Material(MIRROR).setColor(CYAN);
    public static final Material BLUE_MIR = new Material(MIRROR).setColor(BLUE);
    public static final Material PURPLE_MIR = new Material(MIRROR).setColor(PURPLE);

    // Glasses
    public static final Material WHITE_GLASS = new Material(GLASS).setColor(WHITE);
    public static final Material O_WHITE_GLASS = new Material(GLASS).setColor(OFF_WHITE);
    public static final Material L_GRAY_GLASS = new Material(GLASS).setColor(LIGHT_GRAY);
    public static final Material GRAY_GLASS = new Material(GLASS).setColor(GRAY);
    public static final Material D_GRAY_GLASS = new Material(GLASS).setColor(DARK_GRAY);
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

    public Material(DoubleColor color) {
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

    public Material setColor(DoubleColor color) {
        this.color = color;
        return this;
    }

    public Material setEmissivity(double emissivity) {
        this.emissivity = emissivity;
        return this;
    }

    public Material setReflectivity(double reflectivity) {
        this.reflectivity = reflectivity;
        return this;
    }

    public Material setSpecularity(double specularity) {
        this.specularity = specularity;
        return this;
    }

    public Material setOpacity(double opacity) {
        this.opacity = opacity;
        return this;
    }

    public Material setRefractiveIndex(double refractiveIndex) {
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
