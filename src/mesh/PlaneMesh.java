package mesh;

import renderer.RaycastInfo;

import java.util.Objects;

public class PlaneMesh extends Mesh {

    public Vector refPoint, normal;

    public boolean doRearVisibility;

    public PlaneMesh(Vector refPoint, Vector normal) {
        this(refPoint.x, refPoint.y, refPoint.z, normal.x, normal.y, normal.z);
    }

    public PlaneMesh(double refX, double refY, double refZ, double normalX, double normalY, double normalZ) {
        refPoint = new Vector(refX, refY, refZ);
        normal = new Vector(normalX, normalY, normalZ).normalize();
        doRearVisibility = true;
    }

    public PlaneMesh disableRearVisibility() {
        doRearVisibility = false;
        return this;
    }

    public PlaneMesh setDoRearVisibility(boolean doRearVisibility) {
        this.doRearVisibility = doRearVisibility;
        return this;
    }

    @Override
    public RaycastInfo getClosestIntersection(Vector origin, Vector ray, RaycastInfo lastCast) {
        RaycastInfo raycastInfo = new RaycastInfo(origin, ray);

        Vector intersectionPoint = Vector.getRayPlaneIntersection(origin, ray, refPoint, normal);
        if (intersectionPoint != null && (doRearVisibility || Vector.angleBetween(ray, normal) >= 90)) {
            raycastInfo.set(intersectionPoint, normal,
                    this, material, Vector.distanceBetween(origin, intersectionPoint));
        }

        return raycastInfo;
    }

    @Override
    public PlaneMesh setCenterAt(double x, double y, double z) {
        refPoint.set(x, y, z);
        return this;
    }

    @Override
    public PlaneMesh scale(double scaleX, double scaleY, double scaleZ) {
        refPoint.set(Vector.componentMultiply(refPoint, new Vector(scaleX, scaleY, scaleZ)));
        return this;
    }

    @Override
    public PlaneMesh duplicate() {
        return new PlaneMesh(new Vector(refPoint), new Vector(normal));
    }

    @Override
    public int hashCode() {
        return Objects.hash(refPoint, normal, doRearVisibility);
    }
}
