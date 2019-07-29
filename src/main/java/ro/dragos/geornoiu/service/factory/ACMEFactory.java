package ro.dragos.geornoiu.service.factory;

import ro.dragos.geornoiu.consumer.RobotComponentsPair;
import ro.dragos.geornoiu.consumer.Worker;
import ro.dragos.geornoiu.enums.Component;
import ro.dragos.geornoiu.enums.RobotType;
import ro.dragos.geornoiu.exception.InvalidRobotTypeException;
import ro.dragos.geornoiu.producer.FactorySupplier;
import ro.dragos.geornoiu.service.ComponentGeneratorService;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Factory responsible of building the objects of the application.
 */
public class ACMEFactory {
    private final ComponentGeneratorService componentGenerator;

    private static final int NUMBER_OF_MAIN_UNITS_FOR_ROBOTS = 1;
    private static final int NUMBER_OF_BROOMS_FOR_DRY2000_ROBOT = 2;
    private static final int NUMBER_OF_MOPS_FOR_WET2000_ROBOT = 2;

    public ACMEFactory(ComponentGeneratorService componentGeneratorService) {
        this.componentGenerator = componentGeneratorService;
    }

    /**
     * Retrieve factory supplier.
     *
     * @param name representing the name of the factory supplier.
     * @return the factory supplier.
     */
    public FactorySupplier getFactorySupplier(String name) {
        Queue<Component> conveyorBelt = QueueStorage.getConveyorBelt();
        return new FactorySupplier(name, conveyorBelt, this.componentGenerator);
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

        Queue<Component> conveyorBelt = QueueStorage.getConveyorBelt();

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

        workerName = robotType + "-" + workerName;

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

    public ComponentGeneratorService getComponentGenerator() {
        return componentGenerator;
    }
}
