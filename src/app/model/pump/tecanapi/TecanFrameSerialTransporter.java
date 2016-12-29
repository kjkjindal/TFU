package app.model.pump.tecanapi;

import java.util.HashMap;
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

    public TecanFrameSerialTransporter(int address, String portName, int serialBaud,
                                       int serialMillisTimeout, int maxAttempts) {
        super(address);

        this.portName = portName;
        this.serialBaud = serialBaud;
        this.serialMillisTimeout = serialMillisTimeout;
        this.maxAttempts = maxAttempts;
    }

    private byte[] sendReceive(String cmd) throws Exception {
        int attempt = 0;
        byte[] frameIn = null;

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

    private void sendFrame(byte[] frame) {

    }

    private byte[] receiveFrame() {
        return new byte[1];
    }

//    public Map<String, > listSerialPorts() {
//        Map<String, > portMap = new HashMap<>();
//
//        ports = CommPortIdentifier.getPortIdentifiers();
//
//        while (ports.hasMoreElements())
//        {
//            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();
//
//            //get only serial ports
//            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
//                portMap.put(curPort.getName(), curPort);
//
//        }
//        return portMap;
//    }

}
