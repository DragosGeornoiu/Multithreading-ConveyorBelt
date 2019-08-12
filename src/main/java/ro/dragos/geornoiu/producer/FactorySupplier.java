package ro.dragos.geornoiu.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.dragos.geornoiu.constants.ACMEConstants;
import ro.dragos.geornoiu.enums.Component;
import ro.dragos.geornoiu.service.ComponentGeneratorService;

import java.util.Queue;

/**
 * Factory supplier which puts components on the conveyor belt at an interval of one second. If the supplier is unable
 * to put an item on the conveyor belt for 10 seconds, he will remove the first element on the queue.
 */
public class FactorySupplier implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(FactorySupplier.class);

    //volatile in order to not be cached and any modification from an external thread to be seen in current thread.
    private volatile boolean isRunning;

    private final String name;
    private final ComponentGeneratorService componentGenerator;
    private final Queue<Component> conveyorBelt;

    private long waitTime;

    private static final int TIME_IN_MILLIS_TO_WAIT_BEFORE_ADDING_NEXT_COMPONENT = 1000;
    private static final int MAX_TIME_IN_MILLIS_TO_WAIT_WHEN_QUEUE_IS_FULL = 10000;

    public FactorySupplier(String name, Queue<Component> conveyorBelt,
                           ComponentGeneratorService componentGenerator) {
        this.isRunning = true;
        this.name = name;
        this.conveyorBelt = conveyorBelt;
        this.componentGenerator = componentGenerator;
    }

    @Override
    public void run() {
        try {
            while (this.isRunning) {
                synchronized (this.conveyorBelt) {
                    this.waitTime = MAX_TIME_IN_MILLIS_TO_WAIT_WHEN_QUEUE_IS_FULL;
                    long start = System.currentTimeMillis();

                    // if queue is full, wait for 10 seconds
                    while (doWaitCondition()) {
                        LOG.info("Queue is full. {} is waiting.", this.name);

                        this.conveyorBelt.wait(waitTime);
                        this.waitTime = System.currentTimeMillis() - start;
                    }

                    // Check again the capacity in case the FactorySupplier did wait for the entire duration of
                    // MAX_TIME_IN_MILLIS_TO_WAIT_WHEN_QUEUE_IS_FULL and it has to remove the first component
                    // form the queue.
                    if (this.conveyorBelt.size() == ACMEConstants.QUEUE_CAPACITY_LIMIT) {
                        Component component = this.conveyorBelt.remove();
                        LOG.info("{} removed component {} from conveyor belt.", this.name,
                                component.name());
                    }

                    //do not add another component if current thread is stopped
                    if (!isRunning) {
                        break;
                    }

                    Component component = this.componentGenerator.retrieveComponent();

                    this.conveyorBelt.offer(component);

                    LOG.info("{} added component {} to conveyor belt", this.name, component.name());
                    this.printQueue();

                    // Notify that the lock on the conveyor belt will be released in order to awaken the threads
                    // which are in waiting state.
                    this.conveyorBelt.notifyAll();
                }

                //sleep is outside synchronized block
                Thread.sleep(TIME_IN_MILLIS_TO_WAIT_BEFORE_ADDING_NEXT_COMPONENT);
            }
        } catch (InterruptedException e) {
            LOG.error("{} was interrupted and is being shut down", this.name);
            stop();
        }
    }

    private boolean doWaitCondition() {
        return this.conveyorBelt.size() == ACMEConstants.QUEUE_CAPACITY_LIMIT &&
                this.waitTime >= MAX_TIME_IN_MILLIS_TO_WAIT_WHEN_QUEUE_IS_FULL;
    }

    /**
     * Stop execution of thread by setting isRunningFlag to false.
     */
    public void stop() {
        this.isRunning = false;
    }

    private void printQueue() {
        StringBuilder queue = new StringBuilder();

        for (Component item : this.conveyorBelt) {
            queue.append(item.name()).append('-');
        }

        String finalQueue = queue.substring(0, queue.length() - 1);

        LOG.info("Queue: {}", finalQueue);
    }
}