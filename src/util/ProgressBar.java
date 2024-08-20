package util;

public class ProgressBar {
    
    long current, max;
    long startTime, lastUpdateTime;

    String title, progress, msg;
    String status;

    public ProgressBar(int max) {
        this("", max);
    }

    public ProgressBar(String name, long max) {
        title = (name.isBlank()) ? name : name + ": ";
        this.max = max;
        msg = "";
        status = "";
        startTime = System.nanoTime();
        lastUpdateTime = startTime;
    }

    public synchronized void setStatus(String status) {
        this.status = status;
        print();
    }

    public synchronized void increment(long amount) {
        long time = System.nanoTime();
        current += amount;
        lastUpdateTime = time;
        print();
    }

    private void print() {
        for (int i = 0; i < msg.length(); i++) { Debug.printMsg("\b"); }
        double percent = ((double) current / max);
        long estTotalTime = (percent == 0) ? -1 : (long) ((lastUpdateTime - startTime) / percent);
        long estTimeRemaining = estTotalTime - (lastUpdateTime - startTime);
        progress = current + " / " + max + " (" + (int) (percent * 100) + "%) - Status: " + status +
                " - ETR: " + TimeFormatter.timeToString(estTimeRemaining, TimeFormatter.SECOND);
        msg = title + progress;
        Debug.printMsg(msg);
    }
}
