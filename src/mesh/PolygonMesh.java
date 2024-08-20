package mesh;

import renderer.RaycastInfo;
import util.Debug;
import util.Util;

import java.io.*;
import java.util.ArrayList;

public class PolygonMesh extends Mesh {

    private final ArrayList<Polygon> polygonArrayList;

    public Polygon[] polygons;

    private BoundingBox boundingBox;
    public boolean finalized;

    public PolygonMesh() {
        polygonArrayList = new ArrayList<>();
    }

    void addPolygon(Polygon polygon) {
        polygonArrayList.add(polygon);
    }

    public void addPolygon(Vector v1, Vector v2, Vector v3) {
        addPolygon(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z);
    }

    public void addPolygon(double x1, double y1, double z1, double x2, double y2, double z2,
                           double x3, double y3, double z3) {
        polygonArrayList.add(new Polygon(x1, y1, z1, x2, y2, z2, x3, y3, z3));
    }

    public void finalizeMesh(boolean doBBoxCollChecking, boolean genBBoxAsBVH) {
        if (material == null) {
            material = DEFAULT_MATERIAL;
        }
        polygons = polygonArrayList.toArray(new Polygon[0]);
        boundingBox = BoundingBox.newBoundingBox(this, doBBoxCollChecking, genBBoxAsBVH);
        polygonArrayList.clear();
        finalized = true;
    }

    // For scene setup
    public void move(double dx, double dy, double dz) {
        for (Polygon polygon : polygonArrayList) {
            for (Vector point : polygon) {
                point.add(dx, dy, dz);
            }
        }
    }

    public void move(Vector delta) {
        move(delta.x, delta.y, delta.z);
    }

    public void rotate(double dxDeg, double dyDeg, double dzDeg) {
        for (Polygon polygon : polygonArrayList) {
            for (Vector point : polygon) {
                point.rotate(Vector.X_AXIS, dxDeg);
                point.rotate(Vector.Y_AXIS, dyDeg);
                point.rotate(Vector.Z_AXIS, dzDeg);
            }
        }
    }

    public void scale(double x, double y, double z) {
        for (Polygon polygon : polygonArrayList) {
            for (Vector point : polygon) {
                point.x *= x;
                point.y *= y;
                point.z *= z;
            }
        }
    }

    // More complicated transformations
    public void setCenterAt(double x, double y, double z) {
        setCenterAt(new Vector(x, y, z));
    }

    public void setCenterAt(Vector newCenter) {
        Vector delta = Vector.subtract(newCenter, getCenter());
        move(delta);
    }

    // For raytracing
    @Override
    public RaycastInfo getClosestIntersection(Vector origin, Vector ray, RaycastInfo lastCast) {
        Polygon lastPolygon = (lastCast == null) ? null : lastCast.polygon;
        return boundingBox.getClosestIntersection(origin, ray, lastPolygon);
    }

    public static PolygonMesh loadMesh(String filepath) {
        String[] filepathArray = filepath.split("\\.");
        PolygonMesh polygonMesh = new PolygonMesh();
        if (filepathArray.length == 1) {
            Debug.logMsgLn("[WARNING] Unable to read file " + filepath + " - no file extension specified");
            return polygonMesh;
        } else {
            if (filepathArray[filepathArray.length - 1].equals("obj")) {
                polygonMesh = loadObj(filepath);
            } else if (filepathArray[filepathArray.length - 1].equals("stl")) {
                polygonMesh = loadStl(filepath);
            } else {
                Debug.logMsgLn("[WARNING] Unable to load file " + filepath + " - unsupported file format");
                return polygonMesh;
            }
        }

        Vector[] centerAndSize = polygonMesh.getCenterAndSize();
        Debug.logMsgLn(filepath + " successfully loaded @ " + centerAndSize[0] +
                " w/ " + polygonMesh.polygonArrayList.size() + " polygons (Dim: " + centerAndSize[1] + ")");
        return polygonMesh;
    }

    private static PolygonMesh loadStl(String filepath) {
        File file = new File(filepath);
        FileInputStream input = null;
        BufferedReader in = null;
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
             *    (signed) float – Normal vector         (12 bytes)  <- Unnecessary
             *    (signed) float – Vertex 1              (12 bytes)  |
             *    (signed) float – Vertex 2              (12 bytes)  | This is what we need
             *    (signed) float – Vertex 3              (12 bytes)  |
             *    unsigned int   – Attribute byte count  (02 bytes)  <- No idea
             **/
            for (int triangleIndex = 0; triangleIndex < numTriangles; triangleIndex++) {
                input.skip(12);  // Skip normal vector (we'll calculate this manually later)
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
                polygonMesh.addPolygon(v1, v2, v3);
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

    private static PolygonMesh loadObj(String filepath) {
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

    public Vector[] getMinMax() {
        Vector min = new Vector(Double.MAX_VALUE);
        Vector max = new Vector(-Double.MAX_VALUE);
        for (Polygon polygon : polygonArrayList) {
            for (Vector vector : polygon) {
                min.x = Math.min(vector.x, min.x);
                min.y = Math.min(vector.y, min.y);
                min.z = Math.min(vector.z, min.z);
                max.x = Math.max(vector.x, max.x);
                max.y = Math.max(vector.y, max.y);
                max.z = Math.max(vector.z, max.z);
            }
        }
        return new Vector[]{min, max};
    }

    public Vector[] getCenterAndSize() {
        Vector[] minMax = getMinMax();
        Vector min = minMax[0];
        Vector max = minMax[1];
        return new Vector[]{Vector.divide(Vector.add(min, max), 2), Vector.subtract(max, min)};
    }

    public Vector getSize() {
        return getCenterAndSize()[1];
    }

    public Vector getCenter() {
        return getCenterAndSize()[0];
    }

    public ArrayList<Polygon> getPolygonArrayList() {
        return polygonArrayList;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}
