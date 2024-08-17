package mesh;

import java.util.ArrayList;

public class CubeMesh extends PolygonMesh {

    public CubeMesh(double posX, double posY, double posZ, double dimX, double dimY, double dimZ) {
        this(new Vector(posX, posY, posZ), dimX, dimY, dimZ, 0, 0, 0);
    }

    public CubeMesh(Vector pos, double dimX, double dimY, double dimZ) {
        this(pos, dimX, dimY, dimZ, 0, 0, 0);
    }

    public CubeMesh(Vector pos, double dim) {
        this(pos, dim, dim, dim);
    }

    public CubeMesh(double posX, double posY, double posZ, double dim) {
        this(new Vector(posX, posY, posZ), dim);
    }

    public CubeMesh(double posX, double posY, double posZ, double dimX, double dimY, double dimZ,
                    double rotX, double rotY, double rotZ) {
        this(new Vector(posX, posY, posZ), dimX, dimY, dimZ, rotX, rotY, rotZ);
    }

    public CubeMesh(Vector pos, double dimX, double dimY, double dimZ, double rotX, double rotY, double rotZ) {
        Vector backTopLeft = new Vector(dimX / 2, dimY / 2, dimZ / 2);
        Vector backTopRight = new Vector(dimX / 2, dimY / -2, dimZ / 2);
        Vector backBottomLeft = new Vector(dimX / 2, dimY / 2, dimZ / -2);
        Vector backBottomRight = new Vector(dimX / 2, dimY / -2, dimZ / -2);
        Vector frontTopLeft = new Vector(dimX / -2, dimY / 2, dimZ / 2);
        Vector frontTopRight = new Vector(dimX / -2, dimY / -2, dimZ / 2);
        Vector frontBottomLeft = new Vector(dimX / -2, dimY / 2, dimZ / -2);
        Vector frontBottomRight = new Vector(dimX / -2, dimY / -2, dimZ / -2);

        ArrayList<Vector> vectors = new ArrayList<>();
        vectors.add(backTopLeft);
        vectors.add(backTopRight);
        vectors.add(backBottomLeft);
        vectors.add(backBottomRight);
        vectors.add(frontTopLeft);
        vectors.add(frontTopRight);
        vectors.add(frontBottomLeft);
        vectors.add(frontBottomRight);

        for (Vector vector : vectors) {
            vector.rotate(Vector.X_AXIS, rotX);
            vector.rotate(Vector.Y_AXIS, rotY);
            vector.rotate(Vector.Z_AXIS, rotZ);
            vector.add(pos);
        }

        // Back
        addPolygon(backTopLeft, backTopRight, backBottomRight);
        addPolygon(backBottomLeft, backTopLeft, backBottomRight);
        // Front
        addPolygon(frontTopLeft, frontTopRight, frontBottomRight);
        addPolygon(frontBottomLeft, frontTopLeft, frontBottomRight);
        // Left
        addPolygon(frontTopLeft, backTopLeft, backBottomLeft);
        addPolygon(frontTopLeft, frontBottomLeft, backBottomLeft);
        // Right
        addPolygon(frontTopRight, backTopRight, backBottomRight);
        addPolygon(frontTopRight, frontBottomRight, backBottomRight);
        // Bottom
        addPolygon(frontBottomLeft, backBottomLeft, backBottomRight);
        addPolygon(frontBottomLeft, backBottomRight, frontBottomRight);
        // Top
        addPolygon(frontTopLeft, backTopLeft, backTopRight);
        addPolygon(frontTopLeft, backTopRight, frontTopRight);
    }
}
