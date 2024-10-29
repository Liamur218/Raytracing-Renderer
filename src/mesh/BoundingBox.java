package mesh;

import renderer.RaycastInfo;
import util.Util;

import java.util.*;

public class BoundingBox implements Iterable<Polygon> {

    public Vector min, max;

    PolygonMesh mesh;
    boolean doCollChecking;
    int arrayStartIndex, polygonCount;
    int depth;

    public BoundingBox leftChild, rightChild;

    public static final int MAX_DEPTH = 32;
    public static final int MAX_POLYGONS_PER_BOX = 32;
    public static final int MIN_POLYGONS_FOR_BOX = MAX_POLYGONS_PER_BOX / 8;

    private BoundingBox(int arrayStartIndex, int polygonCount, PolygonMesh mesh, int depth, boolean doCollChecking) {
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
                                              int arrayStartIndex, int polygonCount, boolean genFullBox) {
        // Find long axis to divide polygons along
        Vector longestAxis = mesh.getCenterAndSize()[1].getLongestAxis();

        // Sort polygon array based on polygon position along longest axis of bounding box
        Arrays.sort(mesh.polygons, arrayStartIndex, arrayStartIndex + polygonCount - 1,
                new PolygonSorter(longestAxis));

        // Create bounding box
        BoundingBox boundingBox = new BoundingBox(arrayStartIndex, polygonCount, mesh, currentDepth, genFullBox);

        // Create children if there are enough polygons in current bounding box and max depth has not been reached
        if (genFullBox && polygonCount > maxPolygonsPerBox && currentDepth < maxDepth) {
            int midpoint = arrayStartIndex + polygonCount / 2;
            int leftPolygonCount = midpoint - arrayStartIndex;
            int rightPolygonCount = polygonCount - leftPolygonCount;
            boundingBox.leftChild = newBoundingBox(mesh, currentDepth + 1, maxDepth,
                    maxPolygonsPerBox, arrayStartIndex, leftPolygonCount, true);
            boundingBox.rightChild = newBoundingBox(mesh, currentDepth + 1, maxDepth,
                    maxPolygonsPerBox, midpoint, rightPolygonCount, true);
        }

        return boundingBox;
    }

    // public newBoundingBox methods
    public static BoundingBox newBoundingBox(PolygonMesh mesh) {
        return newBoundingBox(mesh, 0, MAX_DEPTH, MAX_POLYGONS_PER_BOX,
                0, mesh.polygons.length,
                mesh.polygons.length > MIN_POLYGONS_FOR_BOX);
    }

    // Intersection methods
    public RaycastInfo getIntersectedPolygon(Vector origin, Vector direction, Polygon polygonToIgnore) {
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
                RaycastInfo leftColl = leftChild.getIntersectedPolygon(origin, direction, polygonToIgnore);
                RaycastInfo rightColl = rightChild.getIntersectedPolygon(origin, direction, polygonToIgnore);
                raycastInfo = (leftColl.distance < rightColl.distance) ? leftColl : rightColl;
            }
        }
        return raycastInfo;
    }

    public boolean intersectedBy(Vector origin, Vector dir) {
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

    // Iteration methods
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

    public void print() {
        print("");
    }

    private void print(String prefix) {
        System.out.println(prefix + "Lvl " + depth + ": " + polygonCount);
        if (leftChild != null) {
            leftChild.print(prefix + "\t");
        }
        if (rightChild != null) {
            rightChild.print(prefix + "\t");
        }
    }

    public int getMaxDepth() {
        if (leftChild == null) {
            return 0;
        } else {
            return Math.max(leftChild.getMaxDepth(), rightChild.getMaxDepth()) + 1;
        }
    }

    public int getMinPolygonsPerBox() {
        if (leftChild == null) {
            return polygonCount;
        } else {
            return Math.min(leftChild.getMinPolygonsPerBox(), rightChild.getMinPolygonsPerBox());
        }
    }

    public int getMaxPolygonsPerBox() {
        if (leftChild == null) {
            return polygonCount;
        } else {
            return Math.max(leftChild.getMaxPolygonsPerBox(), rightChild.getMaxPolygonsPerBox());
        }
    }
}
