package util;

public class ProgressBar {
    
    long current, max;
    long lastUpdateTime;

    String title, progress, msg;
    String status;

    public ProgressBar(int max) {
        this("", max);
    }

    public ProgressBar(String name, long max) {
        title = (name.isBlank()) ? name : name + ": ";
        this.max = max;
        msg = "";
        lastUpdateTime = System.nanoTime();
        status = "";
    }

    public synchronized void setStatus(String status) {
        this.status = status;
        print();
    }

    public synchronized void increment(long amount) {
        long time = System.nanoTime();
        current += amount;
        print();
        lastUpdateTime = time;
    }

    private void print() {
        for (int i = 0; i < msg.length(); i++) { Debug.printMsg("\b"); }
        progress = current + " / " + max + " (" + (int) (((double) current * 100) / max) + "%) - Status: " + status;
        msg = title + progress;
        Debug.printMsg(msg);
    }
}
