package util;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.*;
import java.util.Date;

public abstract class Util {

    private static final DecimalFormat DF = new DecimalFormat("##");

    // Math
    public static float clamp(float a, float low, float high) {
        return Math.max(Math.min(a, high), low);
    }

    public static double clamp(double a, double low, double high) {
        return Math.max(Math.min(a, high), low);
    }

    public static float linearInterpolation(float dMin, float dMax, float dMid, float rMin, float rMax) {
        return (dMid - dMin) * (rMax - rMin) / (dMax - dMin) + rMin;
    }

    public static double linearInterpolation(double dMin, double dMax, double dMid, double rMin, double rMax) {
        return (dMid - dMin) * (rMax - rMin) / (dMax - dMin) + rMin;
    }

    public static double sind(double x) {
        return Math.sin(Math.toRadians(x));
    }

    public static float sind(float x) {
        return (float) Math.sin(Math.toRadians(x));
    }

    public static double cosd(double x) {
        return Math.cos(Math.toRadians(x));
    }

    public static double asind(double x) {
        return Math.toDegrees(Math.asin(x));
    }

    public static float asind(float x) {
        return (float) Math.toDegrees(Math.asin(x));
    }

    // Text formatting stuff
    public static String bracket(String string) {
        return "[" + string + "]";
    }

    public static String getTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public static String getTimeWrapped() {
        return bracket(getTime());
    }

    public static String getDate() {
        return new SimpleDateFormat("MM/dd/yyyy").format(new Date());
    }

    public static String getDateWrapped() {
        return bracket(getDate());
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
        return ByteBuffer.wrap((littleEndian) ? flip(array) : array).getInt();
    }

    public static float floatFromByteArray(byte[] array, boolean littleEndian) {
        return ByteBuffer.wrap((littleEndian) ? flip(array) : array).getFloat();
    }

    public static double getIEEE754Error(double d) {
        return 0b0_01111111111_0000000000000000000000000000000000000000000000000001L;
    }

    public static void printBits(int x) {
        int filter = 0b10000000_00000000_00000000_00000000;
        for (int i = 0; i < 32; i++) {
            System.out.print(((x & filter) > 0) ? "1" : "0");
            filter >>>= 1;
        }
        System.out.println();
    }

    public static void printBits(long x) {
        long filter = 0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        for (int i = 0; i < 64; i++) {
            System.out.print(((x & filter) > 0) ? "1" : "0");
            filter >>>= 1;
        }
        System.out.println();
    }

    // Serialization
    // Stolen from https://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array
    public static byte[] serialize(Object obj) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(byteStream)) {
            output.writeObject(obj);
            output.flush();
            return byteStream.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Object deserialize(byte[] bytes) {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
        try (ObjectInput input = new ObjectInputStream(byteStream)) {
            return input.readObject();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
