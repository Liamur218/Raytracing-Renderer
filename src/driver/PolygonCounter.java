package driver;

import mesh.PolygonMesh;

public class PolygonCounter {
    public static void main(String[] args) {
        String filename = "assets/master chief/helmet.stl";

        PolygonMesh polygonMesh = PolygonMesh.loadMesh(filename);
        System.out.println(filename + " contains " + polygonMesh.getPolygonArrayList().size() + " polygons");
    }
}
