package scene.scenes;

import mesh.*;
import scene.Scene;

import java.awt.*;
import java.util.Arrays;

public abstract class TSMasterChief {
    public static Scene newScene() {
        Scene scene = TSBasicEnvironment.newScene();
        scene.setName("Master Chief");

        // Materials
        NormColor helmetColor = new NormColor(new Color(40, 50, 20));
        NormColor visorColor = new NormColor(new Color(200, 180, 80));
        Material helmetMat = new Material(helmetColor).setSpecularity(0);
        Material visorMat = new Material(visorColor).setSpecularity(0.8f);

        String dir = "assets/Large Models/master Chief";
        Vector rotVector = new Vector(0,0, -45);
        Vector moveVector = new Vector(0, 0, -1.5);
        Vector posVector = new Vector(1, 0, 0);
        double scale = 0.3;

        // Helmet
        PolygonMesh helmet = MeshLoader.loadModel(dir, "Helmet 19K", MeshFileType.STL_BIN);
        helmet.setMaterial(helmetMat);
        helmet.move(moveVector).rotate(rotVector).scale(scale);
        helmet.move(posVector);
        scene.addMesh(helmet);

        // Visor
        PolygonMesh visor = MeshLoader.loadModel(dir, "Visor 3K", MeshFileType.STL_BIN);
        visor.setMaterial(visorMat);
        visor.move(moveVector).rotate(rotVector).scale(scale);
        visor.move(posVector);
        scene.addMesh(visor);

        return scene;
    }
}
