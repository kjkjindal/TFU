package app.model.devices.pump.tecanapi;

import app.model.devices.MaximumAttemptsException;
import app.model.services.serial.JSSCSerialTransport;
import app.model.services.serial.JSSCSerialTransportSingleton;

import java.io.IOException;
import java.util.*;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/28/16
 */
public class TecanPumpManager {

    public TecanPumpManager() {

    }

    public List<PumpData> findSerialTecanPumps() {
        List<String> ports = JSSCSerialTransportSingleton.getSerialPorts();

        List<PumpData> foundPumps = new ArrayList<>();

        for (String portName : ports) {
            TecanFrameSerialTransporter transporter = null;
            try {
                transporter = new TecanFrameSerialTransporter(0, portName, 9600, 200, 3);

                transporter.connect();

                String config = transporter.sendReceive("?76").getData();
                String fwVersion = transporter.sendReceive("&").getData();

                transporter.disconnect();

                foundPumps.add(new PumpData(transporter, portName, config, fwVersion));
            } catch (Exception e) {

            } finally {
                if (transporter != null)
                    try {
                        transporter.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }

        return foundPumps;
    }

    public List<Pump> getSerialTecanPumps(List<PumpData> foundPumps) throws SyringeCommandException, SyringeTimeoutException, MaximumAttemptsException, IOException {
        List<Pump> pumps = new ArrayList<>();

        for (PumpData pumpData : foundPumps) {
            TecanXCalibur pump = new TecanXCalibur(pumpData.getTransporter(), false);

            pump.connect();
            pump.init();
            pump.disconnect();

            pumps.add(new Pump(pumpData.getPortName(), pump));
        }

        return pumps;
    }

    private void registerPort() {

    }

    private void unregisterPort() {

    }

    private class PumpData {

        private String portName;
        private String config;
        private String fwVersion;
        private TecanFrameSerialTransporter transporter;

        public PumpData(TecanFrameSerialTransporter transporter, String portName, String config, String fwVersion) {
            this.transporter = transporter;
            this.portName = portName;
            this.config = config;
            this.fwVersion = fwVersion;
        }

        public String getPortName() {
            return portName;
        }

        public String getConfig() {
            return config;
        }

        public String getFwVersion() {
            return fwVersion;
        }

        public TecanFrameSerialTransporter getTransporter() {
            return transporter;
        }

        public String toString() {
            return this.portName + " - " + this.config + " - " + this.fwVersion + " - " + this.transporter;
        }

    }

    public class Pump {

        private String portName;
        private TecanXCalibur pump;

        public Pump(String portName, TecanXCalibur pump) {
            this.portName = portName;
            this.pump = pump;
        }

        public String getPortName() {
            return portName;
        }

        public TecanXCalibur getPump() {
            return pump;
        }

        public String toString() {
            return this.portName + " - " + this.pump;
        }
    }

    public static void main(String[] args) throws SyringeCommandException, SyringeTimeoutException, MaximumAttemptsException, IOException {
        TecanPumpManager manager = new TecanPumpManager();

        List<PumpData> foundPumps = manager.findSerialTecanPumps();

        System.out.println("ey");

        System.out.println(foundPumps);

        List<Pump> pumps = manager.getSerialTecanPumps(foundPumps);

        System.out.println("eyy");

        System.out.println(pumps);

//        List<TecanXCalibur> pumps = new ArrayList<>();
//
//        for (Map pump : foundPumps) {
//            TecanXCalibur p = new TecanXCalibur((TecanFrameSerialTransporter) pump.get("serialTransport"), false);
//            p.init();
//            pumps.add(p);
//        }
    }

}
