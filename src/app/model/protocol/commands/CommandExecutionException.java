package app.model.protocol.commands;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/13/17
 */
public class CommandExecutionException extends Exception{

    public CommandExecutionException(String message, Throwable e) {
        super(message, e);
    }

}
