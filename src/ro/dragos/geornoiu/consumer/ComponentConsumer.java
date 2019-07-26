package ro.dragos.geornoiu.consumer;

import ro.dragos.geornoiu.enums.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class ComponentConsumer implements Runnable {

    private int noOfAssembledRobots = 0;

    private String name;
    private Map<Component, Integer> componentsCurrentlyPossessed;
    private final Queue<Component> conveyorBelt;
    private final Map<Component, Integer> componentsNeeded;

    public ComponentConsumer(String name, Queue<Component> conveyorBelt, Map<Component, Integer> componentsNeeded) {
        this.name = name;
        this.conveyorBelt = conveyorBelt;
        this.componentsNeeded = componentsNeeded;
        this.componentsCurrentlyPossessed = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (this.conveyorBelt) {
                    while (this.conveyorBelt.isEmpty()) {
                        this.conveyorBelt.wait();
                    }

                    Component component = this.conveyorBelt.peek();

                    if (!checkIfComponentIsNeeded(component)) {
                        System.out.println(name + " released lock because it does not need " + component.name() +
                                " since it has " + componentsCurrentlyPossessed.get(component));
                        conveyorBelt.wait();

                    } else {
                        component = conveyorBelt.poll();

                        putComponent(component);
                        System.out.println(this.name + " consumed resource " + component.name() + " - Queue size now = "
                                + conveyorBelt.size());

                    }

                    conveyorBelt.notifyAll();
                }

                if (areAllComponenetsCollected()) {
                    //sleep is outside synchronized block
                    assembleRobot();
                }
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    private boolean areAllComponenetsCollected() {
        for (Map.Entry<Component, Integer> entry : componentsNeeded.entrySet()) {
            if (!entry.getValue().equals(componentsCurrentlyPossessed.get(entry.getKey()))) {
                return false;
            }
        }

        return true;
    }

    private void putComponent(Component component) {
        componentsCurrentlyPossessed.merge(component, 1, (key, value) -> key + 1);
    }

    private void assembleRobot() throws InterruptedException {
        noOfAssembledRobots++;
        componentsCurrentlyPossessed = new HashMap<>();

        System.out.println(this.name + " finished " + noOfAssembledRobots + " robots.");

        Thread.sleep(3000);
    }

    private boolean checkIfComponentIsNeeded(Component component) {
        Integer numberOfComponentsNeeded = componentsNeeded.get(component);
        if (numberOfComponentsNeeded == null) {
            return false;
        }


        Integer numberOfComponentsOwned = componentsCurrentlyPossessed.get(component);
        if (numberOfComponentsOwned == null) {
            return true;
        }

        return numberOfComponentsNeeded > numberOfComponentsOwned;
    }
}