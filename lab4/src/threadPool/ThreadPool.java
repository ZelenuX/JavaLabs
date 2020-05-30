package threadPool;

public interface ThreadPool {
    void start() throws ThreadPoolExceptions.AlreadyStarted, ThreadPoolExceptions.Terminated;
    /** adding tasks only allowed after starting pull (otherwise ThreadPoolExceptions.NotStarted is thrown) */
    void addTask(Runnable task) throws ThreadPoolExceptions.FullTasksStorage,
            ThreadPoolExceptions.NotStarted, ThreadPoolExceptions.Terminated, NullPointerException;
    void stopAfterCurrentTasks() throws ThreadPoolExceptions.Terminated;
    void stopAfterCompletingTasks() throws ThreadPoolExceptions.Terminated;
}
