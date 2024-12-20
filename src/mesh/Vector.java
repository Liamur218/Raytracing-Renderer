package mesh;

import util.Util;

public class Vector {

    double x, y, z;

    public static final Vector ZERO_VECTOR = new Vector(0);
    public static final Vector X_AXIS = new Vector(1, 0, 0);
    public static final Vector Y_AXIS = new Vector(0, 1, 0);
    public static final Vector Z_AXIS = new Vector(0, 0, 1);
    public static final Vector[] AXES = new Vector[]{ X_AXIS, Y_AXIS, Z_AXIS };

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

    public Vector(String[] points) {
        this(Double.parseDouble(points[0]), Double.parseDouble(points[1]), Double.parseDouble(points[2]));
    }

    public Vector(String listedPoints) {
        this(listedPoints.split(" "));
    }

    public static Vector extractFromMatrixCol(double[][] matrix, int colNumber) {
        return new Vector(matrix[0][colNumber], matrix[1][colNumber], matrix[2][colNumber]);
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
    public static double dot(Vector v1, Vector v2) {
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

    public double getLargestComponent() {
        return (x > y && x > z) ? x : ((y > x && y > z) ? y : z);
    }

    public Vector getLongestAxis() {
        return (x > y && x > z) ? Vector.X_AXIS : ((y > x && y > z) ? Vector.Y_AXIS : Vector.Z_AXIS);
    }

    // Collision Logic
    public static Vector getRayPlaneIntersection(Vector origin, Vector dir, Vector planePos, Vector planeNormal) {
        /*
         * Equation for a plane in 3-space:
         *     a(x - x0) + b(y - y0) + c(z - z0) = 0
         *         n = <a, b, c>  (normal vector)
         *     ax + by + cz = ax0 + by0 + cz0
         * Equation for a line in 3-space:
         *     <X, Y, Z> = mt + <X0, Y0, Z0>
         *     X = (m_X)t + X0
         *     Y = (m_Y)t + Y0
         *     Z = (m_Z)t + Z0
         *         m = slope (i.e. direction)
         * Combined:
         *     a((m_X)t + X0) + b((m_Y)t + Y0) + c((m_Z)t + Z0) = ax0 + by0 + cz0
         *     a(m_X)t + aX0 + b(m_Y)t + bY0 + c(m_Z)t + cZ0 = ax0 + by0 + cz0
         *     a(m_X)t + b(m_Y)t + c(m_Z)t = a(x0 - X0) + b(y0 - Y0) + c(z0 - Z0)
         *     t(a(m_X) + b(m_Y) + c(m_Z)) = a(x0 - X0) + b(y0 - Y0) + c(z0 - Z0)
         *     t = (a(x0 - X0) + b(y0 - Y0) + c(z0 - Z0)) / (a(m_X) + b(m_Y) + c(m_Z))
         * */

        double t = Vector.dot(planeNormal, Vector.subtract(planePos, origin)) / Vector.dot(planeNormal, dir);
        return (t > 0) ? Vector.add(Vector.multiply(dir, t), origin) : null;
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
