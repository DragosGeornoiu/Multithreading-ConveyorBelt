package ro.dragos.geornoiu;

import ro.dragos.geornoiu.consumer.ComponentConsumer;
import ro.dragos.geornoiu.enums.Component;
import ro.dragos.geornoiu.producer.ComponentProducer;
import ro.dragos.geornoiu.service.RandomComponentGeneratorService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Main {

    public static void main(String[] args) throws Exception {

        int numConsumersDry = Integer.valueOf(args[0]);
        int numConsumersWet = Integer.valueOf(args[1]);
        int numberOfSeconds = Integer.valueOf(args[2]);

        Queue<Component> conveyorBelt = new LinkedList<>();

        for (int i = 0; i < numConsumersDry; i++) {
            Runnable runnable = new ComponentConsumer("dry" + i, conveyorBelt, getDry200ComponentsMap());
            Thread t = new Thread(runnable);
            t.start();
        }

        for (int i = 0; i < numConsumersWet; i++) {
            Runnable runnable = new ComponentConsumer("wet" + i, conveyorBelt, getWet200ComponentsMap());
            Thread t = new Thread(runnable);
            t.start();
        }

        RandomComponentGeneratorService randomComponentGeneratorService = new RandomComponentGeneratorService();
        new Thread(new ComponentProducer("Producer", conveyorBelt, randomComponentGeneratorService)).start();

        // Let the simulation run
        Thread.sleep(numberOfSeconds * 1000);

        // End of simulation
        System.exit(0);
    }

    private static Map<Component, Integer> getDry200ComponentsMap() {
        Map<Component, Integer> map = new HashMap<>();

        map.put(Component.MAIN_UNIT, 1);
        map.put(Component.BROOM, 2);

        return map;
    }

    private static Map<Component, Integer> getWet200ComponentsMap() {
        Map<Component, Integer> map = new HashMap<>();

        map.put(Component.MAIN_UNIT, 1);
        map.put(Component.MOP, 2);

        return map;
    }
}
