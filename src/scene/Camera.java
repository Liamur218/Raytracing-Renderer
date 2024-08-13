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
        this(posX, posY, posZ, dirX, dirY, dirZ, normalX, normalY, normalZ, DEFAULT_FOV.width, DEFAULT_FOV.height);
    }

    public Camera(Vector pos, Vector dir, Vector normal) {
        this(pos, dir, normal, DEFAULT_FOV);
    }

    public Camera(Vector pos, Vector dir, Vector normal, Dimension fov) {
        this(pos, dir, normal, fov.width, fov.height);
    }

    public Camera(double posX, double posY, double posZ, double dirX, double dirY, double dirZ,
           double normalX, double normalY, double normalZ, int hFOV, int vFOV) {
        this(new Vector(posX, posY, posZ), new Vector(dirX, dirY, dirZ), new Vector(normalX, normalY, normalZ),
                hFOV, vFOV);
    }

    public Camera(Vector pos, Vector dir, Vector normal, int hFOV, int vFOV) {
        this.pos = new Vector(pos);
        this.dir = new Vector(dir).normalize();
        this.normal = new Vector(normal).normalize();
        if (Vector.angleBetween(normal, dir) != 90) {
            Vector hold = Vector.cross(dir, normal).normalize();
            this.normal = Vector.cross(hold, dir);
        }
        binormal = Vector.cross(dir, normal);
        this.fov = new Dimension(hFOV, vFOV);
    }

    public Camera setFOV(int hFOV, int vFOV) {
        fov.width = hFOV;
        fov.height = vFOV;
        return this;
    }

    public Camera setFOV(int fov) {
        return setFOV(fov, fov);
    }

    public Camera setAspectRatio(int width, int height, boolean RefHFOV) {
        double whRatio = (double) width / height;
        if (RefHFOV) {
            fov.height = (int) (fov.width / whRatio);
        } else {
            fov.width = (int) (fov.height * whRatio);
        }
        System.out.println(fov);
        return this;
    }

    public Camera setAspectRatio(int width, int height) {
        return setAspectRatio(width, height, true);
    }

    public Camera setAspectRatio(int width, int height, int fov, boolean useHFOV) {
        setFOV(fov);
        return setAspectRatio(width, height, useHFOV);
    }

    public Camera setAspectRatio(int width, int height, int fov) {
        return setAspectRatio(width, height, fov, true);
    }

    public void rotate(Vector axis, double angle) {
        dir.subtract(pos).rotate(axis, angle).add(pos);
        normal.subtract(pos).rotate(axis, angle).add(pos);
        binormal.subtract(pos).rotate(axis, angle).add(pos);
    }
}
