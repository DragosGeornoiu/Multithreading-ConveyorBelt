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

    private final String name;
    private final ComponentGeneratorService componentGenerator;
    private final Queue<Component> conveyorBelt;

    private static final int TIME_IN_MILLIS_TO_WAIT_BEFORE_ADDING_NEXT_COMPONENT = 1000;
    private static final int MAX_TIME_IN_MILLIS_TO_WAIT_WHEN_QUEUE_IS_FULL = 10000;

    public FactorySupplier(String name, Queue<Component> conveyorBelt,
                           ComponentGeneratorService componentGenerator) {
        this.name = name;
        this.conveyorBelt = conveyorBelt;
        this.componentGenerator = componentGenerator;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (this.conveyorBelt) {
                    while (this.conveyorBelt.size() == ACMEConstants.QUEUE_CAPACITY_LIMIT) {
                        LOG.info("Queue is full. {} is waiting.", this.name);

                        // queue is full, wait until a consumer takes a component from the conveyor belt and notifies
                        // the producer or remove the fist component on the queue if 10 seconds have passed
                        this.conveyorBelt.wait(MAX_TIME_IN_MILLIS_TO_WAIT_WHEN_QUEUE_IS_FULL);

                        if (this.conveyorBelt.size() == ACMEConstants.QUEUE_CAPACITY_LIMIT) {
                            Component component = this.conveyorBelt.remove();
                            LOG.info("{} removed component {} from conveyor belt.", this.name,
                                    component.name());
                        }
                    }

                    Component component = this.componentGenerator.retrieveComponent();

                    this.conveyorBelt.offer(component);

                    LOG.info("{} added component {} to conveyor belt", this.name, component.name());
                    this.printQueue();

                    this.conveyorBelt.notifyAll();
                }

                //sleep is outside synchronized block
                Thread.sleep(TIME_IN_MILLIS_TO_WAIT_BEFORE_ADDING_NEXT_COMPONENT);
            }
        } catch (InterruptedException e) {
            LOG.error("{} was interrupted and is being shut down", this.name);
            Thread.currentThread().interrupt();
        }
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