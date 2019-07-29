package ro.dragos.geornoiu;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ro.dragos.geornoiu.enums.Component;
import ro.dragos.geornoiu.exception.InvalidRobotTypeException;
import ro.dragos.geornoiu.service.ComponentGeneratorService;
import ro.dragos.geornoiu.service.factory.ACMEFactory;
import ro.dragos.geornoiu.service.factory.QueueStorage;
import ro.dragos.geornoiu.service.impl.DefaultComponentGeneratorService;

import java.util.Queue;

public class AcmeFactoryTest {
    private static ACMEFactory acmeFactory;

    @BeforeClass
    public static void initAcmeFactory() {
        ComponentGeneratorService componentGeneratorService = new DefaultComponentGeneratorService();
        acmeFactory = new ACMEFactory(componentGeneratorService);
    }

    @Before
    public void clearQueue() {
        QueueStorage.getConveyorBelt().clear();
    }

    /**
     * Needed in case the implementation for the queue is changed from a queue with a fixed size which will violate
     * the rule of allowing only at max 10 elements for the conveyor belt.
     */
    @Test
    public void testQueueCapacityLimit() {
        Queue<Component> conveyorBelt = QueueStorage.getConveyorBelt();

        for (int index = 0; index < 10; index++) {
            Assert.assertTrue(conveyorBelt.offer(Component.MAIN_UNIT));
        }

        Assert.assertFalse(conveyorBelt.offer(Component.MAIN_UNIT));
    }

    /**
     * Test that creating a worker with null as type will throw InvalidRobotTypeException.
     */
    @Test(expected = InvalidRobotTypeException.class)
    public void testInvalidRobotTypeException() {
        acmeFactory.getWorker(null, "");
    }
}
