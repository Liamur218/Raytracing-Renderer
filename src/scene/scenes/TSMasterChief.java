package scene.scenes;

import mesh.*;
import scene.Scene;
import util.*;

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
        PolygonMesh helmet = ModelLoader.loadModel("assets/big_assets/master_chief", "Helmet 19K", ModelType.STL_BIN);
        helmet.setMaterial(helmetMat);
        helmet.scale(5);
        scene.addMesh(helmet);

        // Visor

        return scene;
    }
}
