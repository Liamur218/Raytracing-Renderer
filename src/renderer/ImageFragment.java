package renderer;

import mesh.DoubleColor;
import util.ConsoleColors;
import util.Debug;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class ImageFragment implements Serializable {

    public static final Dimension SECTION_SIZE = new Dimension(300, 300);

    DoubleColor[][] array;
    public Dimension size;
    public int posX, posY;
    public int frameNumber;
    public int frameSpaceID;

    ImageFragment(int width, int height, int posX, int posY, int frameNumber, int frameSpaceID) {
        array = new DoubleColor[width][height];
        size = new Dimension(width, height);
        this.posX = posX;
        this.posY = posY;
        this.frameNumber = frameNumber;
        this.frameSpaceID = frameSpaceID;
    }

    void setRGB(int x, int y, DoubleColor color) {
        array[x][y] = color;
    }

    public static void writeBatchToFile(ArrayList<ImageFragment> imageFragments, File file) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            for (ImageFragment imageFragment : imageFragments) {
                outputStream.writeObject(imageFragment);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static ArrayList<ImageFragment> readBatchFromFile(File file) {
        ArrayList<ImageFragment> imageFragments = new ArrayList<>();
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(new FileInputStream(file));
            try {
                for (
                        ImageFragment imageFragment = (ImageFragment) inputStream.readObject();
                        imageFragment != null;
                        imageFragment = (ImageFragment) inputStream.readObject()) {
                    imageFragments.add(imageFragment);
                }
            } catch (EOFException ignored) {  // Thrown when file is empty
            }
        } catch (EOFException ignored) {
            Debug.logMsgLn("\n[WARNING] Encountered empty file while reading from " + file.getName());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                inputStream.close();
            } catch (NullPointerException | IOException ignored) {}
        }
        return imageFragments;
    }
}

