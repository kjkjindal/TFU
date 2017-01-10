package app.model.devices.pump.tecanapi;

import app.model.services.serial.SerialTransport;
import app.model.services.serial.SerialPortInUseException;
import app.model.services.serial.UnsupportedPortTypeException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/27/16
 */
public class TecanFrameSerialTransporter extends TecanFrameHandler {

    private String portName;
    private int serialBaud;
    private int serialMillisTimeout;
    private int maxAttempts;
    public SerialTransport serialTransport;

    public TecanFrameSerialTransporter(int address, String portName, int serialBaud,
                                       int serialMillisTimeout, int maxAttempts) {
        super(address);

        this.portName = portName;
        this.serialBaud = serialBaud;
        this.serialMillisTimeout = serialMillisTimeout;
        this.maxAttempts = maxAttempts;

        this.serialTransport = new SerialTransport(portName, serialBaud, serialMillisTimeout);
        try {
            this.serialTransport.connect();
        } catch (UnsupportedPortTypeException e) {
            e.printStackTrace();
        } catch (SerialPortInUseException e) {
            e.printStackTrace();
        }
    }

    public TecanFrameContents sendReceive(String cmd) throws MaximumAttemptsException, IOException {
        int attempt = 0;
        TecanFrameContents frameIn = null;

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

    private TecanFrameContents receiveFrame() throws IOException {
        return this.parseFrame(this.serialTransport.read());
    }

    public static void main(String[] args) {
        TecanFrameSerialTransporter tecan = new TecanFrameSerialTransporter(0, "/dev/tty.usbserial", 9600, 200, 2);

        try {
            System.out.println((char) 49);
            System.out.println(tecan.sendReceive("?76").getData());
            System.out.println(tecan.sendReceive("N0A3000R").getData());

            tecan.serialTransport.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() throws IOException {
        this.serialTransport.disconnect();
    }

}
