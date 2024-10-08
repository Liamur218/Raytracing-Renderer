package driver;

import mesh.PolygonMesh;
import util.ModelLoader;

public class PolygonCounter {
    public static void main(String[] args) {
        String filename = "assets/big_assets/master_chief/visor.stl";

        PolygonMesh polygonMesh = ModelLoader.loadMesh(filename);
        int polygonCount = polygonMesh.getPolygonArrayList().size();
        System.out.println(polygonCount + "! " + filename +
                " contains " + polygonMesh.getPolygonArrayList().size() + " polygons!  Ah. Ah. Ah.");
    }
}
