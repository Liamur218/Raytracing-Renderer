package util;

public class ProgressBar {
    
    long progress, max;
    long lastUpdateTime;

    boolean autoPrint = true;
    String title, status, msg;

    public ProgressBar(int max) {
        this("", max);
    }

    public ProgressBar(String name, long max) {
        title = (name.isBlank()) ? name : name + ": ";
        this.max = max;
        msg = "";
        lastUpdateTime = System.nanoTime();
    }

    public synchronized void increment(long amount) {
        long time = System.nanoTime();
        progress += amount;
        if (autoPrint) {
            for (int i = 0; i < msg.length(); i++) { Debug.printMsg("\b"); }
            status = progress + " / " + max;
            msg = title + status;
            Debug.printMsg(msg);
        }
        lastUpdateTime = time;
    }
}
