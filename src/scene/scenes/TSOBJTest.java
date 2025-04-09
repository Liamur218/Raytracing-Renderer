package scene.scenes;

import mesh.*;
import scene.Scene;

public abstract class TSOBJTest {
    public static Scene newScene() {
        Scene scene = TSBasicEnvironment.newScene();
        scene.setName("OBJ Test");

        PolygonMesh mesh = MeshLoader.loadModel(
                "assets/Models/Blender Cube", "Blender_Cube", MeshFileType.WAVEFRONT_OBJ);
        mesh.scale(0.25);
        mesh.move(1.5, 0, 0);
        mesh.setMaterial(Material.ORANGE_MAT);
        scene.addMesh(mesh);

        return scene;
    }
}
