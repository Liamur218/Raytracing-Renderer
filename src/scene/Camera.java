package scene;

import mesh.Vector;
import renderer.Dimension;

public class Camera {

    public Vector pos, dir, normal, binormal;
    public float hFOV, vFOV;
    public Dimension imageSize;

    private static final Vector DEFAULT_POS = new Vector(0, 0, 0);
    private static final Vector DEFAULT_DIR = new Vector(1, 0, 0);
    private static final Vector DEFAULT_NORMAL = new Vector(0, 0, 1);
    private static final float DEFAULT_H_FOV = 90;
    private static final float DEFAULT_V_FOV = 75;
    private static final Dimension DEFAULT_IMAGE_SIZE = new Dimension(1920, 1080);

    public Camera() {
        this(DEFAULT_POS, DEFAULT_DIR, DEFAULT_NORMAL);
        setImageSize(DEFAULT_IMAGE_SIZE);
    }

    public Camera(double posX, double posY, double posZ, double dirX, double dirY, double dirZ,
                  double normalX, double normalY, double normalZ) {
        pos = new Vector(posX, posY, posZ);
        dir = new Vector(dirX, dirY, dirZ).normalize();
        normal = new Vector(normalX, normalY, normalZ).normalize();
        binormal = Vector.cross(dir, normal);
        normal = Vector.cross(binormal, dir);  // Re-set normal in case it wasn't @ 90° to dir to begin with
        setFOV(DEFAULT_H_FOV, DEFAULT_V_FOV);
        setImageSize(DEFAULT_IMAGE_SIZE);
    }

    public Camera(Vector pos, Vector dir, Vector normal) {
        this(pos.getX(), pos.getY(), pos.getZ(),
                dir.getX(), dir.getY(), dir.getZ(),
                normal.getX(), normal.getY(), normal.getZ());
        setImageSize(DEFAULT_IMAGE_SIZE);
    }

    public Camera setFOV(float hFOV) {
        return setFOV(hFOV, hFOV * 0.83333f);
    }

    public Camera setFOV(float hFOV, float vFOV) {
        this.hFOV = hFOV;
        this.vFOV = vFOV;
        return this;
    }

    public Camera setImageSize(Dimension imageSize) {
        this.imageSize = imageSize;
        return this;
    }

    public Camera setImageSize(int width, int height) {
        setImageSize(new Dimension(width, height));
        return this;
    }

    public Camera setImageSize(double width, double height) {
        return setImageSize((int) width, (int) height);
    }

    public void rotate(Vector axis, double angle) {
        dir.subtract(pos).rotate(axis, angle).add(pos);
        normal.subtract(pos).rotate(axis, angle).add(pos);
        binormal.subtract(pos).rotate(axis, angle).add(pos);
    }
}
