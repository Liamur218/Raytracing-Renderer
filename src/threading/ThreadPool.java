package threading;

import util.ProgressBar;

import java.util.ArrayList;

public abstract class ThreadPool extends Thread {

    public static final int PORT = 11347;

    protected ArrayList<Runnable> workQueue, returnQueue;

    protected boolean running;

    protected ProgressBar progressBar;

    abstract void addTask(Runnable task);

    abstract void executeTasks();

    abstract void waitForAllToFinish();

    public synchronized ArrayList<Runnable> exportCompletedTasks() {
        ArrayList<Runnable> out = returnQueue;
        returnQueue = new ArrayList<>();
        return out;
    }

    abstract void halt();

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }
}
