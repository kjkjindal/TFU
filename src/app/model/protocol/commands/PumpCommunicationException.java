package app.model.protocol.commands;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/13/17
 */
public class PumpCommunicationException extends Exception {

    public PumpCommunicationException(String message, Exception e) {
        super(message, e);
    }

}
