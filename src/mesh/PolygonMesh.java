package mesh;

import renderer.RaycastInfo;
import util.*;

import java.util.*;

public class PolygonMesh extends Mesh {

    private ArrayList<Vector> tempVertexList;
    private ArrayList<int[]> tempPolygonIndexList;

    public Polygon[] polygons;

    private BoundingBox boundingBox;
    public boolean finalized;

    public PolygonMesh() {
        tempVertexList = new ArrayList<>();
        tempPolygonIndexList = new ArrayList<>();
    }

    public void addPolygon(Vector v1, Vector v2, Vector v3) {
        int baseSize = tempVertexList.size();
        tempVertexList.add(v1);
        tempVertexList.add(v2);
        tempVertexList.add(v3);
        int[] polygonIndices = new int[]{baseSize, baseSize + 1, baseSize + 2};
        tempPolygonIndexList.add(polygonIndices);
    }

    public void addPolygon(Polygon polygon) {
        addPolygon(polygon.points[0], polygon.points[1], polygon.points[2]);
    }

    public void addPolygon(double x1, double y1, double z1, double x2, double y2, double z2,
                           double x3, double y3, double z3) {
        addPolygon(new Vector(x1, y1, z1), new Vector(x2, y2, z2), new Vector(x3, y3, z3));
    }

    public void addQuad(double x1, double y1, double z1, double x2, double y2, double z2,
                        double x3, double y3, double z3, double x4, double y4, double z4) {
        Vector v1 = new Vector(x1, y1, z1);
        Vector v2 = new Vector(x2, y2, z2);
        Vector v3 = new Vector(x3, y3, z3);
        Vector v4 = new Vector(x4, y4, z4);
        addPolygon(v1, v2, v3);
        addPolygon(v3, v4, v1);
    }

    public void finalizeMesh() {
        if (!finalized) {
            Logger.logMsgLn(Util.getTimeWrapped() + " Finalizing mesh \"" + this + "\"... ");
            long startTime = System.nanoTime();

            // Set material if user forgor
            if (material == null) {
                material = DEFAULT_MATERIAL;
            }

            // Instantiate polygons from vertex indices
            Logger.logMsgLn("\t" + Util.getTimeWrapped() + " Building polygons");
            polygons = new Polygon[tempPolygonIndexList.size()];
            for (int i = 0; i < polygons.length; i++) {
                int[] vertexIndices = tempPolygonIndexList.get(i);
                Vector vertex1 = tempVertexList.get(vertexIndices[0]);
                Vector vertex2 = tempVertexList.get(vertexIndices[1]);
                Vector vertex3 = tempVertexList.get(vertexIndices[2]);
                polygons[i] = new Polygon(vertex1, vertex2, vertex3);
            }

            // Build bounding box
            Logger.logMsgLn("\t" + Util.getTimeWrapped() + " Building bounding box");
            boundingBox = BoundingBox.newBoundingBox(this);

            // Yeet the child
            tempVertexList = null;
            tempPolygonIndexList = null;

            // Finish up
            finalized = true;
            long endTime = System.nanoTime();
            Logger.logMsgLn(Util.getTimeWrapped() + " Mesh \"" + name + "\" finalized in " +
                    TimeFormatter.timeToString(endTime - startTime));
        }
    }

    public void unFinalizeMesh() {
        if (finalized) {
            Logger.logMsg(Util.getTimeWrapped() + " Un-finalizing mesh " + this + "... ");
            long startTime = System.nanoTime();

            for (int i = 0; i < polygons.length; i++) {
                addPolygon(polygons[i]);
                polygons[i] = null;
            }

            boundingBox = null;

            // Finish up
            finalized = false;
            long endTime = System.nanoTime();
            Logger.logMsgLn("Done in " + TimeFormatter.timeToString(endTime - startTime));
        }
    }

    // For scene setup
    public void move(double dx, double dy, double dz) {
        for (Vector vertex : tempVertexList) {
            vertex.add(dx, dy, dz);
        }
    }

    public void move(Vector delta) {
        move(delta.x, delta.y, delta.z);
    }

    public void rotate(double dxDeg, double dyDeg, double dzDeg) {
        for (Vector vertex : tempVertexList) {
            vertex.rotate(Vector.X_AXIS, dxDeg);
            vertex.rotate(Vector.Y_AXIS, dyDeg);
            vertex.rotate(Vector.Z_AXIS, dzDeg);
        }
    }

    public void scale(double x, double y, double z) {
        for (Vector vertex : tempVertexList) {
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
        for (Vector vector : tempVertexList) {
            min.x = Math.min(vector.x, min.x);
            min.y = Math.min(vector.y, min.y);
            min.z = Math.min(vector.z, min.z);
            max.x = Math.max(vector.x, max.x);
            max.y = Math.max(vector.y, max.y);
            max.z = Math.max(vector.z, max.z);
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

    public int getVertexCount() {
        return finalized ? -1 : tempVertexList.size();
    }

    public int getPolygonCount() {
        return finalized ? polygons.length : tempPolygonIndexList.size();
    }

    public ArrayList<Vector> getUnFinalizedVertices() {
        return tempVertexList;
    }

    public ArrayList<int[]> getUnFinalizedPolygonIndices() {
        return tempPolygonIndexList;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    // Hashing
    @Override
    public int hashCode() {
        int result = Objects.hash(tempVertexList, tempPolygonIndexList, boundingBox, finalized);
        result = 31 * result + Arrays.hashCode(polygons);
        return result;
    }
}
