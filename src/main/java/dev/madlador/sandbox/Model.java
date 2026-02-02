package dev.madlador.sandbox;

import java.util.ArrayList;

public class Model {

    private int count = 0;
    private final ArrayList<ModelObserver> observers = new ArrayList<>();

    public void attach(ModelObserver observer) {
        this.observers.add(observer);
    }

    public void detach(ModelObserver observer) {
        this.observers.remove(observer);
    }

    public void notifyObservers() {
        for (ModelObserver observer : observers) {
            observer.updateCount(count);
        }
    }

    public void setCount(int count) {
        this.count = count;
        notifyObservers();
    }

    public int getCount() {
        return count;
    }

}
