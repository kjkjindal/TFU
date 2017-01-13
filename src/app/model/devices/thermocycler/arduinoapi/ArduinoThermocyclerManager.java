package app.model.devices.thermocycler.arduinoapi;

import app.model.services.serial.JSSCSerialTransport;
import app.model.services.serial.JSSCSerialTransportSingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/12/17
 */
public class ArduinoThermocyclerManager {

    public List<ThermocyclerData> findSerialTecanPumps() {
        List<String> ports = JSSCSerialTransportSingleton.getSerialPorts();

        List<ThermocyclerData> foundThermocyclers = new ArrayList<>();

        for (String portName : ports) {
            ArduinoFrameSerialTransporter transporter = null;
            try {
                transporter = new ArduinoFrameSerialTransporter(0, portName, 9600, 200, 3);

                transporter.connect();

                String id = transporter.sendReceive("getID");

                transporter.disconnect();

                if (id.equals("ArduinoThermocycler"))
                    foundThermocyclers.add(new ThermocyclerData(transporter, portName, id));
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

        return foundThermocyclers;
    }

    public List<Thermocycler> getSerialTecanPumps(List<ThermocyclerData> foundPumps) throws IOException {
        List<Thermocycler> pumps = new ArrayList<>();

        for (ThermocyclerData thermocyclerData : foundPumps) {
            ArduinoThermocycler pump = new ArduinoThermocycler(thermocyclerData.getTransporter());
            pumps.add(new Thermocycler(thermocyclerData.getPortName(), pump));
        }

        return pumps;
    }

    private class ThermocyclerData {

        private String portName;
        private String ID;
        private ArduinoFrameSerialTransporter transporter;

        public ThermocyclerData(ArduinoFrameSerialTransporter transporter, String portName, String id) {
            this.transporter = transporter;
            this.portName = portName;
            this.ID = id;
        }

        public String getPortName() {
            return portName;
        }

        public String getID() {
            return this.ID;
        }

        public ArduinoFrameSerialTransporter getTransporter() {
            return transporter;
        }

        public String toString() {
            return this.portName + " - " + this.ID + " - " + this.transporter;
        }

    }

    public class Thermocycler {

        private String portName;
        private ArduinoThermocycler thermocycler;

        public Thermocycler(String portName, ArduinoThermocycler thermocycler) {
            this.portName = portName;
            this.thermocycler = thermocycler;
        }

        public String getPortName() {
            return portName;
        }

        public ArduinoThermocycler getThermocycler() {
            return thermocycler;
        }

        public String toString() {
            return this.portName + " - " + this.thermocycler;
        }
    }

    public static void main(String[] args) {

    }

}
