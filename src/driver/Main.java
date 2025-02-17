package driver;

import renderer.*;
import scene.Scene;
import scene.scenes.*;
import util.Logger;

public class Main {
    public static void main(String[] args) {
        Scene scene = TSKnight.newScene();

        RenderSettings settings = RenderSettings.DEFAULT_SETTINGS.copy();
        settings.setSeed(0);
        settings.setScene(scene);

        Image image = Renderer.render(settings);
        image.writeToFile(settings);
        Logger.writeLogsToFile(settings);
    }
}
