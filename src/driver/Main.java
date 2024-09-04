package driver;

import renderer.*;
import scene.Scene;
import scene.scenes.TSObjects;
import util.Debug;

public class Main {
    public static void main(String[] args) {
        Debug.setPrintLogs(true);

        Scene scene = new TSObjects();

        RenderSettings settings = new RenderSettings(RenderSettings.DEFAULT_SETTINGS);
        settings.setScene(scene);

        Image image = Renderer.render(settings);
        image.writeToFile(settings);
    }
}