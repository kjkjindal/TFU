package app.model.services.serial;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/5/17
 */
public class SerialPortInUseException extends Exception {

    public SerialPortInUseException(String message) {
        super(message);
    }

}
