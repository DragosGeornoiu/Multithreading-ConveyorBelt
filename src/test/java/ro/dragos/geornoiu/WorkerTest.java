package ro.dragos.geornoiu;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import ro.dragos.geornoiu.consumer.RobotComponentsPair;
import ro.dragos.geornoiu.consumer.Worker;
import ro.dragos.geornoiu.enums.Component;
import ro.dragos.geornoiu.enums.RobotType;
import ro.dragos.geornoiu.producer.FactorySupplier;
import ro.dragos.geornoiu.service.ComponentGeneratorService;
import ro.dragos.geornoiu.service.factory.ACMEFactory;
import ro.dragos.geornoiu.service.factory.QueueStorage;
import ro.dragos.geornoiu.service.impl.DefaultComponentGeneratorService;

import java.util.Map;
import java.util.Queue;

public class WorkerTest {
    private static ACMEFactory acmeFactory;

    private static final String WORKER_NAME = "test";

    @BeforeClass
    public static void initAcmeFactory() {
        ComponentGeneratorService componentGeneratorService = new DefaultComponentGeneratorService();
        acmeFactory = new ACMEFactory(componentGeneratorService);
    }

    @Before
    public void clearQueue() {
        QueueStorage.getConveyorBelt().clear();
    }

    @Test
    public void testDryRobotComponentsNeeded() {
        Worker dryRobotWorker = acmeFactory.getWorker(RobotType.DRY2000, WORKER_NAME);

        Map<Component, RobotComponentsPair> dryRobotComponents = dryRobotWorker.getRobotComponentsMap();

        RobotComponentsPair dryRobotMainUnitPair = dryRobotComponents.get(Component.MAIN_UNIT);
        Assert.assertEquals(dryRobotMainUnitPair.getNumberOfComponentsCurrentlyPossessed(), 0);
        Assert.assertEquals(dryRobotMainUnitPair.getNumberOfComponentsNeeded(), 1);

        RobotComponentsPair dryRobotBroomPair = dryRobotComponents.get(Component.BROOM);
        Assert.assertEquals(dryRobotBroomPair.getNumberOfComponentsCurrentlyPossessed(), 0);
        Assert.assertEquals(dryRobotBroomPair.getNumberOfComponentsNeeded(), 2);

        RobotComponentsPair dryRobotMopPair = dryRobotComponents.get(Component.MOP);
        Assert.assertNull(dryRobotMopPair);
    }

    @Test
    public void testWetRobotComponentsNeeded() {
        Worker wetRobotWorker = acmeFactory.getWorker(RobotType.WET2000, WORKER_NAME);

        Map<Component, RobotComponentsPair> wetRobotComponents = wetRobotWorker.getRobotComponentsMap();

        RobotComponentsPair wetRobotMainUnitPair = wetRobotComponents.get(Component.MAIN_UNIT);
        Assert.assertEquals(wetRobotMainUnitPair.getNumberOfComponentsCurrentlyPossessed(), 0);
        Assert.assertEquals(wetRobotMainUnitPair.getNumberOfComponentsNeeded(), 1);

        RobotComponentsPair wetRobotBroomPair = wetRobotComponents.get(Component.MOP);
        Assert.assertEquals(wetRobotBroomPair.getNumberOfComponentsCurrentlyPossessed(), 0);
        Assert.assertEquals(wetRobotBroomPair.getNumberOfComponentsNeeded(), 2);

        RobotComponentsPair wetRobotMopPair = wetRobotComponents.get(Component.BROOM);
        Assert.assertNull(wetRobotMopPair);
    }

    @Test
    public void testWorkersCannotCompleteRobotWithOnlyBroomElements() {
        ComponentGeneratorService componentGeneratorService = Mockito.mock(ComponentGeneratorService.class);
        Mockito.when(componentGeneratorService.retrieveComponent()).thenReturn(Component.BROOM);

        testWorkersCannotCompleteRobot(componentGeneratorService);
    }

    @Test
    public void testWorkersCannotCompleteRobotWithOnlyMopElements() {
        ComponentGeneratorService componentGeneratorService = Mockito.mock(ComponentGeneratorService.class);
        Mockito.when(componentGeneratorService.retrieveComponent()).thenReturn(Component.MOP);

        testWorkersCannotCompleteRobot(componentGeneratorService);
    }

    @Test
    public void testWorkersCannotCompleteRobotWithOnlyMainUnitElements() {
        ComponentGeneratorService componentGeneratorService = Mockito.mock(ComponentGeneratorService.class);
        Mockito.when(componentGeneratorService.retrieveComponent()).thenReturn(Component.MAIN_UNIT);

        testWorkersCannotCompleteRobot(componentGeneratorService);
    }

    @Test
    public void testDryWorkerCanCompleteRobot() {
        Queue<Component> conveyorBelt = QueueStorage.getConveyorBelt();
        conveyorBelt.add(Component.MAIN_UNIT);
        conveyorBelt.add(Component.BROOM);
        conveyorBelt.add(Component.BROOM);

        Worker dryRobotWorker = acmeFactory.getWorker(RobotType.DRY2000, WORKER_NAME);
        Thread dryRobotThread = new Thread(dryRobotWorker);
        dryRobotThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        dryRobotThread.interrupt();

        Assert.assertEquals(dryRobotWorker.getNoOfAssembledRobots(), 1);
    }

    @Test
    public void testWetWorkerCanCompleteRobot() {
        Queue<Component> conveyorBelt = QueueStorage.getConveyorBelt();
        conveyorBelt.add(Component.MAIN_UNIT);
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MOP);

        Worker wetRobotWorker = acmeFactory.getWorker(RobotType.WET2000, WORKER_NAME);
        Thread wetRobotThread = new Thread(wetRobotWorker);
        wetRobotThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        wetRobotThread.interrupt();

        Assert.assertEquals(wetRobotWorker.getNoOfAssembledRobots(), 1);
    }

    @Test
    public void testBothWorkersCanCompleteRobot() {
        Queue<Component> conveyorBelt = QueueStorage.getConveyorBelt();
        conveyorBelt.add(Component.MAIN_UNIT);
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MAIN_UNIT);
        conveyorBelt.add(Component.BROOM);
        conveyorBelt.add(Component.BROOM);

        Worker dryRobotWorker = acmeFactory.getWorker(RobotType.DRY2000, WORKER_NAME);
        Thread dryRobotThread = new Thread(dryRobotWorker);
        dryRobotThread.start();

        Worker wetRobotWorker = acmeFactory.getWorker(RobotType.WET2000, WORKER_NAME);
        Thread wetRobotThread = new Thread(wetRobotWorker);
        wetRobotThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        dryRobotThread.interrupt();
        wetRobotThread.interrupt();

        Assert.assertEquals(dryRobotWorker.getNoOfAssembledRobots(), 1);
        Assert.assertEquals(wetRobotWorker.getNoOfAssembledRobots(), 1);
    }

    @Test
    public void testBothWorkersNeedMainUnitToCompleteRobot() {
        Queue<Component> conveyorBelt = QueueStorage.getConveyorBelt();
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.BROOM);
        conveyorBelt.add(Component.BROOM);

        Worker dryRobotWorker = acmeFactory.getWorker(RobotType.DRY2000, WORKER_NAME);
        Thread dryRobotThread = new Thread(dryRobotWorker);
        dryRobotThread.start();

        Worker wetRobotWorker = acmeFactory.getWorker(RobotType.WET2000, WORKER_NAME);
        Thread wetRobotThread = new Thread(wetRobotWorker);
        wetRobotThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        dryRobotThread.interrupt();
        wetRobotThread.interrupt();

        Assert.assertEquals(dryRobotWorker.getNoOfAssembledRobots(), 0);
        Assert.assertEquals(wetRobotWorker.getNoOfAssembledRobots(), 0);

        Map<Component, RobotComponentsPair> dryRobotComponents = dryRobotWorker.getRobotComponentsMap();

        RobotComponentsPair dryRobotMainUnitPair = dryRobotComponents.get(Component.MAIN_UNIT);
        Assert.assertEquals(dryRobotMainUnitPair.getNumberOfComponentsCurrentlyPossessed(), 0);
        Assert.assertEquals(dryRobotMainUnitPair.getNumberOfComponentsNeeded(), 1);

        RobotComponentsPair dryRobotBroomPair = dryRobotComponents.get(Component.BROOM);
        Assert.assertEquals(dryRobotBroomPair.getNumberOfComponentsCurrentlyPossessed(), 2);
        Assert.assertEquals(dryRobotBroomPair.getNumberOfComponentsNeeded(), 2);

        RobotComponentsPair dryRobotMopPair = dryRobotComponents.get(Component.MOP);
        Assert.assertNull(dryRobotMopPair);

        Map<Component, RobotComponentsPair> wetRobotComponents = wetRobotWorker.getRobotComponentsMap();

        RobotComponentsPair wetRobotMainUnitPair = wetRobotComponents.get(Component.MAIN_UNIT);
        Assert.assertEquals(wetRobotMainUnitPair.getNumberOfComponentsCurrentlyPossessed(), 0);
        Assert.assertEquals(wetRobotMainUnitPair.getNumberOfComponentsNeeded(), 1);

        RobotComponentsPair wetRobotBroomPair = wetRobotComponents.get(Component.MOP);
        Assert.assertEquals(wetRobotBroomPair.getNumberOfComponentsCurrentlyPossessed(), 2);
        Assert.assertEquals(wetRobotBroomPair.getNumberOfComponentsNeeded(), 2);

        RobotComponentsPair wetRobotMopPair = wetRobotComponents.get(Component.BROOM);
        Assert.assertNull(wetRobotMopPair);
    }

    private void testWorkersCannotCompleteRobot(ComponentGeneratorService componentGeneratorService) {
        ACMEFactory acmeFactoryWithMockedService = new ACMEFactory(componentGeneratorService);

        Worker dryRobotWorker = acmeFactoryWithMockedService.getWorker(RobotType.DRY2000, WORKER_NAME);
        Thread dryRobotThread = new Thread(dryRobotWorker);
        dryRobotThread.start();

        Worker wetRobotWorker = acmeFactoryWithMockedService.getWorker(RobotType.WET2000, WORKER_NAME);
        Thread wetRobotThread = new Thread(wetRobotWorker);
        wetRobotThread.start();

        FactorySupplier factorySupplier = acmeFactoryWithMockedService.getFactorySupplier("Producer");
        Thread factorySupplierThread = new Thread(factorySupplier);
        factorySupplierThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        factorySupplierThread.interrupt();
        dryRobotThread.interrupt();
        wetRobotThread.interrupt();

        Assert.assertEquals(dryRobotWorker.getNoOfAssembledRobots(), 0);
        Assert.assertEquals(wetRobotWorker.getNoOfAssembledRobots(), 0);
    }
}
