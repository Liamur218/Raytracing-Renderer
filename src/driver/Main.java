package driver;

import renderer.*;
import scene.Scene;
import scene.scenes.*;
import util.Logger;

public class Main {
    public static void main(String[] args) {
        Scene scene = TSStanfordBox.newScene();

        RenderSettings settings = RenderSettings.FANCY_SETTINGS.copy();
        settings.setSeed(0);
        settings.setScene(scene);
        settings.setSize(1500, 1500);

        Image image = Renderer.render(settings);
        image.writeToFile(settings);
        Logger.writeLogsToFile(settings);
    }
}
