package Gui;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    List<Observer> observers = new ArrayList<>();

    public void notifyObservers(){

        for(Observer o : observers){
            o.update();
        }
    }

    public void addObserver(Observer observer){
        observers.add(observer);
    }
}
