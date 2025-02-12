package mesh;

import renderer.RaycastInfo;

public abstract class Mesh {

    public Material material;
    protected static final Material DEFAULT_MATERIAL = Material.WHITE_MAT;

    public int id;
    private static int ID_COUNTER = 0;

    private String name;

    public static final Mesh NULL_MESH = new Mesh() {
        @Override
        public RaycastInfo getClosestIntersection(Vector origin, Vector ray, RaycastInfo lastCast) { return null; }
        @Override
        public void setCenterAt(double x, double y, double z) {}
        @Override
        public void scale(double scaleX, double scaleY, double scaleZ) {}
    }.setMaterial(Material.AIR);

    protected Mesh() {
        id = ID_COUNTER++;
        setName("Mesh " + id);
    }

    public abstract RaycastInfo getClosestIntersection(Vector origin, Vector ray, RaycastInfo lastCast);

    public abstract void setCenterAt(double x, double y, double z);

    public abstract void scale(double scaleX, double scaleY, double scaleZ);

    public void scale(double scale) {
        scale(scale, scale, scale);
    }

    public void scale(Vector scale) {
        scale(scale.x, scale.y, scale.z);
    }

    public Mesh setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Mesh mesh) {
            return mesh.id == id;
        }
        return false;
    }

    @Override
    public String toString() {
        return name;
    }
}
