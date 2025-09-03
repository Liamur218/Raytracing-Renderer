package renderer;

import scene.Scene;

import java.util.Random;

public class RenderSettings {

    // Variables
    Scene scene;

    float imageScale;

    int recursionCount, frameCount;

    int threadCount;
    boolean distributed;
    String ip;

    int sectionWidth, sectionHeight;

    int seed;

    PostProcessor postProcessor;

    // Default settings
    public static final RenderSettings BUDGET_SETTINGS = new RenderSettings();

    static {
        BUDGET_SETTINGS.setRecursionCount(15);
        BUDGET_SETTINGS.setFrameCount(30);
        BUDGET_SETTINGS.setThreadCount(30);
        BUDGET_SETTINGS.setSectionSize(500, 500);
        BUDGET_SETTINGS.setSeed(new Random().nextInt());
        BUDGET_SETTINGS.setIP("127.0.0.1");
    }

    // Other settings
    public static final RenderSettings DEFAULT_SETTINGS = new RenderSettings(BUDGET_SETTINGS);
    public static final RenderSettings FANCY_SETTINGS = new RenderSettings(BUDGET_SETTINGS);
    public static final RenderSettings ULTRA_SETTINGS = new RenderSettings(BUDGET_SETTINGS);

    static {
        DEFAULT_SETTINGS.setFrameCount(150);
        DEFAULT_SETTINGS.setThreadCount(25);

        FANCY_SETTINGS.setRecursionCount(20);
        FANCY_SETTINGS.setFrameCount(200);
        FANCY_SETTINGS.setThreadCount(100);

        ULTRA_SETTINGS.setRecursionCount(30);
        ULTRA_SETTINGS.setFrameCount(300);
        ULTRA_SETTINGS.setThreadCount(150);
    }

    private RenderSettings() {
        imageScale = 1;
    }

    public RenderSettings(RenderSettings settings) {
        this();
        scene = settings.scene;
        imageScale = settings.imageScale;
        recursionCount = settings.recursionCount;
        frameCount = settings.frameCount;
        threadCount = settings.threadCount;
        distributed = settings.distributed;
        ip = settings.ip;
        sectionWidth = settings.sectionWidth;
        sectionHeight = settings.sectionHeight;
        seed = settings.seed;
        postProcessor = settings.postProcessor;
    }

    public RenderSettings setScene(Scene scene) {
        this.scene = scene;
        return this;
    }

    public RenderSettings setImageScale(double imageScale) {
        return setImageScale((float) imageScale);
    }

    public RenderSettings setImageScale(float imageScale) {
        this.imageScale = imageScale;
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

    public RenderSettings setIP(String ip) {
        this.ip = ip;
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

    public RenderSettings copy() {
        return new RenderSettings(this);
    }

    @Override
    public String toString() {
        return scene + " " + scene.camera.imageSize.width + "x" + scene.camera.imageSize.height +
                " R" + recursionCount + " F" + frameCount + " T" + threadCount;
    }

    public String toFilenameString() {
        int width = (int) (scene.camera.imageSize.width * imageScale);
        int height = (int) (scene.camera.imageSize.height * imageScale);
        return scene + " " + width + "x" + height + " R" + recursionCount + " F" + frameCount;
    }
}
