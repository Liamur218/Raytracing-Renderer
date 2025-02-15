package threading;

import util.ProgressBar;

import java.util.ArrayList;

public class ThreadPool extends Thread {

    private final ArrayList<Worker> workers, activeWorkers, idleWorkers;
    private final ArrayList<Runnable> workQueue;
    private ArrayList<Runnable> returnQueue;

    private final Object FINAL_WAKEUP_STICK;

    private boolean running;

    private ProgressBar progressBar;

    public ThreadPool(int maxThreadCount) {
        workers = new ArrayList<>();
        activeWorkers = new ArrayList<>();
        idleWorkers = new ArrayList<>();

        workQueue = new ArrayList<>();
        returnQueue = new ArrayList<>();

        FINAL_WAKEUP_STICK = new Object();

        running = true;

        for (int i = 0; i < maxThreadCount; i++) {
            Worker worker = new Worker(this);
            worker.start();
            workers.add(worker);
            idleWorkers.add(worker);
        }
    }

    public synchronized void addTask(Runnable task) {
        workQueue.add(task);
    }

    public synchronized void executeTasks() {
        notifyAll();
    }

    synchronized void onWorkerFinish(Worker worker) {
        activeWorkers.remove(worker);
        idleWorkers.add(worker);
        returnQueue.add(worker.task);
        worker.task = null;
        if (progressBar != null) {
            progressBar.increment();
        }
        if (workQueue.isEmpty() && activeWorkers.isEmpty()) {
            synchronized (FINAL_WAKEUP_STICK) {
                FINAL_WAKEUP_STICK.notifyAll();
            }
        }
        notifyAll();
    }

    public void waitForAllToFinish() {
        try {
            synchronized (FINAL_WAKEUP_STICK) {
                FINAL_WAKEUP_STICK.wait();
            }
        } catch (InterruptedException ignored) {
        }
    }

    public synchronized ArrayList<Runnable> exportCompletedTasks() {
        ArrayList<Runnable> out = returnQueue;
        returnQueue = new ArrayList<>();
        return out;
    }

    public synchronized void halt() {
        for (Worker worker : workers) {
            worker.halt();
        }
        running = false;
        notifyAll();
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void run() {
        boolean isRunning;
        do {
            synchronized (this) {
                if (!workQueue.isEmpty() && !idleWorkers.isEmpty()) {
                    Worker worker = idleWorkers.remove(0);
                    activeWorkers.add(worker);
                    worker.assignTask(workQueue.remove(0));
                    synchronized (worker) {
                        worker.notifyAll();
                    }
                } else {
                    try {
                        wait();
                    } catch (InterruptedException ignored) {
                    }
//                    if (workQueue.isEmpty() && activeWorkers.isEmpty()) {
//                        synchronized (FINAL_WAKEUP_STICK) {
//                            FINAL_WAKEUP_STICK.notifyAll();
//                        }
//                    }
                }

                isRunning = running;
            }
        } while (isRunning);
    }
}
