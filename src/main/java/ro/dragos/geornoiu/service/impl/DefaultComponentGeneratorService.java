package ro.dragos.geornoiu.service.impl;

import ro.dragos.geornoiu.enums.Component;
import ro.dragos.geornoiu.service.ComponentGeneratorService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Default implementation of {@link ComponentGeneratorService}.
 */
public class DefaultComponentGeneratorService implements ComponentGeneratorService {
    private final int noOfPossibleComponents;
    private final List<Component> componentList;
    private final Random randomGenerator;

    public DefaultComponentGeneratorService() {
        this.componentList = Collections.unmodifiableList(Arrays.asList(Component.values()));
        this.noOfPossibleComponents = this.componentList.size();
        this.randomGenerator = new Random();
    }


    @Override
    public Component retrieveComponent() {
        return this.componentList.get(this.randomGenerator.nextInt(this.noOfPossibleComponents));
    }
}
