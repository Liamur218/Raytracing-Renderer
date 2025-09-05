package driver;

import renderer.*;
import scene.*;
import scene.scenes.*;
import util.Logger;

public class Main {
    public static void main(String[] args) {
        Scene scene = TSCornellBox.newScene();

        RenderSettings renderSettings = RenderSettings.DEFAULT_SETTINGS;
        renderSettings.setScene(scene).setSeed(0).setImageScale(1);

        Image image = Renderer.render(renderSettings);
        image.writeToFile(renderSettings);
        Logger.writeLogsToFile(renderSettings);
    }
}
