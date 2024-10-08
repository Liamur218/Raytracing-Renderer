package renderer;

import mesh.Vector;
import mesh.*;
import scene.*;
import util.*;

import java.awt.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class Renderer {

    private static final ArrayList<ImageFragment> RETURN_BUFFER = new ArrayList<>();

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
        int rngSeed = renderSettings.rngSeed;

        Debug.logMsgLn("\n * * * STARTING RENDERER * * *");
        Image image = new Image(width, height);

        // Variable setup
        Camera camera = scene.activeCamera;
        long sectionStart, masterStart;
        masterStart = System.nanoTime();
        random = new Random(rngSeed);

        // Vector setup
        /*
         * A ---------------- B
         * |                  |
         * |      SCREEN      |
         * |                  |
         * C ---------------- X
         * */
        Debug.logMsg(Util.getCurrentTime() + " Setting up... ");
        sectionStart = System.nanoTime();
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
        ArrayList<RaytracingThread> threads = new ArrayList<>();
        ArrayList<Future<?>> futures = new ArrayList<>();
        ExecutorService threadPool = (threadCount > 0) ?
                Executors.newFixedThreadPool(threadCount) : Executors.newCachedThreadPool();

        Debug.logMsgLn("Done");
        Debug.logElapsedTime("-> Setup complete in: ", sectionStart);
        // Create threads
        Debug.logMsg("Creating Threads... ");
        sectionStart = System.nanoTime();
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
        RETURN_BUFFER.clear();
        int totalImgFragCount = threads.size();

        // Sort threads by position (they should be sorted this way already, we're just making sure)
        threads.sort(new RaytracingThread.ThreadComparator());

        // Misc setup stuff
        ProgressBar progressBar = new ProgressBar("Threads", totalImgFragCount);
        RaytracingThread.setProgressBar(progressBar);

        Debug.logMsgLn("Done");
        Debug.logElapsedTime("-> Thread creation complete in: ", sectionStart);

        // Start render * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        Debug.logMsgLn(Util.getCurrentTime() + " Starting render of " + scene + ":");
        Debug.logMsgLn("\tImage size -------- " + width + " x " + height);
        Debug.logMsgLn("\tFrames ------------ " + frameCount);
        Debug.logMsgLn("\tThreads ----------- " + ((threadCount > 0) ? threadCount : "YES"));
        Debug.logMsgLn("\tMax recursion ----- " + recursionCount);
        Debug.logMsgLn("\tImage fragments --- " + totalImgFragCount);
        Debug.logMsgLn("\tFragments / frame - " + fragsPerFrame);
        Debug.logMsgLn("\tSeed -------------- " + rngSeed);
        if (threadCount > frameCount && threadCount > 0) {
            Debug.logMsgLn("[WARNING] Settings specify threads than frames; excess threads will be unutilized");
        }

        int currentFrameSpaceID = 0;
        while (!threads.isEmpty()) {
            progressBar.setStatus("Rendering");
            // Pull and submit all threads for current frame section
            RaytracingThread thread = threads.get(0);
            while (thread.imageFragment.frameSpaceID == currentFrameSpaceID) {
                futures.add(threadPool.submit(thread));
                threads.remove(thread);
                if (!threads.isEmpty()) {
                    thread = threads.get(0);
                } else {
                    break;
                }
            }

            // Wait for all current threads to finish
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException ignored) {
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }

            // Write image fragments to output image
            progressBar.setStatus("Processing");
            int fragPosX = RETURN_BUFFER.get(0).posX;
            int fragPosY = RETURN_BUFFER.get(0).posY;
            Dimension fragSize = RETURN_BUFFER.get(0).size;
            for (int localX = 0; localX < fragSize.width; localX++) {
                for (int localY = 0; localY < fragSize.height; localY++) {
                    DoubleColor[] colors = new DoubleColor[RETURN_BUFFER.size()];
                    for (int frameNumber = 0; frameNumber < colors.length; frameNumber++) {
                        colors[frameNumber] = RETURN_BUFFER.get(frameNumber).array[localX][localY];
                    }
                    image.setRGB(fragPosX + localX, fragPosY + localY, DoubleColor.average(colors).getRGB());
                }
            }

            // End-of-loop stuff
            currentFrameSpaceID++;
            RETURN_BUFFER.clear();
        }

        threadPool.close();
        Debug.logMsgLn("\n\n" + Util.getCurrentTime() + " Render complete");
        Debug.logElapsedTime("-> Render complete in: ", sectionStart);

        if (renderSettings.postProcessor != null) {
            sectionStart = System.nanoTime();
            Debug.logMsg("Postprocessing...");
            renderSettings.postProcessor.postProcess(image);
            Debug.logMsgLn("Done");
            Debug.logElapsedTime("-> Postprocessing complete in: ", sectionStart);
        }

        Debug.logMsgLn(Util.getCurrentTime() + " All done");
        Debug.logElapsedTime("-> Total elapsed time: ", masterStart);

        return image;
    }

    static RaycastInfo raycast(Vector origin, Vector ray, int bouncesToLive, Scene scene, RaycastInfo lastCast) {
        RaycastInfo raycast = new RaycastInfo(origin, ray);

        // Find intersected mesh and point of intersection
        for (Mesh mesh : scene.meshes) {
            RaycastInfo hitscan = mesh.getClosestIntersection(origin, ray, lastCast);
            if (hitscan.intersection != null && hitscan.distance < raycast.distance) {
                raycast = hitscan;
            }
        }

        // Recursive step
        if (raycast.intersection == null) {
            raycast.rayColor.set(scene.backgroundColor);
            return raycast;
        } else if (raycast.material.reflectivity == 0) {
            raycast.rayColor.set(DoubleColor.multiply(raycast.material.color, raycast.material.emissivity));
            return raycast;
        } else if (bouncesToLive > 0) {
            // Chose direction for next raycast
            Vector nextDir;
            RaycastInfo nextCast;
            if (false/*random.nextDouble() > raycast.material.opacity*/) {
                // Refracted direction
                // TODO: 9/4/24 Refraction
                nextDir = getRefractedDirection(raycast, lastCast);
            } else if (random.nextDouble() > raycast.material.specularity) {
                // Diffuse raycast
                nextDir = getDiffuseDirection(raycast.normal);
            } else {
                // Specular raycast
                nextDir = getSpecularDirection(raycast.direction, raycast.normal);
            }

            // Do raycast
            nextCast = raycast(raycast.intersection, nextDir, bouncesToLive - 1, scene, raycast);

            // Process raycast results and average colors
            // 1. Set color of this outgoing ray to color of incoming ray (for returning later)
            raycast.rayColor = nextCast.rayColor;
            // 2. Scale the brightness of the reflected light by the reflectivity of this material
            // 3. Tint color of this ray by the color of the material this ray is reflected from
            raycast.rayColor.multiply(DoubleColor.multiply(raycast.material.color, raycast.material.reflectivity));
            // 4. Add the color of any light emitted by the next material to the ray's color
            raycast.rayColor.add(DoubleColor.multiply(raycast.material.color, raycast.material.emissivity));
        } else {
            raycast.rayColor.set(DoubleColor.multiply(raycast.material.color, raycast.material.emissivity));
        }
        return raycast;
    }

    static synchronized void returnImageFragment(ImageFragment imageFragment) {
        RETURN_BUFFER.add(imageFragment);
    }

    public static Image quickRender(Scene scene) {
        return quickRender(scene, RenderSettings.DEFAULT_SETTINGS.size);
    }

    public static Image quickRender(Scene scene, Dimension imageSize) {
        return quickRender(scene, imageSize.width, imageSize.height);
    }

    public static Image quickRender(Scene scene, int width, int height) {
        Debug.logMsgLn("Beginning quick render");
        Image image = new Image(width, height);

        // Variable setup
        Camera camera = scene.activeCamera;

        // Vector setup
        /*
         * A ---------------- B
         * |                  |
         * |      SCREEN      |
         * |                  |
         * C ---------------- X
         * */
        Debug.logMsg("Setting up... ");
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
        Debug.logMsgLn("Done");

        // Raytracing loop
        Debug.logMsgLn("Starting render");
        long start = System.nanoTime();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Vector ray = A.copy().add(Vector.multiply(hStep, i)).add(Vector.multiply(vStep, j));
                image.setRGB(i, j, nonRecursiveRaycast(camera.pos, ray, scene).rayColor.getRGB());
            }
        }

        long end = System.nanoTime();
        Debug.logMsgLn("\nRender complete");
        Debug.logMsgLn("Elapsed time: " + TimeFormatter.timeToString(end - start));
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
            raycast.rayColor.set(DoubleColor.BLACK);
        } else {
            raycast.rayColor.set(raycast.material.color);
        }

        return raycast;
    }

    // Raycast direction methods
    private static Vector getDiffuseDirection(Vector polygonNormal) {
        Vector diffuseDir;
        do {
            diffuseDir = new Vector(random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
            if (Vector.angleBetween(diffuseDir, polygonNormal) > 90) {
                diffuseDir.multiply(-1);
            }
        } while (Vector.angleBetween(diffuseDir, polygonNormal) > 90 - MIN_REFLECTION_ANGLE);
        diffuseDir.normalize();
        return diffuseDir;
    }

    private static Vector getSpecularDirection(Vector raycastDir, Vector polygonNormal) {
        return Vector.rotate(raycastDir, polygonNormal, 180).multiply(-1);
    }

    private static Vector getRefractedDirection(RaycastInfo thisCast, RaycastInfo lastCast) {
        // Shell's Law -> n1 * sin(ϴ1) = n2 * sin(ϴ2)
        // asin(n1/n2 * sin(ϴ1)) = ϴ2
        double n1 = (lastCast == null) ? Material.DEFAULT_REFRACTIVE_INDEX : lastCast.material.refractiveIndex;
        double n2 = thisCast.material.refractiveIndex;

        Vector binormal = Vector.cross(thisCast.direction, thisCast.normal);
        Vector newNormal = Vector.multiply(thisCast.normal, -1);
        double incomingAngle = Vector.angleBetween(newNormal, thisCast.direction);
        double outgoingAngle = Util.asind(n1 / n2 * Util.sind(incomingAngle));
        return Vector.rotate(newNormal, binormal, outgoingAngle);
    }

    // Make sure user didn't screw up
    private static void ensureCorrectSettings(RenderSettings settings) {
        if (settings.scene == null) {
            Debug.logErrorMsg("Scene object is null");
            System.exit(1);
        } else {
            boolean doExit = false;
            for (Mesh mesh : settings.scene.meshes) {
                if (mesh.material == null) {
                    Debug.logErrorMsg("Material for mesh " + mesh + " is null");
                    doExit = true;
                }
            }
            if (doExit) {
                System.exit(1);
            }
        }

        if (settings.size.width <= 0 || settings.size.height <= 0) {
            Debug.logErrorMsg("Image size contains invalid dimension (is " + settings.size + " must be > 0)");
            System.exit(1);
        }

        if (settings.recursionCount < 0) {
            Debug.logErrorMsg("Recursion count (" + settings.recursionCount + ") set to negative number");
            System.exit(1);
        }

        if (settings.frameCount <= 0) {
            Debug.logErrorMsg("Frame count (" + settings.frameCount + ") must be greater than 1");
            System.exit(1);
        }

        if (settings.threadCount == 0) {
            Debug.logWarningMsg("Thread count should not be 0. Using thread count of 1 instead.");
            settings.threadCount = 1;
        }

        if (settings.sectionWidth < 1 || settings.sectionHeight < 1) {
            Debug.logErrorMsg("Section size (" + settings.sectionWidth + " x " + settings.sectionHeight +
                    ") contains invalid dimension must be > 0");
            System.exit(1);
        }
    }
}
