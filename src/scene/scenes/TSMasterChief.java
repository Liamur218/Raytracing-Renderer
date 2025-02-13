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
        DoubleColor helmetColor = new DoubleColor(new Color(40, 50, 20));
        DoubleColor visorColor = new DoubleColor(new Color(200, 180, 80));
        Material helmetMat = new Material(helmetColor).setSpecularity(0);
        Material visorMat = new Material(visorColor).setSpecularity(0.8);

        // Helmet
        PolygonMesh helmet = ModelLoader.loadModel("assets/big_assets/master_chief", "Helmet 19K", ModelType.STL_BIN);
        helmet.setMaterial(helmetMat);
        helmet.scale(5);
        scene.addMesh(helmet);

        // Visor

        return scene;
    }
}
