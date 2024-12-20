package renderer;

import scene.Scene;

import java.awt.*;
import java.util.Random;

public class RenderSettings {

    // Variables
    Scene scene;

    Dimension size;

    int recursionCount, frameCount;

    int threadCount;
    boolean distributed;

    int sectionWidth, sectionHeight;

    int seed;

    PostProcessor postProcessor;

    // Default settings
    public static final RenderSettings DEFAULT_SETTINGS = new RenderSettings();

    static {
        DEFAULT_SETTINGS.setSize(1920, 1080);
        DEFAULT_SETTINGS.setRecursionCount(15);
        DEFAULT_SETTINGS.setFrameCount(30);
        DEFAULT_SETTINGS.setThreadCount(25);
        DEFAULT_SETTINGS.setSectionSize(500, 500);
        DEFAULT_SETTINGS.setSeed(new Random().nextInt());
    }

    // Other settings
    public static final RenderSettings FANCY_SETTINGS = new RenderSettings(DEFAULT_SETTINGS);
    public static final RenderSettings ULTRA_SETTINGS = new RenderSettings(DEFAULT_SETTINGS);

    static {
        FANCY_SETTINGS.setFrameCount(150);

        ULTRA_SETTINGS.setSize((int) (1920 * 1.75), (int) (1080 * 1.75));
        ULTRA_SETTINGS.setRecursionCount(30);
        ULTRA_SETTINGS.setFrameCount(300);
    }

    private RenderSettings() {
    }

    public RenderSettings(Scene scene) {
        this(DEFAULT_SETTINGS);
        this.scene = scene;
    }

    public RenderSettings(RenderSettings settings) {
        scene = settings.scene;
        size = settings.size;
        recursionCount = settings.recursionCount;
        frameCount = settings.frameCount;
        threadCount = settings.threadCount;
        sectionWidth = settings.sectionWidth;
        sectionHeight = settings.sectionHeight;
        seed = settings.seed;
        postProcessor = settings.postProcessor;
    }

    public RenderSettings setScene(Scene scene) {
        this.scene = scene;
        return this;
    }

    public RenderSettings setSize(Dimension size) {
        this.size = size;
        return this;
    }

    public RenderSettings setSize(int width, int height) {
        setSize(new Dimension(width, height));
        return this;
    }

    public RenderSettings setRecursionCount(int recursionCount) {
        this.recursionCount = recursionCount;
        return this;
    }

    public RenderSettings setFrameCount(int frameCount) {
        this.frameCount = frameCount;
        return this;
    }

    public RenderSettings setThreadCount(int threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    public RenderSettings setDistributed(boolean distributed) {
        this.distributed = distributed;
        return this;
    }

    public RenderSettings setSectionWidth(int sectionWidth) {
        this.sectionWidth = sectionWidth;
        return this;
    }

    public RenderSettings setSectionHeight(int sectionHeight) {
        this.sectionHeight = sectionHeight;
        return this;
    }

    public RenderSettings setSectionSize(int sectionWidth, int sectionHeight) {
        setSectionWidth(sectionWidth);
        setSectionHeight(sectionHeight);
        return this;
    }

    public RenderSettings setSeed(int rngSeed) {
        this.seed = rngSeed;
        return this;
    }

    public RenderSettings setPostProcessor(PostProcessor postProcessor) {
        this.postProcessor = postProcessor;
        return this;
    }

    @Override
    public String toString() {
        return scene + " " +
                size.width + "x" + size.height + " " +
                "R" + recursionCount + " " +
                "F" + frameCount + " " +
                "T" + threadCount;
    }

    public String toFilenameString() {
        return scene + " " +
                size.width + "x" + size.height + " " +
                "R" + recursionCount + " " +
                "F" + frameCount;
    }
}
