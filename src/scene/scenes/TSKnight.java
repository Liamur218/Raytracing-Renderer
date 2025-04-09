package scene.scenes;

import mesh.*;
import scene.*;

public abstract class TSKnight {
    public static Scene newScene() {
        Scene scene = TSBasicEnvironment.newScene();
        scene.setName("Knight");
        scene.camera.setFOV(100);

        PolygonMesh polygonMesh = ModelLoader.loadModel(
                "assets/Models/Knight", "Knight", ModelFileType.STL_BIN);
        polygonMesh.setCenterAt(0, 0, 0);
        polygonMesh.scale(1.5 / 3.37);
        polygonMesh.rotate(0, 0, 180 + 45);
        polygonMesh.setCenterAt(1.5, 0, -0.25);
        polygonMesh.setMaterial(Material.WHITE_MAT);
        scene.addMesh(polygonMesh);

        return scene;
    }
}
