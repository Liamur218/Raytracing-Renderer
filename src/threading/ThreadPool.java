package threading;

import java.util.*;

public abstract class ThreadPool extends Thread {

    protected boolean running;
    protected final Object START_STOP_SEMAPHORE;

    protected final Object WAKEUP_STICK;

    protected ThreadPool() {
        running = true;
        START_STOP_SEMAPHORE = new Object();

        WAKEUP_STICK = new Object();
    }

    public abstract void addJob(Runnable job);

    public abstract void waitForAllToFinish();

    public abstract ArrayList<Runnable> exportCompletedTasks();

    public void halt() {
        synchronized (START_STOP_SEMAPHORE) {
            running = false;
        }
        synchronized (WAKEUP_STICK) {
            WAKEUP_STICK.notifyAll();
        }
    }
}
