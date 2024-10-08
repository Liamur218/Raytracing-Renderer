package scene.scenes;

import mesh.*;
import util.ModelLoader;

import java.awt.*;

public class TSMasterChief extends TSBasicEnvironment {
    public TSMasterChief() {
        setName("Master Chief");

        // Materials
        DoubleColor helmetColor = new DoubleColor(new Color(40, 50, 20));
        DoubleColor visorColor = new DoubleColor(new Color(200, 180, 80));
        Material helmetMat = new Material(helmetColor).setSpecularity(0);
        Material visorMat = new Material(visorColor).setSpecularity(0.8);

        // Helmet
        PolygonMesh helmet = ModelLoader.loadMesh("assets/big_assets/master_chief/helmet_19K.stl");
        helmet.setMaterial(helmetMat);
        helmet.scale(5);
        addMesh(helmet, true, true);

        // Visor

    }
}
