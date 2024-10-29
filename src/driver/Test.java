package driver;

import mesh.*;

import java.util.Random;

public class Test {
    public static void main(String[] args) {
        for (int i = 1; i < 128; i++) {

            System.out.println("TEST WITH " + i + "  POLYGONS");
            Random random = new Random(0);
            long start, end;
            double elapsedTime;

            // Set Rays
            Vector[][] rays = new Vector[10][2];
            for (int j = 0; j < rays.length; j++) {
                for (int k = 0; k < rays[0].length; k++) {
                    rays[j][k] = new Vector(
                            random.nextDouble(-2, 2),
                            random.nextDouble(-2, 2),
                            random.nextDouble(-2, 2)
                    );
                }
            }

            // Set Polygons
            PolygonMesh polygonMesh = new PolygonMesh();
            for (int j = 0; j < i; j++) {
                polygonMesh.addPolygon(
                        new Vector(
                                random.nextDouble(-1, 1),
                                random.nextDouble(-1, 1),
                                random.nextDouble(-1, 1)
                        ),
                        new Vector(
                                random.nextDouble(-1, 1),
                                random.nextDouble(-1, 1),
                                random.nextDouble(-1, 1)
                        ),
                        new Vector(
                                random.nextDouble(-1, 1),
                                random.nextDouble(-1, 1),
                                random.nextDouble(-1, 1)
                        )
                );
            }
            polygonMesh.finalizeMesh();

            start = System.nanoTime();
            for (int j = 0; j < rays.length; j++) {
                polygonMesh.getBoundingBox().intersectedBy(rays[j][0], rays[j][1]);
            }
            end = System.nanoTime();
            elapsedTime = (end - start) * 1E-9;
            System.out.println("\tBounding Box - " + elapsedTime + " ms");

            start = System.nanoTime();
            for (int j = 0; j < rays.length; j++) {
                for (int k = 0; k < polygonMesh.polygons.length; k++) {
                    polygonMesh.polygons[k].getIntersection(rays[j][0], rays[j][1]);
                }
            }
            end = System.nanoTime();
            elapsedTime = (end - start) * 1E-9;
            System.out.println("\tPolygons ----- " + elapsedTime + " ms");
        }
    }
}
