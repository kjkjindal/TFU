package app.model.devices.thermocycler.arduinoapi;

import app.model.devices.MaximumAttemptsException;
import app.model.devices.pump.tecanapi.SyringeCommandException;

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
        this.sendReceive("start");
    }

    public void stopTemperatureControl() throws SyringeCommandException, IOException, MaximumAttemptsException {
        this.sendReceive("stop");
    }

    public void setSetpoint(int c) throws SyringeCommandException, IOException, MaximumAttemptsException {
        this.sendReceive(String.format("S%d", c));
    }

    public int getSetpoint() throws SyringeCommandException, IOException, MaximumAttemptsException {
        String response = this.sendReceive("getSetpoint");
        return Integer.parseInt(response);
    }

    public int getTemperature() throws SyringeCommandException, IOException, MaximumAttemptsException {
        String response = this.sendReceive("getTemp");
        return Integer.parseInt(response);
    }

}
