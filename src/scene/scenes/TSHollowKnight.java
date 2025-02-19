package scene.scenes;

import mesh.*;
import scene.Scene;

public abstract class TSHollowKnight {
    public static Scene newScene() {
        Scene scene = TSBasicEnvironment.newScene();
        scene.setName("Hollow Knight");

        PolygonMesh polygonMesh = ModelLoader.loadModel("assets/big_assets", "Hollow Knight", ModelFileType.STL_BIN);
        polygonMesh.normalize();
        polygonMesh.scale(0.5);
        polygonMesh.setMaterial(Material.BLUE_MIRROR);
        scene.addMesh(polygonMesh);

        return scene;
    }
}
