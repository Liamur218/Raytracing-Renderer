package driver;

import renderer.*;
import scene.Scene;
import scene.scenes.*;
import util.Logger;

public class Main {
    public static void main(String[] args) {
        Logger.setPrintLogs(true);

        Scene scene = TSCubesAndRays.newScene();

        RenderSettings settings = new RenderSettings(RenderSettings.FANCY_SETTINGS).setThreadCount(25);
        settings.setScene(scene);

        PostProcessor postProcessor = new PostProcessor();
        postProcessor.doLighten(true, 1.5);
        settings.setPostProcessor(postProcessor);

        Image image = Renderer.render(settings);
        image.writeToFile(settings);
    }
}