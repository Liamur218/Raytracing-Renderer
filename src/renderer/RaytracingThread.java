package renderer;

import mesh.Vector;
import mesh.*;
import scene.Scene;
import util.ProgressBar;

import java.io.Serializable;
import java.util.*;

public class RaytracingThread implements Runnable, Serializable {

    Vector origin, startDir;
    Vector hStep, vStep;
    int recursionCount;
    Scene scene;
    ImageFragment imageFragment;

    long startTime, stopTime;

    private Random random;

    int id;
    static int idCounter = 0;

    static ProgressBar progressBar;

    RaytracingThread(Vector origin, Vector startDir, Vector hStep, Vector vStep,
                     ImageFragment imageFragment,
                     int recursionCount, Scene scene, int rngSeed) {
        this.origin = origin;
        this.startDir = startDir;
        this.hStep = hStep;
        this.vStep = vStep;

        this.imageFragment = imageFragment;

        this.recursionCount = recursionCount;
        this.scene = scene;

        random = new Random(rngSeed);

        id = ++idCounter;
    }

    @Override
    public void run() {
        startTime = System.nanoTime();

        for (int i = 0; i < imageFragment.size.width; i++) {
            for (int j = 0; j < imageFragment.size.height; j++) {
                Vector ray = new Vector(startDir);
                ray.add(Vector.multiply(hStep, i)).add(Vector.multiply(vStep, j)).normalize();
                DoubleColor color = Renderer.raycast(origin, ray, recursionCount, scene, null).rayColor;
                imageFragment.setRGB(i, j, color);
            }
        }

        Renderer.returnImageFragment(imageFragment);
        if (progressBar != null) {
            progressBar.increment(1);
        }

        stopTime = System.nanoTime();
    }

    public static int getTotalCreatedThreads() {
        return idCounter;
    }

    public static void setProgressBar(ProgressBar progressBar) {
        RaytracingThread.progressBar = progressBar;
    }

    static class ThreadComparator implements Comparator<RaytracingThread> {
        @Override
        public int compare(RaytracingThread thread1, RaytracingThread thread2) {
            return thread1.imageFragment.frameSpaceID - thread2.imageFragment.frameSpaceID;
        }
    }
}
