package scene;

import mesh.Vector;

import java.awt.*;

public class Camera {

    public Vector pos, dir, normal, binormal;
    public Dimension fov;

    private static final Vector DEFAULT_POS = new Vector(0, 0, 0);
    private static final Vector DEFAULT_DIR = new Vector(1, 0, 0);
    private static final Vector DEFAULT_NORMAL = new Vector(0, 0, 1);
    private static final Dimension DEFAULT_FOV = new Dimension(100, 90);

    public Camera() {
        this(DEFAULT_POS, DEFAULT_DIR, DEFAULT_NORMAL, DEFAULT_FOV.width, DEFAULT_FOV.height);
    }

    public Camera(double posX, double posY, double posZ, double dirX, double dirY, double dirZ,
           double normalX, double normalY, double normalZ) {
        pos = new Vector(posX, posY, posZ);
        dir = new Vector(dirX, dirY, dirZ).normalize();
        normal = new Vector(normalX, normalY, normalZ).normalize();
        binormal = Vector.cross(dir, normal);
        normal = Vector.cross(binormal, dir);  // Re-set normal in cast it wasn't @ 90° to dir to begin with
        setFOV(DEFAULT_FOV);
    }

    public Camera(Vector pos, Vector dir, Vector normal) {
        this(pos.getX(), pos.getY(), pos.getZ(),
                dir.getX(), dir.getY(), dir.getZ(),
                normal.getX(), normal.getY(), normal.getZ());
    }

    public Camera(double posX, double posY, double posZ, double dirX, double dirY, double dirZ,
                  double normalX, double normalY, double normalZ, int hFOV, double aspectRatio) {
        this(posX, posY, posZ, dirX, dirY, dirZ, normalX, normalY, normalZ);
        setFOV(hFOV, DEFAULT_FOV.height);
        setAspectRatio(aspectRatio);
    }

    public Camera(double posX, double posY, double posZ, double dirX, double dirY, double dirZ,
           double normalX, double normalY, double normalZ, int hFOV, int vFOV) {
        this(new Vector(posX, posY, posZ),
                new Vector(dirX, dirY, dirZ),
                new Vector(normalX, normalY, normalZ),
                hFOV, vFOV);
    }

    public Camera(Vector pos, Vector dir, Vector normal, int hFOV, int vFOV) {
        this(pos, dir, normal);
        setFOV(hFOV, vFOV);
    }

    public Camera(Vector pos, Vector dir, Vector normal, int hFOV, double aspectRatio) {
        this(pos, dir, normal);
        setFOV(hFOV, DEFAULT_FOV.height);
        setAspectRatio(aspectRatio);
    }

    public Camera setFOV(int fov) {
        return setFOV(fov, fov);
    }

    public Camera setFOV(int hFOV, int vFOV) {
        fov = new Dimension(hFOV, vFOV);
        return this;
    }

    public Camera setFOV(Dimension fov) {
        return setFOV(fov.width, fov.height);
    }

    public Camera setAspectRatio(double aspectRatio) {
        return setFOV(fov.width, (int) (fov.width / aspectRatio));
    }

    public void rotate(Vector axis, double angle) {
        dir.subtract(pos).rotate(axis, angle).add(pos);
        normal.subtract(pos).rotate(axis, angle).add(pos);
        binormal.subtract(pos).rotate(axis, angle).add(pos);
    }
}
