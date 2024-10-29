package driver;

import renderer.*;
import scene.Scene;
import scene.scenes.*;
import util.Logger;

public class Main {
    public static void main(String[] args) {
        Logger.setPrintLogs(true);

        Scene scene = TSObjects.newScene();

        RenderSettings settings = RenderSettings.FANCY_SETTINGS;
        settings.setSeed(0);
        settings.setScene(scene);

        Image image = Renderer.render(settings);
        image.writeToFile(settings);
    }
}