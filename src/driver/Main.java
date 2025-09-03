package driver;

import renderer.*;
import scene.*;
import scene.scenes.*;

public class Main {
    public static void main(String[] args) {
        Scene scene = TSObjects.newScene();
        SceneIO.writeToFile(scene, "assets/scenes");

        RenderSettings renderSettings = RenderSettings.DEFAULT_SETTINGS;
        renderSettings.setScene(scene).setSeed(0).setImageScale(1.5);
    }
}
