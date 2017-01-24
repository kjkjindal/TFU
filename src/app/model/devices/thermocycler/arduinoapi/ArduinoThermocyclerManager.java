package app.model.devices.thermocycler.arduinoapi;

import app.model.devices.MaximumAttemptsException;
import app.model.devices.pump.tecanapi.SyringeCommandException;
import app.model.services.serial.JSSCSerialTransport;
import app.model.services.serial.JSSCSerialTransportSingleton;
import app.utility.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/12/17
 */
public class ArduinoThermocyclerManager {

    public List<ThermocyclerData> findSerialArduinoThermocyclers() {
        List<String> ports = JSSCSerialTransportSingleton.getSerialPorts();

        List<ThermocyclerData> foundThermocyclers = new ArrayList<>();

        for (String portName : ports) {
            ArduinoFrameSerialTransporter transporter = null;

            try {
                transporter = new ArduinoFrameSerialTransporter(0, portName, 9600, 200, 3);

                transporter.connect();

                String id = transporter.sendReceive("?C");

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

    public List<Thermocycler> getSerialArduinoThermocyclers(List<ThermocyclerData> foundPumps) throws IOException {
        List<Thermocycler> thermocyclers = new ArrayList<>();

        for (ThermocyclerData thermocyclerData : foundPumps) {
            ArduinoThermocycler thermocycler = new ArduinoThermocycler(thermocyclerData.getTransporter());
            thermocyclers.add(new Thermocycler(thermocyclerData.getPortName(), thermocycler));
        }

        return thermocyclers;
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

    public static void main(String[] args) throws IOException {
        ArduinoThermocyclerManager manager = new ArduinoThermocyclerManager();

        List<ThermocyclerData> foundThermocyclers = manager.findSerialArduinoThermocyclers();

        System.out.println("ey");

        System.out.println(foundThermocyclers);

        List<Thermocycler> thermocyclers = manager.getSerialArduinoThermocyclers(foundThermocyclers);

        System.out.println("eyy");

        System.out.println(thermocyclers);

        Thermocycler thermo = thermocyclers.get(0);

        thermo.getThermocycler().connect();
        try {
            System.out.println(thermo.getThermocycler().sendReceive("?S"));
        } catch (MaximumAttemptsException e) {
            e.printStackTrace();
        } catch (SyringeCommandException e) {
            e.printStackTrace();
        }
        thermo.getThermocycler().disconnect();
    }

}
