package scene.scenes;

import mesh.*;
import scene.*;

public class TSKnight extends Scene {
    public TSKnight() {
        setName("Knight");

        Vector camPos = new Vector(0, 0, 0);
        Vector camDir = new Vector(1, 0, 0);
        Vector camNormal = new Vector(0, 0, 1);
        Camera camera = new Camera(camPos, camDir, camNormal);
        addActiveCamera(camera);

        // Vectors
        Vector backLeftTop = new Vector(2, 1, 1);
        Vector backRightTop = new Vector(2, -1, 1);
        Vector backLeftBottom = new Vector(2, 1, -1);
        Vector backRightBottom = new Vector(2, -1, -1);
        Vector frontLeftTop = new Vector(-0.1, 1, 1);
        Vector frontRightTop = new Vector(-0.1, -1, 1);
        Vector frontLeftBottom = new Vector(-0.1, 1, -1);
        Vector frontRightBottom = new Vector(-0.1, -1, -1);

        PolygonMesh pMesh;
        // Back Wall
        pMesh = new PolygonMesh();
        pMesh.addPolygon(backLeftBottom, backRightTop, backRightBottom);
        pMesh.addPolygon(backLeftBottom, backLeftTop, backRightTop);
        pMesh.setMaterial(Material.GRAY_MATERIAL);
        addMesh(pMesh);
        // Left Wall
        pMesh = new PolygonMesh();
        pMesh.addPolygon(frontLeftBottom, backLeftBottom, backLeftTop);
        pMesh.addPolygon(backLeftTop, frontLeftTop, frontLeftBottom);
        pMesh.setMaterial(Material.RED_MATERIAL);
        addMesh(pMesh);
        // Right Wall
        pMesh = new PolygonMesh();
        pMesh.addPolygon(backRightBottom, frontRightBottom, backRightTop);
        pMesh.addPolygon(frontRightBottom, frontRightTop, backRightTop);
        pMesh.setMaterial(Material.BLUE_MATERIAL);
        addMesh(pMesh);
        // Floor
        pMesh = new PolygonMesh();
        pMesh.addPolygon(frontLeftBottom, frontRightBottom, backRightBottom);
        pMesh.addPolygon(backRightBottom, backLeftBottom, frontLeftBottom);
        pMesh.setMaterial(Material.GREEN_MATERIAL);
        addMesh(pMesh);
        // Ceiling
        pMesh = new PolygonMesh();
        pMesh.addPolygon(frontRightTop, frontLeftTop, backRightTop);
        pMesh.addPolygon(backRightTop, frontLeftTop, backLeftTop);
        pMesh.setMaterial(Material.WHITE_MATERIAL);
        addMesh(pMesh);
        // Front Wall
        pMesh = new PolygonMesh();
        pMesh.addPolygon(frontLeftBottom, frontRightTop, frontRightBottom);
        pMesh.addPolygon(frontLeftBottom, frontLeftTop, frontRightTop);
        pMesh.setMaterial(Material.WHITE_MATERIAL);
        addMesh(pMesh);
        // Knight
        pMesh = PolygonMesh.loadMesh("assets/KNIGHT.stl");
        pMesh.setCenterAt(0, 0, 0);
        pMesh.scale(1.5 / 3.37);
        //pMesh.rotate(0, 0, 90 + 45);
        pMesh.setCenterAt(1.5, 0, -0.25);
        pMesh.setMaterial(Material.WHITE_EMISSIVE_MATERIAL);
        addMesh(pMesh, true, true);
    }
}
