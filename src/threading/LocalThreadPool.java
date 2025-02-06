package threading;

import java.util.ArrayList;

public class LocalThreadPool extends ThreadPool {

    private final ArrayList<Runnable> workQueue;
    private final ArrayList<Worker> activeWorkers;
    private final ArrayList<Worker> idleWorkers;
    private final ArrayList<Worker> workers;

    private ArrayList<Runnable> returnQueue;

    public LocalThreadPool(int maxWorkers) {
        workQueue = new ArrayList<>();
        activeWorkers = new ArrayList<>();
        idleWorkers = new ArrayList<>();
        workers = new ArrayList<>();

        returnQueue = new ArrayList<>();

        for (int i = 0; i < maxWorkers; i++) {
            Worker worker = new Worker(this);
            worker.start();
            idleWorkers.add(worker);
            workers.add(worker);
        }

        setName("Local Thread Pool");
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
    public synchronized void addTask(Runnable job) {
        workQueue.add(job);
    }

    @Override
    public void executeTasks() {
        synchronized (WAKEUP_STICK) {
            WAKEUP_STICK.notifyAll();
        }
    }

    @Override
    public synchronized ArrayList<Runnable> exportCompletedTasks() {
        ArrayList<Runnable> completedTasks;
        completedTasks = returnQueue;
        returnQueue = new ArrayList<>();
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

    @Override
    protected synchronized boolean isAllWorkDone() {
        return workQueue.isEmpty() && activeWorkers.isEmpty();
    }

    synchronized void onWorkerFinish(Worker worker) {
        returnQueue.add(worker.work);
        worker.work = null;
        activeWorkers.remove(worker);
        idleWorkers.add(worker);

        if (progressBar != null) {
            progressBar.increment();
        }

        synchronized (WAKEUP_STICK) {
            WAKEUP_STICK.notifyAll();
        }
        if (isAllWorkDone()) {
            synchronized (FINAL_WAKEUP_STICK) {
                FINAL_WAKEUP_STICK.notifyAll();
            }
        }
    }
}
