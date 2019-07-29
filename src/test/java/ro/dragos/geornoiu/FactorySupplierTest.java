package ro.dragos.geornoiu;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ro.dragos.geornoiu.constants.ACMEConstants;
import ro.dragos.geornoiu.producer.FactorySupplier;
import ro.dragos.geornoiu.service.ComponentGeneratorService;
import ro.dragos.geornoiu.service.factory.ACMEFactory;
import ro.dragos.geornoiu.service.factory.QueueStorage;
import ro.dragos.geornoiu.service.impl.DefaultComponentGeneratorService;

import java.util.ArrayList;
import java.util.List;

public class FactorySupplierTest {
    private static ACMEFactory acmeFactory;

    private static final String PRODUCER_NAME = "Producer";

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
     * Verify that one producer will not add more than 10 elements on the conveyor belt.
     */
    @Test
    public void testQueueLimitWithOneProducer() {
        FactorySupplier factorySupplier = acmeFactory.getFactorySupplier(PRODUCER_NAME);
        Thread thread = new Thread(factorySupplier);
        thread.start();

        try {
            Thread.sleep(30000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        thread.interrupt();

        if (QueueStorage.getConveyorBelt().size() > ACMEConstants.QUEUE_CAPACITY_LIMIT) {
            Assert.fail("Queue limit was exceeded");
        }
    }

    /**
     * Verifies that more than one producer will not add more than 10 elements on the conveyor belt.
     */
    @Test
    public void testQueueLimitWithMultipleProducers() {
        List<Thread> threadList = new ArrayList<>();
        for (int index = 0; index < 5; index++) {
            FactorySupplier factorySupplier = acmeFactory.getFactorySupplier(PRODUCER_NAME);
            Thread thread = new Thread(factorySupplier);
            thread.start();
            threadList.add(thread);
        }

        try {
            Thread.sleep(30000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        for (int index = 0; index < 5; index++) {
            threadList.get(index).interrupt();
        }

        if (QueueStorage.getConveyorBelt().size() > ACMEConstants.QUEUE_CAPACITY_LIMIT) {
            Assert.fail("Queue limit was exceeded");
        }
    }

    /**
     * Checks that producer adds elements to queue at a one second interval.
     */
    @Test
    public void testProducerAddsToQueueAtOneSecondInterval() {
        FactorySupplier factorySupplierWorker = acmeFactory.getFactorySupplier(PRODUCER_NAME);
        Thread factorySupplierThread = new Thread(factorySupplierWorker);
        factorySupplierThread.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        factorySupplierThread.interrupt();

        Assert.assertEquals(QueueStorage.getConveyorBelt().size(), 5);
    }
}
