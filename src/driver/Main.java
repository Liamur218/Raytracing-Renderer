package driver;

import renderer.*;
import scene.*;
import scene.scenes.*;

public class Main {
    public static void main(String[] args) {
        Scene scene = TSObjects.newScene();

        RenderSettings renderSettings = RenderSettings.DEFAULT_SETTINGS;
        renderSettings.setScene(scene);
        renderSettings.setSeed(0);

        Image image = Renderer.render(renderSettings);
        image.writeToFile(renderSettings);
    }
}
