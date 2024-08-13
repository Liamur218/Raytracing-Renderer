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
        if (mesh instanceof PolygonMesh pMesh) {
            if (pMesh.boundingVolume == null) {
                pMesh.finalizeMesh();
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

    public String getName() {
        return sceneName;
    }
}
