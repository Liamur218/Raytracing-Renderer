package threading;

import java.util.ArrayList;

public class DistributedThreadPool extends ThreadPool {

    @Override
    public void addTask(Runnable job) {

    }

    @Override
    public void executeTasks() {

    }

    @Override
    public void executeTask(Runnable job) {

    }

    @Override
    protected boolean isAllWorkDone() {
        return false;
    }

    @Override
    public ArrayList<Runnable> exportCompletedTasks() {
        return null;
    }

    @Override
    public void halt() {

    }

    @Override
    public void run() {

    }
}
