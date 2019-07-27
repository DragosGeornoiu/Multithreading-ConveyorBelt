package main.java.ro.dragos.geornoiu.consumer;

import main.java.ro.dragos.geornoiu.enums.Component;
import main.java.ro.dragos.geornoiu.enums.RobotType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;

/**
 * The worker is responsible of taking components from the conveyor belt and assembling robots when he has all the
 * needed components, which are specific for each {@link RobotType}. Assembling the robot takes 3 seconds.
 */
public class Worker implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Worker.class);

    private String name;
    private int noOfAssembledRobots;
    private final Queue<Component> conveyorBelt;
    private Map<Component, RobotComponentsPair> robotComponentsMap;

    private static final int NO_OF_MILLIS_NEEDED_TO_BUILD_A_ROBOT = 3000;

    public Worker(String name, Queue<Component> conveyorBelt,
                  Map<Component, RobotComponentsPair> robotComponentsMap) {
        this.name = name;
        this.conveyorBelt = conveyorBelt;
        this.robotComponentsMap = robotComponentsMap;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (this.conveyorBelt) {
                    while (this.conveyorBelt.isEmpty()) {
                        this.conveyorBelt.wait();
                    }

                    Component component = this.conveyorBelt.peek();

                    RobotComponentsPair robotComponentsPair = robotComponentsMap.get(component);
                    if (robotComponentsPair == null || !robotComponentsPair.isComponentNeeded()) {
                        LOG.info("Worker {} does not need component {} since it has {}", this.name, component.name(),
                                (robotComponentsPair == null ? " no need for that type of component."
                                        : robotComponentsPair.getNumberOfComponentsCurrentlyPossessed()));
                        conveyorBelt.wait();

                    } else {
                        component = conveyorBelt.poll();

                        robotComponentsMap.get(component).addComponent();
                        LOG.info("Worker {} has taken component {} from the conveyor belt. Queue size is now {}.",
                                component.name(), component.name(), conveyorBelt.size());
                        conveyorBelt.notifyAll();
                    }
                }

                if (areAllComponenetsCollected()) {
                    //sleep is outside synchronized block
                    assembleRobot();
                }
            }
        } catch (InterruptedException ie) {
            //log exception
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Check if the worker has all the necessary components to build the robot.
     */
    private boolean areAllComponenetsCollected() {
        for (RobotComponentsPair robotComponentsPair : robotComponentsMap.values()) {
            if (robotComponentsPair.isComponentNeeded()) {
                return false;
            }
        }

        return true;
    }

    private void assembleRobot() throws InterruptedException {
        noOfAssembledRobots++;
        for (RobotComponentsPair robotComponentsPair : robotComponentsMap.values()) {
            robotComponentsPair.clearPossesedComponents();
        }

        LOG.info("Worker {} has finished asselmbling {} in his lifetime.", this.name, this.noOfAssembledRobots);

        Thread.sleep(NO_OF_MILLIS_NEEDED_TO_BUILD_A_ROBOT);
    }
}