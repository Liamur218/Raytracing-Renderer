package scene.scenes;

import mesh.*;
import scene.*;
import util.ModelLoader;

public abstract class TSTeapot {
    public static Scene newScene() {
        Scene scene = new Scene();
        scene.setName("Utah Teapot");

        Vector camPos = new Vector(0.25, 0, 0.25);
        Vector camDir = new Vector(0.75, 0, -0.25);
        Vector camNormal = new Vector(0, 0, 1);
        Camera camera = new Camera(camPos, camDir, camNormal);
        scene.addActiveCamera(camera);

        PlaneMesh planeMesh = new PlaneMesh(0, 0, -1, 0, 0, 1);
        planeMesh.setMaterial(Material.WHITE_MAT);
        scene.addMesh(planeMesh);

        PolygonMesh polygonMesh = ModelLoader.loadAsciiStl("assets/utah_teapot.stl");
        polygonMesh.normalize();
        polygonMesh.rotate(90, 0, -90);
        polygonMesh.scale(0.75);
        polygonMesh.setCenterAt(1.5, 0, -0.25);
        polygonMesh.setMaterial(Material.WHITE_MAT);
        scene.addMesh(polygonMesh);

        polygonMesh = new CubeMesh(1.5, 0, -0.75, 1, 1, 0.5);
        polygonMesh.setMaterial(Material.CYAN_MAT);
        scene.addMesh(polygonMesh);

        return scene;
    }
}
