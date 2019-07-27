package main.java.ro.dragos.geornoiu.producer;

import main.java.ro.dragos.geornoiu.enums.Component;
import main.java.ro.dragos.geornoiu.service.ComponentGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

/**
 * Factory supplier which puts components on the conveyor belt at an interval of one second. If the supplier is unable
 * to put an item on the conveyor belt for 10 seconds, he will remove the first element on the queue.
 */
public class FactorySupplier implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(FactorySupplier.class);

    private String name;
    private ComponentGeneratorService componentGenerator;
    private final Queue<Component> conveyorBelt;

    private static final int QUEUE_SIZE_LIMIT = 10;
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
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (this.conveyorBelt) {
                while (this.conveyorBelt.size() == QUEUE_SIZE_LIMIT) {
                    try {
                        LOG.info("Queue is full. {} is waiting.", this.name);

                        // queue is full, wait until a consumer takes a component from the conveyor belt and notifies
                        // the producer or remove the fist component on the queue if 10 seconds have passed
                        this.conveyorBelt.wait(MAX_TIME_IN_MILLIS_TO_WAIT_WHEN_QUEUE_IS_FULL);

                        if (this.conveyorBelt.size() == QUEUE_SIZE_LIMIT) {
                            LOG.info("{} is removing component {} from conveyor belt.", this.name,
                                    this.conveyorBelt.peek());
                            this.conveyorBelt.remove();
                        }
                    } catch (InterruptedException e) {
                        LOG.error("{} was interrupted and is being shut down", this.name);
                        Thread.currentThread().interrupt();
                    }
                }

                Component component = this.componentGenerator.retrieveComponent();

                this.conveyorBelt.add(component);

                LOG.info("{} is added component {} to conveyor belt", this.name, component.name());
                this.printQueue();

                this.conveyorBelt.notifyAll();
            }

            try {
                //sleep is outside synchronized block
                Thread.sleep(TIME_IN_MILLIS_TO_WAIT_BEFORE_ADDING_NEXT_COMPONENT);
            } catch (InterruptedException ie) {
                LOG.error("{} was interrupted and is being shut down", this.name);
                Thread.currentThread().interrupt();
            }
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