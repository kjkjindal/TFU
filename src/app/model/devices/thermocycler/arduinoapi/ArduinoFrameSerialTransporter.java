package app.model.devices.thermocycler.arduinoapi;

import app.model.devices.MaximumAttemptsException;
import app.model.services.serial.JSSCSerialTransportSingleton;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/12/17
 */
public class ArduinoFrameSerialTransporter extends ArduinoFrameHandler {

    private String portName;
    private int serialBaud;
    private int serialMillisTimeout;
    private int maxAttempts;
    public JSSCSerialTransportSingleton serialTransport;

    public ArduinoFrameSerialTransporter(int address, String portName, int serialBaud, int serialMillisTimeout, int maxAttempts) {
        super();

        this.portName = portName;
        this.serialBaud = serialBaud;
        this.serialMillisTimeout = serialMillisTimeout;
        this.maxAttempts = maxAttempts;

        this.serialTransport = JSSCSerialTransportSingleton.getInstance();
    }

    public String sendReceive(String cmd) throws MaximumAttemptsException, IOException {
        int attempt = 0;
        String frameIn = "";

        while (attempt < this.maxAttempts) {
            attempt++;

            byte[] frame;

            if (attempt == 1)
                frame = this.emitFrame(cmd);
            else
                frame = this.emitRepeat();

            this.sendFrame(frame);

            frameIn = this.receiveFrame();

            if (frameIn != null)
                return frameIn;

            try { TimeUnit.MILLISECONDS.sleep(this.serialMillisTimeout); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }
        throw new MaximumAttemptsException("Maximum serial communication attempts have been reached!");
    }

    private void sendFrame(byte[] frame) throws IOException {
        this.serialTransport.write(frame);
    }

    private String receiveFrame() throws IOException {
        return this.parseFrame(this.serialTransport.read());
    }

    public void connect() throws IOException {
        this.serialTransport.connect(this.portName, this.serialBaud);
    }

    public void disconnect() throws IOException {
        this.serialTransport.disconnect();
    }
}
