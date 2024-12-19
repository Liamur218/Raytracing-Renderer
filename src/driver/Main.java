package driver;

import renderer.*;
import scene.Scene;
import scene.scenes.*;

public class Main {
    public static void main(String[] args) {
        Scene scene = TSTeapot.newScene();

        RenderSettings settings = RenderSettings.DEFAULT_SETTINGS;
        settings.setSeed(0);
        settings.setScene(scene);

        Image image = Renderer.render(settings);
        image.writeToFile(settings);
    }
}
