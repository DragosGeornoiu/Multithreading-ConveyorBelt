package ro.dragos.geornoiu.service;

import ro.dragos.geornoiu.enums.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomComponentGeneratorService {
    private final int noOfPossibleComponents;
    private final List<Component> componentList;
    private final Random randomGenerator;

    public RandomComponentGeneratorService () {
        this.componentList = Collections.unmodifiableList(Arrays.asList(Component.values()));
        this.noOfPossibleComponents = this.componentList.size();
        this.randomGenerator = new Random();
    }

    public Component getRandomComponent() {
        return componentList.get(randomGenerator.nextInt(noOfPossibleComponents));
    }
}
