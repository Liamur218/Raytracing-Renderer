package driver;

import renderer.*;
import scene.*;
import scene.scenes.*;

public class Main {
    public static void main(String[] args) {
        Scene scene = TSDragon.newScene();

        RenderSettings renderSettings = RenderSettings.BUDGET_SETTINGS;
        renderSettings.setScene(scene);
        renderSettings.setSeed(0);

        Image image = Renderer.render(renderSettings);
        image.writeToFile(renderSettings);
    }
}
