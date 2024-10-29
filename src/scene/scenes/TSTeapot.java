package scene.scenes;

import mesh.*;
import scene.Scene;
import util.ModelLoader;

public abstract class TSTeapot {
    public static Scene newScene() {
        Scene scene = TSBasicEnvironment.newScene();
        scene.setName("Utah Teapot");

        PolygonMesh polygonMesh = ModelLoader.loadAsciiStl("assets/utah_teapot.stl");
        polygonMesh.normalize();
        polygonMesh.rotate(90, 0, -90);
        polygonMesh.scale(0.75);
        polygonMesh.setCenterAt(1.5, 0, -0.25);
        scene.addMesh(polygonMesh);
        polygonMesh = new CubeMesh(1.5, 0, -0.75, 1, 1, 0.5);
        polygonMesh.setMaterial(Material.CYAN_MAT);
        scene.addMesh(polygonMesh);

        return scene;
    }
}
