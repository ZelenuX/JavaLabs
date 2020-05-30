package factory;

import factory.resultsAndComponents.Accessory;
import factory.resultsAndComponents.Auto;
import factory.resultsAndComponents.Casing;
import factory.resultsAndComponents.Motor;
import threadPool.FixedSizeThreadPool;
import threadPool.ThreadPool;
import threadPool.ThreadPoolExceptions;

import java.io.PrintStream;
import java.security.InvalidParameterException;

public class Factory {
    private Supplier casingSupplier, motorSupplier;
    private Supplier[] accessorySuppliers;
    private final Storage casings, motors, accessories, autos;
    private Dealer[] dealers;
    private final ThreadPool workers;
    private AutoStorageController autoStorageController;
    final int casingStorageSize, motorStorageSize, accessoryStorageSize, autoStorageSize;
    final int numberOfAccessorySuppliers, numberOfWorkers, numberOfDealers;
    final static int defaultInterval = 100;
    private boolean isStarted = false;
    private boolean isFinished = false;
    final Runnable workerTask = new Runnable() {
        @Override
        public void run() {
            try {
                autos.put(new Auto((Casing)casings.get(), (Motor)motors.get(), (Accessory)accessories.get()));
            } catch (InterruptedException ignored) {
                return;
            }
            synchronized (numberOfProducedAutos){
                ++numberOfProducedAutos;
            }
        }
    };
    private Integer numberOfProducedAutos = 0;
    PrintStream logOutput;

    public Factory(int casingStorageSize, int motorStorageSize, int accessoryStorageSize, int autoStorageSize,
                   int numberOfAccessorySuppliers, int numberOfWorkers, int numberOfDealers, PrintStream logOutput) {
        this.logOutput = logOutput;
        casings = new FixedSizeStorage(casingStorageSize);
        motors = new FixedSizeStorage(motorStorageSize);
        accessories = new FixedSizeStorage(accessoryStorageSize);
        autos = new FixedSizeStorage(autoStorageSize);
        workers = new FixedSizeThreadPool(numberOfWorkers, numberOfWorkers * 2);
        accessorySuppliers = new Supplier[numberOfAccessorySuppliers];
        dealers = new Dealer[numberOfDealers];
        this.casingStorageSize = casingStorageSize;
        this.motorStorageSize = motorStorageSize;
        this.accessoryStorageSize = accessoryStorageSize;
        this.autoStorageSize = autoStorageSize;
        this.numberOfAccessorySuppliers = numberOfAccessorySuppliers;
        this.numberOfWorkers = numberOfWorkers;
        this.numberOfDealers = numberOfDealers;
    }

    public void start() throws IllegalStateException {
        if (isStarted || isFinished)
            throw new IllegalStateException();
        for (int i = 0; i < numberOfDealers; ++i)
            dealers[i] = new Dealer(autos, defaultInterval, new Auto(new Casing(), new Motor(), new Accessory()), logOutput);
        for (int i = 0; i < numberOfAccessorySuppliers; ++i)
            accessorySuppliers[i] = new Supplier(accessories, defaultInterval, new Accessory());
        casingSupplier = new Supplier(casings, defaultInterval, new Casing());
        motorSupplier = new Supplier(motors, defaultInterval, new Motor());
        try {
            workers.start();
        } catch (ThreadPoolExceptions.ThreadPoolException e) {
            e.printStackTrace();
        }
        autoStorageController = new AutoStorageController(this, autos, workers);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (autos){
            autos.notifyAll();
        }
        isStarted = true;
    }
    public void stop() throws IllegalStateException {
        if (isFinished || !isStarted)
            throw new IllegalStateException();
        casingSupplier.terminate();
        motorSupplier.terminate();
        for (int i = 0; i < numberOfAccessorySuppliers; ++i)
            accessorySuppliers[i].terminate();
        autoStorageController.terminate();
        try {
            workers.stopAfterCurrentTasks();
        } catch (ThreadPoolExceptions.Terminated terminated) {
            terminated.printStackTrace();
        }
        for (int i = 0; i < numberOfDealers; ++i)
            dealers[i].terminate();
        isFinished = true;
        if (logOutput != null)
            logOutput.close();
    }

    public double getCasingsFillness(){
        return 1 - (double)casings.remainCapacity() / casingStorageSize;
    }
    public double getMotorsFillness(){
        return 1 - (double)motors.remainCapacity() / motorStorageSize;
    }
    public double getAccessoriesFillness(){
        return 1 - (double)accessories.remainCapacity() / accessoryStorageSize;
    }
    public double getAutosFillness(){
        return 1 - (double)autos.remainCapacity() / autoStorageSize;
    }
    public int getNumberOfAutos(){
        return numberOfProducedAutos;
    }
    public void setCasingSupplierInterval(int ms) throws InvalidParameterException {
        if (ms <= 0)
            throw new InvalidParameterException();
        casingSupplier.setInterval(ms);
    }
    public void setMotorSupplierInterval(int ms) throws InvalidParameterException {
        if (ms <= 0)
            throw new InvalidParameterException();
        motorSupplier.setInterval(ms);
    }
    public void setAccessorySuppliersInterval(int ms) throws InvalidParameterException {
        if (ms <= 0)
            throw new InvalidParameterException();
        for (Supplier cur : accessorySuppliers)
            cur.setInterval(ms);
    }
    public void setDealersInterval(int ms) throws InvalidParameterException {
        if (ms <= 0)
            throw new InvalidParameterException();
        for (Dealer cur : dealers)
            cur.setInterval(ms);
    }
}
