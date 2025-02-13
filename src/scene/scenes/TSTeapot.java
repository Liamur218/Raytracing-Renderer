package scene.scenes;

import mesh.*;
import scene.*;
import util.*;

import java.awt.*;

public abstract class TSTeapot {
    public static Scene newScene() {
        Scene scene = new Scene();
        scene.setName("Utah Teapot LR");

        Vector camPos = new Vector(0.25, 0, 0.125);
        Vector camDir = new Vector(0.75, 0, -0.25);
        Vector camNormal = new Vector(0, 0, 1);
        Camera camera = new Camera(camPos, camDir, camNormal);
        scene.setCamera(camera);

        scene.setLightSourceDir(new Vector(0, 1, 1));

        PolygonMesh polygonMesh = ModelLoader.loadModel("assets", "Utah Teapot low res", ModelType.STL_ASCII);
        polygonMesh.setName("Utah Teapot");
        polygonMesh.normalize();
        polygonMesh.rotate(90, 0, -90);
        polygonMesh.scale(0.75);
        polygonMesh.setCenterAt(1.5, 0, -0.25);
        polygonMesh.setMaterial(Material.WHITE_MAT);
        scene.addMesh(polygonMesh);

        polygonMesh = new CubeMesh(1.5, 0, -0.75, 1, 1, 0.5);
        polygonMesh.setMaterial(Material.CYAN_MAT);
        scene.addMesh(polygonMesh);

        PlaneMesh planeMesh = new PlaneMesh(0, 0, -1, 0, 0, 1);
        planeMesh.setMaterial(Material.GREEN_MAT.copy().setColor(new Color(0, 127, 0)));
        scene.addMesh(planeMesh);

        return scene;
    }
}
