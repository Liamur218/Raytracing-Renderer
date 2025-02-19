package driver;

import renderer.*;
import scene.*;
import scene.scenes.TSObjects;
import util.Logger;

public class Main {
    public static void main(String[] args) {
        Scene scene;

        scene = TSObjects.newScene();
        SceneIOHandler.writeToFile(scene, "output/scenes");

        scene = SceneIOHandler.readFromFile("output/scenes/Objects.scene");

        RenderSettings settings = RenderSettings.BUDGET_SETTINGS.copy();
        settings.setSeed(0);
        settings.setScene(scene);

        Image image = Renderer.render(settings);
        image.writeToFile(settings);
        Logger.writeLogsToFile(settings);
    }
}
