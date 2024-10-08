package driver;

import java.util.Random;

public class Test {
    public static void main(String[] args) {
        double[] array = new double[10000];
        double[] out = new double[array.length];
        Random random = new Random(2118);
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextDouble();
        }

        long start, end;
        start = System.nanoTime();
        for (int i = 0; i < array.length; i++) {
            out[i] = Math.sqrt(i);
        }
        end = System.nanoTime();
        System.out.println("Ave Time");
    }
}
