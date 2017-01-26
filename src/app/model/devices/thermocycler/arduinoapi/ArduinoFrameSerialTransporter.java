package app.model.devices.thermocycler.arduinoapi;

import app.model.devices.MaximumAttemptsException;
import app.model.services.serial.JSSCSerialTransportSingleton;
import app.utility.Util;

import java.io.IOException;
import java.util.Arrays;
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
            Util.sleepMillis(200);
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
        byte[] frame = this.serialTransport.read();
        System.out.println("------------Frame = " + Arrays.toString(frame));
        return this.parseFrame(frame);
    }

    public void connect() throws IOException {
        this.serialTransport.connect(this.portName, this.serialBaud);
        Util.sleepMillis(2500);
    }

    public void disconnect() throws IOException {
        this.serialTransport.disconnect();
    }

    public static void main(String[] args) {
        ArduinoFrameSerialTransporter transporter = new ArduinoFrameSerialTransporter(0, "/dev/tty.usbmodem1411", 9600, 200, 2);
        try {
            transporter.connect();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
