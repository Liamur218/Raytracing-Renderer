package driver;

import renderer.*;
import scene.*;

public class Main {
    public static void main(String[] args) {
        Scene scene = SceneIO.readFromFile("output/scenes/Basic Environment.bsd");

        RenderSettings renderSettings = RenderSettings.DEFAULT_SETTINGS;
        renderSettings.setScene(scene);
        renderSettings.setSeed(0);

        Image image = Renderer.render(renderSettings);
        image.writeToFile(renderSettings);
    }
}
