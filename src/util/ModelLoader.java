package util;

import mesh.*;
import mesh.Vector;

import java.io.*;
import java.util.*;

public abstract class ModelLoader {

    public static PolygonMesh loadModel(String fileLocation, String filename, ModelFileType modelType) {
        String modelName = filename.replace("_", " ");
        Logger.logMsgLn("Loading model " + modelName + " from " + fileLocation + " (type: " + modelType + ")");
        long start = System.nanoTime();

        String filepath = fileLocation + "/" + filename + "." + modelType.getExtension();
        PolygonMesh mesh;
        switch (modelType) {
            case STL_BIN -> mesh = loadStl(filepath);
            case STL_ASCII -> mesh = loadAsciiStl(filepath);
            case WAVEFRONT_OBJ -> mesh = loadObj(filepath);
            default -> mesh = new PolygonMesh();
        }

        long end = System.nanoTime();
        Logger.logMsgLn("Model " + modelName + " loaded in " + TimeFormatter.timeToString(end - start));
        mesh.setName(modelName);
        return mesh;
    }

    private static PolygonMesh loadStl(String filepath) {
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
             *    (signed) float – Normal vector         (12 bytes)  <- This is important
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

    private static PolygonMesh loadAsciiStl(String filename) {
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

    private static PolygonMesh loadObj(String filename) {
        PolygonMesh polygonMesh = new PolygonMesh();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Extract relevant data
            ArrayList<String> vertexStrings = new ArrayList<>();
            ArrayList<String> polygonStrings = new ArrayList<>();
            ArrayList<String> lineStrings = new ArrayList<>();
            ArrayList<String> textureStrings = new ArrayList<>();
            String matLibFilename = null;
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.trim();

                String[] lineAsArray = line.split(" ");
                if (lineAsArray[0].equals("mtllib")) {
                    matLibFilename = lineAsArray[1];
                } else if (lineAsArray[0].equals("v")) {
                    vertexStrings.add(line.substring(lineAsArray[0].length() + 1));
                } else if (lineAsArray[0].equals("f")/* || lineAsArray[0].equals("usemtl")*/) {
                    polygonStrings.add(line.substring(lineAsArray[0].length() + 1));
                } else if (lineAsArray[0].equals("l")) {
                    lineStrings.add(line.substring(lineAsArray[0].length() + 1));
                } else if (lineAsArray[0].equals("vt")) {
                    textureStrings.add(line.substring(lineAsArray[0].length() + 1));
                }
            }

            Vector[] vertices = new Vector[vertexStrings.size()];

            // Define vertices
            // 1. Process vertex strings
            for (int i = 0; i < vertices.length; i++) {
                String[] vertexAsStringArray = vertexStrings.remove(0).split(" ");
                vertices[i] = new Vector(
                        Double.parseDouble(vertexAsStringArray[0]),
                        Double.parseDouble(vertexAsStringArray[1]),
                        Double.parseDouble(vertexAsStringArray[2]));
            }

            // Start building model
            // 1. Process polygon strings into individual polygons with vertex/texture/normal indices
            int[][] polygonIndices = new int[polygonStrings.size()][Polygon.VERTEX_COUNT * 2];
            for (int i = 0; i < polygonStrings.size(); i++) {
                String[] faceAsStringArray = polygonStrings.get(i).split(" ");
                for (int j = 0; j < Polygon.VERTEX_COUNT; j++) {
                    String[] faceVertexAsStringArray = faceAsStringArray[j].split("/");
                    polygonIndices[i][j * 2] = Integer.parseInt(faceVertexAsStringArray[0]);
                    if (faceVertexAsStringArray.length > 1 && !faceVertexAsStringArray[1].isEmpty()) {
                        polygonIndices[i][j * 2 + 1] = Integer.parseInt(faceVertexAsStringArray[1]);
                    }
                }
            }

            // 2. Build polygons
            for (int[] polygonIndexSet : polygonIndices) {
                Polygon polygon = new Polygon(
                        vertices[polygonIndexSet[0] - 1],
                        vertices[polygonIndexSet[2] - 1],
                        vertices[polygonIndexSet[4] - 1]
                );
                // TODO: 2/12/25 Add texture support
                polygonMesh.addPolygon(polygon);
            }

            // 2. Process line element strings

            // 3. Process texture strings

        } catch (IOException e) {
            e.printStackTrace();
        }
        return polygonMesh;
    }
}
