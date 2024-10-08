package scene.scenes;

import mesh.*;

public class TSObjects extends TSBasicEnvironment {
    public TSObjects() {
        setName("Objects");

        PolygonMesh pMesh;
        SphereMesh sMesh;
        CubeMesh cMesh;
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
        pMesh.setMaterial(Material.CYAN_MAT);
        addMesh(pMesh);
        // Large sphere (on ground, to right of ledge)
        sMesh = new SphereMesh(1.5, -0.5, -0.5, 0.5);
        sMesh.setMaterial(Material.WHITE_GLASS);
        addMesh(sMesh);
        // Small sphere (on ledge, left of small cube)
        sMesh = new SphereMesh(1.25, 0.825, -0.125, 0.125);
        sMesh.setMaterial(Material.WHITE_BE_MAT);
        addMesh(sMesh);
        // Large cube (cube on ledge)
        Vector pos = new Vector(1.5, 0.5, 0);
        cMesh = new CubeMesh(pos, 0.5, 0.5, 0.5, 0, 0, 50);
        cMesh.setMaterial(Material.WHITE_GLASS);
        addMesh(cMesh);
        // Small cube (cube under ledge)
        pos = new Vector(1.25, 0.5, -0.875);
        cMesh = new CubeMesh(pos, 0.25, 0.25, 0.25, 0, 0, -30);
        cMesh.setMaterial(Material.ORANGE_BE_MAT);
        addMesh(cMesh);
    }
}
