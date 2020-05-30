package factory;

import factory.resultsAndComponents.Storable;

import java.security.InvalidParameterException;
import java.util.concurrent.ArrayBlockingQueue;

public class FixedSizeStorage implements Storage {
    private ArrayBlockingQueue<Storable> queue;

    public FixedSizeStorage(int storageSize) throws InvalidParameterException{
        if (storageSize <= 0)
            throw new InvalidParameterException();
        queue = new ArrayBlockingQueue<Storable>(storageSize);
    }
    @Override
    public void put(Storable object) throws InvalidParameterException, InterruptedException {
        if (object == null)
            throw new InvalidParameterException();
        synchronized (queue){
            while (isFull()) {
                queue.wait();
            }
            queue.add(object);
            queue.notifyAll();
        }
    }
    @Override
    public Storable get() throws InterruptedException {
        synchronized (queue) {
            while (isEmpty()) {
                queue.wait();
            }
            Storable tmp = queue.poll();
            queue.notifyAll();
            return tmp;
        }
    }
    @Override
    public boolean isEmpty() {
        synchronized (queue){
            return queue.isEmpty();
        }
    }
    @Override
    public boolean isFull() {
        return (queue.remainingCapacity() == 0);
    }
    @Override
    public int remainCapacity() {
        synchronized (queue){
            return queue.remainingCapacity();
        }
    }
}
