package scene;

import mesh.*;
import mesh.Vector;

import java.awt.*;
import java.util.*;

public class Scene {

    public ArrayList<Mesh> meshes;
    public Camera camera;

    // Environment color -> raw color of the skybox (i.e. blue sky)
    // Ambient light color -> color of light illuminating objects in the scene (i.e. while light from the sun)
    public NormColor enviroColor, ambientLightColor;
    public double ambientLightIntensity;
    public Vector lightSourceDir;

    public static final NormColor DEFAULT_ENVIRO_COLOR = new NormColor(new Color(0, 164, 200));
    public static final NormColor DEFAULT_AMBIENT_LIGHT = new NormColor(new Color(255, 255, 255));
    public static final double DEFAULT_AMBIENT_LIGHT_INTENSITY = 1;
    public static final Vector DEFAULT_DIR_LIGHT_DIR = new Vector(-1, 1, 1).normalize();

    public String name;

    public Scene() {
        meshes = new ArrayList<>();
        enviroColor = DEFAULT_ENVIRO_COLOR;
        setAmbientLight(DEFAULT_AMBIENT_LIGHT, DEFAULT_AMBIENT_LIGHT_INTENSITY);
        setLightSourceDir(DEFAULT_DIR_LIGHT_DIR);

        setName(getClass().getName());
    }

    public void setName(String sceneName) {
        this.name = sceneName;
    }

    // Setup
    public void addMesh(Mesh mesh) {
        if (mesh instanceof PolygonMesh pMesh) {
            if (!pMesh.finalized) {
                pMesh.finalizeMesh();
            }
        }
        meshes.add(mesh);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setEnviroColor(NormColor color) {
        enviroColor = color;
    }

    public void setAmbientLight(NormColor color, double intensity) {
        ambientLightColor = color;
        ambientLightIntensity = intensity;
    }

    public void setAmbientLight(NormColor color) {
        ambientLightColor = color;
    }

    public void setAmbientLight(double intensity) {
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
}
