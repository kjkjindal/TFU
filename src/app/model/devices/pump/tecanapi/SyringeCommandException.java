package app.model.devices.pump.tecanapi;

import java.util.Map;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/5/17
 */
public class SyringeCommandException extends Exception {

    private int errorCode;

    public SyringeCommandException(int errorCode, Map<Integer, String> errorMap) {
        super("Error ["+errorCode+"] - "+errorMap.get(errorCode));
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

}