package app.model.devices.pump.tecanapi;

import app.model.services.serial.SerialTransport;
import gnu.io.CommPortIdentifier;

import java.util.Map;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/28/16
 */
public class TecanPumpManager {

    private Map<String, TecanFrameSerialTransporter> portTransportMap;

    public TecanPumpManager() {

    }

    private void findTecanPumps() {
        Map<String, CommPortIdentifier> map = SerialTransport.getSerialPorts();

    }

    private void registerPort() {

    }

    private void unregisterPort() {

    }

}
