package ro.dragos.geornoiu.service;

import ro.dragos.geornoiu.enums.Component;

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
