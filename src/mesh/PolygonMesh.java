package mesh;

import renderer.RaycastInfo;
import renderer.Renderer;
import util.Debug;
import util.Util;

import java.io.*;
import java.util.ArrayList;

public class PolygonMesh extends Mesh {

    private final ArrayList<Polygon> polygonArrayList;
    public Polygon[] polygons;
    public int polygonCount;

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

    public void finalizeMesh() {
        if (material == null) {
            material = DEFAULT_MATERIAL;
        }
        polygons = polygonArrayList.toArray(new Polygon[0]);
        polygonCount = polygons.length;
        polygonArrayList.clear();
    }

    // For scene setup
    public PolygonMesh move(double dx, double dy, double dz) {
        for (Polygon polygon : polygonArrayList) {
            for (Vector point : polygon.points) {
                point.add(dx, dy, dz);
            }
        }
        return this;
    }

    public PolygonMesh move(Vector delta) {
        return move(delta.x, delta.y, delta.z);
    }

    public PolygonMesh rotate(double dxDeg, double dyDeg, double dzDeg) {
        for (Polygon polygon : polygonArrayList) {
            for (Vector point : polygon) {
                point.rotate(Vector.X_AXIS, dxDeg);
                point.rotate(Vector.Y_AXIS, dyDeg);
                point.rotate(Vector.Z_AXIS, dzDeg);
            }
        }
        return this;
    }

    public PolygonMesh scale(double a) {
        return scale(a, a, a);
    }

    public PolygonMesh scale(double x, double y, double z) {
        for (Polygon polygon : polygonArrayList) {
            for (Vector point : polygon) {
                point.x *= x;
                point.y *= y;
                point.z *= z;
            }
        }
        return this;
    }

    // More complicated transformations
    public void setCenterAt(double x, double y, double z) {
        Vector delta = Vector.subtract(new Vector(x, y, z), getCenter());
        for (Polygon polygon : polygonArrayList) {
            for (Vector vector : polygon) {
                vector.add(delta);
            }
        }
    }

    // For raytracing
    @Override
    public RaycastInfo getClosestIntersection(Vector origin, Vector ray, RaycastInfo lastCast) {
        RaycastInfo raycastInfo = new RaycastInfo(origin, ray);
        Polygon lastPolygon = (lastCast == null) ? null : lastCast.polygon;

        for (Polygon polygon : polygons) {
            // Make sure a polygon is not collided with twice
            if (polygon != lastPolygon) {

                // Determine intersection point
                double t = -Vector.componentMultiply(
                        polygon.normal, Vector.subtract(origin, polygon.points[0])).sum() /
                        Vector.componentMultiply(polygon.normal, ray).sum();
                Vector intersection = origin.copy().add(Vector.multiply(ray, t));

                // Check if intersection is in front of origin point
                if (t > 0) {
                    // Check if intersection in inside polygon
                    Vector PA = Vector.subtract(polygon.points[0], intersection);
                    Vector PB = Vector.subtract(polygon.points[1], intersection);
                    Vector PC = Vector.subtract(polygon.points[2], intersection);
                    double totalAngle = Vector.angleBetween(PA, PB) +
                            Vector.angleBetween(PB, PC) + Vector.angleBetween(PC, PA);

                    if (360 - Renderer.POLYGON_ERROR < totalAngle && totalAngle < 360 + Renderer.POLYGON_ERROR) {
                        double distance = Vector.distanceBetween(origin, intersection);
                        // Replace existing raycast info if this collision point is the closest intersection point
                        if (raycastInfo.intersection == null || distance < raycastInfo.distance) {
                            raycastInfo.intersection = intersection;
                            raycastInfo.normal = (Vector.angleBetween(polygon.normal, ray) > 90) ?
                                    polygon.normal : Vector.multiply(polygon.normal, -1);
                            raycastInfo.mesh = this;
                            raycastInfo.material = material;
                            raycastInfo.polygon = polygon;
                            raycastInfo.distance = distance;
                        }
                    }
                }
            }
        }
        return raycastInfo;
    }

    public static PolygonMesh loadMesh(String filepath) {
        String[] array = filepath.split("\\.");
        if (array.length == 1) {
            Debug.logMsgLn("[ERROR] Unable to read file " + filepath + " - no file extension specified");
        } else {
            if (array[array.length - 1].equals("obj")) {
                return loadObj(filepath);
            } else if (array[array.length - 1].equals("stl")) {
                return loadStl(filepath);
            } else {
                Debug.logMsgLn("[ERROR] Unable to load file " + filepath + " - unsupported file format");
            }
        }

        return new PolygonMesh();
    }

    private static PolygonMesh loadStl(String filepath) {
        File file = new File("assets/KNIGHT.stl");
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

    // This is probably going to get removed later
    Vector getCenter() {
        Vector min = Vector.MIN_VECTOR;
        Vector max = Vector.MAX_VECTOR;
        for (Polygon polygon : polygonArrayList) {
            for (Vector vector : polygon) {
                vector.x = Math.max(vector.x, max.x);
                vector.y = Math.max(vector.y, max.y);
                vector.z = Math.max(vector.z, max.z);
                vector.x = Math.min(vector.x, min.x);
                vector.y = Math.min(vector.y, min.y);
                vector.z = Math.min(vector.z, min.z);
            }
        }
        return Vector.divide(Vector.add(max, min), 2);
    }
}
