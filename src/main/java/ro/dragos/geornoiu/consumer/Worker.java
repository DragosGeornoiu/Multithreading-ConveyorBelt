package ro.dragos.geornoiu.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.dragos.geornoiu.enums.Component;
import ro.dragos.geornoiu.enums.RobotType;

import java.util.Map;
import java.util.Queue;

/**
 * The worker is responsible of taking components from the conveyor belt and assembling robots when he has all the
 * needed components, which are specific for each {@link RobotType}. Assembling the robot takes 3 seconds.
 */
public class Worker implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Worker.class);

    //volatile in order to not be cached and any modification from an external thread to be seen in current thread.
    private volatile boolean isRunning;

    private int noOfAssembledRobots;
    private final String name;
    private final Queue<Component> conveyorBelt;
    private final Map<Component, RobotComponentsPair> robotComponentsMap;

    private static final int NO_OF_MILLIS_NEEDED_TO_BUILD_A_ROBOT = 3000;

    public Worker(String name, Queue<Component> conveyorBelt,
                  Map<Component, RobotComponentsPair> robotComponentsMap) {
        this.isRunning = true;
        this.name = name;
        this.conveyorBelt = conveyorBelt;
        this.robotComponentsMap = robotComponentsMap;
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                synchronized (this.conveyorBelt) {
                    // Wait until the queue is not empty and the first component on the queue is one needed by the
                    // current worker.
                    while (!isComponentFromConveyorBeltNeeded(this.conveyorBelt.peek())) {
                        this.conveyorBelt.wait();
                    }

                    //do not remove component from queue if current thread is stopped
                    if (!isRunning) {
                        break;
                    }

                    Component component = this.conveyorBelt.poll();

                    this.robotComponentsMap.get(component).addComponent();
                    LOG.info("Worker {} has taken component {} from the conveyor belt. Queue size is now {}.",
                            this.name, component.name(), this.conveyorBelt.size());

                    // Notify that the lock on the conveyor belt will be released in order to awaken the threads
                    // which are in waiting state.
                    this.conveyorBelt.notifyAll();
                }

                if (areAllComponentsCollected()) {
                    //thread sleep is outside synchronized block
                    assembleRobot();
                }
            }
        } catch (InterruptedException ie) {
            LOG.error("{} was interrupted and is being shut down", this.name);
            stop();
        }
    }

    public void stop() {
        isRunning = false;
    }

    private boolean isComponentFromConveyorBeltNeeded(Component component) {
        if (component == null) {
            //on debug to not pollute the console
            LOG.debug("Worker {} found queue empty.", this.name);
            return false;
        }

        RobotComponentsPair robotComponentsPair = this.robotComponentsMap.get(component);

        if (robotComponentsPair == null) {
            LOG.info("Worker {} does not need component {} since it has no need for that type of component.",
                    this.name, component.name());
            return false;
        }

        if (!robotComponentsPair.isComponentNeeded()) {
            LOG.info("Worker {} does not need component {} since it already has {} of {}.", this.name, component.name(),
                    robotComponentsPair.getNumberOfComponentsCurrentlyPossessed(),
                    robotComponentsPair.getNumberOfComponentsNeeded());
            return false;
        }

        return true;
    }

    /**
     * Check if the worker has all the necessary components to build the robot.
     */
    private boolean areAllComponentsCollected() {
        for (RobotComponentsPair robotComponentsPair : robotComponentsMap.values()) {
            if (robotComponentsPair.isComponentNeeded()) {
                return false;
            }
        }

        return true;
    }

    private void assembleRobot() throws InterruptedException {
        Thread.sleep(NO_OF_MILLIS_NEEDED_TO_BUILD_A_ROBOT);

        noOfAssembledRobots++;

        for (RobotComponentsPair robotComponentsPair : robotComponentsMap.values()) {
            robotComponentsPair.clearPossesedComponents();
        }

        LOG.info("Worker {} has assembled {} robots in his lifetime.", this.name, this.noOfAssembledRobots);
    }

    public int getNoOfAssembledRobots() {
        return noOfAssembledRobots;
    }

    public Map<Component, RobotComponentsPair> getRobotComponentsMap() {
        return robotComponentsMap;
    }
}