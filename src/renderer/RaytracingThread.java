package renderer;

import mesh.Vector;
import mesh.*;
import scene.Scene;
import util.*;

import java.io.Serializable;
import java.util.*;

public class RaytracingThread implements Runnable, Serializable {

    Vector origin, startDir;
    Vector hStep, vStep;
    int recursionCount;
    Scene scene;
    ImageFragment imageFragment;

    long startTime, stopTime;

    private final Random random;

    long elapsedTime;

    int id;
    static int idCounter = 0;

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

        imageFragment.initialize();
        for (int i = 0; i < imageFragment.size.width; i++) {
            for (int j = 0; j < imageFragment.size.height; j++) {
                Vector ray = new Vector(startDir);
                ray.add(Vector.multiply(hStep, i)).add(Vector.multiply(vStep, j)).normalize();
                NormColor color = raycast(origin, ray, recursionCount, scene, null).rayColor;
                imageFragment.setRGB(i, j, color);
            }
        }

        Renderer.returnImageFragment(imageFragment);

        stopTime = System.nanoTime();
        elapsedTime = stopTime - startTime;
    }

    public RaycastInfo raycast(Vector origin, Vector ray, int bouncesToLive, Scene scene, RaycastInfo lastCast) {
        // Create raycast info reference for later use (instantiate it to avoid NullPointerException)
        RaycastInfo raycast = new RaycastInfo(origin, ray);

        // Find intersected mesh and point of intersection
        for (Mesh mesh : scene.meshes) {
            RaycastInfo hitscan = mesh.getClosestIntersection(origin, ray, lastCast);
            if (hitscan.intersection != null && hitscan.distance < raycast.distance) {
                raycast = hitscan;
            }
        }

        // Create new mesh stack for first raycast and fill it with air, or
        // grab stack of previous materials from the last raycast we just did
        if (lastCast == null) {
            raycast.meshStack = new MeshStack();
            raycast.meshStack.push(Mesh.NULL_MESH);
        } else {
            raycast.meshStack = lastCast.meshStack;
        }

        // Recursive step
        if (raycast.intersection == null) {
            // We didn't hit anything: get environment lighting data and return
            if (lastCast == null) {
                raycast.rayColor.set(scene.enviroColor);
            } else {
                /*
                 * The method of doing this taken from Sebastian Lague's code (thank you, Sebastian!)
                 * float sun = pow(max(0, dot(ray.dir, _WorldSpaceLightPos0.xyz)), SunFocus) * SunIntensity;
                 * float3 composite = lerp(GroundColour, skyGradient, groundToSkyT) + sun * (groundToSkyT>=1);
                 * */
                float directionalIntensity = (float) (Math.max(0, Vector.dot(ray, scene.lightSourceDir)) *
                        scene.ambientLightIntensity);
                raycast.rayColor.set(NormColor.multiply(scene.ambientLightColor, directionalIntensity));
            }
            return raycast;
        } else if (raycast.material.reflectivity == 0) {
            // We hit a non-reflective surface: any raycasts after this one don't matter
            raycast.rayColor.set(NormColor.multiply(raycast.material.color, raycast.material.emissivity));
            return raycast;
        } else if (bouncesToLive > 0) {
            // We hit something: chose direction for next raycast
            Vector nextDir;
            RaycastInfo nextCast;
            if (random.nextDouble() > raycast.material.opacity) {
                Material currentMedium;
                Material nextMedium;
                Vector normal;
                if (Vector.angleBetween(raycast.direction, raycast.normal) >= 90) {  // Entering material
                    currentMedium = raycast.meshStack.getLast().material;
                    nextMedium = raycast.material;
                    normal = raycast.normal;
                } else {  // Exiting material
                    currentMedium = raycast.meshStack.removeFromEnd(raycast.mesh).material;
                    nextMedium = raycast.meshStack.getLast().material;
                    normal = Vector.multiply(raycast.normal, -1);
                }
                nextDir = getRefractedDirection(raycast.direction, normal, currentMedium, nextMedium);
            } else if (random.nextDouble() > raycast.material.specularity) {
                nextDir = getDiffuseDirection(raycast.normal);
            } else {
                nextDir = getSpecularDirection(raycast.direction, raycast.normal);
            }

            // Do raycast
            nextCast = raycast(raycast.intersection, nextDir, bouncesToLive - 1, scene, raycast);

            // Process raycast results and average colors
            // 1. Set the color of this outgoing ray to the color of the incoming ray (for returning later)
            raycast.rayColor = nextCast.rayColor;
            // 2. Scale the brightness of the reflected light by the reflectivity of this material
            // 3. Tint color of this ray by the color of the material this ray is reflected from
            raycast.rayColor.multiply(NormColor.multiply(raycast.material.color, raycast.material.reflectivity));
            // 4. Add the color of any light emitted by the next material to the ray's color
            raycast.rayColor.add(NormColor.multiply(raycast.material.color, raycast.material.emissivity));
        } else {
            raycast.rayColor.set(NormColor.multiply(raycast.material.color, raycast.material.emissivity));
        }
        return raycast;
    }

    private Vector getDiffuseDirection(Vector polygonNormal) {
        Vector diffuseDir = new Vector(random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
        if (Vector.angleBetween(diffuseDir, polygonNormal) > 90) {
            diffuseDir.multiply(-1);
        }
        if (Vector.angleBetween(diffuseDir, polygonNormal) > 90 - Renderer.MIN_REFLECTION_ANGLE) {
            diffuseDir.rotate(Vector.cross(diffuseDir, polygonNormal),
                    random.nextDouble(Renderer.MIN_REFLECTION_ANGLE, 90));
        }
        return diffuseDir.normalize();
    }

    private Vector getSpecularDirection(Vector raycastDir, Vector polygonNormal) {
        return Vector.rotate(raycastDir, polygonNormal, 180).multiply(-1);
    }

    private Vector getRefractedDirection(Vector dir, Vector norm, Material currentMedium, Material nextMedium) {
        // Snell's Law -> n1 * sin(ϴ1) = n2 * sin(ϴ2)
        // asin(n1/n2 * sin(ϴ1)) = ϴ2
        float n1 = currentMedium.refractiveIndex;
        float n2 = nextMedium.refractiveIndex;

        Vector binormal = Vector.cross(dir, norm).normalize();
        Vector newNormal = Vector.multiply(norm, -1);
        float incomingAngle = (float) Vector.angleBetween(newNormal, dir);
        float outgoingAngle = Util.asind( n1 / n2 * Util.sind(incomingAngle));
        return Vector.rotate(newNormal, binormal, outgoingAngle);
    }

    public static int getTotalCreatedThreads() {
        return idCounter;
    }

    static class ThreadComparator implements Comparator<RaytracingThread> {
        @Override
        public int compare(RaytracingThread thread1, RaytracingThread thread2) {
            return thread1.imageFragment.frameSpaceID - thread2.imageFragment.frameSpaceID;
        }
    }
}
