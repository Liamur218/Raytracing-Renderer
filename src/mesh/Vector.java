package mesh;

import util.Util;

public class Vector {

    double x, y, z;

    public static final Vector ZERO_VECTOR = new Vector(0);
    public static final Vector X_AXIS = new Vector(1, 0, 0);
    public static final Vector Y_AXIS = new Vector(0, 1, 0);
    public static final Vector Z_AXIS = new Vector(0, 0, 1);
    public static final Vector[] AXES = new Vector[]{ X_AXIS, Y_AXIS, Z_AXIS };
    public static final Vector MAX_VECTOR = new Vector(Double.MAX_VALUE);
    public static final Vector MIN_VECTOR = new Vector(Double.MIN_VALUE);

    public static final int LENGTH = 3;

    public Vector(double x, double y, double z) {
        set(x, y, z);
    }

    public Vector() {
        this(0, 0, 0);
    }

    public Vector(Vector vector) {
        set(vector);
    }

    public Vector(double a) {
        this(a, a, a);
    }

    public Vector set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector set(Vector vector) {
        x = vector.x;
        y = vector.y;
        z = vector.z;
        return this;
    }

    public Vector set(int varIndex, double value) {
        if (varIndex == 0) {
            x = value;
        } else if (varIndex == 1) {
            y = value;
        } else if (varIndex == 3) {
            z = value;
        }
        return this;
    }

    public Vector copy() {
        return new Vector(this);
    }

    // Addition & Subtraction
    public static Vector add(Vector v1, Vector v2) {
        return new Vector(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }

    public Vector add(Vector vector) {
        this.x += vector.x;
        this.y += vector.y;
        this.z += vector.z;
        return this;
    }

    public Vector add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public static Vector subtract(Vector v1, Vector v2) {
        return new Vector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    public Vector subtract(Vector vector) {
        x -= vector.x;
        y -= vector.y;
        z -= vector.z;
        return this;
    }

    // Scalar Multiplication & Division
    public Vector multiply(double a) {
        x *= a;
        y *= a;
        z *= a;
        return this;
    }

    public static Vector multiply(Vector vector, double a) {
        return new Vector(vector).multiply(a);
    }

    public Vector divide(double a) {
        this.x /= a;
        this.y /= a;
        this.z /= a;
        return this;
    }

    public static Vector divide(Vector vector, double a) {
        return new Vector(vector.x / a, vector.y / a, vector.z / a);
    }

    // Dot & Cross Products
    static double dot(Vector v1, Vector v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public static Vector cross(Vector vector1, Vector vector2) {
        return new Vector(
                vector1.y * vector2.z - vector2.y * vector1.z,
                vector2.x * vector1.z - vector1.x * vector2.z,
                vector1.x * vector2.y - vector2.x * vector1.y
        );
    }

    // Other Operations
    public double magnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public static Vector unit(Vector vector) {
        Vector out = new Vector(vector);
        out.normalize();
        return out;
    }

    public Vector normalize() {
        divide(magnitude());
        return this;
    }

    public static Vector componentMultiply(Vector v1, Vector v2) {
        return new Vector(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
    }

    public double sum() {
        return x + y + z;
    }

    public static double angleBetween(Vector vector1, Vector vector2) {
        double degRads = Math.acos(Vector.dot(vector1, vector2) / (vector1.magnitude() * vector2.magnitude()));
        return Math.toDegrees(degRads);
    }

    public static double distanceBetween(Vector vector1, Vector vector2) {
        return Math.sqrt(Math.pow(vector1.x - vector2.x, 2) +
                Math.pow(vector1.y - vector2.y, 2) + Math.pow(vector1.z - vector2.z, 2));
    }

    public static Vector interpolate(Vector low, Vector high, double index) {
        return new Vector(
                Util.linearInterpolation(0, 1, index, low.x, high.x),
                Util.linearInterpolation(0, 1, index, low.y, high.y),
                Util.linearInterpolation(0, 1, index, low.z, high.z));
    }

    // Rotation
    public static Vector rotate(Vector vector, Vector axis, double angle) {
        return new Vector(vector).rotate(axis, angle);
    }

    public Vector rotate(double axisX, double axisY, double axisZ, double angleDeg) {
        return rotate(new Vector(axisX, axisY, axisZ), angleDeg);
    }

    public Vector rotate(Vector axis, double angle) {
        double rad = angle * Math.PI / 180;
        Vector k = Vector.unit(axis);
        Vector rotatedVect = new Vector(multiply(this, Math.cos(rad)));
        rotatedVect.add(multiply(cross(k, this), Math.sin(rad)));
        rotatedVect.add(multiply(k, dot(k, this) * (1 - Math.cos(rad))));
        set(rotatedVect);
        return this;
    }

    // Misc stuff
    public Vector getNormalTo() {
        return cross(new Vector(this).rotate(X_AXIS, 90).rotate(Y_AXIS, 90), this).normalize();
    }

    public Vector getLongestDimension() {
        return (x > y && x > z) ? Vector.X_AXIS : ((y > x && y > z) ? Vector.Y_AXIS : Vector.Z_AXIS);
    }

    public static Vector getPlaneIntersection(Vector origin, Vector vector, Vector planeRefPoint, Vector planeNormal) {
        // Equation of a plane -- a1(x - x0) + b1(y - y0) + c1(z - z0) = 0
        // Equation of a ray ---- <x, y, z> = <a2, b2, c2> * t + <x1, y1, z1>
        // Intersection:  a1*((a2 * t + x1) - x0) + b1*((b2 * t + y1) - y0) + c1*((c2 * t + z1) - z0) = 0
        //                a1*a2*t + a1*x1 - a1*x0 + b1*b2*t - b1*y1 + b1*y0 + c1*c2*t + c1*z1 - c1*z0 = 0
        //                a1*a2*t + b1*b2*t + c1*c2*t + a1*x1 - a1*x0 - b1*y1 + b1*y0 + c1*z1 - c1*z0 = 0
        //                t(a1*a2 + b1*b2 + c1*c2) + a1*x1 - a1*x0 - b1*y1 + b1*y0 + c1*z1 - c1*z0 = 0
        //                t(a1*a2 + b1*b2 + c1*c2) = -a1*x1 + a1*x0 + b1*y1 - b1*y0 - c1*z1 + c1*z0
        //                t(a1*a2 + b1*b2 + c1*c2) = -a1*(x1 + x0) + b1*(y1 - y0) - c1*(z1 + z0)
        //                t = (a1*(x0 - x1) + b1*(y0 - y1) + c1*(z0 - z1)) / (a1*a2 + b1*b2 + c1*c2)

        double denominator = planeNormal.x * vector.x + planeNormal.y * vector.y + planeNormal.z * vector.z;
        if (denominator > 0) {
            double t = (
                    planeNormal.x * (planeRefPoint.x - origin.x) +
                            planeNormal.y * (planeRefPoint.y - origin.y) +
                            planeNormal.z * (planeRefPoint.z - origin.z)) /
                    denominator;

            return Vector.add(origin, Vector.multiply(vector, t));
        } else {
            return null;
        }
    }

    // Other Methods
    @Override
    public String toString() {
        return "<" + x + ", " + y + ", " + z + ">";
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Vector vector) {
            return vector.x == x && vector.y == y && vector.z == z;
        } return false;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
}
