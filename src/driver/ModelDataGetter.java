package driver;

import mesh.*;

public class ModelDataGetter {
    public static void main(String[] args) {
        String filename = "Croix";
        String fileLocation = "assets/big_assets/Crucifix";
        MeshFileType modelType = MeshFileType.STL_BIN;

        String[] modelNameArray = filename.split("/");
        String modelName = modelNameArray[modelNameArray.length - 1];

        PolygonMesh polygonMesh = MeshLoader.loadModel(fileLocation, filename, modelType);
        int polygonCount = polygonMesh.getPolygonCount();

        System.out.println("Model Name: " + modelName);
        System.out.println("MODEL DATA:");
        System.out.println("\tPOLYGON COUNT -- " + polygonCount);

        Vector[] centerAndSize = polygonMesh.getCenterAndSize();
        Vector center = centerAndSize[0];
        Vector size = centerAndSize[1];

        System.out.println("MODEL DIMENSIONS:");
        System.out.println("\tSIZE ------------- " + size);
        System.out.println("\tCENTER ----------- " + center);
        System.out.println("\tMIN DIMENSION ---- " + Vector.subtract(center, Vector.divide(size, 2)));
        System.out.println("\tMAX DIMENSION ---- " + Vector.add(center, Vector.divide(size, 2)));

        polygonMesh.normalize();
        centerAndSize = polygonMesh.getCenterAndSize();
        center = centerAndSize[0];
        size = centerAndSize[1];

        System.out.println("NORMALIZED MODEL DATA:");
        System.out.println("\tSIZE ------------- " + size);
        System.out.println("\tCENTER ----------- " + center);
        System.out.println("\tMIN DIMENSION ---- " + Vector.subtract(center, Vector.divide(size, 2)));
        System.out.println("\tMAX DIMENSION ---- " + Vector.add(center, Vector.divide(size, 2)));

        polygonMesh.finalizeMesh();

        System.out.println("BOUNDING BOX DATA:");
        System.out.println("\tMAX DEPTH -------- " + polygonMesh.getBoundingBox().getMaxDepth());
        System.out.println("\tMIN P-GONS / BOX - " + polygonMesh.getBoundingBox().getMinPolygonsPerBox());
        System.out.println("\tMAX P-GONS / BOX - " + polygonMesh.getBoundingBox().getMaxPolygonsPerBox());
    }
}
