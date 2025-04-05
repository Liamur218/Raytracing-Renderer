package scene.scenes;

import mesh.*;
import scene.Scene;

public abstract class TSRefractionTest {
    public static Scene newScene() {
        Scene scene = TSBasicEnvironment.newScene();
        scene.setName("Refraction Test");

        CubeMesh cubeMesh = new CubeMesh(1.5, 0, 0, 0.125, 1, 0.5);
        cubeMesh.setMaterial(new Material(Material.GLASS).setColorInt(245, 245, 245));
        //scene.addMesh(cMesh);

        SphereMesh sphereMesh = new SphereMesh(1, 0, 0 /*-0.625*/, 0.375);
        sphereMesh.setMaterial(Material.WHITE_GLASS);
        scene.addMesh(sphereMesh);

        cubeMesh = new CubeMesh(1.875, 0, 0, 0.25, 0.125, 2);
        cubeMesh.setMaterial(Material.ORANGE_MAT);
        scene.addMesh(cubeMesh);

        return scene;
    }
}
