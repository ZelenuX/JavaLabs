package factory;

import threadPool.ThreadPool;
import threadPool.ThreadPoolExceptions;

public class AutoStorageController {
    Thread controllerThread;

    public AutoStorageController(Factory factory, Storage autoStorage, ThreadPool workers){
        controllerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (autoStorage){
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            autoStorage.wait(100);
                        } catch (InterruptedException e) {
                            return;
                        }
                        try {
                            for (int i = autoStorage.remainCapacity(); i > 0; --i)
                                workers.addTask(factory.workerTask);
                        } catch (ThreadPoolExceptions.FullTasksStorage ignored) {
                        } catch (ThreadPoolExceptions.ThreadPoolException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        controllerThread.start();
    }
    public void terminate(){
        controllerThread.interrupt();
    }
}
