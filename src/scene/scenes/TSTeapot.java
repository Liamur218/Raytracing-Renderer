package scene.scenes;

import mesh.PolygonMesh;
import scene.Scene;
import util.ModelLoader;

public abstract class TSTeapot {
    public static Scene newScene() {
        Scene scene = TSBasicEnvironment.newScene();
        scene.setName("Utah Teapot");

        PolygonMesh polygonMesh = ModelLoader.loadAsciiStl("assets/utah_teapot.stl");
        polygonMesh.normalize();
        polygonMesh.scale(0.75);
        polygonMesh.setCenterAt(1, 0, -0.125);

        return scene;
    }
}
