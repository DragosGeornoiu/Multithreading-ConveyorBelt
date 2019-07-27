package ro.dragos.geornoiu;

import ro.dragos.geornoiu.enums.RobotType;
import ro.dragos.geornoiu.exception.InvalidRobotTypeException;
import ro.dragos.geornoiu.service.factory.ACMEFactory;

public class Main {

    public static void main(String[] args) throws Exception {

        int noOfDry2000Workers = Integer.valueOf(args[0]);
        int noOfWet2000Workers = Integer.valueOf(args[1]);
        int numberOfSeconds = Integer.valueOf(args[2]);

        ACMEFactory objectFactory = new ACMEFactory();

        for (int i = 0; i < noOfDry2000Workers; i++) {
            try {
                new Thread(objectFactory.getWorker(RobotType.DRY2000, String.valueOf(i))).start();
            } catch (InvalidRobotTypeException irte) {
                //log the excepion
            }

        }

        for (int i = 0; i < noOfWet2000Workers; i++) {
            new Thread(objectFactory.getWorker(RobotType.WET2000, String.valueOf(i))).start();
        }

        new Thread(objectFactory.getComponentProducer("Producer")).start();

        // Let the simulation run
        Thread.sleep(numberOfSeconds * 1000);

        // End of simulation
        System.exit(0);
    }
}
