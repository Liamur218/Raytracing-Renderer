package driver;

import renderer.Image;
import renderer.RenderSettings;
import renderer.Renderer;
import scene.Scene;
import scene.scenes.TestScene3;
import util.Debug;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        Debug.setPrintLogs(true);

        Scene scene = new TestScene3();
        Dimension imageSize = new Dimension(3024, 1964);
        int recursionCount = 30;
        int frameCount = 200;
        int threadCount = 50;
        int rngSeed = 0;

        RenderSettings settings = new RenderSettings(scene);
        //settings.setSize(imageSize);
        settings.setRecursionCount(recursionCount);
        settings.setFrameCount(frameCount);
        settings.setThreadCount(threadCount);
        settings.setRngSeed(rngSeed);
        settings.setHoldingDir("holding_dir");

        Image image = Renderer.render(settings);

        image.writeToFile(settings.toFilenameString() + ".png");
        Debug.writeLogsToFile(settings.toFilenameString() + ".txt");
    }
}