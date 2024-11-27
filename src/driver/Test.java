package driver;

import threading.LocalThreadPool;

import java.util.concurrent.TimeUnit;


public class Test {
    public static void main(String[] args) {
        LocalThreadPool threadPool = new LocalThreadPool(100);
        threadPool.start();
        for (int i = 0; i < 10000; i++) {
            threadPool.addJob(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                    System.out.println("Done");
                }
            });
        }
        threadPool.waitForAllToFinish();
    }
}
