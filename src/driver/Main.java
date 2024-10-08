package driver;

import renderer.*;
import scene.Scene;
import scene.scenes.*;
import util.Debug;

public class Main {
    public static void main(String[] args) {
        Debug.setPrintLogs(true);

        Scene scene = new TSKnight();

        RenderSettings settings = new RenderSettings(RenderSettings.FANCY_SETTINGS).setThreadCount(25);
        settings.setScene(scene);

        Image image = Renderer.render(settings);
        image.writeToFile(settings);
    }
}