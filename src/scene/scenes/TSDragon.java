package scene.scenes;

import mesh.*;
import scene.*;

public abstract class TSDragon {
    public static Scene newScene2() {
        Scene scene = new Scene();
        scene.setName("Dragon");

        Vector camPos = new Vector(-1.75, 1.25, -0.125);
        Vector camDir = new Vector(1, 0, 0);
        Vector camNorm = new Vector(0, 0, 1);
        scene.setCamera(new Camera(camPos, camDir, camNorm));

        PolygonMesh pMesh = MeshLoader.loadModel(
                "assets/Models/Dragon", "Low Poly Dragon", MeshFileType.STL_BIN);
        pMesh.setMaterial(Material.LIGHT_BLUE_MAT);
        pMesh.rotate(0, 0, 180 + 45/2f).normalize();
        scene.addMesh(pMesh);

        PlaneMesh planeMesh = new PlaneMesh(0, 0, -1, 0, 0, 1);
        scene.addMesh(planeMesh.setMaterial(Material.GREEN_MAT));

        return scene;
    }

    public static Scene newScene1() {
        Scene scene = new Scene();
        scene.setName("Dragon");

        Vector camPos = new Vector(-1.75, 0, 0);
        Vector camDir = new Vector(1, 0, 0);
        Vector camNorm = new Vector(0, 0, 1);
        scene.setCamera(new Camera(camPos, camDir, camNorm));

        PolygonMesh pMesh = MeshLoader.loadModel(
                "assets/Models/Dragon", "Low Poly Dragon", MeshFileType.STL_BIN);
        pMesh.setMaterial(Material.GREEN_MAT);
        pMesh.rotate(0, 0, 180 + 45/2f).normalize();
        scene.addMesh(pMesh);

        PlaneMesh planeMesh = new PlaneMesh(0, 0, -1, 0, 0, 1);
        scene.addMesh(planeMesh.setMaterial(Material.LIGHT_BLUE_MAT));

        return scene;
    }
}
