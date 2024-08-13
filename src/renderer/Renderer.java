package renderer;

import mesh.Mesh;
import mesh.DoubleColor;
import scene.Camera;
import mesh.Vector;
import scene.Scene;
import util.Debug;
import util.ProgressBar;
import util.Util;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class Renderer {

    private static final ArrayList<ImageFragment> RETURN_BUFFER = new ArrayList<>();

    public static final double POLYGON_ERROR = 0.01;
    public static final double SPHERE_ERROR = 0.001;
    public static final double ANGLE_ERROR = 0.001;

    private static Random random;
    public static final double DEFAULT_REFRACTIVE_INDEX = 1;

    private static final String IMAGE_FRAGMENT_NAME = "img_frag_";

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
        Scene scene = renderSettings.scene;
        int width = renderSettings.size.width;
        int height = renderSettings.size.height;
        int recursionCount = renderSettings.recursionCount;
        int frameCount = renderSettings.frameCount;
        int threadCount = renderSettings.threadCount;
        int rngSeed = renderSettings.rngSeed;

        Debug.logMsgLn("Starting renderer");

        // Variable setup
        Camera camera = scene.activeCamera;
        long start, end, masterStart, masterEnd;
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
        start = System.nanoTime();
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
        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);

        // Create threads
        start = System.nanoTime();
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
                            imageFragment, recursionCount, scene);

                    threads.add(thread);
                }
                frameSpaceID++;
                fragsPerFrame = Math.max(fragsPerFrame, frameSpaceID);
            }
        }

        // Frames
        RETURN_BUFFER.clear();
        String imageFragmentFilepath = renderSettings.holdingDir + "/" + IMAGE_FRAGMENT_NAME;
        int totalImgFragCount = threads.size();

        // Clear holding directory and create holding files
        //     THIS IS REALLY IMPORTANT!!!!!!!!!!!!
        //File holdingDir = new File(renderSettings.holdingDir);
        for (int i = 0; i < fragsPerFrame; i++) {
            File holdingFile = new File(imageFragmentFilepath + i);
            try {
                holdingFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Encountered error while creating holding file " + i);
                System.exit(1);
            }
        }

        // Sort threads however we feel like today
        threads.sort(new RaytracingThread.ThreadComparator());

        // Misc setup stuff
        RaytracingThread.setProgressBar(new ProgressBar("Threads", totalImgFragCount));

        Debug.logMsgLn("Done");
        Debug.logElapsedTime("-> Setup complete in: ", start);

        // Start render * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        Debug.logMsgLn(Util.getCurrentTime() + " Starting render:");
        Debug.logMsgLn("\tImage size -------- " + width + " x " + height);
        Debug.logMsgLn("\tFrames ------------ " + frameCount);
        Debug.logMsgLn("\tThreads ----------- " + threadCount);
        Debug.logMsgLn("\tMax recursion ----- " + recursionCount);
        Debug.logMsgLn("\tImage fragments --- " + totalImgFragCount);
        Debug.logMsgLn("\tFragments / frame - " + fragsPerFrame);

        int currentFrameSpaceID = 0;
        while (!threads.isEmpty()) {
            // Pull and submit all threads for current frame-space
            RaytracingThread thread = threads.get(0);
            while (thread.imageFragment.frameSpaceID == currentFrameSpaceID) {
                futures.add(threadPool.submit(thread));
                threads.remove(thread);
                if (!threads.isEmpty()) { thread = threads.get(0); } else { break; }
            }

            // Wait for all current threads to finish
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException ignored) {}
            }

            // Write image fragment objects to holding file in holding directory
            File holdingFile = new File(imageFragmentFilepath + currentFrameSpaceID);
            ImageFragment.writeBatchToFile(RETURN_BUFFER, holdingFile);

            // Increment counter AFTER writing to your files using counter
            // Dumbass
            currentFrameSpaceID++;

            // CLEAR RETURN BUFFER!!!!!!!!!!
            RETURN_BUFFER.clear();
        }

        threadPool.close();
        Debug.logMsgLn("\n" + Util.getCurrentTime() + " Render complete");
        Debug.logElapsedTime("-> Render complete in: ", start);

        // Build image * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        Debug.logMsg(Util.getCurrentTime() + " Building image... ");
        start = System.nanoTime();
        // Setup image object to write image fragments to
        Image image = new Image(width, height);
        for (int i = 0; i < fragsPerFrame; i++) {
            File holdingFile = new File(imageFragmentFilepath + i);
            // Read image fragments from holding directory
            ArrayList<ImageFragment> imageFragments = ImageFragment.readBatchFromFile(holdingFile);
            holdingFile.delete();
            if (!imageFragments.isEmpty()) {
                // Write image fragments to image
                int fragPosX = imageFragments.get(0).posX;
                int fragPosY = imageFragments.get(0).posY;
                Dimension fragSize = imageFragments.get(0).size;
                for (int localX = 0; localX < fragSize.width; localX++) {
                    for (int localY = 0; localY < fragSize.height; localY++) {
                        DoubleColor[] colors = new DoubleColor[imageFragments.size()];
                        for (int frameNumber = 0; frameNumber < imageFragments.size(); frameNumber++) {
                            colors[frameNumber] = imageFragments.get(frameNumber).array[localX][localY];
                        }
                        image.setRGB(fragPosX + localX, fragPosY + localY, DoubleColor.average(colors).getRGB());
                    }
                }
            }
        }

        end = System.nanoTime();
        Debug.logMsgLn("Done");
        Debug.logElapsedTime("-> Image build complete in: ", start, end);

        masterEnd = System.nanoTime();
        Debug.logMsgLn(Util.getCurrentTime() + " All done");
        Debug.logElapsedTime("-> Total elapsed time: ", masterStart, masterEnd);

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
            // Do raycast(s) to next surface(s)
            RaycastInfo diffCast, specCast, refCast;

            // Diffuse raycast
            if (raycast.material.specularity == 1) {
                diffCast = new RaycastInfo();
            } else {
                Vector diffuseDir = new Vector(random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
                if (Vector.angleBetween(diffuseDir, raycast.normal) > 90) {
                    diffuseDir.multiply(-1);
                } else if (Vector.angleBetween(diffuseDir, raycast.normal) > 90 - ANGLE_ERROR) {
                    Vector binormal = Vector.cross(diffuseDir, raycast.normal);
                    diffuseDir.rotate(binormal, Vector.angleBetween(diffuseDir, raycast.normal) / 2);
                }
                diffuseDir.normalize();
                //diffuseDir.add(raycast.normal);
                //diffuseDir.normalize();

                diffCast = raycast(raycast.intersection, diffuseDir, bouncesToLive - 1, scene, raycast);
            }

            // Specular raycast
            if (raycast.material.specularity == 0) {
                specCast = new RaycastInfo();
            } else {
                Vector specularDir = Vector.rotate(ray, raycast.normal, 180).multiply(-1);

                specCast = raycast(raycast.intersection, specularDir, bouncesToLive - 1, scene, raycast);
            }

            // Refracted raycast
            if (raycast.material.opacity == 1) {
                refCast = new RaycastInfo();
            } else {
                // Shell's Law -> n1 * sin(ϴ1) = n2 * sin(ϴ2)
                // asin(n1/n2 * sin(ϴ1)) = ϴ2
                double n1 = (lastCast == null) ? DEFAULT_REFRACTIVE_INDEX : lastCast.material.refractiveIndex;
                double n2 = raycast.material.refractiveIndex;

                Vector binormal = Vector.cross(raycast.direction, raycast.normal);
                Vector newNormal = Vector.multiply(raycast.normal, -1);
                double incomingAngle = Vector.angleBetween(newNormal, raycast.direction);
                double outgoingAngle = Util.asind(n1/n2 * Util.sind(incomingAngle));
                Vector refDir = Vector.rotate(newNormal, binormal, outgoingAngle);

                refCast = raycast(raycast.intersection, refDir, bouncesToLive - 1, scene, raycast);
            }

            // Process raycast results and average colors
            // 1. Set color of this outgoing ray to color of incoming ray (for returning later)
            // 2. Scale the brightness of the reflected light by the reflectivity of this material
            // 3. Tint color of this ray by the color of the material this ray is reflected from
            // 4. Add the color of any light emitted by the next material to the ray's color

            // Step 1
            // A. Reflected-reflected interpolation
            if (raycast.material.specularity == 0) {
                specCast.rayColor = diffCast.rayColor;
            } else if (raycast.material.specularity == 1) {
                diffCast.rayColor = specCast.rayColor;
            }
            raycast.rayColor = DoubleColor.interpolate(
                    diffCast.rayColor, specCast.rayColor, raycast.material.specularity);

            // B. Reflected-refracted interpolation
            if (raycast.material.opacity == 1) {
                refCast.rayColor = raycast.rayColor;
            }
            raycast.rayColor = DoubleColor.interpolate(refCast.rayColor, raycast.rayColor, raycast.material.opacity);

            // Steps 2 & 3
            raycast.rayColor.multiply(DoubleColor.multiply(raycast.material.color, raycast.material.reflectivity));
            // Step 4
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
        Debug.logMsgLn("Elapsed time: " + ((end - start) / 1E9) + " sec");
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
}
