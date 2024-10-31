package threading;

import java.util.*;

public class LocalThreadPool extends ThreadPool {

    private final ArrayList<Runnable> workQueue;
    private final ArrayList<Worker> activeWorkers;
    private final ArrayList<Worker> idleWorkers;
    private final ArrayList<Worker> workers;
    private final int MAX_WORKERS;

    private ArrayList<Runnable> returnQueue;
    private final Object RETURN_QUEUE_SEMAPHORE;

    public LocalThreadPool(int maxWorkers) {
        workQueue = new ArrayList<>();
        activeWorkers = new ArrayList<>();
        idleWorkers = new ArrayList<>();
        workers = new ArrayList<>();

        returnQueue = new ArrayList<>();
        RETURN_QUEUE_SEMAPHORE = new Object();

        for (int i = 0; i < maxWorkers; i++) {
            Worker worker = new Worker(this);
            worker.start();
            idleWorkers.add(worker);
            workers.add(worker);
        }
        MAX_WORKERS = maxWorkers;
    }

    @Override
    public void run() {
        boolean isRunning;
        do {
            try {
                synchronized (WAKEUP_STICK) {
                    WAKEUP_STICK.wait();
                }
            } catch (InterruptedException ignored) {
            }

            while (isThereWorkToDo() && getIdleWorkerCount() > 0) {
                synchronized (this) {
                    Runnable runnable = workQueue.remove(0);
                    Worker worker = idleWorkers.remove(0);
                    activeWorkers.add(worker);
                    worker.assignWork(runnable);
                }
            }

            synchronized (START_STOP_SEMAPHORE) {
                isRunning = running;
            }
        } while (isRunning);

        for (Worker worker : workers) {
            worker.halt();
        }
    }

    @Override
    public void addJob(Runnable job) {
        synchronized (this) {
            workQueue.add(job);
        }
        synchronized (WAKEUP_STICK) {
            WAKEUP_STICK.notifyAll();
        }
    }

    @Override
    public void waitForAllToFinish() {
        while (isThereWorkToDo() || isWorkBeingDone()) {
            try {
                synchronized (WAKEUP_STICK) {
                    WAKEUP_STICK.wait();
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public ArrayList<Runnable> exportCompletedTasks() {
        ArrayList<Runnable> completedTasks;
        synchronized (RETURN_QUEUE_SEMAPHORE) {
            completedTasks = returnQueue;
            returnQueue = new ArrayList<>();
        }
        return completedTasks;
    }

    private synchronized int getIdleWorkerCount() {
        return idleWorkers.size();
    }

    private synchronized boolean isThereWorkToDo() {
        return !workQueue.isEmpty();
    }

    private synchronized boolean isWorkBeingDone() {
        return !activeWorkers.isEmpty();
    }

    void onWorkerFinish(Worker worker) {
        synchronized (RETURN_QUEUE_SEMAPHORE) {
            returnQueue.add(worker.work);
        }
        worker.setWork(null);
        synchronized (this) {
            activeWorkers.remove(worker);
            idleWorkers.add(worker);
        }
        synchronized (WAKEUP_STICK) {
            WAKEUP_STICK.notifyAll();
        }
    }
}
