package driver;

import renderer.*;
import scene.Scene;
import scene.scenes.TSKnight;

public class Main {
    public static void main(String[] args) {
        Scene scene = TSKnight.newScene();

        RenderSettings settings = RenderSettings.DEFAULT_SETTINGS;
        settings.setSeed(0);
        settings.setScene(scene);

        Image image = Renderer.render(settings);
        image.writeToFile(settings);
    }
}
