package mesh;

import renderer.RaycastInfo;
import util.Util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public class BoundingBox implements Iterable<Polygon> {

    public Vector min, max;

    PolygonMesh mesh;
    boolean doCollChecking;
    int arrayStartIndex, polygonCount;
    int depth;

    public BoundingBox leftChild, rightChild;

    private static final int DEFAULT_MAX_DEPTH = 32;
    private static final int DEFAULT_MAX_POLYGONS_PER_BOX = 32;

    public static final boolean DEFAULT_DO_COLL_CHECKING = false;
    public static final boolean DEFAULT_IS_BVH = false;

    private BoundingBox(int arrayStartIndex, int polygonCount, PolygonMesh mesh, boolean doCollChecking, int depth) {
        this.arrayStartIndex = arrayStartIndex;
        this.polygonCount = polygonCount;
        this.mesh = mesh;
        this.doCollChecking = doCollChecking;
        this.depth = depth;

        min = new Vector(Double.MAX_VALUE);
        max = new Vector(-Double.MAX_VALUE);
        for (int i = arrayStartIndex; i < arrayStartIndex + polygonCount; i++) {
            for (Vector vector : mesh.polygons[i]) {
                min.x = Math.min(min.x, vector.x);
                min.y = Math.min(min.y, vector.y);
                min.z = Math.min(min.z, vector.z);
                max.x = Math.max(max.x, vector.x);
                max.y = Math.max(max.y, vector.y);
                max.z = Math.max(max.z, vector.z);
            }
        }
    }

    private static BoundingBox newBoundingBox(PolygonMesh mesh, int currentDepth, int maxDepth, int maxPolygonsPerBox,
                                       int arrayStartIndex, int polygonCount, boolean doCollChecking, boolean isBVH) {
        // Find long axis to divide polygons along
        Vector longestAxis = mesh.getCenterAndSize()[1].getLongestDimension();

        // Sort polygon array based on polygon position along longest axis of bounding box
        Arrays.sort(mesh.polygons, arrayStartIndex, arrayStartIndex + polygonCount - 1,
                new PolygonSorter(longestAxis));

        // Create bounding box
        BoundingBox boundingBox = new BoundingBox(arrayStartIndex, polygonCount, mesh, doCollChecking, currentDepth);

        // Create children if there are enough polygons in current bounding box and max depth has not been reached
        if (isBVH && polygonCount > maxPolygonsPerBox && currentDepth < maxDepth) {
            int midpoint = arrayStartIndex + polygonCount / 2;
            int leftPolygonCount = midpoint - arrayStartIndex;
            int rightPolygonCount = polygonCount - leftPolygonCount;
            boundingBox.leftChild = newBoundingBox(mesh, currentDepth + 1, maxDepth,
                    maxPolygonsPerBox, arrayStartIndex, leftPolygonCount, doCollChecking, true);
            boundingBox.rightChild = newBoundingBox(mesh, currentDepth + 1, maxDepth,
                    maxPolygonsPerBox, midpoint, rightPolygonCount, doCollChecking, true);
        }

        return boundingBox;
    }

    public RaycastInfo getClosestIntersection(Vector origin, Vector direction, Polygon polygonToIgnore) {
        RaycastInfo raycastInfo = new RaycastInfo(origin, direction);
        if (!doCollChecking || intersectedBy(origin, direction)) {
            if (leftChild == null) {
                for (Polygon polygon : this) {
                    if (!polygon.equals(polygonToIgnore)) {
                        Vector intersection = polygon.getIntersection(origin, direction);
                        if (intersection != null) {
                            double distance = Vector.distanceBetween(origin, intersection);
                            if (distance < raycastInfo.distance) {
                                raycastInfo.set(intersection,
                                        (Vector.angleBetween(polygon.normal, direction) > 90) ?
                                                polygon.normal : Vector.multiply(polygon.normal, -1),
                                        mesh, mesh.material, polygon, distance);
                            }
                        }
                    }
                }
            } else {
                RaycastInfo leftColl = leftChild.getClosestIntersection(origin, direction, polygonToIgnore);
                RaycastInfo rightColl = rightChild.getClosestIntersection(origin, direction, polygonToIgnore);
                raycastInfo = (leftColl.distance < rightColl.distance) ? leftColl : rightColl;
            }
        }
        return raycastInfo;
    }

    private boolean intersectedBy(Vector origin, Vector dir) {
        double tLow, tHigh;
        boolean intersectsLow, intersectsHigh;

        // Check XY plane
        tLow = (min.z - origin.z) / dir.z;
        tHigh = (max.z - origin.z) / dir.z;
        intersectsLow = Util.isPointInside2DBox(
                tLow * dir.x + origin.x,
                tLow * dir.y + origin.y,
                min.x, min.y, max.x, max.y);
        intersectsHigh = Util.isPointInside2DBox(
                tHigh * dir.x + origin.x,
                tHigh * dir.y + origin.y,
                min.x, min.y, max.x, max.y);
        if (intersectsLow || intersectsHigh) {
            return true;
        }

        // Check XZ plane
        tLow = (min.y - origin.y) / dir.y;
        tHigh = (max.y - origin.y) / dir.y;
        intersectsLow = Util.isPointInside2DBox(
                dir.x * tLow + origin.x,
                dir.z * tLow + origin.z,
                min.x, min.z, max.x, max.z);
        intersectsHigh = Util.isPointInside2DBox(
                dir.x * tHigh + origin.x,
                dir.z * tHigh + origin.z,
                min.x, min.z, max.x, max.z);
        if (intersectsLow || intersectsHigh) {
            return true;
        }

        // Check YZ plane
        tLow = (min.x - origin.x) / dir.x;
        tHigh = (max.x - origin.x) / dir.x;
        intersectsLow = Util.isPointInside2DBox(
                dir.y * tLow + origin.y,
                dir.z * tLow + origin.z,
                min.y, min.z, max.y, max.z);
        intersectsHigh = Util.isPointInside2DBox(
                dir.y * tHigh + origin.y,
                dir.z * tHigh + origin.z,
                min.y, min.z, max.y, max.z);
        return intersectsLow || intersectsHigh;
    }

    private static class PolygonSorter implements Comparator<Polygon> {

        Vector longestAxis;

        PolygonSorter(Vector longestAxis) {
            this.longestAxis = longestAxis;
        }

        @Override
        public int compare(Polygon p1, Polygon p2) {
            return Double.compare(Vector.dot(p1.getCentroid(), longestAxis), Vector.dot(p2.getCentroid(), longestAxis));
        }
    }

    // public newBoundingBox methods
    public static BoundingBox newBoundingBox(PolygonMesh mesh, boolean isBVH, int maxDepth, int maxPolygonsPerBox,
                                             boolean doCollChecking) {
        return newBoundingBox(mesh, 0, maxDepth, maxPolygonsPerBox, 0,
                mesh.polygons.length, doCollChecking, isBVH);
    }

    public static BoundingBox newBoundingBox(PolygonMesh mesh, boolean doCollChecking, boolean isBVH) {
        return newBoundingBox(mesh, isBVH, DEFAULT_MAX_DEPTH, DEFAULT_MAX_POLYGONS_PER_BOX, doCollChecking);
    }

    public static BoundingBox newBoundingBox(PolygonMesh mesh) {
        return newBoundingBox(mesh, DEFAULT_IS_BVH,
                DEFAULT_MAX_DEPTH, DEFAULT_MAX_POLYGONS_PER_BOX, DEFAULT_DO_COLL_CHECKING);
    }

    // Iterable methods
    @Override
    public Iterator<Polygon> iterator() {
        return new BBoxIterator();
    }

    private class BBoxIterator implements Iterator<Polygon> {

        int current;

        BBoxIterator() {
            current = arrayStartIndex;
        }

        @Override
        public boolean hasNext() {
            return current < arrayStartIndex + polygonCount;
        }

        @Override
        public Polygon next() {
            return mesh.polygons[current++];
        }
    }
}
