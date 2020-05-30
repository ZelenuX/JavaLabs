package factory;

import factory.resultsAndComponents.Storable;

import java.security.InvalidParameterException;

public interface Storage {
    void put(Storable object) throws InvalidParameterException, InterruptedException;
    Storable get() throws InterruptedException;
    boolean isEmpty();
    boolean isFull();
    int remainCapacity();
}
