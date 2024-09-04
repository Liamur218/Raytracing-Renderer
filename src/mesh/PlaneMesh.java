package mesh;

import renderer.RaycastInfo;

public class PlaneMesh extends Mesh {

    Vector refPoint, normal;

    public PlaneMesh(Vector refPoint, Vector normal) {
        this(refPoint.x, refPoint.y, refPoint.z, normal.x, normal.y, normal.z);
    }

    public PlaneMesh(double refX, double refY, double refZ, double normalX, double normalY, double normalZ) {
        refPoint = new Vector(refX, refY, refZ);
        normal = new Vector(normalX, normalY, normalZ).normalize();
    }

    @Override
    public RaycastInfo getClosestIntersection(Vector origin, Vector ray, RaycastInfo lastCast) {
        RaycastInfo raycastInfo = new RaycastInfo(origin, ray);

        Vector intersectionPoint = Vector.getRayPlaneIntersection(origin, ray, refPoint, normal);
        if (intersectionPoint != null) {
            raycastInfo.set(intersectionPoint, normal,
                    this, material, Vector.distanceBetween(origin, intersectionPoint));
        }

        return raycastInfo;
    }

    @Override
    public void setCenterAt(double x, double y, double z) {
        refPoint.set(x, y, z);
    }

    @Override
    public void scale(double scaleX, double scaleY, double scaleZ) {
        refPoint.set(Vector.componentMultiply(refPoint, new Vector(scaleX, scaleY, scaleZ)));
    }
}
