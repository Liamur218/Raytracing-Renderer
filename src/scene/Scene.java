package scene;

import mesh.*;

import java.awt.*;
import java.util.ArrayList;

public class Scene {

    public ArrayList<Mesh> meshes;
    public ArrayList<Camera> cameras;
    public Camera activeCamera;
    public DoubleColor backgroundColor;

    private static final DoubleColor DEFAULT_BACKGROUND_COLOR = new DoubleColor(new Color(0, 164, 200));

    String sceneName;

    public Scene() {
        this("");
    }

    public Scene(String sceneName) {
        meshes = new ArrayList<>();
        cameras = new ArrayList<>();
        backgroundColor = DEFAULT_BACKGROUND_COLOR;

        this.sceneName = sceneName;
    }

    // Setup
    public void addMesh(Mesh mesh) {
        addMesh(mesh, BoundingBox.DEFAULT_DO_COLL_CHECKING, BoundingBox.DEFAULT_IS_BVH);
    }

    public void addMesh(Mesh mesh, boolean doBBoxCollChecking, boolean genBBoxAsBVH) {
        if (mesh instanceof PolygonMesh pMesh) {
            if (!pMesh.finalized) {
                pMesh.finalizeMesh(doBBoxCollChecking, genBBoxAsBVH);
            }
        }
        meshes.add(mesh);
    }

    public void addCamera(Camera camera) {
        cameras.add(camera);
    }

    public void addCamera(double posX, double posY, double posZ, double dirX, double dirY, double dirZ,
                          double normalX, double normalY, double normalZ, int hFOV, int vFOV) {
        addCamera(new Vector(posX, posY, posZ), new Vector(dirX, dirY, dirZ), new Vector(normalX, normalY, normalZ),
                hFOV, vFOV);
    }

    public void addCamera(Vector pos, Vector dir, Vector normal, int hFOV, int vFOV) {
        addCamera(new Camera(pos, dir, normal, hFOV, vFOV));
    }

    public void addActiveCamera(Camera camera) {
        cameras.add(camera);
        setActiveCamera(camera);
    }

    public void addActiveCamera(Vector pos, Vector dir, Vector normal, int hFOV, int vFOV) {
        addActiveCamera(new Camera(pos, dir, normal, hFOV, vFOV));
    }

    public void addActiveCamera(double posX, double posY, double posZ, double dirX, double dirY, double dirZ,
                                double normalX, double normalY, double normalZ, int hFOV, int vFOV) {
        addActiveCamera(
                new Vector(posX, posY, posZ), new Vector(dirX, dirY, dirZ), new Vector(normalX, normalY, normalZ),
                hFOV, vFOV);
    }

    public void setActiveCamera(Camera camera) {
        activeCamera = camera;
    }

    public void setActiveCamera(int cameraIndex) {
        activeCamera = cameraIndex == -1 || cameraIndex > cameras.size() ? null : cameras.get(cameraIndex);
    }

    public void setBackgroundColor(DoubleColor color) {
        backgroundColor = color;
    }

    // Printing stuff
    @Override
    public String toString() {
        return sceneName;
    }

    public void printAllObjects(boolean detailMaterials) {
        for (Mesh mesh : meshes) {
            if (mesh instanceof PolygonMesh polygonMesh) {
                Vector[] centerAndSize = polygonMesh.getCenterAndSize();
                System.out.print("Polygon Mesh w/ " + polygonMesh.polygons.length + " polygons @ " +
                        centerAndSize[0] + " (Dim: " + centerAndSize[1] + ") ");
            } else if (mesh instanceof SphereMesh sphereMesh) {
                System.out.print("Sphere Mesh @ " + sphereMesh.center + " radius: " + sphereMesh.radius);
            } else {
                System.out.print(mesh);
            }

            System.out.println("Mat: " + mesh.material);
            if (detailMaterials) {
                System.out.println("\tColor ------------ " + mesh.material.color);
                System.out.println("\tReflectivity ----- " + mesh.material.reflectivity);
                System.out.println("\tEmissivity ------- " + mesh.material.emissivity);
                System.out.println("\tSpecularity ------ " + mesh.material.specularity);
                System.out.println("\tRefractive Index - " + mesh.material.refractiveIndex);
                System.out.println("\tOpacity ---------- " + mesh.material.opacity);
            }
        }
    }
}
