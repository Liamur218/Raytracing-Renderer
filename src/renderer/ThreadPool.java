package renderer;

import java.util.*;
import java.util.concurrent.*;

public class ThreadPool {

    private ExecutorService threadPool;
    private HashMap<Runnable, Future<?>> futuresMap;

    private final boolean distributed;

    public ThreadPool(int maxThreadCount, boolean distributed) {
        this.distributed = distributed;
        if (distributed) {

        } else {
            threadPool = (maxThreadCount > 0) ?
                    Executors.newFixedThreadPool(maxThreadCount) : Executors.newCachedThreadPool();
            futuresMap = new HashMap();
        }
    }

    public void execute(Runnable runnable) {
        if (distributed) {

        } else {
            addToMap(runnable, threadPool.submit(runnable));
        }
    }

    public void waitToFinish(Runnable runnable) {
        if (distributed) {

        } else {
            try {
                getFromMap(runnable).get();
                removeFromMap(runnable);
            } catch (InterruptedException ignored) {
            } catch (ExecutionException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public void waitForAllToFinish() {
        if (distributed) {

        } else {
            for (Runnable runnable : getAllActiveThreads()) {
                waitToFinish(runnable);
            }
        }
    }

    public void commitDie() {
        if (distributed) {

        } else {
            threadPool.close();
        }
    }

    private synchronized void addToMap(Runnable thread, Future<?> future) {
        futuresMap.put(thread, future);
    }

    private synchronized Future<?> getFromMap(Runnable thread) {
        return futuresMap.get(thread);
    }

    private synchronized void removeFromMap(Runnable thread) {
        futuresMap.remove(thread);
    }

    private synchronized Runnable[] getAllActiveThreads() {
        return futuresMap.keySet().toArray(new Runnable[0]);
    }
}
