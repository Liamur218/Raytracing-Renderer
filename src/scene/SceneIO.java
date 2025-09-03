package scene;

import mesh.*;
import util.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public abstract class SceneIO {

    public static final String SCENE_FILE_EXT = ".scn";

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
    * int x2 -------> Image dimensions (width, height)
    *
    * Material... --> Materials
    * Mesh... ------> Meshes
    * */
    public static void writeToFile(Scene scene, String outputDir) {
        File file = new File(outputDir + "/" + scene.name + SCENE_FILE_EXT);
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            outputStream.write(serialize(scene));
            outputStream.flush();
        } catch (IOException e) {
            Logger.logWarningMsg("Encountered IOException while writing to file");
        }
    }

    public static byte[] serialize(Scene scene) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
            writeString(scene.name, byteStream);

            ArrayList<Material> materials = new ArrayList<>();
            for (Mesh mesh : scene.meshes) {
                if (!materials.contains(mesh.material)) {
                    materials.add(mesh.material);
                }
            }

            writeInt(materials.size(), byteStream);
            writeInt(scene.meshes.size(), byteStream);

            writeVector(scene.camera.pos, byteStream);
            writeVector(scene.camera.dir, byteStream);
            writeVector(scene.camera.normal, byteStream);
            writeFloat(scene.camera.hFOV, byteStream);
            writeFloat(scene.camera.vFOV, byteStream);
            writeInt(scene.camera.imageSize.width, byteStream);
            writeInt(scene.camera.imageSize.height, byteStream);

            for (Material material : materials) {
                writeMaterial(material, byteStream);
            }

            for (Mesh mesh : scene.meshes) {
                writeMesh(mesh, materials.indexOf(mesh.material), byteStream);
            }
            return byteStream.toByteArray();
        } catch (IOException e) {
            Logger.logWarningMsg("Encountered IOException during scene serialization");
        }
        return null;
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
            camera.setImageSize(readInt(inputStream), readInt(inputStream));
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
            Logger.logWarningMsg("Encountered IOException while reading from file");
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
    private static void writeMesh(Mesh mesh, int localMatID, OutputStream outputStream) throws IOException {
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

    private static Mesh readMesh(Material[] materialDict, InputStream inputStream) throws IOException {
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

    private static void writeMaterial(Material material, OutputStream outputStream) throws IOException {
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

    public static Material readMaterial(InputStream inputStream) throws IOException {
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
    private static void writeBool(boolean b, OutputStream outputStream) throws IOException {
        outputStream.write(b ? 1 : 0);
    }

    private static boolean readBool(InputStream inputStream) throws IOException {
        return inputStream.read() != 0;
    }

    // byte I/O (for consistency's sake)
    private static void writeByte(byte b, OutputStream outputStream) throws IOException {
        outputStream.write(b);
    }

    private static byte readByte(InputStream inputStream) throws IOException {
        return (byte) inputStream.read();
    }

    // int I/O
    private static void writeInt(int i, OutputStream outputStream) throws IOException {
        outputStream.write(i >> 24);
        outputStream.write(i >> 16);
        outputStream.write(i >> 8);
        outputStream.write(i);
    }

    private static int readInt(InputStream inputStream) throws IOException {
        return inputStream.read() << 24 | inputStream.read() << 16 | inputStream.read() << 8 | inputStream.read();
    }

    // char I/O
    private static void writeChar(char c, OutputStream outputStream) throws IOException {
        outputStream.write(c >> 8);
        outputStream.write(c);
    }

    private static char readChar(InputStream inputStream) throws IOException {
        return (char) (inputStream.read() << 8 | inputStream.read());
    }

    // long I/O
    private static void writeLong(long l, OutputStream outputStream) throws IOException {
        outputStream.write((byte) (l >> 56));
        outputStream.write((byte) (l >> 48));
        outputStream.write((byte) (l >> 40));
        outputStream.write((byte) (l >> 32));
        outputStream.write((byte) (l >> 24));
        outputStream.write((byte) (l >> 16));
        outputStream.write((byte) (l >> 8));
        outputStream.write((byte) (l));
    }

    private static long readLong(InputStream inputStream) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.mark();
        for (int i = 0; i < 8; i++) {
            buffer.put((byte) inputStream.read());
        }
        return buffer.reset().getLong();
    }

    // float I/O
    private static void writeFloat(float f, OutputStream outputStream) throws IOException {
        writeInt(Float.floatToRawIntBits(f), outputStream);
    }

    private static float readFloat(InputStream inputStream) throws IOException {
        return Float.intBitsToFloat(readInt(inputStream));
    }

    // double I/O
    private static void writeDouble(double d, OutputStream outputStream) throws IOException {
        writeLong(Double.doubleToRawLongBits(d), outputStream);
    }

    private static double readDouble(InputStream inputStream) throws IOException {
        return Double.longBitsToDouble(readLong(inputStream));
    }

    // String I/O
    private static void writeString(String string, OutputStream outputStream) throws IOException {
        writeInt(string.length(), outputStream);
        for (char c : string.toCharArray()) {
            writeChar(c, outputStream);
        }
    }

    private static String readString(InputStream inputStream) throws IOException {
        int length = readInt(inputStream);
        char[] charArray = new char[length];
        for (int i = 0; i < charArray.length; i++) {
            charArray[i] = readChar(inputStream);
        }
        return new String(charArray);
    }

    // Vector I/O
    private static void writeVector(Vector vector, OutputStream outputStream) throws IOException {
        writeDouble(vector.getX(), outputStream);
        writeDouble(vector.getY(), outputStream);
        writeDouble(vector.getZ(), outputStream);
    }

    private static Vector readVector(InputStream inputStream) throws IOException {
        return new Vector(readDouble(inputStream), readDouble(inputStream), readDouble(inputStream));
    }
}
