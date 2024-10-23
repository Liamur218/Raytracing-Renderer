package util;

import mesh.*;

import java.io.*;
import java.util.ArrayList;

public abstract class ModelLoader {
    public static PolygonMesh loadMesh(String filepath) {
        String[] filepathArray = filepath.split("\\.");
        PolygonMesh polygonMesh = new PolygonMesh();
        if (filepathArray.length == 1) {
            Logger.logMsgLn("[WARNING] Unable to read file " + filepath + " - no file extension specified");
            return polygonMesh;
        } else {
            if (filepathArray[filepathArray.length - 1].equals("obj")) {
                polygonMesh = loadObj(filepath);
            } else if (filepathArray[filepathArray.length - 1].equals("stl")) {
                polygonMesh = loadStl(filepath);
            } else {
                Logger.logMsgLn("[WARNING] Unable to load file " + filepath + " - unsupported file format");
                return polygonMesh;
            }
        }

        Vector[] centerAndSize = polygonMesh.getCenterAndSize();
        Logger.logMsgLn(filepath + " successfully loaded @ " + centerAndSize[0] +
                " w/ " + polygonMesh.getPolygonArrayList().size() + " polygons (Dim: " + centerAndSize[1] + ")");
        return polygonMesh;
    }

    public static PolygonMesh loadStl(String filepath) {
        File file = new File(filepath);
        FileInputStream input = null;
        PolygonMesh polygonMesh = new PolygonMesh();
        try {
            final int HEADER_SIZE = 80;
            final int BYTES_PER_INT = 4;
            final int BYTES_PER_FLOAT = 4;

            input = new FileInputStream(file);
            input.skip(HEADER_SIZE);  // Skip header

            byte[] array = input.readNBytes(BYTES_PER_INT);
            int numTriangles = Util.intFromByteArray(array, true);

            /*
             * Triangle  (50 bytes):
             *    (signed) float – Normal vector         (12 bytes)  <- This is actually important
             *    (signed) float – Vertex 1              (12 bytes)  |
             *    (signed) float – Vertex 2              (12 bytes)  | This is what we mainly need
             *    (signed) float – Vertex 3              (12 bytes)  |
             *    unsigned int   – Attribute byte count  (02 bytes)  <- No idea
             **/
            for (int triangleIndex = 0; triangleIndex < numTriangles; triangleIndex++) {
                // TODO: 10/8/24 Test this and switch to looping method @ some point
//                Vector[] vectorArray = new Vector[4];
//                for (int i = 0; i < vectorArray.length; i++) {
//                    vectorArray[i] = new Vector();
//                    for (int j = 0; j < Vector.LENGTH; j++) {
//                        vectorArray[i].set(j,
//                                Util.floatFromByteArray(input.readNBytes(BYTES_PER_FLOAT), true));
//                    }
//                }
                Vector normal = new Vector(
                        Util.floatFromByteArray(input.readNBytes(BYTES_PER_FLOAT), true),
                        Util.floatFromByteArray(input.readNBytes(BYTES_PER_FLOAT), true),
                        Util.floatFromByteArray(input.readNBytes(BYTES_PER_FLOAT), true));
                Vector v1 = new Vector(
                        Util.floatFromByteArray(input.readNBytes(BYTES_PER_FLOAT), true),
                        Util.floatFromByteArray(input.readNBytes(BYTES_PER_FLOAT), true),
                        Util.floatFromByteArray(input.readNBytes(BYTES_PER_FLOAT), true));
                Vector v2 = new Vector(
                        Util.floatFromByteArray(input.readNBytes(BYTES_PER_FLOAT), true),
                        Util.floatFromByteArray(input.readNBytes(BYTES_PER_FLOAT), true),
                        Util.floatFromByteArray(input.readNBytes(BYTES_PER_FLOAT), true));
                Vector v3 = new Vector(
                        Util.floatFromByteArray(input.readNBytes(BYTES_PER_FLOAT), true),
                        Util.floatFromByteArray(input.readNBytes(BYTES_PER_FLOAT), true),
                        Util.floatFromByteArray(input.readNBytes(BYTES_PER_FLOAT), true));
                polygonMesh.addPolygon(new Polygon(v1, v2, v3, normal));
                input.skip(2);  // Skip last two bytes to keep the next 50 bytes the correct 50 bytes
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException | NullPointerException ignored) {
            }
        }

        return polygonMesh;
    }

    public static PolygonMesh loadObj(String filepath) {
        BufferedReader reader = null;
        ArrayList<Vector> vertices = new ArrayList<>();
        ArrayList<int[]> indexedFaces = new ArrayList<>();
        PolygonMesh polygonMesh = new PolygonMesh();
        try {
            reader = new BufferedReader(new FileReader(filepath));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.startsWith("v ")) {
                    String[] array = line.split(" ");
                    vertices.add(new Vector(
                            Double.parseDouble(array[1]), Double.parseDouble(array[2]), Double.parseDouble(array[3])));
                } else if (line.startsWith("f ")) {
                    String[] splitLine = line.split(" ");
                    indexedFaces.add(new int[]{
                            Integer.parseInt(splitLine[1].split("/")[0]),
                            Integer.parseInt(splitLine[2].split("/")[0]),
                            Integer.parseInt(splitLine[3].split("/")[0])});
                }
            }

            for (int[] face : indexedFaces) {
                polygonMesh.addPolygon(new Polygon(
                        vertices.get(face[0] - 1),
                        vertices.get(face[1] - 1),
                        vertices.get(face[2] - 1)));
            }

            return polygonMesh;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (NullPointerException | IOException ignored) {
            }
        }
        return polygonMesh;
    }
}
