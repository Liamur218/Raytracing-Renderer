package threading;

import util.ProgressBar;

import java.util.*;

public abstract class ThreadPool extends Thread {

    protected boolean running;
    protected final Object START_STOP_SEMAPHORE;

    protected final Object WAKEUP_STICK;
    protected final Object FINAL_WAKEUP_STICK;

    ProgressBar progressBar;

    protected ThreadPool() {
        running = true;
        START_STOP_SEMAPHORE = new Object();

        WAKEUP_STICK = new Object();

        FINAL_WAKEUP_STICK = new Object();

        setName("Thread Pool");
    }

    public abstract void addTask(Runnable job);
    public abstract void executeTasks();

    protected abstract boolean isAllWorkDone();

    public void waitForAllToFinish() {
        if (!isAllWorkDone()) {
            try {
                synchronized (FINAL_WAKEUP_STICK) {
                    FINAL_WAKEUP_STICK.wait();
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    public abstract ArrayList<Runnable> exportCompletedTasks();

    public void halt() {
        synchronized (START_STOP_SEMAPHORE) {
            running = false;
        }
        synchronized (WAKEUP_STICK) {
            WAKEUP_STICK.notifyAll();
        }
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }
}
