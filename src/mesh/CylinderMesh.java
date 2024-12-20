package mesh;

import renderer.RaycastInfo;
import util.MatrixUtil;

public class CylinderMesh extends Mesh {

    private Vector position, direction, antidirection, endPosition;
    private double radius, length;

    public CylinderMesh(Vector position, Vector direction, double radius, double length) {
        this.position = position;
        this.direction = direction.normalize();
        this.radius = radius;
        this.length = length;
        antidirection = Vector.multiply(direction, -1);
        endPosition = Vector.add(position, Vector.multiply(direction, length));
    }

    @Override
    public RaycastInfo getClosestIntersection(Vector origin, Vector ray, RaycastInfo lastCast) {
        RaycastInfo raycastInfo = new RaycastInfo(origin, ray);

        Vector intersection;
        double distance;

        // Check for collision on ends
        intersection = Vector.getRayPlaneIntersection(origin, ray, position, direction);
        if (intersection != null) {
            distance = Vector.distanceBetween(origin, intersection);
            if (distance < raycastInfo.distance) {
                raycastInfo.set(intersection, antidirection, this, material, distance);
            }
        }

        intersection = Vector.getRayPlaneIntersection(origin, ray, endPosition, direction);
        if (intersection != null) {
            distance = Vector.distanceBetween(origin, intersection);
            if (distance < raycastInfo.distance) {
                raycastInfo.set(intersection, direction, this, material, distance);
            }
        }

        // Check for collision on cylinder
        /*
         * Closest point on line 1: o1 + v1 * t1
         * Closest point on line 2: o2 + v2 * t2
         * Line 3 (connecting line): o1 + v1 * t1 + v3 * t3
         * Since line 3 connects to line 2: o1 + v1 * t1 + v3 * t3 = o2 + v2 * t2
         * 3 equations (b/c 3 directions), 3 unknowns (t1, t2, t3)
         *
         * o1 + v1 * t1 + v3 * t3 = o2 + v2 * t2
         * v1 * t1 + v3 * t3 - v2 * t2 = o2 - o1
         *
         * x1*t1 - x2*t2 + x3*t3 = (o2-o1)(x)
         * y1*t1 - y2*t2 + y3*t3 = (o2-o1)(y)
         * z1*t1 - z2*t2 + z3*t3 = (o2-o1)(z)
         *
         *   t1  t2  t3       ∑
         * [ x1 -x2  x3  o2(x)-o1(x) ]
         * [ y1 -y2  y3  o2(y)-o1(y) ]
         * [ z1 -z2  z3  o2(z)-o1(z) ]
         * */

        Vector src1 = new Vector(-1, 0, 0);
        Vector dir1 = new Vector(1, 0, 0);
        Vector src2 = new Vector(0, -1, 1);
        Vector dir2 = new Vector(0, 1, 0);

        Vector dir3 = Vector.cross(dir1, dir2);

        double[][] matrix = new double[][]{
                {dir1.getX(), -dir2.getX(), dir3.getX(), src2.getX() - src1.getX()},
                {dir1.getY(), -dir2.getY(), dir3.getY(), src2.getY() - src1.getY()},
                {dir1.getZ(), -dir2.getZ(), dir3.getZ(), src2.getZ() - src1.getZ()}
        };

        MatrixUtil.rref(matrix);

        System.out.println(Vector.extractFromMatrixCol(matrix, 3));

        return null;
    }

    @Override
    public void setCenterAt(double x, double y, double z) {
        /*
        * <x0, y0, z0> + <x, y, z>(L/2) = <0, 0, 0>
        * -L/2*<x, y, z> = <x0, y0, z0>
        * */
        position = Vector.multiply(direction, -0.5 * length);
    }

    @Override
    public void scale(double scaleX, double scaleY, double scaleZ) {
        position = Vector.componentMultiply(position, new Vector(scaleX, scaleY, scaleZ));
        double min = Math.min(scaleX, Math.min(scaleY, scaleZ));
        length *= min;
        radius *= min;
    }
}
