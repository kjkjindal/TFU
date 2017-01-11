package app.model.devices.pump.tecanapi;

import app.model.services.serial.JSSCSerialTransport;
import app.utility.Util;

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
    public JSSCSerialTransport serialTransport;

    public TecanFrameSerialTransporter(int address, String portName, int serialBaud,
                                       int serialMillisTimeout, int maxAttempts) {
        super(address);

        this.portName = portName;
        this.serialBaud = serialBaud;
        this.serialMillisTimeout = serialMillisTimeout;
        this.maxAttempts = maxAttempts;

        this.serialTransport = new JSSCSerialTransport(portName, serialBaud, serialMillisTimeout);
        this.serialTransport.disconnect();
        this.serialTransport.connect();
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

    public void disconnect() throws IOException {
        this.serialTransport.disconnect();
    }

    public static void main(String[] args) {
        System.out.println(JSSCSerialTransport.getSerialPorts());

        TecanFrameSerialTransporter tecan1 = new TecanFrameSerialTransporter(0, "/dev/tty.usbserial", 9600, 200, 2);

        try {
            TecanXCalibur pump = new TecanXCalibur(tecan1, false);
            pump.init();
//            pump.setSpeed(6);
//            pump.extract(2, 1000);
//            double time = pump.executeChain();
//            System.out.println("SHOULD TAKE: " + time);
//            Util.sleep(15*1000);
//            pump.dispense(1, 1000);
//            pump.executeChain();

//            pump.sendReceive("S6IA24000OS16A0", true);

//            System.out.println(pump.getCurrentPort());

            tecan1.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MaximumAttemptsException e) {
            e.printStackTrace();
        } catch (SyringeCommandException e) {
            e.printStackTrace();
        } catch (SyringeTimeoutException e) {
            e.printStackTrace();
        }

        Util.sleep(5000);

        TecanFrameSerialTransporter tecan2 = new TecanFrameSerialTransporter(0, "/dev/tty.usbserial2", 9600, 200, 2);
        try {
            TecanXCalibur pump2 = new TecanXCalibur(tecan2, false);
            pump2.init();
//            System.out.println(pump2.getConfig());
//            pump.setSpeed(6);
//            pump.extract(2, 1000);
//            double time = pump.executeChain();
//            System.out.println("SHOULD TAKE: " + time);
//            Util.sleep(15*1000);
//            pump.dispense(1, 1000);
//            pump.executeChain();

//            pump.sendReceive("S6IA24000OS16A0", true);

//            System.out.println(pump.getCurrentPort());

            tecan2.disconnect();

            System.exit(0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MaximumAttemptsException e) {
            e.printStackTrace();
        } catch (SyringeCommandException e) {
            e.printStackTrace();
        } catch (SyringeTimeoutException e) {
            e.printStackTrace();
        }


//        try {
//            System.out.println(tecan1.sendReceive("?10").getData());
////            System.out.println(tecan2.sendReceive("Z0,0,9R").getData());
////            System.out.println(tecan.sendReceive("IA3000OA0R").getData());
//            tecan1.serialTransport.disconnect();
////            tecan2.serialTransport.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
