package ro.dragos.geornoiu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.dragos.geornoiu.enums.RobotType;
import ro.dragos.geornoiu.exception.InvalidRobotTypeException;
import ro.dragos.geornoiu.service.ComponentGeneratorService;
import ro.dragos.geornoiu.service.factory.ACMEFactory;
import ro.dragos.geornoiu.service.impl.DefaultComponentGeneratorService;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        int noOfDry2000Workers = Integer.valueOf(args[0]);
        int noOfWet2000Workers = Integer.valueOf(args[1]);
        int numberOfSeconds = Integer.valueOf(args[2]);

        ComponentGeneratorService componentGeneratorService = new DefaultComponentGeneratorService();
        ACMEFactory objectFactory = new ACMEFactory(componentGeneratorService);

        for (int i = 0; i < noOfDry2000Workers; i++) {
            try {
                new Thread(objectFactory.getWorker(RobotType.DRY2000, String.valueOf(i))).start();
            } catch (InvalidRobotTypeException irte) {
                LOG.error("Invalid robot type given as parameter for Worker");
            }

        }

        for (int i = 0; i < noOfWet2000Workers; i++) {
            try {
                new Thread(objectFactory.getWorker(RobotType.WET2000, String.valueOf(i))).start();
            } catch (InvalidRobotTypeException irte) {
                LOG.error("Invalid robot type given as parameter for Worker");
            }
        }

        new Thread(objectFactory.getFactorySupplier("Producer")).start();

        // Let the simulation run
        Thread.sleep(numberOfSeconds * 1000);

        // End of simulation
        System.exit(0);
    }
}
