package util;

import mesh.Vector;
import renderer.Renderer;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

public abstract class Util {

    private static final DecimalFormat DF = new DecimalFormat("##");

    public static double clamp(double x, double low, double high) {
        return Math.max(Math.min(x, high), low);
    }

    public static double linearInterpolation(double dMin, double dMax, double dMid, double rMin, double rMax) {
        return (dMid - dMin) * (rMax - rMin) / (dMax - dMin) + rMin;
    }

    public static double sind(double x) {
        return Math.sin(Math.toRadians(x));
    }

    public static double cosd(double x) {
        return Math.cos(Math.toRadians(x));
    }

    public static double asind(double x) {
        return Math.toDegrees(Math.asin(x));
    }

    public static String getCurrentTime() {
        LocalDateTime ldt = LocalDateTime.now();
        return "[" + DF.format(ldt.getHour()) + ":" +
                DF.format(ldt.getMinute()) + ":" +
                DF.format(ldt.getSecond()) + "]";
    }

    // Collision logic
    /*
    * Using c1 and c2 instead of x and y here because it makes IntelliJ happy
    * (It doesn't like me passing in y as x in certain scenarios)
    * */
    public static boolean isPointInside2DBox(double pointC1, double pointC2,
                                             double boxMinC1, double boxMinC2, double boxMaxC1, double boxMaxC2) {
        return boxMinC1 <= pointC1 && pointC1 <= boxMaxC1 && boxMinC2 <= pointC2 && pointC2 <= boxMaxC2;
    }

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

    // Byte shaboingery
    public static byte[] flip(byte[] array) {
        byte[] bytes = new byte[array.length];
        System.arraycopy(array, 0, bytes, 0, array.length);
        return flipInPlace(bytes);
    }

    public static byte[] flipInPlace(byte[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            byte hold = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = hold;
        }
        return array;
    }

    public static int intFromByteArray(byte[] array, boolean littleEndian) {
        return ByteBuffer.wrap((littleEndian) ? Util.flip(array) : array).getInt();
//        int out = 0;
//        if (littleEndian) { array = flip(array); }
//        for (int i = 0; i < 4; i++) {
//            byte b = array[i];
//            out <<= 8;
//            if (b < 0) {
//                b &= 0b01111111;
//                out |= b;
//                out += 0b10000000;
//            } else {
//                out |= b;
//            }
//        }
//        return out;
    }

    public static float floatFromByteArray(byte[] array, boolean littleEndian) {
        return ByteBuffer.wrap((littleEndian) ? Util.flip(array) : array).getFloat();
    }
}
