package ro.dragos.geornoiu;

import org.junit.After;
import org.junit.Assert;
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

    @After
    public void clearQueue() {
        QueueStorage.getConveyorBelt().clear();
    }

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

    @Test
    public void testProducerAddsToQueuAtOneSecondInterval() {
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
