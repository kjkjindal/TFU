package app.model.pump.tecanapi;

import app.model.SerialTransport;

import java.io.IOException;
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
        } catch (SerialTransport.UnsupportedPortTypeException e) {
            e.printStackTrace();
        } catch (SerialTransport.SerialPortInUseException e) {
            e.printStackTrace();
        }
    }

    private TecanFrameContents sendReceive(String cmd) throws Exception {
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
        throw new Exception("Maximum serial communication attempts have been reached!");
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
            System.out.println(tecan.sendReceive("A0R"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
