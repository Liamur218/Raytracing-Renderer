package util;

import mesh.*;
import mesh.Vector;

import java.io.*;
import java.util.*;

public abstract class ModelLoader {
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

    public static PolygonMesh loadAsciiStl(String filename) {
        PolygonMesh polygonMesh = new PolygonMesh();
        try (BufferedReader inputStream = new BufferedReader(new FileReader(filename))) {
            Vector normal = null;
            Polygon polygon;
            for (String line = inputStream.readLine(); line != null; line = inputStream.readLine()) {
                if (line.contains("facet")) {
                    String[] array = line.trim().split(" ");
                    double x = Double.parseDouble(array[2]);
                    double y = Double.parseDouble(array[3]);
                    double z = Double.parseDouble(array[4]);
                    normal = new Vector(x, y, z);
                } else if (line.contains("loop")) {
                    String point1 = inputStream.readLine().replace("vertex", "").trim();
                    String point2 = inputStream.readLine().replace("vertex", "").trim();
                    String point3 = inputStream.readLine().replace("vertex", "").trim();
                    polygon = new Polygon(new Vector(point1), new Vector(point2), new Vector(point3));
                    polygon.setNormal(normal);
                    polygonMesh.addPolygon(polygon);
                    inputStream.readLine();
                    inputStream.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return polygonMesh;
    }

    public static PolygonMesh loadObj(String filename) {
        PolygonMesh polygonMesh = new PolygonMesh();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Extract relevant data
            ArrayList<String> vertexLines = new ArrayList<>();
            ArrayList<String> polygonLines = new ArrayList<>();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String string = line.trim().split(" ")[0];
                if (string.equals("v")) {
                    vertexLines.add(line);
                } else if (string.equals("vt")) {
                    polygonLines.add(line);
                } else if (string.equals("f")) {

                }
            }

            // Process lines
            Vector[] vertices = new Vector[vertexLines.size()];
            for (int i = 0; i < vertices.length; i++) {
                String[] lineArray = vertexLines.get(i).split(" ");
                vertices[i] = new Vector(lineArray[1], lineArray[2], lineArray[3]);
            }

            // Process polygons
            for (String line : polygonLines) {
                String[] lineArray = line.replace('f', ' ').trim().split(" ");
                Vector[] vectors = new Vector[Polygon.VERTEX_COUNT];
                for (int i = 0; i < vectors.length; i++) {
                    String vertexIdString = lineArray[i].split("/")[0];
                    try {
                        int vertexId = Integer.parseInt(vertexIdString);
                        vectors[i] = vertices[vertexId];
                    } catch (NumberFormatException ignored) {
                        Logger.logWarningMsg("Encountered bad index when parsing model data for model " + filename +
                                " at index " + vertexIdString + ". Model building stopped.");
                        return polygonMesh;
                    }
                    polygonMesh.addPolygon(new Polygon(vectors));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return polygonMesh;
    }
}
