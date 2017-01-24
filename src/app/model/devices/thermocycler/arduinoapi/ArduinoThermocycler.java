package app.model.devices.thermocycler.arduinoapi;

import app.model.devices.MaximumAttemptsException;
import app.model.devices.pump.tecanapi.SyringeCommandException;
import app.utility.Util;

import java.io.IOException;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/12/17
 */
public class ArduinoThermocycler {

    private ArduinoFrameSerialTransporter serialTransporter;

    private int setpoint;

    public ArduinoThermocycler(ArduinoFrameSerialTransporter transporter) {
        this.serialTransporter = transporter;
    }

    protected String sendReceive(String cmd) throws IOException, MaximumAttemptsException, SyringeCommandException {
        return this.serialTransporter.sendReceive(cmd);
    }

    public void connect() throws IOException {
        this.serialTransporter.connect();
    }

    public void disconnect() throws IOException {
        this.serialTransporter.disconnect();
    }

    public void startTemperatureControl() throws SyringeCommandException, IOException, MaximumAttemptsException {
        this.sendReceive("G");
    }

    public void stopTemperatureControl() throws SyringeCommandException, IOException, MaximumAttemptsException {
        this.sendReceive("T");
    }

    public void setSetpoint(int c) throws SyringeCommandException, IOException, MaximumAttemptsException {
        this.setpoint = c;
        this.sendReceive(String.format("S%d/", c));
    }

    public int getConfig() throws SyringeCommandException, IOException, MaximumAttemptsException {
        String response = this.sendReceive("?C");
        return Integer.parseInt(response);
    }

    public int getSetpoint() throws SyringeCommandException, IOException, MaximumAttemptsException {
        String response = this.sendReceive("?S");
        return Integer.parseInt(response);
    }

    public int getTemperature() throws SyringeCommandException, IOException, MaximumAttemptsException {
        String response = this.sendReceive("?T");
        return Integer.parseInt(response);
    }

    public void waitUntilReachedSetpoint() throws SyringeCommandException, IOException, MaximumAttemptsException {
        while (this.getTemperature() < setpoint) {
            Util.sleepMillis(200);
        }
    }

}
