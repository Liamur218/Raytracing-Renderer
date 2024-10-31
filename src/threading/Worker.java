package threading;

public class Worker extends Thread {

    Runnable work;
    private final LocalThreadPool owner;

    boolean isRunning;

    final int id;
    private static int ID_COUNTER = 0;

    public Worker(LocalThreadPool owner) {
        this.owner = owner;
        isRunning = true;
        id = ID_COUNTER++;
    }

    public void assignWork(Runnable job) {
        work = job;
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public void run() {
        while (isWorkerRunning()) {
            if (work == null) {
                try {
                    synchronized (this) {
                        wait();
                    }
                } catch (InterruptedException ignored) {
                }
            } else {
                work.run();
                owner.onWorkerFinish(this);
            }
        }
    }

    private synchronized boolean isWorkerRunning() {
        return isRunning;
    }

    synchronized void halt() {
        isRunning = false;
        notifyAll();
    }

    void setWork(Runnable runnable) {
        work = runnable;
    }

    @Override
    public String toString() {
        return "worker " + id;
    }
}
