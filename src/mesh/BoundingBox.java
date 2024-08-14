package mesh;

import java.util.Arrays;
import java.util.Comparator;

public class BoundingBox {

    Vector min, max;

    int arrayStartIndex;
    int polygonCount;

    BoundingBox leftChild, rightChild;

    private static final int DEFAULT_MAX_DEPTH = 32;
    private static final int DEFAULT_MAX_POLYGONS_PER_BOX = 32;

    private BoundingBox(int arrayStartIndex, int polygonCount) {
        this.arrayStartIndex = arrayStartIndex;
        this.polygonCount = polygonCount;
    }

    private static BoundingBox newBoundingBox(PolygonMesh mesh, int currentDepth, int maxDepth, int maxPolygonsPerBox,
                                       int arrayStartIndex, int polygonCount) {
        // Find long axis to divide polygons along
        Vector longestAxis = mesh.getCenterAndSize()[0].getLongestDimension();

        // Sort polygon array along axis
        Arrays.sort(mesh.polygons, arrayStartIndex, polygonCount - 1, new PolygonSorter(longestAxis));

        // Create bounding box
        BoundingBox boundingBox = new BoundingBox(arrayStartIndex, polygonCount);

        // Create children if there are enough polygons in current bounding box and max depth has not been reached
        if (polygonCount > maxPolygonsPerBox && currentDepth < maxDepth) {
            int midpoint = polygonCount / 2;
            boundingBox.leftChild = newBoundingBox(mesh, currentDepth + 1, maxDepth + 1,
                    maxPolygonsPerBox, arrayStartIndex, midpoint - arrayStartIndex);
            boundingBox.rightChild = newBoundingBox(mesh, currentDepth + 1, maxDepth + 1,
                    maxPolygonsPerBox, midpoint, polygonCount - midpoint);
        }

        return boundingBox;
    }

    private void setMinMax(Vector[] minMax) {
        min = minMax[0];
        max = minMax[1];
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
    public static BoundingBox newBoundingBox(PolygonMesh mesh, int maxDepth, int maxPolygonsPerBox) {
        return newBoundingBox(mesh, 0, maxDepth, maxPolygonsPerBox, 0, mesh.polygons.length);
    }

    public static BoundingBox newBoundingBox(PolygonMesh mesh, int maxDepth) {
        return newBoundingBox(mesh, maxDepth, DEFAULT_MAX_POLYGONS_PER_BOX);
    }

    public static BoundingBox newBoundingBox(PolygonMesh mesh) {
        return newBoundingBox(mesh, DEFAULT_MAX_DEPTH, DEFAULT_MAX_POLYGONS_PER_BOX);
    }
}
