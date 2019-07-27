package main.java.ro.dragos.geornoiu.consumer;

/**
 * Class which is responsible for managing what components are needed for constructing a robot and what components
 * a worker currently has.
 */
public class RobotComponentsPair {
    private final int numberOfComponentsNeeded;
    private int numberOfComponentsCurrentlyPossessed;

    public RobotComponentsPair(int numberOfComponentsNeeded) {
        this.numberOfComponentsNeeded = numberOfComponentsNeeded;
    }

    public boolean isComponentNeeded() {
        return numberOfComponentsCurrentlyPossessed != numberOfComponentsNeeded;
    }

    public void addComponent() {
        numberOfComponentsCurrentlyPossessed++;
    }

    public void clearPossesedComponents() {
        numberOfComponentsCurrentlyPossessed = 0;
    }

    public int getNumberOfComponentsNeeded() {
        return numberOfComponentsNeeded;
    }

    public int getNumberOfComponentsCurrentlyPossessed() {
        return numberOfComponentsCurrentlyPossessed;
    }
}
