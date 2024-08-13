package mesh;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class BVBuilder {
    // Constants
    private static final int MAX_TRIANGLES_PER_BOX = 50;
    private static final int MAX_DEPTH = 16;

    // Base bounding volume for simple operations
    // Not a binary tree
    public static BoundingVolume newBV(ArrayList<Polygon> polygons) {
        Vector min = new Vector(Double.MAX_VALUE);
        Vector max = new Vector(Double.MIN_VALUE);
        for (Polygon polygon : polygons) {
            for (Vector point : polygon.points) {
                min.x = Math.min(point.x, min.x);
                min.y = Math.min(point.y, min.y);
                min.z = Math.min(point.z, min.z);
                max.x = Math.max(point.x, max.x);
                max.y = Math.max(point.y, max.y);
                max.z = Math.max(point.z, max.z);
            }
        }

        BoundingVolume boundingVolume = new BoundingVolume();
        boundingVolume.setMinMax(min, max);
        boundingVolume.polygons = polygons.toArray(new Polygon[0]);
        return boundingVolume;
    }

    // Bounding Volume Hierarchy
    // Used for collision detection and polygon (triangle) culling in more advanced collision detection
    // NOTE: THIS IS DESTRUCTIVE TO THE ARRAY LIST PASSED INTO THE METHOD
    public static BoundingVolume newBVH(ArrayList<Polygon> polygons) {
        return newBVH(polygons, 0);
    }

    private static BoundingVolume newBVH(ArrayList<Polygon> polygons, int currentDepth) {
        BoundingVolume boundingVolume = newBV(polygons);

        if (polygons.size() > MAX_TRIANGLES_PER_BOX && currentDepth < MAX_DEPTH) {
            Vector size = boundingVolume.getSize();
            Vector longAxis = (size.x > size.y && size.x > size.z) ? Vector.X_AXIS :
                    ((size.y > size.x && size.y > size.z) ? Vector.Y_AXIS : Vector.Z_AXIS);

            PolygonSorter polygonSorter = new PolygonSorter(longAxis);
            polygons.sort(polygonSorter);

            ArrayList<Polygon> leftPolygons = new ArrayList<>();
            for (int i = 0; i < polygons.size() / 2; i++) {
                leftPolygons.add(polygons.remove(0));
            }

            boundingVolume.left = newBVH(leftPolygons, currentDepth + 1);
            boundingVolume.right = newBVH(polygons, currentDepth + 1);
        } else {
            boundingVolume.polygons = polygons.toArray(new Polygon[0]);
        }

        return boundingVolume;
    }

    private static class PolygonSorter implements Comparator<Polygon> {

        Vector longAxis;

        PolygonSorter(Vector longAxis) {
            this.longAxis = longAxis;
        }

        @Override
        public int compare(Polygon p1, Polygon p2) {
            return Double.compare(Vector.dot(p1.getCentroid(), longAxis), Vector.dot(p2.getCentroid(), longAxis));
        }
    }
}
