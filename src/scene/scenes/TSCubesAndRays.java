package scene.scenes;

import mesh.*;
import scene.*;

public abstract class TSCubesAndRays {
    public static Scene newScene() {
        Scene scene = new Scene();
        scene.setName("Cubes and Rays");

        Vector camPos = new Vector(-0.375, -0.375, 0.45);
        Vector camDir = new Vector(1, 1, -1);
        Vector camNormal = new Vector(0, 0, 1);
        Camera camera = new Camera(camPos, camDir, camNormal);
        scene.setCamera(camera);
        scene.setAmbientLight(0);

        // Variables
        double cubeBuffer = 0.0625;
        double cubeSize = 0.25;
        double cubeSpacing = 0.25;

        CubeMesh cube;
        PlaneMesh plMesh;

        // Floor
        plMesh = new PlaneMesh(0, 0, 0, 0, 0, 1);
        plMesh.setMaterial(Material.WHITE_MAT);
        scene.addMesh(plMesh);

        // Central Cube
        cube = new CubeMesh(0, 0, 0.0625, 0.125, 0.125, 0.125);
        cube.setMaterial(Material.WHITE_EMISSIVE_MAT);
        scene.addMesh(cube);

        // Surrounding Cubes
        CubeMesh[] cubes = new CubeMesh[]{
                new CubeMesh(0, cubeSpacing, cubeSize / 2 - cubeBuffer, cubeSize - cubeBuffer),
                new CubeMesh(cubeSpacing, cubeSize * 3 / 4, cubeSize / 2 - cubeBuffer,
                        cubeSize - cubeBuffer, cubeSize * 1.5 - cubeBuffer, cubeSize - cubeBuffer),
                new CubeMesh(cubeSpacing, -cubeSize * 3 / 4, cubeSize / 2 - cubeBuffer,
                        cubeSize - cubeBuffer, cubeSize * 1.5 - cubeBuffer, cubeSize - cubeBuffer),
                new CubeMesh(0, -cubeSpacing, cubeSize / 2 - cubeBuffer, cubeSize - cubeBuffer),
                new CubeMesh(-cubeSpacing, -cubeSize * 3 / 4, cubeSize / 2 - cubeBuffer,
                        cubeSize - cubeBuffer, cubeSize * 1.5 - cubeBuffer, cubeSize - cubeBuffer),
                new CubeMesh(-cubeSpacing, cubeSize * 3 / 4, cubeSize / 2 - cubeBuffer,
                        cubeSize - cubeBuffer, cubeSize * 1.5 - cubeBuffer, cubeSize - cubeBuffer)
        };
        for (CubeMesh cubeMesh : cubes) {
            cubeMesh.setMaterial(Material.CYAN_MAT);
            scene.addMesh(cubeMesh);
        }

        return scene;
    }
}
