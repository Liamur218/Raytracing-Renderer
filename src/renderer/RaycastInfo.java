package renderer;

import mesh.*;

public class RaycastInfo {

    // Raw collision info
    Vector origin;
    Vector direction;
    public Vector intersection, normal;
    public Mesh mesh;
    public Material material;
    public Polygon polygon;
    public SphereMesh sphere;
    public double distance;

    // Carries color info back to camera
    DoubleColor rayColor;

    public RaycastInfo() {}

    public RaycastInfo(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction;
        distance = Double.MAX_VALUE;

        rayColor = new DoubleColor();
    }

    @Override
    public String toString() {
        return origin + " -> " + direction + " -| " + intersection + " (" + ((sphere == null) ? polygon : sphere) + ")";
    }
}
