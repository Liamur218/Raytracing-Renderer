package driver;

import renderer.Image;
import renderer.RenderSettings;
import renderer.Renderer;
import scene.Scene;
import scene.scenes.TestScene1;
import util.Debug;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        Debug.setPrintLogs(true);

        Scene scene = new TestScene1();
        Dimension imageSize = new Dimension(3024, 1964);
        int recursionCount = 15;
        int frameCount = 50;
        int threadCount = 50;

        RenderSettings settings = new RenderSettings(scene);
        //settings.setSize(imageSize);
        settings.setRecursionCount(recursionCount);
        settings.setFrameCount(frameCount);
        settings.setThreadCount(threadCount);

        Image image = Renderer.render(settings);

        image.writeToFile(settings);
        //Debug.writeLogsToFile(settings);
    }
}