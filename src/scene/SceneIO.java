package scene;

import mesh.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public abstract class SceneIO {

    /*
    * Scene data formatting:
    * String -------> Scene name
    *
    * int ----------> Material count
    * int ----------> Mesh count
    *
    * Vector -------> Camera position
    * Vector -------> Camera direction
    * Vector -------> Camera normal
    * float x2 -----> Camera FOV (horizontal, vertical)
    *
    * Material... --> Materials
    * Mesh... ------> Meshes
    * */
    public static void writeToFile(Scene scene, String outputDir) {
        File file = new File(outputDir + "/" + scene.name + ".scene");
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            writeString(scene.name, outputStream);

            ArrayList<Material> materials = new ArrayList<>();
            for (Mesh mesh : scene.meshes) {
                if (!materials.contains(mesh.material)) {
                    materials.add(mesh.material);
                }
            }

            writeInt(materials.size(), outputStream);
            writeInt(scene.meshes.size(), outputStream);

            writeVector(scene.camera.pos, outputStream);
            writeVector(scene.camera.dir, outputStream);
            writeVector(scene.camera.normal, outputStream);
            writeFloat(scene.camera.hFOV, outputStream);
            writeFloat(scene.camera.vFOV, outputStream);

            for (Material material : materials) {
                writeMaterial(material, outputStream);
            }

            for (Mesh mesh : scene.meshes) {
                writeMesh(mesh, materials.indexOf(mesh.material), outputStream);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Scene readFromFile(String filename) {
        File file = new File(filename);
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            String name = readString(inputStream);
            int materialCount = readInt(inputStream);
            int meshCount = readInt(inputStream);

            Scene scene = new Scene().setName(name);

            Camera camera = new Camera(readVector(inputStream), readVector(inputStream), readVector(inputStream));
            camera.setFOV(readFloat(inputStream), readFloat(inputStream));
            scene.setCamera(camera);

            Material[] materials = new Material[materialCount];
            for (int i = 0; i < materialCount; i++) {
                materials[i] = readMaterial(inputStream);
            }

            for (int i = 0; i < meshCount; i++) {
                Mesh mesh = readMesh(materials, inputStream);
                scene.addMesh(mesh);
            }

            return scene;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    * Mesh data formatting:
    * byte -------> Mesh type
    *                   1 -> Polygon mesh
    *                   2 -> Sphere mesh
    *                   3 -> Plane mesh
    * byte -------> Material id number
    * String -----> Mesh name
    *
    * Polygon mesh:
    * int --------> Vertex count
    * int --------> Polygon count
    * Vector... --> Vertex list
    * int[]... ---> Polygon index list
    *
    * Sphere mesh:
    * Vector -----> center
    * double -----> radius
    *
    * Plane mesh:
    * Vector -----> pos
    * Vector -----> normal
    * boolean ----> do rear visibility
    * */
    private static void writeMesh(Mesh mesh, int localMatID, BufferedOutputStream outputStream) throws IOException {
        if (mesh instanceof PolygonMesh polygonMesh) {
            writeByte((byte) 1, outputStream);
            writeInt(localMatID, outputStream);
            writeString(mesh.toString(), outputStream);

            polygonMesh.unFinalizeMesh();

            writeInt(polygonMesh.getVertexCount(), outputStream);
            writeInt(polygonMesh.getPolygonCount(), outputStream);

            for (Vector vertex : polygonMesh.getUnFinalizedVertices()) {
                writeVector(vertex, outputStream);
            }
            for (int[] polygonIndices : polygonMesh.getUnFinalizedPolygonIndices()) {
                writeInt(polygonIndices[0], outputStream);
                writeInt(polygonIndices[1], outputStream);
                writeInt(polygonIndices[2], outputStream);
            }
        } else if (mesh instanceof SphereMesh sphereMesh) {
            writeByte((byte) 2, outputStream);
            writeInt(localMatID, outputStream);
            writeString(mesh.toString(), outputStream);

            writeVector(sphereMesh.center, outputStream);
            writeDouble(sphereMesh.radius, outputStream);
        } else if (mesh instanceof PlaneMesh planeMesh) {
            writeByte((byte) 3, outputStream);
            writeInt(localMatID, outputStream);
            writeString(mesh.toString(), outputStream);

            writeVector(planeMesh.refPoint, outputStream);
            writeVector(planeMesh.normal, outputStream);
            writeBool(planeMesh.doRearVisibility, outputStream);
        }
    }

    private static Mesh readMesh(Material[] materialDict, BufferedInputStream inputStream) throws IOException {
        byte meshType = readByte(inputStream);
        Material material = materialDict[readInt(inputStream)];
        String name = readString(inputStream);

        switch (meshType) {
            case 1 -> {
                PolygonMesh mesh = new PolygonMesh();
                mesh.setMaterial(material).setName(name);

                int vertexCount = readInt(inputStream);
                int polygonCount = readInt(inputStream);

                Vector[] vertexDict = new Vector[vertexCount];
                for (int i = 0; i < vertexCount; i++) {
                    vertexDict[i] = readVector(inputStream);
                }

                for (int i = 0; i < polygonCount; i++) {
                    int vector1Index = readInt(inputStream);
                    int vector2Index = readInt(inputStream);
                    int vector3Index = readInt(inputStream);
                    mesh.addPolygon(vertexDict[vector1Index], vertexDict[vector2Index], vertexDict[vector3Index]);
                }

                return mesh;
            }
            case 2 -> {
                Vector center = readVector(inputStream);
                double radius = readDouble(inputStream);
                return new SphereMesh(center, radius).setMaterial(material).setName(name);
            }
            case 3 -> {
                Vector pos = readVector(inputStream);
                Vector normal = readVector(inputStream);
                boolean doRearVisibility = readBool(inputStream);
                PlaneMesh mesh = new PlaneMesh(pos, normal);
                return mesh.setDoRearVisibility(doRearVisibility).setMaterial(material).setName(name);
            }
            default -> {
                return null;
            }
        }
    }

    private static void writeMaterial(Material material, BufferedOutputStream outputStream) throws IOException {
        /*
         * Material properties:
         *     color -----------> float x3
         *     emissivity ------> float
         *     reflectivity ----> float
         *     specularity -----> float
         *     opacity ---------> float
         *     refractiveIndex -> float
         */
        writeFloat(material.color.r, outputStream);
        writeFloat(material.color.g, outputStream);
        writeFloat(material.color.b, outputStream);
        writeFloat(material.emissivity, outputStream);
        writeFloat(material.reflectivity, outputStream);
        writeFloat(material.specularity, outputStream);
        writeFloat(material.opacity, outputStream);
        writeFloat(material.refractiveIndex, outputStream);
    }

    public static Material readMaterial(BufferedInputStream inputStream) throws IOException {
        Material material = new Material();
        material.setColor(new NormColor(readFloat(inputStream), readFloat(inputStream), readFloat(inputStream)));
        material.setEmissivity(readFloat(inputStream));
        material.setReflectivity(readFloat(inputStream));
        material.setSpecularity(readFloat(inputStream));
        material.setOpacity(readFloat(inputStream));
        material.setRefractiveIndex(readFloat(inputStream));
        return material;
    }

    // Primitives
    // boolean I/O
    private static void writeBool(boolean b, BufferedOutputStream outputStream) throws IOException {
        outputStream.write(b ? 1 : 0);
    }

    private static boolean readBool(BufferedInputStream inputStream) throws IOException {
        return inputStream.read() != 0;
    }

    // byte I/O (for consistency's sake)
    private static void writeByte(byte b, BufferedOutputStream outputStream) throws IOException {
        outputStream.write(b);
    }

    private static byte readByte(BufferedInputStream inputStream) throws IOException {
        return (byte) inputStream.read();
    }

    // int I/O
    private static void writeInt(int i, BufferedOutputStream outputStream) throws IOException {
        outputStream.write(i >> 24);
        outputStream.write(i >> 16);
        outputStream.write(i >> 8);
        outputStream.write(i);
    }

    private static int readInt(BufferedInputStream inputStream) throws IOException {
        return inputStream.read() << 24 | inputStream.read() << 16 | inputStream.read() << 8 | inputStream.read();
    }

    // char I/O
    private static void writeChar(char c, BufferedOutputStream outputStream) throws IOException {
        outputStream.write(c >> 8);
        outputStream.write(c);
    }

    private static char readChar(BufferedInputStream inputStream) throws IOException {
        return (char) (inputStream.read() << 8 | inputStream.read());
    }

    // long I/O
    private static void writeLong(long l, BufferedOutputStream outputStream) throws IOException {
        outputStream.write((byte) (l >> 56));
        outputStream.write((byte) (l >> 48));
        outputStream.write((byte) (l >> 40));
        outputStream.write((byte) (l >> 32));
        outputStream.write((byte) (l >> 24));
        outputStream.write((byte) (l >> 16));
        outputStream.write((byte) (l >> 8));
        outputStream.write((byte) (l));
    }

    private static long readLong(BufferedInputStream inputStream) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.mark();
        for (int i = 0; i < 8; i++) {
            buffer.put((byte) inputStream.read());
        }
        return buffer.reset().getLong();
    }

    // float I/O
    private static void writeFloat(float f, BufferedOutputStream outputStream) throws IOException {
        writeInt(Float.floatToRawIntBits(f), outputStream);
    }

    private static float readFloat(BufferedInputStream inputStream) throws IOException {
        return Float.intBitsToFloat(readInt(inputStream));
    }

    // double I/O
    private static void writeDouble(double d, BufferedOutputStream outputStream) throws IOException {
        writeLong(Double.doubleToRawLongBits(d), outputStream);
    }

    private static double readDouble(BufferedInputStream inputStream) throws IOException {
        return Double.longBitsToDouble(readLong(inputStream));
    }

    // String I/O
    private static void writeString(String string, BufferedOutputStream outputStream) throws IOException {
        writeInt(string.length(), outputStream);
        for (char c : string.toCharArray()) {
            writeChar(c, outputStream);
        }
    }

    private static String readString(BufferedInputStream inputStream) throws IOException {
        int length = readInt(inputStream);
        char[] charArray = new char[length];
        for (int i = 0; i < charArray.length; i++) {
            charArray[i] = readChar(inputStream);
        }
        return new String(charArray);
    }

    // Vector I/O
    private static void writeVector(Vector vector, BufferedOutputStream outputStream) throws IOException {
        writeDouble(vector.getX(), outputStream);
        writeDouble(vector.getY(), outputStream);
        writeDouble(vector.getZ(), outputStream);
    }

    private static Vector readVector(BufferedInputStream inputStream) throws IOException {
        return new Vector(readDouble(inputStream), readDouble(inputStream), readDouble(inputStream));
    }
}
