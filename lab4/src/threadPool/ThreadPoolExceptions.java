package threadPool;

public class ThreadPoolExceptions {
    public static class ThreadPoolException extends Exception {
        private ThreadPoolException(){}
    }

    public static class FullTasksStorage extends ThreadPoolException {
        private FullTasksStorage(){}
    }
    private static FullTasksStorage fullTasksStorage = null;
    public static FullTasksStorage fullTasksStorage(){
        if (fullTasksStorage == null)
            fullTasksStorage = new FullTasksStorage();
        return fullTasksStorage;
    }

    public static class NotStarted extends ThreadPoolException {
        private NotStarted(){}
    }
    private static NotStarted notStarted = null;
    public static NotStarted notStarted(){
        if (notStarted == null)
            notStarted = new NotStarted();
        return notStarted;
    }

    public static class AlreadyStarted extends ThreadPoolException {
        private AlreadyStarted(){}
    }
    private static AlreadyStarted alreadyStarted = null;
    public static AlreadyStarted alreadyStarted(){
        if (alreadyStarted == null)
            alreadyStarted = new AlreadyStarted();
        return alreadyStarted;
    }

    public static class Terminated extends ThreadPoolException {
        private Terminated(){}
    }
    private static Terminated terminated = null;
    public static Terminated terminated(){
        if (terminated == null)
            terminated = new Terminated();
        return terminated;
    }
}
