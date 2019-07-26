package ro.dragos.geornoiu.producer;

import ro.dragos.geornoiu.enums.Component;
import ro.dragos.geornoiu.service.RandomComponentGeneratorService;

import java.util.Queue;

public class ComponentProducer implements Runnable {

    private String name;
    private final Queue<Component> conveyorBelt;
    private final RandomComponentGeneratorService randomComponentGeneratorService;

    public ComponentProducer(String name, Queue<Component> conveyorBelt,
                             RandomComponentGeneratorService randomComponentGeneratorService) {
        this.name = name;
        this.conveyorBelt = conveyorBelt;
        this.randomComponentGeneratorService = randomComponentGeneratorService;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this.conveyorBelt) {
                while (this.conveyorBelt.size() == 10) {
                    try {
                        System.out.println("Queue is full");
                        this.conveyorBelt.wait(10000);

                        if (this.conveyorBelt.size() == 10) {
                            System.out.println("Removing component : " + this.conveyorBelt.peek());
                            this.conveyorBelt.remove();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                Component component = randomComponentGeneratorService.getRandomComponent();

                System.out.println(this.name + " is adding component " + component.name() + " to queue");
                this.conveyorBelt.add(component);
                printQueue();

                this.conveyorBelt.notifyAll();
            }

            try {
                //sleep is outside synchronized block
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    private void printQueue() {
        StringBuilder sb = new StringBuilder();
        for (Component item : this.conveyorBelt) {
            sb.append(item.name()).append(" - ");
        }

        System.out.println("Queue: " + sb.toString());
    }


}