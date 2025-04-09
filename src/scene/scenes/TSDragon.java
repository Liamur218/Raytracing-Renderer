package scene.scenes;

import mesh.*;
import scene.*;

public abstract class TSDragon {
    public static Scene newScene() {
        Scene scene = new Scene();
        scene.setName("Dragon");

        Vector camPos = new Vector(-1.75, 1.25, -0.125);
        Vector camDir = new Vector(1, 0, 0);
        Vector camNorm = new Vector(0, 0, 1);
        scene.setCamera(new Camera(camPos, camDir, camNorm));

        PolygonMesh pMesh = ModelLoader.loadModel(
                "assets/Models/Dragon", "Low Poly Dragon", ModelFileType.STL_BIN);
        pMesh.setMaterial(Material.LIGHT_BLUE_MAT);
        pMesh.rotate(0, 0, 180 + 45/2).normalize();
        scene.addMesh(pMesh);

        PlaneMesh planeMesh = new PlaneMesh(0, 0, -1, 0, 0, 1);
        scene.addMesh(planeMesh.setMaterial(Material.GREEN_MAT));

        return scene;
    }
}
