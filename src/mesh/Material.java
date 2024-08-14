package mesh;

public class Material {

    public DoubleColor color;

    // Emission & Reflection
    public double emissivity;
    public double reflectivity;
    public double specularity;

    // Refraction
    public double opacity;
    public double refractiveIndex;

    // Defaults
    private static final double DEFAULT_EMISSIVITY = 0;
    private static final double DEFAULT_REFLECTIVITY = 1;
    private static final double DEFAULT_SPECULARITY = 0;
    private static final double DEFAULT_OPACITY = 1;
    private static final double DEFAULT_REFRACTIVE_INDEX = 1;

    // Basic materials
    public static final Material WHITE_MATERIAL = new Material(DoubleColor.WHITE);
    public static final Material LIGHT_GRAY_MATERIAL = new Material(DoubleColor.LIGHT_GRAY);
    public static final Material GRAY_MATERIAL = new Material(DoubleColor.GRAY);
    public static final Material DARK_GRAY_MATERIAL = new Material(DoubleColor.DARK_GRAY);
    public static final Material BLACK_MATERIAL = new Material(DoubleColor.BLACK);
    public static final Material RED_MATERIAL = new Material(DoubleColor.RED);
    public static final Material PINK_MATERIAL = new Material(DoubleColor.PINK);
    public static final Material ORANGE_MATERIAL = new Material(DoubleColor.ORANGE);
    public static final Material YELLOW_MATERIAL = new Material(DoubleColor.YELLOW);
    public static final Material GREEN_MATERIAL = new Material(DoubleColor.GREEN);
    public static final Material MAGENTA_MATERIAL = new Material(DoubleColor.MAGENTA);
    public static final Material CYAN_MATERIAL = new Material(DoubleColor.CYAN);
    public static final Material BLUE_MATERIAL = new Material(DoubleColor.BLUE);
    public static final Material PURPLE_MATERIAL = new Material(DoubleColor.PURPLE);

    // Emissive materials
    public static final Material WHITE_EMISSIVE_MATERIAL = new Material(DoubleColor.WHITE, 0, 1);
    public static final Material LIGHT_GRAY_EMISSIVE_MATERIAL = new Material(DoubleColor.LIGHT_GRAY, 0, 1);
    public static final Material GRAY_EMISSIVE_MATERIAL = new Material(DoubleColor.GRAY, 0, 1);
    public static final Material DARK_GRAY_EMISSIVE_MATERIAL = new Material(DoubleColor.DARK_GRAY, 0, 1);
    public static final Material BLACK_EMISSIVE_MATERIAL = new Material(DoubleColor.BLACK, 0, 1);
    public static final Material RED_EMISSIVE_MATERIAL = new Material(DoubleColor.RED, 0, 1);
    public static final Material PINK_EMISSIVE_MATERIAL = new Material(DoubleColor.PINK, 0, 1);
    public static final Material ORANGE_EMISSIVE_MATERIAL = new Material(DoubleColor.ORANGE, 0, 1);
    public static final Material YELLOW_EMISSIVE_MATERIAL = new Material(DoubleColor.YELLOW, 0, 1);
    public static final Material GREEN_EMISSIVE_MATERIAL = new Material(DoubleColor.GREEN, 0, 1);
    public static final Material MAGENTA_EMISSIVE_MATERIAL = new Material(DoubleColor.MAGENTA, 0, 1);
    public static final Material CYAN_EMISSIVE_MATERIAL = new Material(DoubleColor.CYAN, 0, 1);
    public static final Material BLUE_EMISSIVE_MATERIAL = new Material(DoubleColor.BLUE, 0, 1);
    public static final Material PURPLE_EMISSIVE_MATERIAL = new Material(DoubleColor.PURPLE, 0, 1);

    // Bright emissive materials
    public static final Material WHITE_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.WHITE, 0, 2);
    public static final Material LIGHT_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.LIGHT_GRAY, 0, 2);
    public static final Material GRAY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.GRAY, 0, 2);
    public static final Material DARK_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.DARK_GRAY, 0, 2);
    public static final Material BLACK_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.BLACK, 0, 2);
    public static final Material RED_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.RED, 0, 2);
    public static final Material PINK_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.PINK, 0, 2);
    public static final Material ORANGE_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.ORANGE, 0, 2);
    public static final Material YELLOW_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.YELLOW, 0, 2);
    public static final Material GREEN_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.GREEN, 0, 2);
    public static final Material MAGENTA_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.MAGENTA, 0, 2);
    public static final Material CYAN_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.CYAN, 0, 2);
    public static final Material BLUE_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.BLUE, 0, 2);
    public static final Material PURPLE_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.PURPLE, 0, 2);

    // Very bright emissive materials
    public static final Material WHITE_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.WHITE, 0, 3);
    public static final Material LIGHT_VERY_GRAY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.LIGHT_GRAY, 0, 3);
    public static final Material GRAY_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.GRAY, 0, 3);
    public static final Material DARK_VERY_GRAY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.DARK_GRAY, 0, 3);
    public static final Material BLACK_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.BLACK, 0, 3);
    public static final Material RED_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.RED, 0, 3);
    public static final Material PINK_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.PINK, 0, 3);
    public static final Material ORANGE_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.ORANGE, 0, 3);
    public static final Material YELLOW_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.YELLOW, 0, 3);
    public static final Material GREEN_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.GREEN, 0, 3);
    public static final Material MAGENTA_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.MAGENTA, 0, 3);
    public static final Material CYAN_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.CYAN, 0, 3);
    public static final Material BLUE_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.BLUE, 0, 3);
    public static final Material PURPLE_VERY_BRIGHT_EMISSIVE_MATERIAL = new Material(DoubleColor.PURPLE, 0, 3);

    // Mirrors
    public static final Material WHITE_MIRROR = new Material(DoubleColor.WHITE, 1, 0, 1);
    public static final Material OFF_WHITE_MIRROR = new Material(DoubleColor.OFF_WHITE, 1, 0, 1);
    public static final Material LIGHT_GRAY_MIRROR = new Material(DoubleColor.LIGHT_GRAY, 1, 0, 1);
    public static final Material GRAY_MIRROR = new Material(DoubleColor.GRAY, 1, 0, 1);
    public static final Material DARK_GRAY_MIRROR = new Material(DoubleColor.DARK_GRAY, 1, 0, 1);
    public static final Material BLACK_MIRROR = new Material(DoubleColor.BLACK, 1, 0, 1);
    public static final Material RED_MIRROR = new Material(DoubleColor.RED, 1, 0, 1);
    public static final Material PINK_MIRROR = new Material(DoubleColor.PINK, 1, 0, 1);
    public static final Material ORANGE_MIRROR = new Material(DoubleColor.ORANGE, 1, 0, 1);
    public static final Material YELLOW_MIRROR = new Material(DoubleColor.YELLOW, 1, 0, 1);
    public static final Material GREEN_MIRROR = new Material(DoubleColor.GREEN, 1, 0, 1);
    public static final Material MAGENTA_MIRROR = new Material(DoubleColor.MAGENTA, 1, 0, 1);
    public static final Material CYAN_MIRROR = new Material(DoubleColor.CYAN, 1, 0, 1);
    public static final Material BLUE_MIRROR = new Material(DoubleColor.BLUE, 1, 0, 1);
    public static final Material PURPLE_MIRROR = new Material(DoubleColor.PURPLE, 1, 0, 1);

    // Glasses
    public static final Material WHITE_GLASS = new Material(DoubleColor.WHITE, 1, 0, 1, 0.1, 1.5);
    public static final Material OFF_WHITE_GLASS = new Material(DoubleColor.OFF_WHITE, 1, 0, 1, 0.1, 1.5);
    public static final Material LIGHT_GRAY_GLASS = new Material(DoubleColor.LIGHT_GRAY, 1, 0, 1, 0.1, 1.5);
    public static final Material GRAY_GLASS = new Material(DoubleColor.GRAY, 1, 0, 1, 0.1, 1.5);
    public static final Material DARK_GRAY_GLASS = new Material(DoubleColor.DARK_GRAY, 1, 0, 1, 0.1, 1.5);
    public static final Material BLACK_GLASS = new Material(DoubleColor.BLACK, 1, 0, 1, 0.1, 1.5);
    public static final Material RED_GLASS = new Material(DoubleColor.RED, 1, 0, 1, 0.1, 1.5);
    public static final Material PINK_GLASS = new Material(DoubleColor.PINK, 1, 0, 1, 0.1, 1.5);
    public static final Material ORANGE_GLASS = new Material(DoubleColor.ORANGE, 1, 0, 1, 0.1, 1.5);
    public static final Material YELLOW_GLASS = new Material(DoubleColor.YELLOW, 1, 0, 1, 0.1, 1.5);
    public static final Material GREEN_GLASS = new Material(DoubleColor.GREEN, 1, 0, 1, 0.1, 1.5);
    public static final Material MAGENTA_GLASS = new Material(DoubleColor.MAGENTA, 1, 0, 1, 0.1, 1.5);
    public static final Material CYAN_GLASS = new Material(DoubleColor.CYAN, 1, 0, 1, 0.1, 1.5);
    public static final Material BLUE_GLASS = new Material(DoubleColor.BLUE, 1, 0, 1, 0.1, 1.5);
    public static final Material PURPLE_GLASS = new Material(DoubleColor.PURPLE, 1, 0, 1, 0.1, 1.5);

    public Material(DoubleColor color, double reflectivity, double emissivity, double specularity,
                    double opacity, double refractiveIndex) {
        this.color = color;
        this.reflectivity = reflectivity;
        this.emissivity = emissivity;
        this.specularity = specularity;
        this.opacity = opacity;
        this.refractiveIndex = refractiveIndex;
    }

    public Material(DoubleColor color) {
        this(color, DEFAULT_REFLECTIVITY, DEFAULT_EMISSIVITY);
    }

    public Material(double r, double g, double b) {
        this(new DoubleColor(r, g, b));
    }

    public Material(DoubleColor color, double reflectivity, double emissivity) {
        this(color, reflectivity, emissivity, DEFAULT_SPECULARITY, DEFAULT_OPACITY, DEFAULT_REFRACTIVE_INDEX);
    }

    public Material(DoubleColor color, double reflectivity, double emissivity, double specularity) {
        this(color, reflectivity, emissivity, specularity, DEFAULT_OPACITY, DEFAULT_REFRACTIVE_INDEX);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Material material) {
            return material.color == color && material.reflectivity == reflectivity &&
                    material.emissivity == emissivity && material.specularity == specularity &&
                    material.opacity == opacity && material.refractiveIndex == refractiveIndex;
        }
        return false;
    }
}
