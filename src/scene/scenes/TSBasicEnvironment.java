package scene.scenes;

import mesh.*;
import scene.*;

public abstract class TSBasicEnvironment {
    public static Scene newScene() {
        Vector camPos, camDir, camNormal;
        Camera camera;

        PolygonMesh backWall, leftWall, rightWall, floor, ceiling, frontWall;

        Vector backLeftTop, backRightTop, backLeftBottom, backRightBottom;
        Vector frontLeftTop, frontRightTop, frontLeftBottom, frontRightBottom;

        Scene scene = new Scene();
        scene.setName("Basic Environment");

        camPos = new Vector(0, 0, 0);
        camDir = new Vector(1, 0, 0);
        camNormal = new Vector(0, 0, 1);
        camera = new Camera(camPos, camDir, camNormal).setFOV(105);
        scene.setCamera(camera);

        // Vectors
        backLeftTop = new Vector(2, 1, 1);
        backRightTop = new Vector(2, -1, 1);
        backLeftBottom = new Vector(2, 1, -1);
        backRightBottom = new Vector(2, -1, -1);
        frontLeftTop = new Vector(-0.1, 1, 1);
        frontRightTop = new Vector(-0.1, -1, 1);
        frontLeftBottom = new Vector(-0.1, 1, -1);
        frontRightBottom = new Vector(-0.1, -1, -1);

        // Back Wall
        backWall = new PolygonMesh();
        backWall.addPolygon(backLeftBottom, backRightTop, backRightBottom);
        backWall.addPolygon(backLeftBottom, backLeftTop, backRightTop);
        backWall.setMaterial(Material.WHITE_MAT);
        scene.addMesh(backWall);
        // Left Wall
        leftWall = new PolygonMesh();
        leftWall.addPolygon(frontLeftBottom, backLeftBottom, backLeftTop);
        leftWall.addPolygon(backLeftTop, frontLeftTop, frontLeftBottom);
        leftWall.setMaterial(Material.RED_MAT);
        scene.addMesh(leftWall);
        // Right Wall
        rightWall = new PolygonMesh();
        rightWall.addPolygon(backRightBottom, frontRightBottom, backRightTop);
        rightWall.addPolygon(frontRightBottom, frontRightTop, backRightTop);
        rightWall.setMaterial(Material.BLUE_MAT);
        scene.addMesh(rightWall);
        // Floor
        floor = new PolygonMesh();
        floor.addPolygon(frontLeftBottom, frontRightBottom, backRightBottom);
        floor.addPolygon(backRightBottom, backLeftBottom, frontLeftBottom);
        floor.setMaterial(Material.GREEN_MAT);
        scene.addMesh(floor);
        // Ceiling
        ceiling = new PolygonMesh();
        ceiling.addPolygon(frontRightTop, frontLeftTop, backRightTop);
        ceiling.addPolygon(backRightTop, frontLeftTop, backLeftTop);
        ceiling.setMaterial(Material.WHITE_EMISSIVE_MAT);
        scene.addMesh(ceiling);
        // Front Wall
        frontWall = new PolygonMesh();
        frontWall.addPolygon(frontLeftBottom, frontRightTop, frontRightBottom);
        frontWall.addPolygon(frontLeftBottom, frontLeftTop, frontRightTop);
        frontWall.setMaterial(Material.WHITE_MAT);
        scene.addMesh(frontWall);

        return scene;
    }
}
