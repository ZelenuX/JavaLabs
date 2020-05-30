package threadPool;

import java.security.InvalidParameterException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class FixedSizeThreadPool implements ThreadPool {
    private Thread threads[];
    private Queue<Runnable> tasksQueue;
    private boolean isStarted = false;
    private Boolean isTerminated = false;

    public FixedSizeThreadPool(int numberOfThreads, int maxNumberOfTasks) throws InvalidParameterException {
        if (numberOfThreads <= 0 || maxNumberOfTasks <= 0)
            throw new InvalidParameterException();
        tasksQueue = new ArrayBlockingQueue<Runnable>(maxNumberOfTasks);
        threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; ++i) {
            threads[i] = new Thread(new ThreadTask(tasksQueue, isTerminated));
            threads[i].setDaemon(true);
        }
    }

    @Override
    public void addTask(Runnable task) throws ThreadPoolExceptions.FullTasksStorage,
            ThreadPoolExceptions.NotStarted, ThreadPoolExceptions.Terminated, NullPointerException {
        if (isTerminated)
            throw ThreadPoolExceptions.terminated();
        if (!isStarted)
            throw ThreadPoolExceptions.notStarted();
        if (task == null)
            throw new NullPointerException();
        synchronized (tasksQueue) {
            try {
                tasksQueue.add(task);
            } catch (IllegalStateException e) {
                throw ThreadPoolExceptions.fullTasksStorage();
            }
            for (Thread cur : threads){
                if (cur.getState() == Thread.State.WAITING)
                    continue;
                if (cur.getState() == Thread.State.NEW){
                    cur.start();
                    break;
                }
                break;
            }
            tasksQueue.notifyAll();
        }
    }
    @Override
    public void start() throws ThreadPoolExceptions.AlreadyStarted, ThreadPoolExceptions.Terminated {
        if (isStarted)
            throw ThreadPoolExceptions.alreadyStarted();
        if (isTerminated)
            throw ThreadPoolExceptions.terminated();
        threads[0].start();
        isStarted = true;
    }
    @Override
    public void stopAfterCurrentTasks() throws ThreadPoolExceptions.Terminated {
        if (isTerminated)
            throw ThreadPoolExceptions.terminated();
        isTerminated = true;
        isStarted = false;
        for (Thread cur : threads)
            cur.interrupt();
    }
    @Override
    public void stopAfterCompletingTasks() throws ThreadPoolExceptions.Terminated {
        if (isTerminated)
            throw ThreadPoolExceptions.terminated();
        isTerminated = true;
        isStarted = false;
        synchronized (tasksQueue){
            if (!tasksQueue.isEmpty())
                try {
                    tasksQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            while (!tasksQueue.isEmpty()){
                tasksQueue.notifyAll();
                try {
                    tasksQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Thread cur : threads)
            cur.interrupt();
    }
}

/*
public class FixedSizeThreadPool implements ThreadPool {
    private Thread threads[];
    private Queue<Runnable> tasksQueue;
    private boolean isStarted = false;
    private Boolean isTerminated = false;

    public FixedSizeThreadPool(int numberOfThreads, int maxNumberOfTasks) throws InvalidParameterException {
        if (numberOfThreads <= 0 || maxNumberOfTasks <= 0)
            throw new InvalidParameterException();
        tasksQueue = new ArrayBlockingQueue<Runnable>(maxNumberOfTasks);
        threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; ++i) {
            threads[i] = new Thread(new ThreadTask(tasksQueue, isTerminated));
            threads[i].setDaemon(true);
        }
    }

    @Override
    public void addTask(Runnable task) throws ThreadPoolExceptions.FullTasksStorage,
            ThreadPoolExceptions.NotStarted, ThreadPoolExceptions.Terminated, NullPointerException {
        if (isTerminated)
            throw ThreadPoolExceptions.terminated();
        if (!isStarted)
            throw ThreadPoolExceptions.notStarted();
        if (task == null)
            throw new NullPointerException();
        synchronized (tasksQueue) {
            try {
                tasksQueue.add(task);
            } catch (IllegalStateException e) {
                throw ThreadPoolExceptions.fullTasksStorage();
            }
            tasksQueue.notifyAll();
        }
    }
    @Override
    public void start() throws ThreadPoolExceptions.AlreadyStarted, ThreadPoolExceptions.Terminated {
        if (isStarted)
            throw ThreadPoolExceptions.alreadyStarted();
        if (isTerminated)
            throw ThreadPoolExceptions.terminated();
        for (Thread cur : threads)
            cur.start();
        isStarted = true;
    }
    @Override
    public void stopAfterCurrentTasks() throws ThreadPoolExceptions.Terminated {
        if (isTerminated)
            throw ThreadPoolExceptions.terminated();
        isTerminated = true;
        isStarted = false;
        for (Thread cur : threads)
            cur.interrupt();
    }
    @Override
    public void stopAfterCompletingTasks() throws ThreadPoolExceptions.Terminated {
        if (isTerminated)
            throw ThreadPoolExceptions.terminated();
        isTerminated = true;
        isStarted = false;
        synchronized (tasksQueue){
            if (!tasksQueue.isEmpty())
                try {
                    tasksQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            while (!tasksQueue.isEmpty()){
                tasksQueue.notifyAll();
                try {
                    tasksQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Thread cur : threads)
            cur.interrupt();
    }
}
*/

class ThreadTask implements Runnable{
    private Queue<Runnable> tasksQueue;
    private Boolean stopAfterCompletingQueue;

    public ThreadTask(Queue<Runnable> tasksQueue, Boolean stopAfterCompletingQueue) throws NullPointerException {
        if (tasksQueue == null || stopAfterCompletingQueue == null)
            throw new NullPointerException();
        this.tasksQueue = tasksQueue;
        this.stopAfterCompletingQueue = stopAfterCompletingQueue;
    }

    @Override
    public void run() {
        Runnable curTask;
        while (!Thread.interrupted()){
            synchronized (tasksQueue) {
                if (tasksQueue.isEmpty()) {
                    if (stopAfterCompletingQueue) {
                        Thread.currentThread().interrupt();
                        tasksQueue.notifyAll();
                    }
                    else {
                        try {
                            tasksQueue.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                if (tasksQueue.isEmpty()) {
                    if (stopAfterCompletingQueue) {
                        Thread.currentThread().interrupt();
                        tasksQueue.notifyAll();
                    }
                    continue;
                }
                else{
                    if (!stopAfterCompletingQueue && Thread.currentThread().isInterrupted())
                        break;
                }
                curTask = tasksQueue.poll();
                tasksQueue.notifyAll();
            }
            curTask.run();
        }
    }

    private void threadMessage(Object message){
        System.out.println("" + Thread.currentThread().getId() + ": " + message);
    }
}
