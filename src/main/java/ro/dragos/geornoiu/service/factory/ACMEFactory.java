package main.java.ro.dragos.geornoiu.service.factory;

import main.java.ro.dragos.geornoiu.consumer.RobotComponentsPair;
import main.java.ro.dragos.geornoiu.consumer.Worker;
import main.java.ro.dragos.geornoiu.enums.Component;
import main.java.ro.dragos.geornoiu.enums.RobotType;
import main.java.ro.dragos.geornoiu.exception.InvalidRobotTypeException;
import main.java.ro.dragos.geornoiu.producer.FactorySupplier;
import main.java.ro.dragos.geornoiu.service.ComponentGeneratorService;
import main.java.ro.dragos.geornoiu.service.impl.DefaultComponentGeneratorService;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Factory responsible of building the objects of the application.
 */
public class ACMEFactory {
    private QueueStorage queueStorage;
    private ComponentGeneratorService componentGenerator;

    private static final int NUMBER_OF_MAIN_UNITS_FOR_ROBOTS = 1;
    private static final int NUMBER_OF_BROOMS_FOR_DRY2000_ROBOT = 2;
    private static final int NUMBER_OF_MOPS_FOR_WET2000_ROBOT = 2;

    public ACMEFactory() {
        this.queueStorage = QueueStorage.getInstance();
        this.componentGenerator = new DefaultComponentGeneratorService();
    }

    /**
     * Retrieve factory supplier.
     *
     * @param producerName representing the name of the factory supplier.
     * @return the factory supplier.
     */
    public FactorySupplier getComponentProducer(String producerName) {
        Queue<Component> conveyorBelt = queueStorage.getConveyorBelt();
        return new FactorySupplier(producerName, conveyorBelt, componentGenerator);
    }

    /**
     * Retrieve worker based on the robot it will construct.
     *
     * @param robotType  the type of robot the worker can construct.
     * @param workerName the name of the worker
     * @return worker which can construct the robot specified as parameter.
     */
    public Worker getWorker(RobotType robotType, String workerName) {
        if (robotType == null) {
            throw new InvalidRobotTypeException("RobotType cannot be null.");
        }

        Queue<Component> conveyorBelt = queueStorage.getConveyorBelt();

        Map<Component, RobotComponentsPair> robotComponentsMap = null;

        if (robotType.equals(RobotType.DRY2000)) {
            robotComponentsMap = getDry200ComponentsMap();
        }

        if (robotType.equals(RobotType.WET2000)) {
            robotComponentsMap = getWet200ComponentsMap();
        }

        if (workerName == null) {
            workerName = "";
        }

        workerName = robotType + workerName;

        return new Worker(workerName, conveyorBelt, robotComponentsMap);
    }


    /**
     * Retrieve a map which holds the components a worker would need to fully construct a DRY2000 robot.
     */
    private Map<Component, RobotComponentsPair> getDry200ComponentsMap() {
        Map<Component, RobotComponentsPair> robotComponentsPairMap = new HashMap<>();

        RobotComponentsPair mainUnitPairs = new RobotComponentsPair(NUMBER_OF_MAIN_UNITS_FOR_ROBOTS);
        RobotComponentsPair broomPair = new RobotComponentsPair(NUMBER_OF_BROOMS_FOR_DRY2000_ROBOT);

        robotComponentsPairMap.put(Component.MAIN_UNIT, mainUnitPairs);
        robotComponentsPairMap.put(Component.BROOM, broomPair);

        return robotComponentsPairMap;
    }

    /**
     * Retrieve a map which holds the components a worker would need to fully construct a WET2000 robot.
     */
    private Map<Component, RobotComponentsPair> getWet200ComponentsMap() {
        Map<Component, RobotComponentsPair> robotComponentsPairMap = new HashMap<>();

        RobotComponentsPair mainUnitPairs = new RobotComponentsPair(NUMBER_OF_MAIN_UNITS_FOR_ROBOTS);
        RobotComponentsPair mopPairs = new RobotComponentsPair(NUMBER_OF_MOPS_FOR_WET2000_ROBOT);

        robotComponentsPairMap.put(Component.MAIN_UNIT, mainUnitPairs);
        robotComponentsPairMap.put(Component.MOP, mopPairs);

        return robotComponentsPairMap;
    }

}
