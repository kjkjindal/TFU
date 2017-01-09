package app.model.devices.pump.tecanapi;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/5/17
 */
public class SyringeTimeoutException extends Exception{

    public SyringeTimeoutException(String message) {
        super(message);
    }

}
