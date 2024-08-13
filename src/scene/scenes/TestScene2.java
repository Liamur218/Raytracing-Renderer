package scene.scenes;

import mesh.*;
import scene.Camera;
import scene.Scene;

public class TestScene2 extends Scene {
    public TestScene2() {
        super("TSc2");

        Vector camPos = new Vector(-0.375, -0.375, 0.5);
        Vector camDir = new Vector(1, 1, -1);
        Vector camNormal = new Vector(0, 0, 1);
        Camera camera = new Camera(camPos, camDir, camNormal);

        addActiveCamera(camera);
        setBackgroundColor(DoubleColor.BLACK);

        // Variables
        double cubeBuffer = 0.0625;
        double cubeSize = 0.25;
        double cubeSpacing = 0.25;

        CubeMesh cube;
        PlaneMesh plMesh;
        PolygonMesh pMesh;

        // Floor
        pMesh = new PolygonMesh();
        pMesh.addPolygon(
                new Vector(-1, 1, 0),
                new Vector(1, -1, 0),
                new Vector(1, 1, 0));
        pMesh.addPolygon(
                new Vector(-1, 1, 0),
                new Vector(-1, -1, 0),
                new Vector(1, -1, 0));
        pMesh.setMaterial(Material.WHITE_MATERIAL);
        addMesh(pMesh);

        // Central Cube
        cube = new CubeMesh(0, 0, 0.0625, 0.125, 0.125, 0.125);
        cube.setMaterial(Material.WHITE_BRIGHT_EMISSIVE_MATERIAL);
        addMesh(cube);

        // Surrounding Cubes
        CubeMesh[] cubes = new CubeMesh[]{
                new CubeMesh(0, cubeSpacing, cubeSize / 2 - cubeBuffer, cubeSize - cubeBuffer),
                new CubeMesh(cubeSpacing, cubeSize * 3 / 4, cubeSize / 2 - cubeBuffer, cubeSize - cubeBuffer, cubeSize * 1.5 - cubeBuffer, cubeSize - cubeBuffer),
                new CubeMesh(cubeSpacing, -cubeSize * 3 / 4, cubeSize / 2 - cubeBuffer, cubeSize - cubeBuffer, cubeSize * 1.5 - cubeBuffer, cubeSize - cubeBuffer),
                new CubeMesh(0, -cubeSpacing, cubeSize / 2 - cubeBuffer, cubeSize - cubeBuffer),
                new CubeMesh(-cubeSpacing, -cubeSize * 3 / 4, cubeSize / 2 - cubeBuffer, cubeSize - cubeBuffer, cubeSize * 1.5 - cubeBuffer, cubeSize - cubeBuffer),
                new CubeMesh(-cubeSpacing, cubeSize * 3 / 4, cubeSize / 2 - cubeBuffer, cubeSize - cubeBuffer, cubeSize * 1.5 - cubeBuffer, cubeSize - cubeBuffer)
        };
        for (CubeMesh cubeMesh : cubes) {
            cubeMesh.setMaterial(Material.CYAN_MATERIAL);
            addMesh(cubeMesh);
        }
    }
}
