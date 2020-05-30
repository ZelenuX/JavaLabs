package factory;

import factory.resultsAndComponents.Storable;

import java.io.PrintStream;
import java.security.InvalidParameterException;
import java.util.Timer;
import java.util.TimerTask;

public class Dealer {
    private static int dealerCounter = 0;
    private int dealerNumber = dealerCounter++;
    private final Timer timer;
    private int timerInterval;
    private TimerTask task;
    private Storage storage;
    private Storable exampleObject;
    private class MyTimerTask extends TimerTask {
        PrintStream logOutput;
        public MyTimerTask(PrintStream logOutput){
            this.logOutput = logOutput;
        }
        @Override
        public void run() {
            Storable tmp;
            try {
                tmp = storage.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            if (logOutput != null)
                logOutput.println(getTime() + ": Dealer " + dealerNumber + ": " + tmp.getFullInfo());
            synchronized (storage){
                storage.notifyAll();
            }
        }
    };
    private String getTime(){
        int ms = (int)(System.currentTimeMillis() % 86400000);
        int s = ms / 1000;
        int m = s / 60;
        int h = m / 60;
        s %= 60;
        m %= 60;
        h %= 24;
        h += 7;
        return "" + h + ":" + m + ":" + s;
    }
    private PrintStream logOutput;

    public Dealer(Storage storage, int msInterval, Storable exampleObject, PrintStream logOutput) throws InvalidParameterException{
        if (storage == null || msInterval <= 0)
            throw new InvalidParameterException();
        this.logOutput = logOutput;
        timer = new Timer(true);
        this.storage = storage;
        this.exampleObject = exampleObject;
        task = new MyTimerTask(logOutput);
        timer.scheduleAtFixedRate(task, 0, msInterval);
        timerInterval = msInterval;
    }
    public void terminate(){
        timer.cancel();
    }
    public void setInterval(int msInterval) throws InvalidParameterException {
        if (msInterval <= 0)
            throw new InvalidParameterException();
        if (msInterval == timerInterval)
            return;
        task.cancel();
        task = new MyTimerTask(logOutput);
        timer.purge();
        timer.scheduleAtFixedRate(task, msInterval, msInterval);
        timerInterval = msInterval;
    }
    public int getInterval(){
        return timerInterval;
    }
}
