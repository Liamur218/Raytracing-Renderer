package scene.scenes;

import mesh.*;
import scene.Scene;
import util.ModelLoader;

public abstract class TSHollowKnight {
    public static Scene newScene() {
        Scene scene = TSBasicEnvironment.newScene();
        scene.setName("Hollow Knight");

        PolygonMesh polygonMesh = ModelLoader.loadStl("assets/big_assets/HollowKnight.stl");
        polygonMesh.normalize();
        polygonMesh.scale(0.5);
        polygonMesh.setMaterial(Material.BLUE_MIR);
        scene.addMesh(polygonMesh);

        return scene;
    }
}
