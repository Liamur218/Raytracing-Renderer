package scene.scenes;

import mesh.*;
import scene.Scene;

import java.awt.*;

public abstract class TSMasterChief {
    public Scene TSMasterChief() {
        Scene scene = TSBasicEnvironment.newScene();
        scene.setName("Master Chief");

        // Materials
        NormColor helmetColor = new NormColor(new Color(40, 50, 20));
        NormColor visorColor = new NormColor(new Color(200, 180, 80));
        Material helmetMat = new Material(helmetColor).setSpecularity(0);
        Material visorMat = new Material(visorColor).setSpecularity(0.8f);

        // Helmet
        PolygonMesh helmet = MeshLoader.loadModel(
                "assets/Large Models/master Chief", "Helmet 19K", MeshFileType.STL_BIN);
        helmet.setMaterial(helmetMat);
        helmet.scale(5);
        scene.addMesh(helmet);

        // Visor

        return scene;
    }
}
