package scene;

import mesh.*;
import mesh.Vector;

import java.awt.*;
import java.util.*;

public class Scene {

    public ArrayList<Mesh> meshes;
    public Camera camera;

    // Environment color -> raw color of the skybox (e.g. blue sky)
    // Ambient light color -> color of light illuminating objects in the scene (e.g. while light from the sun)
    public NormColor enviroColor, ambientLightColor;
    public float ambientLightIntensity;
    public Vector lightSourceDir;

    public static final NormColor DEFAULT_ENVIRO_COLOR = new NormColor(new Color(0, 164, 200));
    public static final NormColor DEFAULT_AMBIENT_LIGHT = new NormColor(new Color(255, 255, 255));
    public static final float DEFAULT_AMBIENT_LIGHT_INTENSITY = 1;
    public static final Vector DEFAULT_DIR_LIGHT_DIR = new Vector(-1, 1, 1).normalize();

    public String name;
    boolean finalized;

    public static final HashMap<Integer, Scene> SCENES_LIST = new HashMap<>();

    public Scene() {
        meshes = new ArrayList<>();
        enviroColor = DEFAULT_ENVIRO_COLOR;
        setAmbientLight(DEFAULT_AMBIENT_LIGHT, DEFAULT_AMBIENT_LIGHT_INTENSITY);
        setLightSourceDir(DEFAULT_DIR_LIGHT_DIR);

        setName(getClass().getName());
    }

    public Scene setName(String sceneName) {
        this.name = sceneName;
        return this;
    }

    // Setup
    public void addMesh(Mesh mesh) {
        meshes.add(mesh);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setSkyColor(NormColor color) {
        enviroColor = color;
    }

    public void setAmbientLight(NormColor color, float intensity) {
        ambientLightColor = color;
        ambientLightIntensity = intensity;
    }

    public void setAmbientLight(NormColor color) {
        ambientLightColor = color;
    }

    public void setAmbientLight(float intensity) {
        ambientLightIntensity = intensity;
    }

    public void setLightSourceDir(Vector dir) {
        lightSourceDir = dir;
    }

    public void scale(double scaleX, double scaleY, double scaleZ) {
        for (Mesh mesh : meshes) {
            mesh.scale(scaleX, scaleY, scaleZ);
        }
        Vector scale = new Vector(scaleX, scaleY, scaleZ);
        camera.pos.set(Vector.componentMultiply(camera.pos, scale));
    }

    public void scale(double scale) {
        scale(scale, scale, scale);
    }

    public void scale(Vector scale) {
        scale(scale.getX(), scale.getY(), scale.getZ());
    }

    public void finalizeMeshes() {
        for (Mesh mesh : meshes) {
            if (mesh instanceof PolygonMesh polygonMesh) {
                polygonMesh.finalizeMesh();
            }
        }
        finalized = true;
        SCENES_LIST.put(hashCode(), this);
    }

    // Printing stuff
    @Override
    public String toString() {
        return name;
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

    // Hashcode
    @Override
    public int hashCode() {
        return Objects.hash(meshes, camera, enviroColor,
                ambientLightColor, ambientLightIntensity, lightSourceDir, name);
    }
}
