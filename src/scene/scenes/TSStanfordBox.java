package scene.scenes;

import mesh.Material;
import mesh.PlaneMesh;
import mesh.PolygonMesh;
import mesh.Vector;
import scene.Camera;
import scene.Scene;

public abstract class TSStanfordBox {
    // https://www.graphics.cornell.edu/online/box/data.html
    public static Scene newScene() {
        Scene scene = new Scene();
        scene.setName("Stanford Box");

        // Scene lighting
        scene.setAmbientLight(0);

        // Camera
        Vector cameraPos = new Vector(278, 273, -800);
        Vector cameraDir = new Vector(0, 0, 1);
        Vector cameraNorm = new Vector(0, 1, 0);
        Camera camera = new Camera(cameraPos, cameraDir, cameraNorm);
        camera.setFOV(36);
        scene.setCamera(camera);

        // Floor (white)
        PolygonMesh mesh = new PolygonMesh();
        mesh.addQuad(552.8, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 559.2, 549.6, 0.0, 559.2);
        mesh.setMaterial(Material.WHITE_MAT);
        scene.addMesh(mesh);

        // Light (white)
        mesh = new PolygonMesh();
        mesh.addQuad(343.0, 548.8, 227.0, 343.0, 548.8, 332.0, 213.0, 548.8, 332.0, 213.0, 548.8, 227.0);
        mesh.setMaterial(Material.WHITE_VERY_BRIGHT_EMISSIVE_MATERIAL);
        scene.addMesh(mesh);

        // Ceiling (white)
        mesh = new PolygonMesh();
        mesh.addQuad(556.0, 548.8, 0.0, 556.0, 548.8, 559.2, 0.0, 548.8, 559.2, 0.0, 548.8, 0.0);
        mesh.setMaterial(Material.WHITE_MAT);
        scene.addMesh(mesh);

        // Back wall (white)
        mesh = new PolygonMesh();
        mesh.addQuad(549.6, 0.0, 559.2, 0.0, 0.0, 559.2, 0.0, 548.8, 559.2, 556.0, 548.8, 559.2);
        mesh.setMaterial(Material.WHITE_MAT);
        scene.addMesh(mesh);

        // Right wall (green)
        mesh = new PolygonMesh();
        mesh.addQuad(0.0, 0.0, 559.2, 0.0, 0.0, 0.0, 0.0, 548.8, 0.0, 0.0, 548.8, 559.2);
        mesh.setMaterial(Material.GREEN_MAT);
        scene.addMesh(mesh);

        // Left wall (red)
        mesh = new PolygonMesh();
        mesh.addQuad(552.8, 0.0, 0.0, 549.6, 0.0, 559.2, 556.0, 548.8, 559.2, 556.0, 548.8, 0.0);
        mesh.setMaterial(Material.RED_MAT);
        scene.addMesh(mesh);

        // Short block (white)
        mesh = new PolygonMesh();
        mesh.addQuad(130.0, 165.0,  65.0, 82.0, 165.0, 225.0, 240.0, 165.0, 272.0, 290.0, 165.0, 114.0);
        mesh.addQuad(290.0, 0.0, 114.0, 290.0, 165.0, 114.0, 240.0, 165.0, 272.0, 240.0, 0.0, 272.0);
        mesh.addQuad(130.0, 0.0, 65.0, 130.0, 165.0, 65.0, 290.0, 165.0, 114.0, 290.0, 0.0, 114.0);
        mesh.addQuad(82.0, 0.0, 225.0, 82.0, 165.0, 225.0, 130.0, 165.0, 65.0, 130.0, 0.0, 65.0);
        mesh.addQuad(240.0, 0.0, 272.0, 240.0, 165.0, 272.0, 82.0, 165.0, 225.0, 82.0, 0.0, 225.0);
        mesh.setMaterial(Material.WHITE_MAT);
        scene.addMesh(mesh);

        // Tall block (white)
        mesh = new PolygonMesh();
        mesh.addQuad(423.0, 330.0, 247.0, 265.0, 330.0, 296.0, 314.0, 330.0, 456.0, 472.0, 330.0, 406.0);
        mesh.addQuad(423.0, 0.0, 247.0, 423.0, 330.0, 247.0, 472.0, 330.0, 406.0, 472.0, 0.0, 406.0);
        mesh.addQuad(472.0, 0.0, 406.0, 472.0, 330.0, 406.0, 314.0, 330.0, 456.0, 314.0, 0.0, 456.0);
        mesh.addQuad(314.0, 0.0, 456.0, 314.0, 330.0, 456.0, 265.0, 330.0, 296.0, 265.0, 0.0, 296.0);
        mesh.addQuad(265.0, 0.0, 296.0, 265.0, 330.0, 296.0, 423.0, 330.0, 247.0, 423.0, 0.0, 247.0);
        mesh.setMaterial(Material.WHITE_MAT);
        scene.addMesh(mesh);

        // Front plane
        PlaneMesh planeMesh = new PlaneMesh(0, 0, 0, 0, 0, 1);
        planeMesh.setMaterial(Material.WHITE_MAT);
        planeMesh.disableRearVisibility();
        scene.addMesh(planeMesh);

        return scene;
    }
}
