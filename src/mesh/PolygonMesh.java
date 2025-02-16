package mesh;

import renderer.RaycastInfo;
import util.*;

import java.util.*;

public class PolygonMesh extends Mesh {

    private ArrayList<Polygon> polygonArrayList;
    private ArrayList<Vector> vertexArrayList;

    public Polygon[] polygons;

    private BoundingBox boundingBox;
    public boolean finalized;

    public PolygonMesh() {
        polygonArrayList = new ArrayList<>();
        vertexArrayList = new ArrayList<>();
    }

    public void addPolygon(Polygon polygon) {
        polygonArrayList.add(polygon);
        for (Vector polygonVertex : polygon.getVertices()) {
            if (!vertexArrayList.contains(polygonVertex)) {
                vertexArrayList.add(polygonVertex);
            }
        }
        polygonArrayList.add(polygon);
    }

    public void addPolygon(Vector v1, Vector v2, Vector v3) {
        addPolygon(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z);
    }

    public void addPolygon(double x1, double y1, double z1, double x2, double y2, double z2,
                           double x3, double y3, double z3) {
        addPolygon(new Polygon(x1, y1, z1, x2, y2, z2, x3, y3, z3));
    }

    public void addQuad(double x1, double y1, double z1, double x2, double y2, double z2,
                        double x3, double y3, double z3, double x4, double y4, double z4) {
        addPolygon(new Polygon(x1, y1, z1, x2, y2, z2, x3, y3, z3));
        addPolygon(new Polygon(x3, y3, z3, x4, y4, z4, x1, y1, z1));
    }

    public void finalizeMesh() {
        Logger.logMsg("Finalizing mesh " + this + "... ");
        long startTime = System.nanoTime();

        if (material == null) {
            material = DEFAULT_MATERIAL;
        }
        polygons = polygonArrayList.toArray(new Polygon[0]);
        boundingBox = BoundingBox.newBoundingBox(this);

        // *Emperor Palpatine voice* "You have been replaced"
        polygonArrayList = null;
        vertexArrayList = null;

        finalized = true;

        long endTime = System.nanoTime();
        Logger.logMsgLn("Done in " + TimeFormatter.timeToString(endTime - startTime));
    }

    // For scene setup
    public void move(double dx, double dy, double dz) {
        for (Vector vertex : vertexArrayList) {
            vertex.add(dx, dy, dz);
        }
    }

    public void move(Vector delta) {
        move(delta.x, delta.y, delta.z);
    }

    public void rotate(double dxDeg, double dyDeg, double dzDeg) {
        for (Vector vertex : vertexArrayList) {
            vertex.rotate(Vector.X_AXIS, dxDeg);
            vertex.rotate(Vector.Y_AXIS, dyDeg);
            vertex.rotate(Vector.Z_AXIS, dzDeg);
        }
        for (Polygon polygon : polygonArrayList) {
            polygon.normal.rotate(Vector.X_AXIS, dxDeg);
            polygon.normal.rotate(Vector.Y_AXIS, dyDeg);
            polygon.normal.rotate(Vector.Z_AXIS, dzDeg);
        }
    }

    public void scale(double x, double y, double z) {
        for (Vector vertex : vertexArrayList) {
            vertex.x *= x;
            vertex.y *= y;
            vertex.z *= z;
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
        return boundingBox.getIntersectedPolygon(origin, ray, lastPolygon);
    }

    // Positional data getters
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

    public void normalize() {
        setCenterAt(Vector.ZERO_VECTOR);
        scale(1 / (getSize().getLargestComponent() / 2));
    }

    public int getPolygonCount() {
        return (finalized) ? polygons.length : polygonArrayList.size();
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}
