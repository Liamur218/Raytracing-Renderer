package threading;

import java.util.ArrayList;

public class DistributedThreadPool extends ThreadPool {

    public DistributedThreadPool() {
        setName("Distributed Thread Pool");
    }

    @Override
    public void addTask(Runnable job) {

    }

    @Override
    public void executeTasks() {

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
