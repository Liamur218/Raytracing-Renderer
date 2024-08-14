package mesh;

import java.util.Iterator;

public class Polygon implements Iterable<Vector> {

    Vector[] points;
    Vector normal;
    double area;

    Polygon(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3) {
        this(new Vector(x1, y1, z1), new Vector(x2, y2, z2), new Vector(x3, y3, z3));
    }

    Polygon(Vector v1, Vector v2, Vector v3) {
        points = new Vector[]{v1, v2, v3};
        normal = Vector.cross(Vector.subtract(v1, v2), Vector.subtract(v1, v3)).normalize();
        area = Vector.cross(Vector.subtract(v1, v2), Vector.subtract(v1, v3)).magnitude() / 2;
    }

    Vector getCentroid() {
        return new Vector(
                points[0].x + points[1].x + points[2].x,
                points[0].y + points[1].y + points[2].y,
                points[0].z + points[1].z + points[2].z).divide(3);
    }

    @Override
    public String toString() {
        return points[0] + "-" + points[1] + "-" + points[2];
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Polygon polygon) {
            for (int i = 0; i < points.length; i++) {
                if (!points[i].equals(polygon.points[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    @Override
    public Iterator<Vector> iterator() {
        return new PolygonIterator();
    }

    private class PolygonIterator implements Iterator<Vector> {

        int index = (points == null) ? -1 : 0;

        @Override
        public boolean hasNext() {
            return index > -1 && index < points.length;
        }

        @Override
        public Vector next() {
            return points[index++];
        }
    }
}
