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

import java.util.ArrayList;
import java.util.List;
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

    /**
     * Verifies that a worker which builds DRY2000 robots needs a MainUnit and two BROOM components for each robot.
     */
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

    /**
     * Verifies that a worker which builds WET2000 robots needs a MainUnit and two MOP components for each robot.
     */
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

    /**
     * Verifies that using only BROOM components, neither the worker that builds DRY2000 robots, neither the worker
     * that builds WET2000 components can complete a robot.
     */
    @Test
    public void testWorkersCannotCompleteRobotWithOnlyBroomElements() {
        ComponentGeneratorService componentGeneratorService = Mockito.mock(ComponentGeneratorService.class);
        Mockito.when(componentGeneratorService.retrieveComponent()).thenReturn(Component.BROOM);

        testWorkersCannotCompleteRobot(componentGeneratorService);
    }

    /**
     * Verifies that using only MOP components, neither the worker that builds DRY2000 robots, neither the worker that
     * builds WET2000 components can complete a robot.
     */
    @Test
    public void testWorkersCannotCompleteRobotWithOnlyMopElements() {
        ComponentGeneratorService componentGeneratorService = Mockito.mock(ComponentGeneratorService.class);
        Mockito.when(componentGeneratorService.retrieveComponent()).thenReturn(Component.MOP);

        testWorkersCannotCompleteRobot(componentGeneratorService);
    }

    /**
     * Verifies that using only MainUnit components, neither the worker that builds DRY2000 robots, neither the worker
     * that builds WET2000 components can complete a robot.
     */
    @Test
    public void testWorkersCannotCompleteRobotWithOnlyMainUnitElements() {
        ComponentGeneratorService componentGeneratorService = Mockito.mock(ComponentGeneratorService.class);
        Mockito.when(componentGeneratorService.retrieveComponent()).thenReturn(Component.MAIN_UNIT);

        testWorkersCannotCompleteRobot(componentGeneratorService);
    }

    /**
     * Tests that a Worker that builds DRY2000 robots can complete a robot using one MainUnit component and two BROOM
     * components.
     */
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
            Thread.sleep(6000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        dryRobotWorker.stop();

        Assert.assertEquals(dryRobotWorker.getNoOfAssembledRobots(), 1);
    }

    /**
     * Tests that a Worker that builds WET2000 robots can complete a robot using one MainUnit component and two MOP
     * components.
     */
    @Test
    public void testWetWorkerCanCompleteRobot() {
        Queue<Component> conveyorBelt = QueueStorage.getConveyorBelt();
        conveyorBelt.add(Component.MAIN_UNIT);
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MOP);

        Worker wetRobotWorker = acmeFactory.getWorker(RobotType.WET2000, WORKER_NAME);
        new Thread(wetRobotWorker).start();

        try {
            Thread.sleep(4000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        wetRobotWorker.stop();

        Assert.assertEquals(wetRobotWorker.getNoOfAssembledRobots(), 1);
    }

    /**
     * Tests that if the queue has two MainUnit components, two BROOM components and two MOP components, both the
     * Worker that builds the WET2000 robots and the Worker that builds the DRY2000 robots will be able to complete
     * a single robot.
     */
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
        new Thread(dryRobotWorker).start();

        Worker wetRobotWorker = acmeFactory.getWorker(RobotType.WET2000, WORKER_NAME);
        new Thread(wetRobotWorker).start();

        try {
            Thread.sleep(4000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        dryRobotWorker.stop();
        wetRobotWorker.stop();

        Assert.assertEquals(dryRobotWorker.getNoOfAssembledRobots(), 1);
        Assert.assertEquals(wetRobotWorker.getNoOfAssembledRobots(), 1);
    }

    /**
     * Tests that if no MainUnit component is on the queue, no Worker can complete assembling a robot.
     */
    @Test
    public void testBothWorkersNeedMainUnitToCompleteRobot() {
        Queue<Component> conveyorBelt = QueueStorage.getConveyorBelt();
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.BROOM);
        conveyorBelt.add(Component.BROOM);

        Worker dryRobotWorker = acmeFactory.getWorker(RobotType.DRY2000, WORKER_NAME);
        new Thread(dryRobotWorker).start();

        Worker wetRobotWorker = acmeFactory.getWorker(RobotType.WET2000, WORKER_NAME);
        new Thread(wetRobotWorker).start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        dryRobotWorker.stop();
        wetRobotWorker.stop();

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

    /**
     * Tests that if a Worker which assembles DRY200 robots can complete multiple robots.
     */
    @Test
    public void testDryWorkerCanAssembleMultipleRobots() {
        Queue<Component> conveyorBelt = QueueStorage.getConveyorBelt();
        conveyorBelt.add(Component.BROOM);
        conveyorBelt.add(Component.BROOM);
        conveyorBelt.add(Component.MAIN_UNIT);
        conveyorBelt.add(Component.BROOM);
        conveyorBelt.add(Component.BROOM);
        conveyorBelt.add(Component.MAIN_UNIT);
        conveyorBelt.add(Component.BROOM);
        conveyorBelt.add(Component.BROOM);
        conveyorBelt.add(Component.MAIN_UNIT);

        Worker dryRobotWorker = acmeFactory.getWorker(RobotType.DRY2000, WORKER_NAME);
        new Thread(dryRobotWorker).start();

        try {
            Thread.sleep(12000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        dryRobotWorker.stop();

        Assert.assertEquals(dryRobotWorker.getNoOfAssembledRobots(), 3);
    }

    /**
     * Tests that if a Worker which assembles WET2000 ROBOTS can complete multiple robots.
     */
    @Test
    public void testWetWorkerCanAssembleMultipleRobots() {
        Queue<Component> conveyorBelt = QueueStorage.getConveyorBelt();
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MAIN_UNIT);
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MAIN_UNIT);
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MAIN_UNIT);

        Worker wetRobotWorker = acmeFactory.getWorker(RobotType.WET2000, WORKER_NAME);
        Thread t = new Thread(wetRobotWorker);
        t.start();

        try {
            Thread.sleep(12000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        t.stop();

        Assert.assertEquals(wetRobotWorker.getNoOfAssembledRobots(), 3);
    }

    /**
     * Tests that multiple Workers that assemble WET200 robots can complete one robot each.
     */
    @Test
    public void testMultipleWetRobotsCanAssembleEachARobot() {
        Queue<Component> conveyorBelt = QueueStorage.getConveyorBelt();
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MAIN_UNIT);
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MAIN_UNIT);
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MOP);
        conveyorBelt.add(Component.MAIN_UNIT);

        List<Worker> listOfWorkers = new ArrayList<>();

        for (int index = 0; index < 3; index++) {
            Worker wetRobotWorker = acmeFactory.getWorker(RobotType.WET2000, WORKER_NAME + '-' + index);

            listOfWorkers.add(wetRobotWorker);

            new Thread(wetRobotWorker).start();
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        for (int index = 0; index < 3; index++) {
            listOfWorkers.get(index).stop();

            Assert.assertEquals(listOfWorkers.get(index).getNoOfAssembledRobots(), 1);
        }
    }

    /**
     * Tests that multiple Workers that assemble DRY200 robots can complete one robot each.
     */
    @Test
    public void testMultipleDryRobotsCanAssembleEachARobot() {
        Queue<Component> conveyorBelt = QueueStorage.getConveyorBelt();
        conveyorBelt.add(Component.BROOM);
        conveyorBelt.add(Component.BROOM);
        conveyorBelt.add(Component.MAIN_UNIT);
        conveyorBelt.add(Component.BROOM);
        conveyorBelt.add(Component.BROOM);
        conveyorBelt.add(Component.MAIN_UNIT);
        conveyorBelt.add(Component.BROOM);
        conveyorBelt.add(Component.BROOM);
        conveyorBelt.add(Component.MAIN_UNIT);

        List<Worker> listOfWorkers = new ArrayList<>();

        for (int index = 0; index < 3; index++) {
            Worker dryRobotWorker = acmeFactory.getWorker(RobotType.DRY2000, WORKER_NAME);

            listOfWorkers.add(dryRobotWorker);

            new Thread(dryRobotWorker).start();
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        for (int index = 0; index < 3; index++) {
            listOfWorkers.get(index).stop();

            Assert.assertEquals(listOfWorkers.get(index).getNoOfAssembledRobots(), 1);
        }
    }

    private void testWorkersCannotCompleteRobot(ComponentGeneratorService componentGeneratorService) {
        ACMEFactory acmeFactoryWithMockedService = new ACMEFactory(componentGeneratorService);

        Worker dryRobotWorker = acmeFactoryWithMockedService.getWorker(RobotType.DRY2000, WORKER_NAME);
        new Thread(dryRobotWorker).start();

        Worker wetRobotWorker = acmeFactoryWithMockedService.getWorker(RobotType.WET2000, WORKER_NAME);
        new Thread(wetRobotWorker).start();

        FactorySupplier factorySupplier = acmeFactoryWithMockedService.getFactorySupplier("Producer");
        new Thread(factorySupplier).start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Assert.fail("Current thread was interrupted");
        }

        factorySupplier.stop();
        dryRobotWorker.stop();
        wetRobotWorker.stop();

        Assert.assertEquals(dryRobotWorker.getNoOfAssembledRobots(), 0);
        Assert.assertEquals(wetRobotWorker.getNoOfAssembledRobots(), 0);
    }
}
