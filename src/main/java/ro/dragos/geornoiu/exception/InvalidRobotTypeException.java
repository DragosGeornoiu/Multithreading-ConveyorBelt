package main.java.ro.dragos.geornoiu.exception;

/**
 * Exception thrown when the robot type is invalid.
 */
public class InvalidRobotTypeException extends RuntimeException {

    public InvalidRobotTypeException(final String message) {
        super(message);
    }

    public InvalidRobotTypeException(final String message, final Throwable cause) {
        super(message, cause);
    }

}