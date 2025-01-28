package scene.scenes;

import mesh.*;
import scene.Scene;
import util.ModelLoader;

public abstract class TSOBJTest {
    public Scene newScene() {
        Scene scene = TSBasicEnvironment.newScene();

        PolygonMesh mesh = ModelLoader.loadObj("src/assets/Blender Cube.obj");
        scene.addMesh(mesh);

        return scene;
    }
}
