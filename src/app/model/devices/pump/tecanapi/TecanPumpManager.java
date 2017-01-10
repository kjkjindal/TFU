package app.model.devices.pump.tecanapi;

import app.model.services.serial.SerialTransport;
import gnu.io.CommPortIdentifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private List<Map> findTecanPumps() {
        Map<String, CommPortIdentifier> map = SerialTransport.getSerialPorts();

        List<Map> foundPumps = new ArrayList<>();

        for (String portName : map.keySet()) {
            try {
                TecanFrameSerialTransporter t = new TecanFrameSerialTransporter(0, portName, 9600, 200, 3);
                String config = t.sendReceive("?76").getData();
                String fwVersion = t.sendReceive("&").getData();

                Map pumpData = new HashMap();
                pumpData.put("name", portName);
                pumpData.put("config", config);
                pumpData.put("fwVersion", fwVersion);
                pumpData.put("serialTransport", t);

                foundPumps.add(pumpData);
            } catch (Exception e) {  }
        }

        return foundPumps;
    }

    private void registerPort() {

    }

    private void unregisterPort() {

    }

    public static void main(String[] args) throws SyringeCommandException, SyringeTimeoutException, MaximumAttemptsException, IOException {
        TecanPumpManager manager = new TecanPumpManager();

        List<Map> foundPumps = manager.findTecanPumps();

        System.out.println("eyy");

        System.out.println(foundPumps);

//        List<TecanXCalibur> pumps = new ArrayList<>();
//
//        for (Map pump : foundPumps) {
//            TecanXCalibur p = new TecanXCalibur((TecanFrameSerialTransporter) pump.get("serialTransport"), false);
//            p.init();
//            pumps.add(p);
//        }
    }

}
