package threading;

import util.ProgressBar;

import java.util.ArrayList;

public abstract class ThreadPool extends Thread {

    protected ArrayList<Runnable> workQueue, returnQueue;

    protected boolean running;

    protected ProgressBar progressBar;

    protected final Object FINAL_WAKEUP_STICK;

    protected ThreadPool() {
        FINAL_WAKEUP_STICK = new Object();
        workQueue = new ArrayList<>();
        returnQueue = new ArrayList<>();
    }

    public synchronized void addTask(Runnable task) {
        workQueue.add(task);
    }

    abstract public void executeTasks();

    abstract public void waitForAllToFinish();

    protected void incrementProgressBar() {
        if (progressBar != null) { progressBar.increment(); }
    }

    public synchronized ArrayList<Runnable> exportCompletedTasks() {
        ArrayList<Runnable> out = returnQueue;
        returnQueue = new ArrayList<>();
        return out;
    }

    abstract public void halt();

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }
}
