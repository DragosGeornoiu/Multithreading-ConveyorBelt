package main.java.ro.dragos.geornoiu.service;

import main.java.ro.dragos.geornoiu.enums.Component;

/**
 * Service used to manage component related actions.
 */
public interface ComponentGeneratorService {

    /**
     * Returns a component.
     *
     * @return a component entry.
     */
    Component retrieveComponent();
}
