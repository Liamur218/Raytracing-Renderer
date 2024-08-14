package scene.scenes;

import mesh.*;
import scene.Camera;
import scene.Scene;

public class TestScene1 extends Scene {
    public TestScene1() {
        super("TSc1");

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
        SphereMesh sMesh;
        CubeMesh cMesh;
        // Back Wall
        pMesh = new PolygonMesh();
        pMesh.addPolygon(backLeftBottom, backRightTop, backRightBottom);
        pMesh.addPolygon(backLeftBottom, backLeftTop, backRightTop);
        pMesh.setMaterial(Material.WHITE_MATERIAL);
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
        pMesh.setMaterial(Material.WHITE_EMISSIVE_MATERIAL);
        addMesh(pMesh);
        // Front Wall
        pMesh = new PolygonMesh();
        pMesh.addPolygon(frontLeftBottom, frontRightTop, frontRightBottom);
        pMesh.addPolygon(frontLeftBottom, frontLeftTop, frontRightTop);
        pMesh.setMaterial(Material.WHITE_MATERIAL);
        addMesh(pMesh);
        // Ledge
        pMesh = new PolygonMesh();
        pMesh.addPolygon(2, 1, -0.25, 1, 1, -0.25, 2, 0.125, -0.25);
        pMesh.addPolygon(2, 0.125, -0.25, 1, 1, -0.25, 1, 0.125, -0.25);
        pMesh.addPolygon(1, 0.125, -0.25, 1, 0.125, -0.5, 2, 0.125, -0.5);
        pMesh.addPolygon(2, 0.125, -0.5, 2, 0.125, -0.25, 1, 0.125, -0.25);
        pMesh.addPolygon(1, 0.125, -0.5, 1, 0.125, -0.25, 1, 1, -0.25);
        pMesh.addPolygon(1, 1, -0.5, 1, 0.125, -0.5, 1, 1, -0.25);
        pMesh.addPolygon(2, 1, -0.5, 2, 0.125, -0.5, 1, 1, -0.5);
        pMesh.addPolygon(2, 0.125, -0.5, 1, 0.125, -0.5, 1, 1, -0.5);
        pMesh.setMaterial(Material.CYAN_MATERIAL);
        addMesh(pMesh);
        // Large sphere (on ground, to right of ledge)
        sMesh = new SphereMesh(1.5, -0.5, -0.5, 0.5);
        sMesh.setMaterial(Material.WHITE_MIRROR);
        addMesh(sMesh);
        // Small sphere (on ledge, left of small cube)
        sMesh = new SphereMesh(1.25, 0.825, -0.125, 0.125);
        sMesh.setMaterial(Material.YELLOW_MATERIAL);
        addMesh(sMesh);
        // Large cube (cube on ledge)
        Vector pos = new Vector(1.5, 0.5, 0);
        cMesh = new CubeMesh(pos, 0.5, 0.5, 0.5, 0, 0, 45);
        cMesh.setMaterial(Material.WHITE_GLASS);
        addMesh(cMesh);
        // Small cube (cube under ledge)
        pos = new Vector(1.25, 0.5, -0.875);
        cMesh = new CubeMesh(pos, 0.25, 0.25, 0.25, 0, 0, -30);
        cMesh.setMaterial(Material.ORANGE_VERY_BRIGHT_EMISSIVE_MATERIAL);
        addMesh(cMesh);
    }
}
