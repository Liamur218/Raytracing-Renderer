package scene.scenes;

import mesh.*;
import scene.Scene;

public abstract class TSHollowKnight {
    public static Scene newScene() {
        Scene scene = TSBasicEnvironment.newScene();
        scene.setName("Hollow Knight");

        PolygonMesh mesh = MeshLoader.loadModel(
                "assets/large Models/Hollow Knight", "Hollow Knight", MeshFileType.STL_BIN);
        mesh.normalize();
        mesh.scale(0.5);
        mesh.setMaterial(Material.BLUE_MIRROR);
        scene.addMesh(mesh);

        return scene;
    }
}
