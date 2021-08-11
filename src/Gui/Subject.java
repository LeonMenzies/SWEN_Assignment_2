package Gui;

import java.util.ArrayList;
import java.util.List;

/***
 * The Subject class is apart of the MVC design
 */
public class Subject {
    List<Observer> observers = new ArrayList<>();

    /**
     * This method is used to update all the observers when the state of the game changes
     */
    public void notifyObservers() {

        for (Observer o : observers) {
            o.update();
        }
    }

    /**
     * Add a observer to the list to be updated later
     *
     * @param observer the observer to be added
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }
}
