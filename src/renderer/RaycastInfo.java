package renderer;

import mesh.Vector;
import mesh.*;
import util.YetAnotherStack;

public class RaycastInfo {

    // Raw collision info
    public Vector origin;
    public Vector direction;
    public Vector intersection, normal;
    public Mesh mesh;
    public Material material;
    public Polygon polygon;
    public SphereMesh sphere;
    public double distance;

    // Carries color info back to camera
    public DoubleColor rayColor;

    // Carries info forward along successive raycasts
    public YetAnotherStack<Mesh> meshStack;

    public RaycastInfo(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction;
        distance = Double.MAX_VALUE;

        rayColor = new DoubleColor();
    }

    public void set(Vector intersection, Vector normal, Mesh mesh, Material material, double distance) {
        this.intersection = intersection;
        this.normal = normal;
        this.mesh = mesh;
        this.material = material;
        this.distance = distance;
    }

    public void set(Vector intersection, Vector normal, Mesh mesh, Material material,
                    Polygon polygon, double distance) {
        this.intersection = intersection;
        this.normal = normal;
        this.mesh = mesh;
        this.material = material;
        this.polygon = polygon;
        this.distance = distance;
    }

    public void set(RaycastInfo raycastInfo) {
        origin = raycastInfo.origin;
        direction = raycastInfo.direction;
        intersection = raycastInfo.intersection;
        normal = raycastInfo.normal;
        mesh = raycastInfo.mesh;
        material = raycastInfo.material;
        polygon = raycastInfo.polygon;
        sphere = raycastInfo.sphere;
        distance = raycastInfo.distance;
    }

    @Override
    public String toString() {
        return origin + " -> " + direction + " - " + distance + " -| " + intersection +
                " (Obj: " + ((sphere == null) ? polygon : sphere) + ")";
    }
}
