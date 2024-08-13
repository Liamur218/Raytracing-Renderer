package mesh;

import util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class BoundingVolume implements Iterable<Polygon> {

    // Used for all volumes
    Vector min, max;

    // This is used for any simple or leaf volumes
    Polygon[] polygons;

    // Used for volume hierarchy (not needed for simple bounding volume)
    // Left null for leaf volumes
    BoundingVolume left, right;

    void setMin(Vector min) {
        this.min = min;
    }

    void setMax(Vector max) {
        this.max = max;
    }

    void setMinMax(Vector min, Vector max) {
        setMin(min);
        setMax(max);
    }

    public Vector getCenter() {
        return Vector.divide(Vector.add(min, max), 2);
    }

    public Vector getSize() {
        return Vector.subtract(max, min);
    }

    ArrayList<BoundingVolume> getIntersectedVolumes(Vector origin, Vector dir) {
        // <x, y, z> = <mx, my, mz> * t + <x0, y0, z0>
        // x = mx * t + x0  ->  t = (x - x0) / mx
        // y = my * t + y0  ->  t = (y - y0) / my
        // z = mz * t + z0  ->  t = (z - z0) / mz

        double tLow, tHigh;

        boolean intersectsThis = false;
        // XY Plane intersection (2 Z-coords at intersection points)
        if (dir.z != 0) {
            tLow = (min.z - origin.z) / dir.z;
            tHigh = (max.z - origin.z) / dir.z;
            intersectsThis = Util.isPointInside2DBox(tLow * dir.x + origin.x, tLow * dir.y + origin.y,
                    min.x, max.x, min.y, max.y) ||
                    Util.isPointInside2DBox(tHigh * dir.x + origin.x, tHigh * dir.y + origin.y,
                            min.x, max.x, min.y, max.y);
        }

        // YZ Plane intersection (2 X-coords at intersection points)
        if (!intersectsThis && dir.x != 0) {
            tLow = (min.x - origin.x) / dir.x;
            tHigh = (max.x - origin.x) / dir.x;
            intersectsThis = Util.isPointInside2DBox(tLow * dir.y + origin.y, tLow * dir.z + origin.z,
                    min.y, max.y, min.z, max.z) ||
                    Util.isPointInside2DBox(tHigh * dir.y + origin.y, tHigh * dir.z + origin.z,
                            min.y, max.y, min.z, max.z);
        }

        // XZ Plane intersection (2 Y-coords at intersection points)
        if (!intersectsThis && dir.y != 0) {
            tLow = (min.y - origin.y) / dir.y;
            tHigh = (max.y - origin.y) / dir.y;
            intersectsThis = Util.isPointInside2DBox(tLow * dir.x + origin.x, tLow * dir.z + origin.z,
                    min.x, max.x, min.z, max.z) ||
                    Util.isPointInside2DBox(tHigh * dir.x + origin.x, tHigh * dir.z + origin.z,
                            min.x, max.x, min.z, max.z);
        }

        ArrayList<BoundingVolume> intersectedVolumes = new ArrayList<>();
        if (intersectsThis) {
            if (left == null) {
                intersectedVolumes.add(this);
            } else {
                intersectedVolumes.addAll(left.getIntersectedVolumes(origin, dir));
                intersectedVolumes.addAll(right.getIntersectedVolumes(origin, dir));
            }
        }
        return intersectedVolumes;
    }

    @Override
    public String toString() {
        return min + " x " + max;
    }

    // Iterator stuff
    @Override
    public Iterator<Polygon> iterator() {
        return new BVIterator(this);
    }

    private static class BVIterator implements Iterator<Polygon> {

        Stack<Polygon> stack;

        BVIterator(BoundingVolume root) {
            stack = new Stack<>();
            buildStack(root);
        }

        void buildStack(BoundingVolume volume) {
            if (volume.left != null) {
                buildStack(volume.right);
                buildStack(volume.left);
            } else {
                for (int i = volume.polygons.length - 1; i > -1; i--) {
                    stack.push(volume.polygons[i]);
                }
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public Polygon next() {
            return stack.pop();
        }
    }
}
