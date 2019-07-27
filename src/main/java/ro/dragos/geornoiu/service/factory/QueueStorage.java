package ro.dragos.geornoiu.service.factory;

import ro.dragos.geornoiu.enums.Component;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Storage class of the conveyor belt is retrieved, a queue of {@link Component} elements.
 * The QueueStorage is a singleton, allowing the creation of a single storage in the entire application.
 * The retrieval of conveyor belt also implements the singleton pattern for the purpose of allowing a single
 * conveyor belt in the entire application.
 */
public class QueueStorage {
    private static volatile QueueStorage instance;
    private volatile Queue<Component> conveyorBelt;

    private QueueStorage() {
    }

    /**
     * Retrieve the QueueStorage instance.
     *
     * @return the QueueStorage instance.
     */
    public static QueueStorage getInstance() {
        // minimize access to volatile member
        QueueStorage result = instance;
        if (result != null) {
            return result;
        }

        synchronized (QueueStorage.class) {
            if (instance == null) {
                instance = new QueueStorage();
            }

            return instance;
        }
    }

    /**
     * Retrieve the conveyor belt instance, the queue of {@link Component} elements.
     *
     * @return the conveyor belt instance.
     */
    public Queue<Component> getConveyorBelt() {
        // minimize access to volatile member
        Queue<Component> result = conveyorBelt;

        if (result != null) {
            return result;
        }

        synchronized (this) {
            if (conveyorBelt == null) {
                conveyorBelt = new LinkedList<>();
            }

            return conveyorBelt;
        }
    }
}
