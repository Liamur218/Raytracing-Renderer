package scene.scenes;

import mesh.*;

import java.awt.*;

public class TSMasterChief extends TSBasicEnvironment {
    public TSMasterChief() {
        setName("Master Chief");

        // Materials
        DoubleColor helmetColor = new DoubleColor(new Color(40, 50, 20));
        DoubleColor visorColor = new DoubleColor(new Color(200, 180, 80));
        Material helmetMat = new Material(helmetColor, 1, 0, 0, 1, Material.DEFAULT_REFRACTIVE_INDEX);
        Material visorMat = new Material(visorColor, 1, 0, 0.8, 1, Material.DEFAULT_REFRACTIVE_INDEX);

        // Helmet
        PolygonMesh helmet = PolygonMesh.loadMesh("assets/big_assets/master_chief/helmet_19K.stl");
        helmet.setMaterial(helmetMat);
        helmet.scale(5);
        addMesh(helmet, true, true);

        // Visor

    }
}
