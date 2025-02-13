package threading;

public class Worker extends Thread {

    Runnable task;
    private final ThreadPool owner;

    private boolean running;

    public Worker(ThreadPool owner) {
        this.owner = owner;
        running = true;
    }

    public void assignTask(Runnable task) {
        this.task = task;
    }

    public synchronized void halt() {
        running = false;
        notifyAll();
    }

    @Override
    public void run() {
        boolean isRunning;
        do {
            if (task == null) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            } else {
                task.run();
                owner.onWorkerFinish(this);
            }

            synchronized (this) {
                isRunning = running;
            }
        } while (isRunning);
    }
}
