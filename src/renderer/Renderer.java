package renderer;

import mesh.Vector;
import mesh.*;
import scene.*;
import threading.*;
import util.*;

import java.awt.*;
import java.util.*;

public abstract class Renderer {

    private static final ArrayList<ImageFragment> renderedImageFragments = new ArrayList<>();

    public static final double POLYGON_ERROR = 0.01;
    public static final double MIN_REFLECTION_ANGLE = 0.001;

    private static Random random;

    public static Image render(Scene scene, int width, int height, int recursionCount, int frameCount) {
        return render(scene, width, height, recursionCount, frameCount, 1);
    }

    public static Image render(Scene scene, Dimension size, int recursionCount, int frameCount, int threadCount) {
        return render(scene, size.width, size.height, recursionCount, frameCount, threadCount);
    }

    public static Image render(Scene scene, int width, int height,
                               int recursionCount, int frameCount, int threadCount) {
        RenderSettings settings = new RenderSettings(scene);
        settings.setSize(width, height);
        settings.setRecursionCount(recursionCount);
        settings.setFrameCount(frameCount);
        settings.setThreadCount(threadCount);
        return render(settings);
    }

    /*
     * "This is where the fun begins"
     * -Anakin Skywalker, right before the fun began
     * */
    public static Image render(RenderSettings renderSettings) {
        ensureCorrectSettings(renderSettings);

        Scene scene = renderSettings.scene;
        int width = renderSettings.size.width;
        int height = renderSettings.size.height;
        int recursionCount = renderSettings.recursionCount;
        int frameCount = renderSettings.frameCount;
        int threadCount = renderSettings.threadCount;
        int rngSeed = renderSettings.seed;

        Logger.logMsgLn("\nRENDERER START ON " + Util.getDate() + " AT " + Util.getTime());
        Image image = new Image(width, height);

        // Variable setup
        Camera camera = scene.camera;
        long masterStart = System.nanoTime();
        random = new Random(rngSeed);

        // Vector setup
        /*
         * A ---------------- B
         * |                  |
         * |      SCREEN      |
         * |                  |
         * C ---------------- X
         * */
        Logger.newLogSection("Setup", "Setting up");
        Vector A = camera.dir.copy();
        A.rotate(camera.normal, camera.fov.width / 2f);
        A.rotate(camera.binormal, camera.fov.height / 2f);
        Vector B = camera.dir.copy();
        B.rotate(camera.normal, camera.fov.width / -2f);
        B.rotate(camera.binormal, camera.fov.height / 2f);
        Vector C = camera.dir.copy();
        C.rotate(camera.normal, camera.fov.width / 2f);
        C.rotate(camera.binormal, camera.fov.height / -2f);
        Vector rCA = Vector.subtract(C, A);
        Vector rBA = Vector.subtract(B, A);
        Vector hStep = Vector.divide(rBA, width);
        Vector vStep = Vector.divide(rCA, height);

        // Thread stuff
        int totalImgFragCount = ((image.getWidth() - 1) / ImageFragment.SECTION_SIZE.width + 1) *
                ((image.getHeight() - 1) / ImageFragment.SECTION_SIZE.height + 1) * frameCount;
        ProgressBar progressBar = new ProgressBar("Threads", totalImgFragCount);
        ArrayList<RaytracingThread> threads = new ArrayList<>();
        ThreadPool threadPool = new ThreadPool(threadCount);
        // TODO: 2/12/25 Add support for distributed rendering
        threadPool.setProgressBar(progressBar);
        threadPool.start();

        // Create threads
        Logger.newLogSection("Thread creation", "Creating Threads");
        int frameSpaceID = 0;
        int fragsPerFrame = 0;
        for (int xPos = 0; xPos < width; xPos += ImageFragment.SECTION_SIZE.width) {
            for (int yPos = 0; yPos < height; yPos += ImageFragment.SECTION_SIZE.height) {
                for (int frameNumber = 0; frameNumber < frameCount; frameNumber++) {
                    Vector baseDir = A.copy().add(Vector.multiply(hStep, xPos)).add(Vector.multiply(vStep, yPos));
                    int hStepCount = (xPos + ImageFragment.SECTION_SIZE.width >= width) ?
                            width - xPos : ImageFragment.SECTION_SIZE.width;
                    int vStepCount = (yPos + ImageFragment.SECTION_SIZE.height >= height) ?
                            height - yPos : ImageFragment.SECTION_SIZE.height;

                    ImageFragment imageFragment = new ImageFragment(
                            hStepCount, vStepCount, xPos, yPos, frameNumber, frameSpaceID);
                    RaytracingThread thread = new RaytracingThread(camera.pos, baseDir, hStep, vStep,
                            imageFragment, recursionCount, scene, random.nextInt());

                    threads.add(thread);
                }
                frameSpaceID++;
                fragsPerFrame = Math.max(fragsPerFrame, frameSpaceID);
            }
        }

        // Frames
        renderedImageFragments.clear();

        // Sort threads by position (they should be sorted this way already, we're just making sure)
        Logger.newLogSection("Thread organization", "Organizing Threads");
        threads.sort(new RaytracingThread.ThreadComparator());

        // Start render * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        Logger.newLogSection("Render", "Starting render of " + scene);
        Logger.logMsg("\n");
        Logger.logMsgLn("\tImage size -------- " + width + " x " + height);
        Logger.logMsgLn("\tFrames ------------ " + frameCount);
        Logger.logMsgLn("\tThreads ----------- " + threadCount);
        Logger.logMsgLn("\tMax recursion ----- " + recursionCount);
        Logger.logMsgLn("\tImage fragments --- " + totalImgFragCount);
        Logger.logMsgLn("\tFragments / frame - " + fragsPerFrame);
        Logger.logMsgLn("\tRNG Seed ---------- " + rngSeed);
        if (threadCount > frameCount && threadCount > 0) {
            Logger.logWarningMsg("Settings specify more threads than frames; excess threads will not be used");
        }

        int currentFrameSpaceID = 0;
        while (!threads.isEmpty()) {
            progressBar.setStatus("Rendering");
            // Pull and submit all threads for current frame section
            RaytracingThread rtThread = threads.get(0);
            while (rtThread.imageFragment.frameSpaceID == currentFrameSpaceID) {
                threadPool.addTask(rtThread);
                threads.remove(rtThread);
                if (!threads.isEmpty()) {
                    rtThread = threads.get(0);
                } else {
                    break;
                }
            }
            threadPool.executeTasks();

            // Wait for all current threads to finish, then copy their imageFragments to an array list for processing
            threadPool.waitForAllToFinish();
            for (Runnable runnable : threadPool.exportCompletedTasks()) {
                RaytracingThread trThread = (RaytracingThread) runnable;
                renderedImageFragments.add(trThread.imageFragment);
            }

            // Write image fragments to output image
            progressBar.setStatus("Processing");
            int fragPosX = renderedImageFragments.get(0).posX;
            int fragPosY = renderedImageFragments.get(0).posY;
            Dimension fragSize = renderedImageFragments.get(0).size;
            for (int localX = 0; localX < fragSize.width; localX++) {
                for (int localY = 0; localY < fragSize.height; localY++) {
                    NormColor[] colors = new NormColor[renderedImageFragments.size()];
                    for (int frameNumber = 0; frameNumber < colors.length; frameNumber++) {
                        colors[frameNumber] = renderedImageFragments.get(frameNumber).array[localX][localY];
                    }
                    image.setRGB(fragPosX + localX, fragPosY + localY, NormColor.average(colors).getRGB());
                }
            }

            // End-of-loop stuff
            currentFrameSpaceID++;
            renderedImageFragments.clear();
        }

        threadPool.halt();
        Logger.logMsg("\n");
        Logger.endLogSection();

        if (renderSettings.postProcessor != null) {
            Logger.newLogSection("Postprocessing", "Postprocessing");
            renderSettings.postProcessor.postProcess(image);
            Logger.endLogSection();
        }

        Logger.logMsgLn(Util.getTimeWrapped() + " All done");
        Logger.logElapsedTime("-> Total elapsed time: ", masterStart);

        return image;
    }

    static synchronized void returnImageFragment(ImageFragment imageFragment) {
        renderedImageFragments.add(imageFragment);
    }

    public static Image quickRender(Scene scene) {
        return quickRender(scene, RenderSettings.BUDGET_SETTINGS.size);
    }

    public static Image quickRender(Scene scene, Dimension imageSize) {
        return quickRender(scene, imageSize.width, imageSize.height);
    }

    public static Image quickRender(Scene scene, int width, int height) {
        Logger.logMsgLn("Beginning quick render");
        Image image = new Image(width, height);

        // Variable setup
        Camera camera = scene.camera;

        // Vector setup
        /*
         * A ---------------- B
         * |                  |
         * |      SCREEN      |
         * |                  |
         * C ---------------- X
         * */
        Logger.logMsg("Setting up... ");
        Vector A = camera.dir.copy();
        A.rotate(camera.normal, camera.fov.width / 2f);
        A.rotate(camera.binormal, camera.fov.height / 2f);
        Vector B = camera.dir.copy();
        B.rotate(camera.normal, camera.fov.width / -2f);
        B.rotate(camera.binormal, camera.fov.height / 2f);
        Vector C = camera.dir.copy();
        C.rotate(camera.normal, camera.fov.width / 2f);
        C.rotate(camera.binormal, camera.fov.height / -2f);
        Vector rCA = Vector.subtract(C, A);
        Vector rBA = Vector.subtract(B, A);
        Vector hStep = Vector.divide(rBA, width);
        Vector vStep = Vector.divide(rCA, height);
        Logger.logMsgLn("Done");

        // Raytracing loop
        Logger.logMsgLn("Starting render");
        long start = System.nanoTime();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Vector ray = A.copy().add(Vector.multiply(hStep, i)).add(Vector.multiply(vStep, j));
                image.setRGB(i, j, nonRecursiveRaycast(camera.pos, ray, scene).rayColor.getRGB());
            }
        }

        long end = System.nanoTime();
        Logger.logMsgLn("\nRender complete");
        Logger.logMsgLn("Elapsed time: " + TimeFormatter.timeToString(end - start));
        return image;
    }

    private static RaycastInfo nonRecursiveRaycast(Vector origin, Vector ray, Scene scene) {
        RaycastInfo raycast = new RaycastInfo(origin, ray);

        // Find intersected mesh and point of intersection
        for (Mesh mesh : scene.meshes) {
            RaycastInfo hitscan = mesh.getClosestIntersection(origin, ray, null);
            if (raycast.intersection == null || hitscan.distance < raycast.distance) {
                raycast = hitscan;
            }
        }

        if (raycast.intersection == null) {
            raycast.rayColor.set(NormColor.BLACK);
        } else {
            raycast.rayColor.set(raycast.material.color);
        }

        return raycast;
    }

    // Make sure user didn't screw up
    private static void ensureCorrectSettings(RenderSettings settings) {
        boolean doExit = false;
        if (settings.scene == null) {
            Logger.logErrorMsg("Scene object is null");
            System.exit(1);
        } else {
            for (Mesh mesh : settings.scene.meshes) {
                if (mesh.material == null) {
                    Logger.logErrorMsg("Material for mesh " + mesh + " is null");
                    doExit = true;
                }
            }
        }

        if (settings.size.width <= 0 || settings.size.height <= 0) {
            Logger.logErrorMsg("Image size contains invalid dimension (is " + settings.size + " must be > 0)");
            doExit = true;
        }

        if (settings.recursionCount < 0) {
            Logger.logErrorMsg("Recursion count (" + settings.recursionCount + ") set to negative number");
            doExit = true;
        }

        if (settings.frameCount <= 0) {
            Logger.logErrorMsg("Frame count (" + settings.frameCount + ") must be greater than 1");
            doExit = true;
        }

        if (settings.threadCount <= 0) {
            Logger.logWarningMsg("Thread count should be a positive integer. Using thread count of 1 instead.");
            settings.threadCount = 1;
        }

        if (settings.sectionWidth < 1 || settings.sectionHeight < 1) {
            Logger.logErrorMsg("Section size (" + settings.sectionWidth + " x " + settings.sectionHeight +
                    ") contains invalid dimension must be > 0");
            doExit = true;
        }

        if (doExit) {
            System.exit(1);
        }
    }
}
