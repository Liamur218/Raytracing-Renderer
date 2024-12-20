package driver;

import renderer.*;
import scene.Scene;
import scene.scenes.*;
import util.Logger;

public class Main {
    public static void main(String[] args) {
        Scene scene = TSCubesAndRays.newScene();

        RenderSettings settings = RenderSettings.ULTRA_SETTINGS;
        settings.setSeed(0);
        settings.setScene(scene);

        Image image = Renderer.render(settings);
        image.writeToFile(settings);
        Logger.writeLogsToFile(settings);
    }
}
