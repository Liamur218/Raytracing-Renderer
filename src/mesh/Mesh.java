package mesh;

import renderer.RaycastInfo;

public abstract class Mesh {

    public Material material;

    protected static final Material DEFAULT_MATERIAL = Material.WHITE_MATERIAL;

    public abstract RaycastInfo getClosestIntersection(Vector origin, Vector ray, RaycastInfo lastCast);

    public abstract void setCenterAt(double x, double y, double z);

    public void setMaterial(Material material) {
        this.material = material;
    }
}
