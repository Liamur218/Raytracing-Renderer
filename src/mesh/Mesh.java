package mesh;

import renderer.RaycastInfo;

public abstract class Mesh {

    public Material material;
    protected static final Material DEFAULT_MATERIAL = Material.WHITE_MAT;

    public int id;
    private static int ID_COUNTER = 0;

    protected String name;

    public static final Mesh NULL_MESH = new Mesh() {
        @Override
        public RaycastInfo getClosestIntersection(Vector origin, Vector ray, RaycastInfo lastCast) { return null; }
        @Override
        public Mesh setCenterAt(double x, double y, double z) { return null; }
        @Override
        public Mesh scale(double scaleX, double scaleY, double scaleZ) { return null; }
        @Override
        public Mesh duplicate() { return null; }
    }.setMaterial(Material.AIR);

    protected Mesh() {
        id = ID_COUNTER++;
        setName("Mesh " + id);
    }

    public abstract Mesh duplicate();

    public abstract RaycastInfo getClosestIntersection(Vector origin, Vector ray, RaycastInfo lastCast);

    public abstract Mesh setCenterAt(double x, double y, double z);

    public abstract Mesh scale(double scaleX, double scaleY, double scaleZ);

    public Mesh scale(double scale) {
        return scale(scale, scale, scale);
    }

    public Mesh scale(Vector scale) {
        return scale(scale.x, scale.y, scale.z);
    }

    public Mesh setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public Mesh setName(String name) {
        this.name = name;
        return this;
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
