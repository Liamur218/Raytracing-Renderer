package renderer;

import scene.Scene;

import java.awt.*;

public class RenderSettings {

    // Variables
    Scene scene;

    Dimension size;

    int recursionCount, frameCount;

    int threadCount;
    int sectionWidth, sectionHeight;

    int rngSeed;

    String holdingDir;

    // Default settings
    static final RenderSettings DEFAULT_SETTINGS = new RenderSettings();
    static {
        DEFAULT_SETTINGS.setSize(1920, 1080);
        DEFAULT_SETTINGS.setRecursionCount(15);
        DEFAULT_SETTINGS.setFrameCount(30);
        DEFAULT_SETTINGS.setThreadCount(25);
        DEFAULT_SETTINGS.setSectionSize(500, 500);
        DEFAULT_SETTINGS.setRngSeed(0);
    }

    private RenderSettings() {}

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
        rngSeed = settings.rngSeed;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public void setSize(int width, int height) {
        size = new Dimension(width, height);
    }

    public void setRecursionCount(int recursionCount) {
        this.recursionCount = recursionCount;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public void setSectionWidth(int sectionWidth) {
        this.sectionWidth = sectionWidth;
    }

    public void setSectionHeight(int sectionHeight) {
        this.sectionHeight = sectionHeight;
    }

    public void setSectionSize(int sectionWidth, int sectionHeight) {
        setSectionWidth(sectionWidth);
        setSectionHeight(sectionHeight);
    }

    public void setRngSeed(int rngSeed) {
        this.rngSeed = rngSeed;
    }

    public void setHoldingDir(String filepath) {
        holdingDir = filepath;
    }

    @Override
    public String toString() {
        return scene.getName() + "_" +
                size.width + "x" + size.height + "_" +
                "R" + recursionCount + "_" +
                "F" + frameCount + "_" +
                "T" + threadCount;
    }

    public String toFilenameString() {
        return scene.getName() + "_" +
                size.width + "x" + size.height + "_" +
                "R" + recursionCount + "_" +
                "F" + frameCount;
    }
}
