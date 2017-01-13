package app.model.devices.pump.tecanapi;

import app.model.devices.MaximumAttemptsException;
import app.model.services.serial.JSSCSerialTransport;
import app.model.services.serial.JSSCSerialTransportSingleton;
import app.utility.Util;

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
    public JSSCSerialTransportSingleton serialTransport;

    public TecanFrameSerialTransporter(int address, String portName, int serialBaud,
                                       int serialMillisTimeout, int maxAttempts) {
        super(address);

        this.portName = portName;
        this.serialBaud = serialBaud;
        this.serialMillisTimeout = serialMillisTimeout;
        this.maxAttempts = maxAttempts;

        this.serialTransport = JSSCSerialTransportSingleton.getInstance();
//        this.serialTransport.disconnect();
//        this.serialTransport.connect();
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

    public void connect() throws IOException {
        this.serialTransport.connect(this.portName, this.serialBaud);
    }

    public void disconnect() throws IOException {
        this.serialTransport.disconnect();
    }

    public static void main(String[] args) {
        System.out.println(JSSCSerialTransport.getSerialPorts());

        TecanFrameSerialTransporter tecan1 = new TecanFrameSerialTransporter(0, "/dev/tty.usbserial", 9600, 200, 2);
        TecanFrameSerialTransporter tecan2 = new TecanFrameSerialTransporter(0, "/dev/tty.usbserial6", 9600, 200, 2);

        try {
            TecanXCalibur pump1 = new TecanXCalibur(tecan1, false);

            tecan1.connect();

//            pump1.init();

            pump1.setSpeed(8);

            pump1.extract(7, 1000);

            pump1.setSpeed(10);

            pump1.dispense(1, 1000);

            double time = pump1.executeChain() * 1.1;

            Util.sleepMillis((int) time);

            tecan1.disconnect();



            TecanXCalibur pump2 = new TecanXCalibur(tecan2, false);

            tecan2.connect();

//            pump2.init();

            pump2.setSpeed(15);

            pump2.extract(7, 500);

            pump2.setSpeed(18);

            pump2.dispense(1, 500);

            time = pump2.executeChain() * 1.1;

            Util.sleepMillis((int) time);

            tecan2.disconnect();



//            System.out.println(tecan1.sendReceive("S15O2P12000I1D12000R").getData());
//            pump.init();
            // S15Z2P12000Y1D12000
//            System.out.println(pump.getConfig());
//            pump.setSpeed(15);
//            pump.extract(1, 1000);
//            pump.dispense(9, 1000);
//            double time = pump.executeChain();
//            System.out.println("SHOULD TAKE: " + time);
//            Util.sleepMillis(15*1000);
//            pump.dispense(1, 1000);
//            pump.executeChain();

//            pump.sendReceive("S6IA24000OS16A0", true);

//            System.out.println(pump.getCurrentPort());

//            tecan1.disconnect();

        } catch (IOException | MaximumAttemptsException | SyringeTimeoutException | SyringeCommandException e) {
            e.printStackTrace();
        }

//        Util.sleepMillis(5000);
//
//        TecanFrameSerialTransporter tecan2 = new TecanFrameSerialTransporter(0, "/dev/tty.usbserial1", 9600, 200, 2);
//        try {
//            TecanXCalibur pump2 = new TecanXCalibur(tecan2, false);
////            pump2.init();
//////            System.out.println(pump2.getConfig());
//////            pump.setSpeed(6);
//////            pump.extract(2, 1000);
//////            double time = pump.executeChain();
//////            System.out.println("SHOULD TAKE: " + time);
//////            Util.sleepMillis(15*1000);
//////            pump.dispense(1, 1000);
//////            pump.executeChain();
////
//////            pump.sendReceive("S6IA24000OS16A0", true);
////
//////            System.out.println(pump.getCurrentPort());
////
//            tecan1.disconnect();
//            tecan2.disconnect();
////
////            System.exit(0);
////
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (MaximumAttemptsException e) {
//            e.printStackTrace();
//        } catch (SyringeCommandException e) {
//            e.printStackTrace();
//        } catch (SyringeTimeoutException e) {
//            e.printStackTrace();
//        }


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
