package mesh;

import renderer.RaycastInfo;
import renderer.Renderer;
import util.Util;

public class SphereMesh extends Mesh {

    public Vector center;
    public double radius;

    public SphereMesh(Vector center, double radius) {
        this(center.x, center.y, center.z, radius);
    }

    public SphereMesh(double posX, double posY, double posZ, double radius) {
        center = new Vector(posX, posY, posZ);
        this.radius = Math.max(radius, 0);
    }

    @Override
    public RaycastInfo getClosestIntersection(Vector origin, Vector ray, RaycastInfo lastCast) {
        RaycastInfo raycastInfo = new RaycastInfo(origin, ray);
        double minDistance = (lastCast != null && lastCast.sphere == this) ? Util.getIEEE754Error(0) : 0;
        // P = origin
        // U = direction
        // C = center
        // r = radius
        // (𝐏−𝐂)⋅(𝐏−𝐂)−𝑟^2 + 2𝑡𝐔⋅(𝐏−𝐂) + 𝑡^2(𝐔⋅𝐔) = 0
        // a = (𝐔⋅𝐔)
        // b = 2𝐔⋅(𝐏−𝐂)
        // c = (𝐏−𝐂)⋅(𝐏−𝐂)−𝑟^2
        Vector rOC = Vector.subtract(origin, center);
        double a = Vector.dot(ray, ray);
        double b = Vector.dot(Vector.multiply(ray, 2), rOC);
        double c = Vector.dot(rOC, rOC) - radius * radius;
        double discriminant = b * b - 4 * a * c;
        if (discriminant >= 0) {
            double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
            double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);
            double t;
            if (t1 > minDistance && t2 > minDistance) {
                t = Math.min(t1, t2);
            } else if (t1 > minDistance || t2 > minDistance) {
                t = (t1 > minDistance) ? t1 : t2;
            } else {
                return raycastInfo;
            }

            raycastInfo.intersection = Vector.add(origin, Vector.multiply(ray, t));
            raycastInfo.normal = Vector.subtract(raycastInfo.intersection, center);
            raycastInfo.mesh = this;
            raycastInfo.material = material;
            raycastInfo.sphere = this;
            raycastInfo.distance = Vector.subtract(raycastInfo.intersection, origin).magnitude();
        }
        return raycastInfo;
    }

    @Override
    public void setCenterAt(double x, double y, double z) {
        center.set(x, y, z);
    }

    @Override
    public void scale(double scaleX, double scaleY, double scaleZ) {
        center.set(Vector.componentMultiply(center, new Vector(scaleX, scaleY, scaleZ)));
        radius *= Math.min(scaleX, Math.min(scaleY, scaleZ));
    }
}
