package threading;

import java.util.ArrayList;

public class LocalThreadPool extends ThreadPool {

    private final ArrayList<Worker> workers, activeWorkers, idleWorkers;

    public LocalThreadPool(int maxThreadCount) {
        workers = new ArrayList<>();
        activeWorkers = new ArrayList<>();
        idleWorkers = new ArrayList<>();

        running = true;

        for (int i = 0; i < maxThreadCount; i++) {
            Worker worker = new Worker(this);
            worker.start();
            workers.add(worker);
            idleWorkers.add(worker);
        }
    }

    @Override
    public synchronized void executeTasks() {
        notifyAll();
    }

    synchronized void onWorkerFinish(Worker worker) {
        activeWorkers.remove(worker);
        idleWorkers.add(worker);
        returnQueue.add(worker.task);
        worker.task = null;
        incrementProgressBar();
        if (workQueue.isEmpty() && activeWorkers.isEmpty()) {
            synchronized (FINAL_WAKEUP_STICK) {
                FINAL_WAKEUP_STICK.notifyAll();
            }
        }
        notifyAll();
    }

    @Override
    public void waitForAllToFinish() {
        try {
            synchronized (FINAL_WAKEUP_STICK) {
                FINAL_WAKEUP_STICK.wait();
            }
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public synchronized void halt() {
        for (Worker worker : workers) {
            worker.halt();
        }
        running = false;
        notifyAll();
    }

    @Override
    public void run() {
        boolean isRunning;
        do {
            synchronized (this) {
                if (!workQueue.isEmpty() && !idleWorkers.isEmpty()) {
                    Worker worker = idleWorkers.removeFirst();
                    activeWorkers.add(worker);
                    worker.assignTask(workQueue.removeFirst());
                    synchronized (worker) {
                        worker.notifyAll();
                    }
                } else {
                    try {
                        wait();
                    } catch (InterruptedException ignored) {
                    }
                }
                isRunning = running;
            }
        } while (isRunning);
    }
}
