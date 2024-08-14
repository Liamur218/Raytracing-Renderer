package util;

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

    /*
    * Using a and b instead of x and y here because it makes IntelliJ happy
    * */
    public static boolean isPointInside2DBox(double pointX1, double pointX2,
                                             double boxMinX1, double boxMaxX1, double boxMinX2, double boxMaxX2) {
        return boxMinX1 < pointX1 && pointX1 < boxMaxX1 && boxMinX2 < pointX2 && pointX2 < boxMaxX2;
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
