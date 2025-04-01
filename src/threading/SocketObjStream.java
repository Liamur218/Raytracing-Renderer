package threading;

import java.io.*;
import java.net.Socket;

public class SocketObjStream {

    Socket socket;

    BufferedInputStream inputStream;
    BufferedOutputStream outputStream;

    SocketObjStream(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new BufferedInputStream(socket.getInputStream());
        outputStream = new BufferedOutputStream(socket.getOutputStream());
    }

    void writeObject(Serializable object) throws IOException {
        byte[] serializedObj = serialize(object);
        outputStream.write(serializedObj.length);
        outputStream.write(serializedObj);
        outputStream.flush();
    }

    Object readObject() throws IOException {
        return deserialize(inputStream.readNBytes(inputStream.read()));
    }

    void close() {
        try { inputStream.close(); } catch (IOException ignored) {}
        try { outputStream.close(); } catch (IOException ignored) {}
        try { socket.close(); } catch (IOException ignored) {}
    }

    String getIP() {
        return socket.getInetAddress().getHostAddress();
    }

    // Serialization / deserialization
    // Stolen from https://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array
    private static byte[] serialize(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Object deserialize(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
