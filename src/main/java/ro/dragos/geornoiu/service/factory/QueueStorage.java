package ro.dragos.geornoiu.service.factory;

import ro.dragos.geornoiu.constants.ACMEConstants;
import ro.dragos.geornoiu.enums.Component;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Acts as a storage class for the conveyor belt, a queue of {@link Component} elements.
 * The retrieval of conveyor belt also implements the singleton pattern for the purpose of allowing a single
 * conveyor belt in the entire application.
 */
public class QueueStorage {
    private static volatile Queue<Component> conveyorBelt;

    private QueueStorage() {
    }

    /**
     * Retrieve the conveyor belt instance, the queue of {@link Component} elements.
     *
     * @return the conveyor belt instance.
     */
    public static Queue<Component> getConveyorBelt() {
        // minimize access to volatile member
        Queue<Component> result = conveyorBelt;

        if (result != null) {
            return result;
        }

        synchronized (QueueStorage.class) {
            if (conveyorBelt == null) {
                conveyorBelt = new LinkedBlockingDeque<>(ACMEConstants.QUEUE_CAPACITY_LIMIT);
            }

            return conveyorBelt;
        }
    }
}
