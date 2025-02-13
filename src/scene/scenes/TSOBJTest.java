package scene.scenes;

import mesh.*;
import scene.Scene;
import util.*;

public abstract class TSOBJTest {
    public Scene newScene() {
        Scene scene = TSBasicEnvironment.newScene();

        PolygonMesh mesh = ModelLoader.loadModel("src/assets", "Blender Cube", ModelType.WAVEFRONT_OBJ);
        scene.addMesh(mesh);

        return scene;
    }
}
